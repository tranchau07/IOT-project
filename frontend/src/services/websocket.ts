import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type WsCallback = (payload: any) => void;

class WebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, StompSubscription> = new Map();
  private connectionStatusCallbacks: Set<(status: 'CONNECTED' | 'DISCONNECTED' | 'CONNECTING') => void> = new Set();
  private currentStatus: 'CONNECTED' | 'DISCONNECTED' | 'CONNECTING' = 'DISCONNECTED';

  connect(onConnectSuccess?: () => void) {
    if (this.client?.active) {
      return;
    }

    this.setStatus('CONNECTING');

    // Create SockJS client proxying to backend WebSocket endpoint /ws/smart-classroom
    const socketFactory = () => new SockJS('/ws/smart-classroom');

    this.client = new Client({
      webSocketFactory: socketFactory,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (msg) => {
        if (import.meta.env.MODE !== 'production') {
          console.log('[STOMP-DEBUG]:', msg);
        }
      },
    });

    this.client.onConnect = () => {
      console.log('[WebSocket] Connected successfully to STOMP Broker.');
      this.setStatus('CONNECTED');
      if (onConnectSuccess) {
        onConnectSuccess();
      }
    };

    this.client.onDisconnect = () => {
      console.log('[WebSocket] Disconnected from STOMP Broker.');
      this.setStatus('DISCONNECTED');
    };

    this.client.onStompError = (frame) => {
      console.error('[WebSocket] STOMP Error encountered:', frame.headers['message']);
      console.error('[WebSocket] Error Details:', frame.body);
      this.setStatus('DISCONNECTED');
    };

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      // Clear all active subscriptions
      this.subscriptions.forEach((sub) => sub.unsubscribe());
      this.subscriptions.clear();

      this.client.deactivate();
      this.client = null;
      this.setStatus('DISCONNECTED');
      console.log('[WebSocket] STOMP connection deactivated.');
    }
  }

  subscribe(topic: string, callback: WsCallback) {
    if (!this.client || !this.client.connected) {
      console.warn(`[WebSocket] Delaying subscription to topic "${topic}" - client not yet connected.`);
      
      // Retry in 1 second if not connected yet
      setTimeout(() => this.subscribe(topic, callback), 1000);
      return;
    }

    // Unsubscribe from existing matching topic if already registered
    if (this.subscriptions.has(topic)) {
      this.subscriptions.get(topic)?.unsubscribe();
      this.subscriptions.delete(topic);
    }

    const subscription = this.client.subscribe(topic, (message) => {
      try {
        const payload = JSON.parse(message.body);
        callback(payload);
      } catch (err) {
        console.error(`[WebSocket] Error parsing JSON body on topic ${topic}:`, err);
        callback(message.body);
      }
    });

    this.subscriptions.set(topic, subscription);
    console.log(`[WebSocket] Subscribed to topic: ${topic}`);
  }

  unsubscribe(topic: string) {
    const subscription = this.subscriptions.get(topic);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(topic);
      console.log(`[WebSocket] Unsubscribed from topic: ${topic}`);
    }
  }

  onStatusChange(callback: (status: 'CONNECTED' | 'DISCONNECTED' | 'CONNECTING') => void) {
    this.connectionStatusCallbacks.add(callback);
    callback(this.currentStatus);
    return () => {
      this.connectionStatusCallbacks.delete(callback);
    };
  }

  private setStatus(status: 'CONNECTED' | 'DISCONNECTED' | 'CONNECTING') {
    this.currentStatus = status;
    this.connectionStatusCallbacks.forEach((cb) => cb(status));
  }

  getConnectionStatus() {
    return this.currentStatus;
  }
}

export const wsService = new WebSocketService();
export default wsService;
