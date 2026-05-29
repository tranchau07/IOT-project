import axios from 'axios';
import { useAuthStore } from '../store/authStore';
import { 
  ApiResponse, 
  AuthenticationResponse, 
  Classroom, 
  ControlLog, 
  SensorReading, 
  UserResponse,
  Role,
  Permission
} from '../types';

const API_BASE = '/api';

export const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token automatically
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handle HTTP 401 Unauthorized globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      useAuthStore.getState().logout();
    }
    return Promise.reject(error);
  }
);

// --- 1. AUTHENTICATION SERVICES ---
export const authService = {
  login: async (username: string, password: string): Promise<string> => {
    const response = await api.post<ApiResponse<AuthenticationResponse>>('/auth/login', {
      username,
      password,
    });
    return response.data.result.token;
  },

  introspect: async (token: string): Promise<boolean> => {
    const response = await api.post<ApiResponse<{ isValid: boolean }>>('/auth/introspect', {
      token,
    });
    return response.data.result.isValid;
  },
};

// --- 2. USER MANAGEMENT SERVICES ---
export const userService = {
  getMyInfo: async (): Promise<UserResponse> => {
    const response = await api.get<ApiResponse<UserResponse>>('/users/myInfo');
    return response.data.result;
  },

  getAll: async (): Promise<UserResponse[]> => {
    // Note: Returns an array of UserResponse directly from the Spring endpoint
    const response = await api.get<UserResponse[]>('/users');
    return response.data;
  },

  getById: async (id: string): Promise<UserResponse> => {
    const response = await api.get<ApiResponse<UserResponse>>(`/users/${id}`);
    return response.data.result;
  },

  create: async (data: any): Promise<UserResponse> => {
    const response = await api.post<ApiResponse<UserResponse>>('/users', data);
    return response.data.result;
  },

  update: async (id: string, data: any): Promise<UserResponse> => {
    const response = await api.put<ApiResponse<UserResponse>>(`/users/${id}`, data);
    return response.data.result;
  },

  delete: async (id: string): Promise<string> => {
    const response = await api.delete<ApiResponse<string>>(`/users/${id}`);
    return response.data.result;
  },
};

// --- 3. CLASSROOM MANAGEMENT SERVICES ---
export const classroomService = {
  getAll: async (): Promise<Classroom[]> => {
    const response = await api.get<ApiResponse<Classroom[]>>('/classrooms');
    return response.data.result;
  },

  getById: async (id: string): Promise<Classroom> => {
    const response = await api.get<ApiResponse<Classroom>>(`/classrooms/${id}`);
    return response.data.result;
  },

  create: async (data: any): Promise<Classroom> => {
    const response = await api.post<ApiResponse<Classroom>>('/classrooms', data);
    return response.data.result;
  },

  update: async (id: string, data: any): Promise<Classroom> => {
    const response = await api.put<ApiResponse<Classroom>>(`/classrooms/${id}`, data);
    return response.data.result;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/classrooms/${id}`);
  },

  clearFault: async (id: string): Promise<void> => {
    await api.post(`/classrooms/${id}/clear-fault`);
  },

  turnOff: async (id: string): Promise<void> => {
    await api.post(`/classrooms/${id}/turn-off`);
  },
};

// --- 4. SENSOR READINGS TELEMETRY SERVICES ---
export const sensorReadingService = {
  getLatest: async (classroomId: string): Promise<SensorReading> => {
    const response = await api.get<ApiResponse<SensorReading>>(`/sensor-readings/latest/${classroomId}`);
    return response.data.result;
  },

  getLimit20: async (classroomId: string): Promise<SensorReading[]> => {
    const response = await api.get<ApiResponse<SensorReading[]>>(`/sensor-readings/limit-20/${classroomId}`);
    return response.data.result;
  },

  getHistory: async (classroomId: string, start: string, end: string): Promise<SensorReading[]> => {
    const response = await api.get<ApiResponse<SensorReading[]>>(
      `/sensor-readings/${classroomId}/sensor`,
      { params: { start, end } }
    );
    return response.data.result;
  },
};

// --- 5. DEVICE CONTROL SERVICES ---
export const controlService = {
  sendControlCommand: async (controlRequest: {
    classroomId: string;
    command: {
      power: 'ON' | 'OFF' | 'CLEAR_FAULT';
      acMode: 'COOL' | 'HEAT' | 'FAN' | 'DRY' | 'OFF';
      acTemp: number;
      lightStates: number[];
      fanSpeed: number[];
    };
  }): Promise<void> => {
    await api.post('/control-logs/send/control', controlRequest);
  },

  getHistory: async (classroomId: string, start: string, end: string): Promise<ControlLog[]> => {
    const response = await api.get<ApiResponse<ControlLog[]>>(
      `/control-logs/between/${classroomId}`,
      { params: { start, end } }
    );
    return response.data.result;
  },
};

// --- 6. ROLE & PERMISSION SERVICES ---
export const roleService = {
  getAll: async (): Promise<Role[]> => {
    const response = await api.get<ApiResponse<Role[]>>('/roles');
    return response.data.result;
  },
  create: async (data: any): Promise<Role> => {
    const response = await api.post<ApiResponse<Role>>('/roles', data);
    return response.data.result;
  },
  delete: async (name: string): Promise<void> => {
    await api.delete(`/roles/${name}`);
  },
};

export const permissionService = {
  getAll: async (): Promise<Permission[]> => {
    const response = await api.get<ApiResponse<Permission[]>>('/permissions');
    return response.data.result;
  },
  create: async (data: any): Promise<Permission> => {
    const response = await api.post<ApiResponse<Permission>>('/permissions', data);
    return response.data.result;
  },
  delete: async (name: string): Promise<void> => {
    await api.delete(`/permissions/${name}`);
  },
};
