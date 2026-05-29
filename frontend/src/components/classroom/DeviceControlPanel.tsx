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

  const handleAcModeChange = (mode: 'COOL' | 'HEAT' | 'FAN' | 'DRY' | 'OFF') => {
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
        <div className="rounded-2xl border border-danger/40 bg-danger-glowing/20 p-5 flex flex-col md:flex-row items-center justify-between gap-4 animate-pulse">
          <div className="flex items-center gap-3 text-left">
            <div className="h-10 w-10 rounded-xl bg-danger/20 text-danger flex items-center justify-center shrink-0">
              <Flame className="h-5 w-5" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-danger">EMERGENCY SYSTEM FAULT DETECTED</h4>
              <p className="text-xs text-slate-400 mt-1">
                A fire alarm or telemetry electrical fault has locked down the room hardware relays.
              </p>
            </div>
          </div>
          <button
            onClick={() => clearClassroomFault(id)}
            className="px-4 py-2 rounded-xl text-xs font-bold text-white bg-danger hover:bg-danger-hover shadow-lg shadow-danger-glowing cursor-pointer transition-all flex items-center gap-2"
          >
            <RotateCcw className="h-3.5 w-3.5" />
            Clear Alarm & Reset
          </button>
        </div>
      )}

      {/* 2. Quick Operations Header */}
      <div className="glass-panel rounded-2xl p-5 flex flex-wrap items-center justify-between gap-4">
        <div>
          <h3 className="font-bold text-slate-200">Device Control Deck</h3>
          <p className="text-xs text-slate-400 font-medium mt-1">Manually override device status</p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => shutdownClassroom(id)}
            className="px-3.5 py-2 rounded-xl text-xs font-bold border border-slate-700 bg-slate-800/40 text-slate-300 hover:bg-slate-800 hover:text-white transition-all flex items-center gap-2 cursor-pointer"
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
                ? 'bg-success text-white shadow-lg shadow-success-glowing hover:bg-success-hover'
                : 'bg-slate-800 border border-slate-700 text-slate-400 hover:bg-slate-700 hover:text-white'
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
        <div className="glass-panel rounded-2xl p-5 space-y-4">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-primary-glowing text-primary flex items-center justify-center">
              <Thermometer className="h-4.5 w-4.5" />
            </div>
            <h4 className="text-sm font-bold text-slate-200">Air Conditioner</h4>
          </div>

          {/* Temperature controller dial */}
          <div className="flex items-center justify-center py-4 bg-slate-950/40 rounded-2xl border border-slate-900">
            <div className="text-center space-y-1">
              <span className="text-[10px] font-bold text-slate-500 block uppercase">Target Temperature</span>
              <div className="flex items-center gap-4">
                <button
                  onClick={() => handleAcTempChange(-1)}
                  disabled={currentState.acMode === 'OFF'}
                  className="h-8 w-8 rounded-lg border border-slate-800 hover:border-slate-700 bg-slate-900/60 font-bold hover:text-primary transition-all cursor-pointer disabled:opacity-30 disabled:pointer-events-none"
                >
                  -
                </button>
                <span className={`text-3xl font-extrabold tracking-tight ${currentState.acMode !== 'OFF' ? 'text-primary' : 'text-slate-600'}`}>
                  {currentState.acMode !== 'OFF' ? `${currentState.acTemp}°C` : '--'}
                </span>
                <button
                  onClick={() => handleAcTempChange(1)}
                  disabled={currentState.acMode === 'OFF'}
                  className="h-8 w-8 rounded-lg border border-slate-800 hover:border-slate-700 bg-slate-900/60 font-bold hover:text-primary transition-all cursor-pointer disabled:opacity-30 disabled:pointer-events-none"
                >
                  +
                </button>
              </div>
            </div>
          </div>

          {/* Mode Selector buttons */}
          <div className="space-y-1.5">
            <span className="text-[10px] font-bold text-slate-500 block uppercase">AC Mode</span>
            <div className="grid grid-cols-3 gap-1.5">
              {(['COOL', 'HEAT', 'FAN', 'DRY', 'OFF'] as const).map((mode) => (
                <button
                  key={mode}
                  onClick={() => handleAcModeChange(mode)}
                  className={`py-1.5 rounded-lg text-xs font-semibold border transition-all cursor-pointer ${
                    currentState.acMode === mode
                      ? 'bg-primary text-white border-primary shadow-sm shadow-primary-glowing'
                      : 'bg-slate-900/40 border-slate-800/60 text-slate-400 hover:text-slate-200 hover:bg-slate-800/40'
                  }`}
                >
                  {mode}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* B. LIGHTING RELAYS DECK */}
        <div className="glass-panel rounded-2xl p-5 space-y-4">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-warning-glowing text-warning flex items-center justify-center">
              <Lightbulb className="h-4.5 w-4.5" />
            </div>
            <h4 className="text-sm font-bold text-slate-200">Lighting Relays</h4>
          </div>

          <div className="space-y-3">
            {currentState.lightStates.length === 0 ? (
              <p className="text-xs text-slate-500 italic py-6 text-center">No active lighting relays detected.</p>
            ) : (
              currentState.lightStates.map((status, idx) => {
                const isActive = status === 1;
                return (
                  <div
                    key={idx}
                    onClick={() => handleLightToggle(idx)}
                    className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer select-none transition-all ${
                      isActive
                        ? 'bg-warning-glowing/10 border-warning/30 text-warning'
                        : 'bg-slate-900/30 border-slate-800/40 text-slate-400 hover:bg-slate-800/30'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <Lightbulb className={`h-4.5 w-4.5 ${isActive ? 'fill-warning animate-pulse' : 'text-slate-500'}`} />
                      <span className="text-xs font-bold">Relay Light {idx + 1}</span>
                    </div>
                    <span className={`text-[10px] font-extrabold uppercase px-2 py-0.5 rounded ${
                      isActive ? 'bg-warning/20 text-warning' : 'bg-slate-800 text-slate-500'
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
        <div className="glass-panel rounded-2xl p-5 space-y-4">
          <div className="flex items-center gap-2.5">
            <div className="h-8 w-8 rounded-lg bg-success-glowing text-success flex items-center justify-center">
              <Wind className="h-4.5 w-4.5" />
            </div>
            <h4 className="text-sm font-bold text-slate-200">Wind Fans</h4>
          </div>

          <div className="space-y-4">
            {currentState.fanSpeed.length === 0 ? (
              <p className="text-xs text-slate-500 italic py-6 text-center">No fan relays mapped in room.</p>
            ) : (
              currentState.fanSpeed.map((speed, idx) => {
                const isActive = speed > 0;
                return (
                  <div key={idx} className="bg-slate-900/30 border border-slate-800/40 rounded-xl p-3.5 space-y-2">
                    <div className="flex items-center justify-between text-xs">
                      <div className="flex items-center gap-2">
                        <Wind className={`h-4 w-4 ${isActive ? 'text-success animate-spin-slow' : 'text-slate-500'}`} />
                        <span className="font-bold text-slate-300">Wind Fan {idx + 1}</span>
                      </div>
                      <span className={`text-[10px] font-bold ${isActive ? 'text-success' : 'text-slate-500'}`}>
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
                              ? 'bg-success text-white border-success'
                              : 'bg-slate-950/40 border-slate-800/40 text-slate-500 hover:text-slate-300 hover:bg-slate-800/30'
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
