import { create } from 'zustand';
import { Classroom, SensorReading, ControlLog } from '../types';
import { classroomService, sensorReadingService, controlService } from '../services/api';
import { wsService } from '../services/websocket';

interface ClassroomState {
  classrooms: Classroom[];
  selectedClassroom: Classroom | null;
  sensorReadings: SensorReading[]; // recent readings for the active classroom
  controlLogs: ControlLog[];       // recent control logs for the active classroom
  isLoading: boolean;
  isControlling: boolean;          // Optimistic UI state block
  wsStatus: 'CONNECTED' | 'DISCONNECTED' | 'CONNECTING';

  fetchClassrooms: () => Promise<void>;
  selectClassroom: (classroom: Classroom) => Promise<void>;
  clearSelectedClassroom: () => void;
  sendControl: (command: any) => Promise<void>;
  clearClassroomFault: (classroomId: string) => Promise<void>;
  shutdownClassroom: (classroomId: string) => Promise<void>;
  
  // Real-time Event Handlers triggered by WebSocket subscriptions
  handleLiveSensorUpdate: (reading: SensorReading) => void;
  handleLiveStateUpdate: (update: { power: 'ON' | 'OFF'; connectivity: 'ONLINE' | 'OFFLINE' }) => void;
  handleLiveControlLogUpdate: (log: ControlLog) => void;
  
  // Connect and subscribe to active classroom
  subscribeToClassroom: (classroomId: string) => void;
  unsubscribeFromClassroom: (classroomId: string) => void;
}

export const useClassroomStore = create<ClassroomState>((set, get) => ({
  classrooms: [],
  selectedClassroom: null,
  sensorReadings: [],
  controlLogs: [],
  isLoading: false,
  isControlling: false,
  wsStatus: 'DISCONNECTED',

  fetchClassrooms: async () => {
    set({ isLoading: true });
    try {
      const data = await classroomService.getAll();
      set({ classrooms: data, isLoading: false });
    } catch (error) {
      console.error('Failed to fetch classrooms:', error);
      set({ isLoading: false });
    }
  },

  selectClassroom: async (classroom: Classroom) => {
    set({ selectedClassroom: classroom, isLoading: true, sensorReadings: [], controlLogs: [] });
    const classroomId = classroom.id;

    try {
      // 1. Fetch recent 20 readings for historical data representation
      const readings = await sensorReadingService.getLimit20(classroomId);
      
      // 2. Fetch recent control logs (last 24 hours as default)
      const now = new Date();
      const oneDayAgo = new Date(now.getTime() - 24 * 60 * 60 * 1000);
      const logs = await controlService.getHistory(
        classroomId,
        oneDayAgo.toISOString(),
        now.toISOString()
      );

      set({ 
        sensorReadings: readings.reverse(), // chronologically ordered (oldest to newest)
        controlLogs: logs,
        isLoading: false 
      });

      // 3. Connect and subscribe to real-time WebSockets
      get().subscribeToClassroom(classroomId);
    } catch (error) {
      console.error(`Failed to load details for classroom ${classroomId}:`, error);
      set({ isLoading: false });
      // Still subscribe even if history fails to load
      get().subscribeToClassroom(classroomId);
    }
  },

  clearSelectedClassroom: () => {
    const active = get().selectedClassroom;
    if (active) {
      get().unsubscribeFromClassroom(active.id);
    }
    set({ selectedClassroom: null, sensorReadings: [], controlLogs: [] });
  },

  sendControl: async (command: any) => {
    const active = get().selectedClassroom;
    if (!active) return;

    set({ isControlling: true });
    
    const nextLightStates = command.lightStates || active.currentState.lightStates;
    const nextFanSpeed = command.fanSpeed || active.currentState.fanSpeed;
    const nextAcMode = command.acMode || active.currentState.acMode;

    // Detect if any sub-device is being turned on
    const hasActiveDevice = 
      (nextAcMode && nextAcMode !== 'OFF') ||
      (nextLightStates && nextLightStates.some((s: number) => s > 0)) ||
      (nextFanSpeed && nextFanSpeed.some((s: number) => s > 0));

    // Force power state to ON if any sub-device is active, otherwise fallback to current power
    const nextPower = command.power 
      ? command.power 
      : (hasActiveDevice ? 'ON' : active.currentState.power);

    // OPTIMISTIC UI: Instantly preview state change for highly premium visual responsiveness
    const optimisticClassroom = {
      ...active,
      currentState: {
        ...active.currentState,
        ...command,
        power: nextPower,
        lastUpdated: new Date().toISOString(),
      }
    };
    set({ selectedClassroom: optimisticClassroom });

    try {
      await controlService.sendControlCommand({
        classroomId: active.id,
        command: {
          power: nextPower,
          acMode: nextAcMode,
          acTemp: command.acTemp !== undefined ? command.acTemp : active.currentState.acTemp,
          lightStates: nextLightStates,
          fanSpeed: nextFanSpeed,
        }
      });
      // The state will finalize once WebSocket triggers the success event
      set({ isControlling: false });
    } catch (error) {
      console.error('Failed to send control command:', error);
      // Revert optimistic UI on error
      set({ selectedClassroom: active, isControlling: false });
    }
  },

  clearClassroomFault: async (classroomId: string) => {
    try {
      await classroomService.clearFault(classroomId);
      const active = get().selectedClassroom;
      if (active && active.id === classroomId) {
        set({ selectedClassroom: { ...active, faultLatched: false } });
      }
    } catch (error) {
      console.error('Failed to clear classroom fault:', error);
    }
  },

  shutdownClassroom: async (classroomId: string) => {
    try {
      await classroomService.turnOff(classroomId);
    } catch (error) {
      console.error('Failed to shutdown classroom:', error);
    }
  },

  // --- Real-time WebSocket Event Sinks ---
  handleLiveSensorUpdate: (reading: SensorReading) => {
    const { selectedClassroom, sensorReadings } = get();
    if (selectedClassroom && reading.classroomId === selectedClassroom.id) {
      // Keep only recent 20 readings for graph scrolling
      const updated = [...sensorReadings, reading].slice(-20);
      set({ sensorReadings: updated });
    }
  },

  handleLiveStateUpdate: (update: { power: 'ON' | 'OFF'; connectivity: 'ONLINE' | 'OFFLINE' }) => {
    const { selectedClassroom, classrooms } = get();
    
    // 1. Sync list
    const updatedList = classrooms.map(c => {
      if (c.id === selectedClassroom?.id) {
        return {
          ...c,
          device: {
            ...c.device,
            power: update.power,
            connectivity: update.connectivity,
            lastSeen: new Date().toISOString(),
          }
        };
      }
      return c;
    });
    set({ classrooms: updatedList });

    // 2. Sync active classroom view
    if (selectedClassroom) {
      set({
        selectedClassroom: {
          ...selectedClassroom,
          device: {
            ...selectedClassroom.device,
            power: update.power,
            connectivity: update.connectivity,
            lastSeen: new Date().toISOString(),
          }
        }
      });
    }
  },

  handleLiveControlLogUpdate: (log: ControlLog) => {
    const { selectedClassroom, controlLogs } = get();
    if (selectedClassroom && log.classroomId === selectedClassroom.id) {
      // Add log, ensuring no duplicates by checking ID
      const exists = controlLogs.some(l => l.id === log.id);
      const updatedLogs = exists 
        ? controlLogs.map(l => l.id === log.id ? log : l)
        : [log, ...controlLogs].slice(0, 50); // limit to 50 logs in view

      set({ controlLogs: updatedLogs });

      // If control log matches status SUCCESS, finalize the classroom currentState
      if (log.status === 'SUCCESS') {
        set({
          selectedClassroom: {
            ...selectedClassroom,
            currentState: log.command,
            faultLatched: log.command.power === 'CLEAR_FAULT' ? false : selectedClassroom.faultLatched
          }
        });
      }
    }
  },

  subscribeToClassroom: (classroomId: string) => {
    // Sync WS connection status
    wsService.onStatusChange((status) => {
      set({ wsStatus: status });
    });

    // Establish WebSocket Connection
    wsService.connect(() => {
      // Once connected, subscribe to three crucial topics matching backend WebSocketConfig
      
      // 1. Live Sensor Readings topic
      wsService.subscribe(`/topic/classroom/${classroomId}/sensors`, (payload) => {
        get().handleLiveSensorUpdate(payload);
      });

      // 2. Live Device Connection State topic
      wsService.subscribe(`/topic/classroom/${classroomId}/state`, (payload) => {
        get().handleLiveStateUpdate(payload);
      });

      // 3. Live Control Logs feedback topic
      wsService.subscribe(`/topic/classroom/${classroomId}/control`, (payload) => {
        get().handleLiveControlLogUpdate(payload);
      });
    });
  },

  unsubscribeFromClassroom: (classroomId: string) => {
    wsService.unsubscribe(`/topic/classroom/${classroomId}/sensors`);
    wsService.unsubscribe(`/topic/classroom/${classroomId}/state`);
    wsService.unsubscribe(`/topic/classroom/${classroomId}/control`);
    wsService.disconnect();
  },
}));
