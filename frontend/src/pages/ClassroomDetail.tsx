import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useClassroomStore } from '../store/classroomStore';
import { DeviceControlPanel } from '../components/classroom/DeviceControlPanel';
import { SensorDisplay } from '../components/classroom/SensorDisplay';
import { LiveTelemetryChart } from '../components/classroom/LiveTelemetryChart';
import { ControlLogsTable } from '../components/classroom/ControlLogsTable';
import { 
  ArrowLeft, 
  School, 
  Settings, 
  Activity, 
  Radio, 
  RefreshCw,
  Trash2
} from 'lucide-react';
import { classroomService } from '../services/api';

export const ClassroomDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { 
    selectedClassroom, 
    sensorReadings, 
    controlLogs, 
    isLoading, 
    selectClassroom, 
    clearSelectedClassroom,
    classrooms,
    fetchClassrooms
  } = useClassroomStore();

  useEffect(() => {
    const initRoom = async () => {
      if (id) {
        // If rooms list is empty, fetch list first
        if (classrooms.length === 0) {
          await fetchClassrooms();
        }
        
        // Find room config in local store
        const room = useClassroomStore.getState().classrooms.find(c => c.id === id);
        if (room) {
          selectClassroom(room);
        } else {
          // If not found in cache, pull directly from DB
          try {
            const freshRoom = await classroomService.getById(id);
            selectClassroom(freshRoom);
          } catch (err) {
            console.error('Room not found:', err);
            navigate('/');
          }
        }
      }
    };
    
    initRoom();

    // Clean up WS subscriptions on component unmount
    return () => {
      clearSelectedClassroom();
    };
  }, [id, selectClassroom, clearSelectedClassroom, classrooms.length, fetchClassrooms, navigate]);

  const handleDeleteRoom = async () => {
    if (!id) return;
    const confirm = window.confirm('Are you absolutely sure you want to delete this smart room configuration? Physical devices will lose connection.');
    if (!confirm) return;

    try {
      await classroomService.delete(id);
      navigate('/');
    } catch (err) {
      alert('Failed to delete room configuration. Admin rights might be required.');
    }
  };

  if (isLoading && !selectedClassroom) {
    return (
      <div className="h-96 flex flex-col items-center justify-center text-xs text-slate-500 font-medium">
        <RefreshCw className="h-6 w-6 animate-spin mr-2 text-primary" />
        Syncing live data feed channels...
      </div>
    );
  }

  if (!selectedClassroom) {
    return (
      <div className="text-center py-12 space-y-4">
        <School className="h-12 w-12 text-slate-600 mx-auto" />
        <h3 className="text-lg font-bold text-slate-300">Classroom Configuration Missing</h3>
        <button
          onClick={() => navigate('/')}
          className="px-4 py-2 rounded-xl bg-primary text-white text-xs font-bold transition-all"
        >
          Return to Cockpit
        </button>
      </div>
    );
  }

  const { name, building, device } = selectedClassroom;
  const isOnline = device.connectivity === 'ONLINE';
  const latestReading = sensorReadings[sensorReadings.length - 1] || null;

  return (
    <div className="space-y-6 py-6 select-none">
      {/* 1. Page Header & Action deck */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate('/')}
            className="h-9 w-9 rounded-xl border border-slate-700 bg-slate-800/40 text-slate-300 hover:bg-slate-850 hover:text-white flex items-center justify-center transition-all cursor-pointer"
          >
            <ArrowLeft className="h-4.5 w-4.5" />
          </button>
          
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-xl font-bold tracking-tight text-white">{name}</h2>
              <span className={`h-2 w-2 rounded-full ${isOnline ? 'bg-success glowing-dot' : 'bg-slate-600'}`} />
              <span className="text-[10px] text-slate-400 font-bold uppercase">{building} Building</span>
            </div>
            <p className="text-xs text-slate-500 font-medium mt-0.5">Gateway: {device.deviceId} ({device.deviceType})</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {/* Realtime stream active banner */}
          <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl border border-slate-800 bg-[#0d1425] text-[10px] font-bold text-primary">
            <Radio className="h-3.5 w-3.5 animate-pulse text-primary" />
            <span>WebSocket Live Sync Active</span>
          </div>

          <button
            onClick={handleDeleteRoom}
            className="p-2 rounded-xl border border-danger/20 bg-danger-glowing/10 text-danger hover:bg-danger hover:text-white transition-all cursor-pointer"
            title="Delete Room Configuration"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </div>

      {/* 2. Unified Workspace grid */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
        {/* Left 2 Columns: Telemetry, Controls, and Curve Charts */}
        <div className="xl:col-span-2 space-y-6">
          {/* Telemetry gauge widgets */}
          <SensorDisplay reading={latestReading} />

          {/* Interactive controls (lights, fans, AC, faults) */}
          <DeviceControlPanel classroom={selectedClassroom} />

          {/* scrolling trends chart */}
          <LiveTelemetryChart readings={sensorReadings} />
        </div>

        {/* Right 1 Column: Activity Logs table */}
        <div className="xl:col-span-1">
          <ControlLogsTable logs={controlLogs} />
        </div>
      </div>
    </div>
  );
};

export default ClassroomDetail;
