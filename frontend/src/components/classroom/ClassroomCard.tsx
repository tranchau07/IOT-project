import React from 'react';
import { Classroom } from '../../types';
import { School, Activity, Power, AlertOctagon, Flame } from 'lucide-react';

interface ClassroomCardProps {
  classroom: Classroom;
  onClick: () => void;
}

export const ClassroomCard: React.FC<ClassroomCardProps> = ({ classroom, onClick }) => {
  const { name, building, device, currentState, faultLatched } = classroom;
  const isOnline = device.connectivity === 'ONLINE';

  const getPowerColor = () => {
    if (currentState.power === 'ON') return 'text-success bg-success-glowing border-success/20';
    return 'text-slate-400 bg-slate-800/40 border-slate-700/20';
  };

  return (
    <div
      onClick={onClick}
      className={`glass-panel glass-panel-hover rounded-2xl p-5 cursor-pointer relative overflow-hidden ${
        faultLatched ? 'border-danger/30 shadow-lg shadow-danger-glowing' : ''
      }`}
    >
      {/* Background glowing gradients */}
      {isOnline && (
        <div className="absolute top-0 right-0 w-24 h-24 bg-primary/10 rounded-full blur-2xl -mr-6 -mt-6"></div>
      )}
      {faultLatched && (
        <div className="absolute top-0 right-0 w-24 h-24 bg-danger/10 rounded-full blur-2xl -mr-6 -mt-6 animate-pulse"></div>
      )}

      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className={`p-3 rounded-xl ${isOnline ? 'bg-primary-glowing text-primary' : 'bg-slate-800 text-slate-500'}`}>
            <School className="h-5 w-5" />
          </div>
          <div>
            <h3 className="font-bold text-slate-100">{name}</h3>
            <p className="text-xs text-slate-400 font-semibold">{building} Building</p>
          </div>
        </div>

        {/* Live connectivity indicator */}
        <div className="flex items-center gap-1.5 px-2 py-0.5 rounded-md border border-slate-800 text-[10px] font-bold">
          <span className={`h-1.5 w-1.5 rounded-full ${isOnline ? 'bg-success glowing-dot' : 'bg-slate-600'}`} />
          <span className={isOnline ? 'text-success' : 'text-slate-400'}>{device.connectivity}</span>
        </div>
      </div>

      {/* Sensor stats preview */}
      <div className="grid grid-cols-2 gap-3 mt-6">
        <div className="bg-slate-900/30 rounded-xl p-2.5 border border-slate-800/30">
          <span className="text-[10px] font-bold text-slate-500 block uppercase">AC Status</span>
          <span className="text-xs font-bold text-slate-200 mt-1 block">
            {currentState.acMode !== 'OFF' ? `${currentState.acMode} @ ${currentState.acTemp}°C` : 'Inactive'}
          </span>
        </div>
        <div className="bg-slate-900/30 rounded-xl p-2.5 border border-slate-800/30">
          <span className="text-[10px] font-bold text-slate-500 block uppercase">Lights & Fans</span>
          <span className="text-xs font-bold text-slate-200 mt-1 block">
            {currentState.lightStates.filter(s => s === 1).length} Lights • {currentState.fanSpeed.filter(s => s > 0).length} Fans
          </span>
        </div>
      </div>

      <div className="flex items-center justify-between border-t border-slate-800/60 mt-4 pt-4 text-xs font-semibold text-slate-400">
        <div className="flex items-center gap-1">
          <Activity className="h-3.5 w-3.5 text-slate-500" />
          <span>Device: {device.deviceId}</span>
        </div>

        {/* Active Power status */}
        <div className="flex items-center gap-1.5">
          {faultLatched ? (
            <div className="flex items-center gap-1 px-2.5 py-0.5 rounded-md border border-danger/30 bg-danger-glowing text-danger text-[10px] font-bold animate-pulse">
              <Flame className="h-3 w-3" />
              <span>EMERGENCY FAULT</span>
            </div>
          ) : (
            <div className={`flex items-center gap-1 px-2.5 py-0.5 rounded-md border text-[10px] font-bold ${getPowerColor()}`}>
              <Power className="h-3 w-3" />
              <span>POWER {currentState.power}</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ClassroomCard;
