import React from 'react';
import { Classroom } from '../../types';
import { School, Activity, Power, Flame } from 'lucide-react';

interface ClassroomCardProps {
  classroom: Classroom;
  onClick: () => void;
}

export const ClassroomCard: React.FC<ClassroomCardProps> = ({ classroom, onClick }) => {
  const { name, building, device, currentState, faultLatched } = classroom;
  const isOnline = device.connectivity === 'ONLINE';

  const getPowerColor = () => {
    if (currentState.power === 'ON') return 'text-emerald-600 bg-emerald-50 border-emerald-250/60';
    return 'text-slate-500 bg-slate-50 border-slate-200/80';
  };

  return (
    <div
      onClick={onClick}
      className={`bg-white border rounded-2xl p-5 cursor-pointer relative overflow-hidden transition-all duration-200 group hover:border-blue-200 hover:shadow-md ${
        faultLatched ? 'border-rose-250 shadow-sm shadow-rose-100 bg-rose-50/10 hover:border-rose-300' : 'border-slate-200/80 shadow-sm'
      }`}
    >
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className={`p-3 rounded-xl transition-colors duration-200 ${
            isOnline ? 'bg-blue-50 text-blue-600 border border-blue-100/50' : 'bg-slate-50 text-slate-400 border border-slate-100'
          }`}>
            <School className="h-5 w-5" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900 group-hover:text-blue-600 transition-colors duration-150">{name}</h3>
            <p className="text-xs text-slate-500 font-semibold">{building} Building</p>
          </div>
        </div>

        {/* Live connectivity indicator */}
        <div className="flex items-center gap-1.5 px-2 py-0.5 rounded-md border border-slate-150 text-[10px] font-bold">
          <span className={`h-1.5 w-1.5 rounded-full ${isOnline ? 'bg-emerald-500 glowing-dot' : 'bg-slate-400'}`} />
          <span className={isOnline ? 'text-emerald-600' : 'text-slate-400'}>{device.connectivity}</span>
        </div>
      </div>

      {/* Sensor stats preview */}
      <div className="grid grid-cols-2 gap-3 mt-6">
        <div className="bg-slate-50/50 rounded-xl p-2.5 border border-slate-100">
          <span className="text-[10px] font-bold text-slate-400 block uppercase tracking-wider">AC Status</span>
          <span className="text-xs font-bold text-slate-700 mt-1 block">
            {currentState.acMode !== 'OFF' ? `${currentState.acMode} @ ${currentState.acTemp}°C` : 'Inactive'}
          </span>
        </div>
        <div className="bg-slate-50/50 rounded-xl p-2.5 border border-slate-100">
          <span className="text-[10px] font-bold text-slate-400 block uppercase tracking-wider">Lights & Fans</span>
          <span className="text-xs font-bold text-slate-700 mt-1 block">
            {currentState.lightStates.filter(s => s === 1).length} Lights • {currentState.fanSpeed.filter(s => s > 0).length} Fans
          </span>
        </div>
      </div>

      <div className="flex items-center justify-between border-t border-slate-100 mt-4 pt-4 text-xs font-semibold text-slate-400">
        <div className="flex items-center gap-1 text-[11px] font-medium text-slate-500">
          <Activity className="h-3.5 w-3.5 text-slate-400" />
          <span>Node: {device.deviceId}</span>
        </div>

        {/* Active Power status */}
        <div className="flex items-center gap-1.5">
          {faultLatched ? (
            <div className="flex items-center gap-1 px-2.5 py-0.5 rounded-md border border-rose-250 bg-rose-50 text-rose-600 text-[10px] font-bold animate-pulse">
              <Flame className="h-3 w-3" />
              <span>FAULT LOCKED</span>
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

