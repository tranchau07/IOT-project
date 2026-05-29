// 1. Roles and Permissions
export interface Permission {
  name: string;
  description: string;
}

export interface Role {
  name: string;
  description: string;
  permissions: Permission[];
}

// 2. User Interfaces
export interface User {
  id?: string;
  username: string;
  phone?: string;
  email?: string;
  roles?: Role[];
}

export interface UserResponse {
  id: string;
  username: string;
  phone: string;
  email: string;
  roles: Role[];
}

export interface AuthenticationResponse {
  token: string;
}

export interface IntrospectResponse {
  isValid: boolean;
}

// 3. IoT Classroom Models
export interface Device {
  deviceId: string;
  deviceType: string;
  connectivity: 'ONLINE' | 'OFFLINE';
  power: 'ON' | 'OFF';
  lastSeen: string;
}

export interface LightState {
  index: number;
  status: 0 | 1;
}

export interface CurrentState {
  power: 'ON' | 'OFF' | 'CLEAR_FAULT';
  acMode: 'COOL' | 'HEAT' | 'FAN' | 'DRY' | 'OFF';
  acTemp: number;
  lightStates: number[]; // e.g. [0, 1] or [0, 0]
  fanSpeed: number[];    // e.g. [0, 3] or [1, 2]
  lastUpdated: string;
}

export interface Classroom {
  id: string;
  name: string;
  building: string;
  createdAt: string;
  device: Device;
  currentState: CurrentState;
  faultLatched: boolean;
}

// 4. Sensor Telemetry Models
export interface Environment {
  temperature: number;
  humidity: number;
  occupancy: boolean;
  lightLevel: number;
}

export interface SensorReading {
  id?: string;
  classroomId: string;
  deviceId: string;
  environment: Environment;
  voltage: number | null;
  smokeDetected: boolean | null;
  doorOpen: boolean | null;
  timestamp: string;
}

// 5. Control Logs Models
export interface ControlLog {
  id: string;
  classroomId: string;
  command: CurrentState;
  status: 'CREATE' | 'SENT' | 'SUCCESS' | 'FAILED';
  timestamp: string;
  mode: 'MANUAL' | 'AUTO';
  reason: 'MANUAL_OVERRIDE' | 'AUTO_ADJUST' | 'EMERGENCY_SHUTDOWN' | 'SCHEDULED_TRIGGER';
}

// 6. Generic API Response Wrapper
export interface ApiResponse<T> {
  code?: number;
  message?: string;
  result: T;
}
