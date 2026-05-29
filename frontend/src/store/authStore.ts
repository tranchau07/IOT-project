import { create } from 'zustand';

interface AuthState {
  token: string | null;
  username: string | null;
  roles: string[];
  permissions: string[];
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (token: string) => void;
  logout: () => void;
  initialize: () => void;
}

// Simple Helper to parse JWT without external library
const parseJwt = (token: string) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  username: null,
  roles: [],
  permissions: [],
  isAuthenticated: false,
  isLoading: true,

  login: (token: string) => {
    localStorage.setItem('jwt_token', token);
    const decoded = parseJwt(token);
    
    if (decoded) {
      const scopeString = decoded.scope || '';
      const scopes = scopeString.split(' ');
      
      // Standardize extracting Roles and Permissions from the scopes array
      const roles = scopes.filter((s: string) => s.startsWith('ROLE_') || s === 'ADMIN' || s === 'USER');
      const permissions = scopes.filter((s: string) => !roles.includes(s));

      set({
        token,
        username: decoded.sub || null,
        roles,
        permissions,
        isAuthenticated: true,
        isLoading: false,
      });
    } else {
      set({ isLoading: false });
    }
  },

  logout: () => {
    localStorage.removeItem('jwt_token');
    set({
      token: null,
      username: null,
      roles: [],
      permissions: [],
      isAuthenticated: false,
      isLoading: false,
    });
  },

  initialize: () => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      const decoded = parseJwt(token);
      // Check token expiration
      if (decoded && decoded.exp * 1000 > Date.now()) {
        const scopeString = decoded.scope || '';
        const scopes = scopeString.split(' ');
        
        const roles = scopes.filter((s: string) => s.startsWith('ROLE_') || s === 'ADMIN' || s === 'USER');
        const permissions = scopes.filter((s: string) => !roles.includes(s));

        set({
          token,
          username: decoded.sub || null,
          roles,
          permissions,
          isAuthenticated: true,
          isLoading: false,
        });
        return;
      }
    }
    localStorage.removeItem('jwt_token');
    set({ token: null, isAuthenticated: false, isLoading: false });
  },
}));
