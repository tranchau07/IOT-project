import React from 'react';
import { SensorReading } from '../../types';
import { 
  Thermometer, 
  Droplets, 
  UserCheck, 
  UserX, 
  Sun, 
  Battery, 
  ShieldAlert, 
  DoorClosed, 
  DoorOpen,
  Activity
} from 'lucide-react';

interface SensorDisplayProps {
  reading: SensorReading | null;
}

export const SensorDisplay: React.FC<SensorDisplayProps> = ({ reading }) => {
  if (!reading) {
    return (
      <div className="bg-white border border-slate-200/80 rounded-2xl p-8 flex flex-col items-center justify-center text-center space-y-3 shadow-sm">
        <Activity className="h-8 w-8 text-slate-400 animate-pulse" />
        <p className="text-xs text-slate-500 font-semibold">Waiting for realtime sensor telemetry...</p>
      </div>
    );
  }

  const { environment, voltage, smokeDetected, doorOpen, timestamp } = reading;
  const isOccupied = environment.occupancy;
  const isSmokeAlarm = smokeDetected === true;

  return (
    <div className="space-y-4">
      {/* Realtime Telemetry Header */}
      <div className="flex items-center justify-between">
        <div>
          <h4 className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider">REALTIME TELEMETRY</h4>
          <span className="text-[9px] text-slate-400 font-mono">Last Reading: {new Date(timestamp).toLocaleTimeString()}</span>
        </div>
        <div className="flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-600 border border-emerald-100 text-[10px] font-bold">
          <span className="h-1.5 w-1.5 rounded-full bg-emerald-500 glowing-dot" />
          <span>Realtime Feed</span>
        </div>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Widget 1: Temperature */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-3 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider">Temperature</span>
            <div className="h-8 w-8 rounded-lg bg-rose-50 text-rose-600 flex items-center justify-center border border-rose-100/50">
              <Thermometer className="h-4.5 w-4.5" />
            </div>
          </div>
          <div>
            <h3 className="text-xl font-extrabold text-slate-900">{environment.temperature.toFixed(1)}°C</h3>
            <span className="text-[9px] text-slate-500 font-semibold">Thermostat Sensor</span>
          </div>
        </div>

        {/* Widget 2: Humidity */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-3 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider">Humidity</span>
            <div className="h-8 w-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center border border-blue-100/50">
              <Droplets className="h-4.5 w-4.5" />
            </div>
          </div>
          <div>
            <h3 className="text-xl font-extrabold text-slate-900">{environment.humidity.toFixed(0)}%</h3>
            <span className="text-[9px] text-slate-500 font-semibold">Relative Humidity</span>
          </div>
        </div>

        {/* Widget 3: Occupancy */}
        <div className={`border rounded-2xl p-5 space-y-3 shadow-sm transition-all duration-200 ${
          isOccupied ? 'border-blue-200 bg-blue-50/10' : 'bg-white border-slate-200/80'
        }`}>
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider">Occupancy</span>
            <div className={`h-8 w-8 rounded-lg flex items-center justify-center border ${
              isOccupied ? 'bg-blue-50 border-blue-100 text-blue-600' : 'bg-slate-50 border-slate-100 text-slate-400'
            }`}>
              {isOccupied ? <UserCheck className="h-4.5 w-4.5" /> : <UserX className="h-4.5 w-4.5" />}
            </div>
          </div>
          <div>
            <h3 className={`text-xl font-extrabold ${isOccupied ? 'text-blue-600' : 'text-slate-555'}`}>
              {isOccupied ? 'Occupied' : 'Empty'}
            </h3>
            <span className="text-[9px] text-slate-500 font-semibold">PIR Motion Sensor</span>
          </div>
        </div>

        {/* Widget 4: Light Level */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-3 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider">Light Level</span>
            <div className="h-8 w-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center border border-amber-100/50">
              <Sun className="h-4.5 w-4.5" />
            </div>
          </div>
          <div>
            <h3 className="text-xl font-extrabold text-slate-900">{environment.lightLevel.toFixed(0)} Lux</h3>
            <span className="text-[9px] text-slate-500 font-semibold">LDR Light Sensor</span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Widget 5: Smoke Detection */}
        <div className={`border rounded-2xl p-5 space-y-3 transition-all duration-200 shadow-sm ${
          isSmokeAlarm ? 'border-rose-350 bg-rose-50/50 shadow-md shadow-rose-100' : 'bg-white border-slate-200/80'
        }`}>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <ShieldAlert className={`h-4.5 w-4.5 ${isSmokeAlarm ? 'text-rose-600 animate-bounce' : 'text-slate-400'}`} />
              <span className="text-xs font-bold text-slate-700">Smoke / Fire Alarm</span>
            </div>
            <span className={`h-2.5 w-2.5 rounded-full ${isSmokeAlarm ? 'bg-rose-600 glowing-dot' : 'bg-emerald-500'}`} />
          </div>
          <div className="pt-2">
            <h3 className={`text-base font-extrabold ${isSmokeAlarm ? 'text-rose-600 animate-pulse' : 'text-slate-900'}`}>
              {isSmokeAlarm ? '🔥 SMOKE DETECTED!' : 'Normal (No Smoke)'}
            </h3>
            <p className="text-[10px] text-slate-500 font-semibold mt-1">
              {isSmokeAlarm ? 'Emergency shutdown triggers and lockdown relays engaged.' : 'Standard air ventilation levels active.'}
            </p>
          </div>
        </div>

        {/* Widget 6: Door Status */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-3 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              {doorOpen ? (
                <DoorOpen className="h-4.5 w-4.5 text-amber-500" />
              ) : (
                <DoorClosed className="h-4.5 w-4.5 text-emerald-500" />
              )}
              <span className="text-xs font-bold text-slate-700">Magnetic Door Lock</span>
            </div>
            <span className={`text-[9px] font-extrabold px-1.5 py-0.5 rounded uppercase ${
              doorOpen ? 'bg-amber-50 text-amber-600 border border-amber-100' : 'bg-emerald-50 text-emerald-600 border border-emerald-100'
            }`}>
              {doorOpen ? 'Open' : 'Locked'}
            </span>
          </div>
          <div className="pt-2">
            <h3 className="text-base font-extrabold text-slate-900">
              {doorOpen ? 'Room Door Open' : 'Room Secure'}
            </h3>
            <p className="text-[10px] text-slate-500 font-semibold mt-1">
              {doorOpen ? 'Intrusion alert if unoccupied or after standard school hours.' : 'Solenoid magnetic lock fully powered.'}
            </p>
          </div>
        </div>

        {/* Widget 7: Voltage Monitor */}
        <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-3 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Battery className="h-4.5 w-4.5 text-blue-600" />
              <span className="text-xs font-bold text-slate-700">Line Voltage Grid</span>
            </div>
            <span className="text-[9px] text-slate-400 font-mono">Realtime Grid</span>
          </div>
          <div className="pt-2">
            <h3 className="text-base font-extrabold text-slate-900">
              {voltage ? `${voltage.toFixed(1)} VAC` : '220.0 VAC'}
            </h3>
            <p className="text-[10px] text-slate-500 font-semibold mt-1">
              Monitors load currents to catch brownouts or mechanical failure.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SensorDisplay;

