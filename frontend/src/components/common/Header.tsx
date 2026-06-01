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
        return 'text-emerald-600 bg-emerald-50 border-emerald-200';
      case 'CONNECTING':
        return 'text-amber-600 bg-amber-50 border-amber-200';
      case 'DISCONNECTED':
      default:
        return 'text-slate-500 bg-slate-50 border-slate-200';
    }
  };

  const getStatusText = () => {
    switch (wsStatus) {
      case 'CONNECTED':
        return 'Live Connected';
      case 'CONNECTING':
        return 'Connecting...';
      case 'DISCONNECTED':
      default:
        return 'Live Offline';
    }
  };

  return (
    <header className="h-16 border-b border-slate-200/80 bg-white px-6 flex items-center justify-between sticky top-0 z-40 shadow-sm/5">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-600 shadow-md shadow-blue-500/10">
          <Radio className="h-5 w-5 text-white animate-pulse" />
        </div>
        <div>
          <h1 className="text-sm font-extrabold tracking-tight text-slate-900">
            HEMS SMART ROOM
          </h1>
          <p className="text-[10px] text-slate-500 font-semibold uppercase tracking-wider">IoT Classroom Control Hub</p>
        </div>
      </div>

      <div className="flex items-center gap-6">
        {/* Real-time Connection Badge */}
        <div className={`flex items-center gap-2 px-3 py-1 rounded-full border text-xs font-semibold transition-colors duration-200 ${getStatusColor()}`}>
          {wsStatus === 'CONNECTED' && <Wifi className="h-3.5 w-3.5" />}
          {wsStatus === 'CONNECTING' && <RefreshCw className="h-3.5 w-3.5 animate-spin" />}
          {wsStatus === 'DISCONNECTED' && <WifiOff className="h-3.5 w-3.5" />}
          <span className="hidden sm:inline">{getStatusText()}</span>
        </div>

        {/* User Profile and Action block */}
        <div className="flex items-center gap-4 border-l border-slate-100 pl-6">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center text-blue-600 font-bold">
              <User className="h-4.5 w-4.5" />
            </div>
            <div className="text-left hidden md:block">
              <p className="text-xs font-extrabold text-slate-900 leading-none">{username || 'Unknown'}</p>
              <p className="text-[9px] text-slate-500 font-semibold mt-0.5">Active Account</p>
            </div>
          </div>

          <button
            onClick={logout}
            className="h-8 w-8 rounded-lg bg-rose-50 border border-rose-200 text-rose-600 hover:bg-rose-600 hover:text-white transition-all flex items-center justify-center cursor-pointer"
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

