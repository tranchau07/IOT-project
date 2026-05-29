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
      <div className="glass-panel rounded-2xl p-8 flex flex-col items-center justify-center text-center space-y-3">
        <Activity className="h-8 w-8 text-slate-500 animate-pulse" />
        <p className="text-sm text-slate-400 font-medium">Waiting for realtime sensor telemetry...</p>
      </div>
    );
  }

  const { environment, voltage, smokeDetected, doorOpen, timestamp } = reading;
  const isOccupied = environment.occupancy;
  const isSmokeAlarm = smokeDetected === true;

  return (
    <div className="space-y-6">
      {/* Realtime Telemetry Header */}
      <div className="flex items-center justify-between">
        <div>
          <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider">REALTIME TELEMETRY</h4>
          <span className="text-[10px] text-slate-500 font-mono">Last Reading: {new Date(timestamp).toLocaleTimeString()}</span>
        </div>
        <div className="flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-success-glowing text-success border border-success/30 text-[10px] font-bold">
          <span className="h-1.5 w-1.5 rounded-full bg-success glowing-dot" />
          <span>Realtime Feed</span>
        </div>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Widget 1: Temperature */}
        <div className="glass-panel rounded-2xl p-4.5 space-y-3 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider">Temperature</span>
            <div className="h-8 w-8 rounded-lg bg-danger-glowing text-danger flex items-center justify-center">
              <Thermometer className="h-4.5 w-4.5" />
            </div>
          </div>
          <div>
            <h3 className="text-2xl font-extrabold text-slate-100">{environment.temperature.toFixed(1)}°C</h3>
            <span className="text-[10px] text-slate-400 font-medium">Thermostat Sensor</span>
          </div>
        </div>

        {/* Widget 2: Humidity */}
        <div className="glass-panel rounded-2xl p-4.5 space-y-3 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider">Humidity</span>
            <div className="h-8 w-8 rounded-lg bg-primary-glowing text-primary flex items-center justify-center">
              <Droplets className="h-4.5 w-4.5" />
            </div>
          </div>
          <div>
            <h3 className="text-2xl font-extrabold text-slate-100">{environment.humidity.toFixed(0)}%</h3>
            <span className="text-[10px] text-slate-400 font-medium">Relative Humidity</span>
          </div>
        </div>

        {/* Widget 3: Occupancy */}
        <div className={`glass-panel rounded-2xl p-4.5 space-y-3 relative overflow-hidden transition-all ${
          isOccupied ? 'border-primary/20 bg-primary-glowing/5' : ''
        }`}>
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider">Occupancy</span>
            <div className={`h-8 w-8 rounded-lg flex items-center justify-center ${
              isOccupied ? 'bg-primary-glowing text-primary' : 'bg-slate-800 text-slate-500'
            }`}>
              {isOccupied ? <UserCheck className="h-4.5 w-4.5" /> : <UserX className="h-4.5 w-4.5" />}
            </div>
          </div>
          <div>
            <h3 className={`text-2xl font-extrabold ${isOccupied ? 'text-primary' : 'text-slate-400'}`}>
              {isOccupied ? 'Occupied' : 'Empty'}
            </h3>
            <span className="text-[10px] text-slate-400 font-medium">PIR Motion Sensor</span>
          </div>
        </div>

        {/* Widget 4: Light Level */}
        <div className="glass-panel rounded-2xl p-4.5 space-y-3 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider">Light Level</span>
            <div className="h-8 w-8 rounded-lg bg-warning-glowing text-warning flex items-center justify-center">
              <Sun className="h-4.5 w-4.5" />
            </div>
          </div>
          <div>
            <h3 className="text-2xl font-extrabold text-slate-100">{environment.lightLevel.toFixed(0)} Lux</h3>
            <span className="text-[10px] text-slate-400 font-medium">Photoresistor Sensor</span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Widget 5: Smoke Detection */}
        <div className={`glass-panel rounded-2xl p-5 space-y-3 transition-all ${
          isSmokeAlarm ? 'border-danger/40 bg-danger-glowing/10 shadow-lg shadow-danger-glowing' : ''
        }`}>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <ShieldAlert className={`h-4.5 w-4.5 ${isSmokeAlarm ? 'text-danger animate-bounce' : 'text-slate-500'}`} />
              <span className="text-xs font-bold text-slate-300">Smoke / Fire Alarm</span>
            </div>
            <span className={`h-2.5 w-2.5 rounded-full ${isSmokeAlarm ? 'bg-danger glowing-dot' : 'bg-success'}`} />
          </div>
          <div className="pt-2">
            <h3 className={`text-lg font-extrabold ${isSmokeAlarm ? 'text-danger animate-pulse' : 'text-slate-100'}`}>
              {isSmokeAlarm ? '🔥 SMOKE DETECTED!' : 'Normal (No Smoke)'}
            </h3>
            <p className="text-[10px] text-slate-400 font-medium mt-1">
              {isSmokeAlarm ? 'Emergency shutdown triggers and lockdown relays engaged.' : 'Standard air ventilation levels active.'}
            </p>
          </div>
        </div>

        {/* Widget 6: Door Status */}
        <div className="glass-panel rounded-2xl p-5 space-y-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              {doorOpen ? (
                <DoorOpen className="h-4.5 w-4.5 text-warning" />
              ) : (
                <DoorClosed className="h-4.5 w-4.5 text-success" />
              )}
              <span className="text-xs font-bold text-slate-300">Magnetic Door Lock</span>
            </div>
            <span className={`text-[9px] font-extrabold px-1.5 py-0.5 rounded uppercase ${
              doorOpen ? 'bg-warning/20 text-warning border border-warning/30' : 'bg-success/20 text-success border border-success/30'
            }`}>
              {doorOpen ? 'Open' : 'Locked'}
            </span>
          </div>
          <div className="pt-2">
            <h3 className="text-lg font-extrabold text-slate-100">
              {doorOpen ? 'Room Door Open' : 'Room Secure'}
            </h3>
            <p className="text-[10px] text-slate-400 font-medium mt-1">
              {doorOpen ? 'Intrusion alert if unoccupied or after standard school hours.' : 'Solenoid magnetic lock fully powered.'}
            </p>
          </div>
        </div>

        {/* Widget 7: Voltage Monitor */}
        <div className="glass-panel rounded-2xl p-5 space-y-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Battery className="h-4.5 w-4.5 text-primary" />
              <span className="text-xs font-bold text-slate-300">Line Voltage Grid</span>
            </div>
            <span className="text-[10px] text-slate-500 font-mono">Realtime Grid</span>
          </div>
          <div className="pt-2">
            <h3 className="text-lg font-extrabold text-slate-100">
              {voltage ? `${voltage.toFixed(1)} VAC` : '220.0 VAC'}
            </h3>
            <p className="text-[10px] text-slate-400 font-medium mt-1">
              Monitors load currents to catch brownouts or mechanical failure.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SensorDisplay;
