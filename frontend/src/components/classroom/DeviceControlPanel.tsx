import React from 'react';
import { Classroom } from '../../types';
import { useClassroomStore } from '../../store/classroomStore';
import { 
  Power, 
  Thermometer, 
  Lightbulb, 
  Wind, 
  Flame, 
  RefreshCw, 
  RotateCcw,
  ZapOff 
} from 'lucide-react';

interface DeviceControlPanelProps {
  classroom: Classroom;
}

export const DeviceControlPanel: React.FC<DeviceControlPanelProps> = ({ classroom }) => {
  const { currentState, faultLatched, id } = classroom;
  const { sendControl, clearClassroomFault, shutdownClassroom, isControlling } = useClassroomStore();

  const handlePowerToggle = () => {
    const nextPower = currentState.power === 'ON' ? 'OFF' : 'ON';
    sendControl({ power: nextPower });
  };

  const handleAcModeChange = (mode: 'COOL' | 'HEAT' | 'FAN' | 'DRY' | 'ECO' | 'OFF') => {
    sendControl({ acMode: mode });
  };

  const handleAcTempChange = (delta: number) => {
    const nextTemp = Math.min(Math.max(currentState.acTemp + delta, 16), 30);
    sendControl({ acTemp: nextTemp });
  };

  const handleLightToggle = (index: number) => {
    const nextLights = [...currentState.lightStates];
    nextLights[index] = nextLights[index] === 1 ? 0 : 1;
    sendControl({ lightStates: nextLights });
  };

  const handleFanSpeedChange = (index: number, speed: number) => {
    const nextFans = [...currentState.fanSpeed];
    nextFans[index] = speed;
    sendControl({ fanSpeed: nextFans });
  };

  return (
    <div className="space-y-6">
      {/* 1. Emergency Fire/Fault Latched Warning Box */}
      {faultLatched && (
        <div className="rounded-2xl border border-rose-250 bg-rose-50/50 p-5 flex flex-col md:flex-row items-center justify-between gap-4 animate-pulse">
          <div className="flex items-center gap-3 text-left">
            <div className="h-10 w-10 rounded-xl bg-rose-100 text-rose-600 flex items-center justify-center shrink-0 border border-rose-200">
              <Flame className="h-5 w-5" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-rose-700">EMERGENCY SYSTEM FAULT DETECTED</h4>
              <p className="text-xs text-slate-500 mt-1">
                A fire alarm or telemetry electrical fault has locked down the room hardware relays.
              </p>
            </div>
          </div>
          <button
            onClick={() => clearClassroomFault(id)}
            className="px-4 py-2 rounded-xl text-xs font-bold text-white bg-rose-600 hover:bg-rose-700 shadow-md cursor-pointer transition-all flex items-center gap-2"
          >
            <RotateCcw className="h-3.5 w-3.5" />
            Clear Alarm & Reset
          </button>
        </div>
      )}

      {/* 2. Quick Operations Header */}
      <div className="bg-white border border-slate-200/80 rounded-2xl p-5 flex flex-wrap items-center justify-between gap-4 shadow-sm">
        <div>
          <h3 className="font-extrabold text-slate-900">Device Control Deck</h3>
          <p className="text-xs text-slate-500 font-semibold mt-1">Manually override device status</p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => shutdownClassroom(id)}
            className="px-3.5 py-2 rounded-xl text-xs font-bold border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 hover:text-slate-900 transition-all flex items-center gap-2 cursor-pointer shadow-sm"
            title="Turns off all AC, Lights and Fans immediately"
          >
            <ZapOff className="h-3.5 w-3.5" />
            All Off
          </button>
          
          <button
            onClick={handlePowerToggle}
            disabled={isControlling}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-2 cursor-pointer ${
              currentState.power === 'ON'
                ? 'bg-emerald-600 text-white shadow-md hover:bg-emerald-700'
                : 'bg-white border border-slate-200 text-slate-500 hover:bg-slate-50'
            }`}
          >
            <Power className="h-3.5 w-3.5" />
            Master Power {currentState.power}
            {isControlling && <RefreshCw className="h-3 w-3 animate-spin ml-1" />}
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* A. AIR CONDITIONER DECK */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-4 shadow-sm">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center border border-blue-100/50">
              <Thermometer className="h-4.5 w-4.5" />
            </div>
            <h4 className="text-sm font-bold text-slate-900">Air Conditioner</h4>
          </div>

          {/* Temperature controller dial */}
          <div className="flex items-center justify-center py-4 bg-slate-50 rounded-2xl border border-slate-100">
            <div className="text-center space-y-1">
              <span className="text-[10px] font-bold text-slate-400 block uppercase tracking-wider">Target Temperature</span>
              <div className="flex items-center gap-4">
                <button
                  onClick={() => handleAcTempChange(-1)}
                  disabled={currentState.acMode === 'OFF'}
                  className="h-8 w-8 rounded-lg border border-slate-200 hover:border-slate-300 bg-white font-bold text-slate-700 hover:text-blue-600 transition-all cursor-pointer shadow-sm disabled:opacity-30 disabled:pointer-events-none"
                >
                  -
                </button>
                <span className={`text-2xl font-extrabold tracking-tight ${currentState.acMode !== 'OFF' ? 'text-blue-600' : 'text-slate-400'}`}>
                  {currentState.acMode !== 'OFF' ? `${currentState.acTemp}°C` : '--'}
                </span>
                <button
                  onClick={() => handleAcTempChange(1)}
                  disabled={currentState.acMode === 'OFF'}
                  className="h-8 w-8 rounded-lg border border-slate-200 hover:border-slate-300 bg-white font-bold text-slate-700 hover:text-blue-600 transition-all cursor-pointer shadow-sm disabled:opacity-30 disabled:pointer-events-none"
                >
                  +
                </button>
              </div>
            </div>
          </div>

          {/* Mode Selector buttons */}
          <div className="space-y-1.5">
            <span className="text-[10px] font-bold text-slate-450 block uppercase tracking-wider">AC Mode</span>
            <div className="grid grid-cols-3 gap-1.5">
              {(['COOL', 'HEAT', 'FAN', 'DRY', 'ECO', 'OFF'] as const).map((mode) => (
                <button
                  key={mode}
                  onClick={() => handleAcModeChange(mode)}
                  className={`py-1.5 rounded-lg text-xs font-bold border transition-all cursor-pointer ${
                    currentState.acMode === mode
                      ? 'bg-blue-600 text-white border-blue-600 shadow-sm'
                      : 'bg-white border-slate-200 text-slate-500 hover:text-slate-900 hover:bg-slate-50'
                  }`}
                >
                  {mode}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* B. LIGHTING RELAYS DECK */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-4 shadow-sm">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center border border-amber-100/50">
              <Lightbulb className="h-4.5 w-4.5" />
            </div>
            <h4 className="text-sm font-bold text-slate-900">Lighting Relays</h4>
          </div>

          <div className="space-y-3">
            {currentState.lightStates.length === 0 ? (
              <p className="text-xs text-slate-400 italic py-6 text-center">No active lighting relays detected.</p>
            ) : (
              currentState.lightStates.map((status, idx) => {
                const isActive = status === 1;
                return (
                  <div
                    key={idx}
                    onClick={() => handleLightToggle(idx)}
                    className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer select-none transition-all duration-150 ${
                      isActive
                        ? 'bg-amber-50 border-amber-200 text-amber-600'
                        : 'bg-white border-slate-200 text-slate-500 hover:bg-slate-50 hover:text-slate-800'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <Lightbulb className={`h-4.5 w-4.5 ${isActive ? 'fill-amber-400 text-amber-500 animate-pulse' : 'text-slate-400'}`} />
                      <span className="text-xs font-bold">Relay Light {idx + 1}</span>
                    </div>
                    <span className={`text-[10px] font-extrabold uppercase px-2 py-0.5 rounded ${
                      isActive ? 'bg-amber-100 text-amber-700' : 'bg-slate-100 text-slate-500'
                    }`}>
                      {isActive ? 'Active' : 'Off'}
                    </span>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* C. FANS CONTROL DECK */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-4 shadow-sm">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center border border-emerald-100/50">
              <Wind className="h-4.5 w-4.5" />
            </div>
            <h4 className="text-sm font-bold text-slate-900">Wind Fans</h4>
          </div>

          <div className="space-y-4">
            {currentState.fanSpeed.length === 0 ? (
              <p className="text-xs text-slate-400 italic py-6 text-center">No fan relays mapped in room.</p>
            ) : (
              currentState.fanSpeed.map((speed, idx) => {
                const isActive = speed > 0;
                return (
                  <div key={idx} className="bg-slate-50/50 border border-slate-100 rounded-xl p-3.5 space-y-2.5">
                    <div className="flex items-center justify-between text-xs">
                      <div className="flex items-center gap-2">
                        <Wind className={`h-4 w-4 ${isActive ? 'text-emerald-500 animate-spin-slow' : 'text-slate-400'}`} />
                        <span className="font-bold text-slate-700">Wind Fan {idx + 1}</span>
                      </div>
                      <span className={`text-[10px] font-bold ${isActive ? 'text-emerald-600' : 'text-slate-400'}`}>
                        {isActive ? `Level ${speed} Active` : 'Inactive'}
                      </span>
                    </div>

                    {/* Speed selector bar */}
                    <div className="grid grid-cols-4 gap-1 pt-1">
                      {([0, 1, 2, 3] as const).map((lvl) => (
                        <button
                          key={lvl}
                          onClick={() => handleFanSpeedChange(idx, lvl)}
                          className={`py-1 rounded text-[10px] font-bold border transition-all cursor-pointer ${
                            speed === lvl
                              ? 'bg-emerald-600 text-white border-emerald-600 shadow-sm'
                              : 'bg-white border-slate-200 text-slate-500 hover:text-slate-800 hover:bg-slate-50'
                          }`}
                        >
                          {lvl === 0 ? 'OFF' : `L${lvl}`}
                        </button>
                      ))}
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeviceControlPanel;

