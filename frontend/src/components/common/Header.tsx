import React from 'react';
import { useAuthStore } from '../../store/authStore';
import { useClassroomStore } from '../../store/classroomStore';
import { LogOut, User, Radio, Wifi, WifiOff, RefreshCw } from 'lucide-react';

export const Header: React.FC = () => {
  const { username, logout } = useAuthStore();
  const { wsStatus } = useClassroomStore();

  const getStatusColor = () => {
    switch (wsStatus) {
      case 'CONNECTED':
        return 'text-success bg-success-glowing border-success/30';
      case 'CONNECTING':
        return 'text-warning bg-warning-glowing border-warning/30';
      case 'DISCONNECTED':
      default:
        return 'text-slate-400 bg-slate-800/40 border-slate-700/30';
    }
  };

  const getStatusText = () => {
    switch (wsStatus) {
      case 'CONNECTED':
        return 'Live System Connected';
      case 'CONNECTING':
        return 'Reconnecting...';
      case 'DISCONNECTED':
      default:
        return 'Live System Offline';
    }
  };

  return (
    <header className="h-16 border-b border-border bg-[#0d1527] px-6 flex items-center justify-between sticky top-0 z-40">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-primary to-blue-400 shadow-lg shadow-primary-glowing">
          <Radio className="h-5 w-5 text-white animate-pulse" />
        </div>
        <div>
          <h1 className="text-lg font-bold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">
            HEMS SMART ROOM
          </h1>
          <p className="text-xs text-slate-500 font-medium">IoT Classroom Control Hub</p>
        </div>
      </div>

      <div className="flex items-center gap-6">
        {/* Real-time Connection Badge */}
        <div className={`flex items-center gap-2 px-3 py-1 rounded-full border text-xs font-semibold ${getStatusColor()}`}>
          {wsStatus === 'CONNECTED' && <Wifi className="h-3.5 w-3.5" />}
          {wsStatus === 'CONNECTING' && <RefreshCw className="h-3.5 w-3.5 animate-spin" />}
          {wsStatus === 'DISCONNECTED' && <WifiOff className="h-3.5 w-3.5" />}
          <span className="hidden sm:inline">{getStatusText()}</span>
        </div>

        {/* User Profile and Action block */}
        <div className="flex items-center gap-4 border-l border-slate-800 pl-6">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-primary">
              <User className="h-4.5 w-4.5" />
            </div>
            <div className="text-left hidden md:block">
              <p className="text-sm font-semibold leading-none">{username || 'Unknown'}</p>
              <p className="text-[10px] text-slate-500 font-medium mt-0.5">Active Account</p>
            </div>
          </div>

          <button
            onClick={logout}
            className="h-8 w-8 rounded-lg bg-danger-glowing border border-danger/20 text-danger hover:bg-danger hover:text-white transition-all flex items-center justify-center cursor-pointer"
            title="Log Out"
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
