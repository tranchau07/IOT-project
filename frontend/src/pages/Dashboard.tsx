import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useClassroomStore } from '../store/classroomStore';
import { classroomService } from '../services/api';
import ClassroomCard from '../components/classroom/ClassroomCard';
import { 
  School, 
  Cpu, 
  Thermometer, 
  AlertTriangle, 
  Plus, 
  X, 
  LayoutGrid, 
  RefreshCw 
} from 'lucide-react';

export const Dashboard: React.FC = () => {
  const { classrooms, isLoading, fetchClassrooms, selectClassroom } = useClassroomStore();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [name, setName] = useState('');
  const [building, setBuilding] = useState('');
  const [deviceId, setDeviceId] = useState('');
  const [deviceType, setDeviceType] = useState('SMART_ROOM_GATEWAY');
  const [lightsCount, setLightsCount] = useState(2);
  const [fansCount, setFansCount] = useState(2);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    fetchClassrooms();
  }, [fetchClassrooms]);

  const handleCardClick = (room: any) => {
    selectClassroom(room);
    navigate(`/classrooms/${room.id}`);
  };

  const handleCreateClassroom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !building || !deviceId) {
      setError('Please fill in all required fields.');
      return;
    }

    setError(null);
    try {
      // Map standard structure expected by Classroom database
      const classroomRequest = {
        name,
        building,
        device: {
          deviceId,
          deviceType,
          connectivity: 'OFFLINE',
          power: 'OFF',
          lastSeen: new Date().toISOString()
        },
        currentState: {
          power: 'OFF',
          acMode: 'OFF',
          acTemp: 24.0,
          lightStates: Array(lightsCount).fill(0),
          fanSpeed: Array(fansCount).fill(0)
        }
      };

      await classroomService.create(classroomRequest);
      setIsModalOpen(false);
      
      // Reset Form fields
      setName('');
      setBuilding('');
      setDeviceId('');
      setLightsCount(2);
      setFansCount(2);
      
      // Refresh list
      fetchClassrooms();
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to create room config.');
    }
  };

  // Metrics calculators
  const totalClassrooms = classrooms.length;
  const onlineDevices = classrooms.filter(c => c.device.connectivity === 'ONLINE').length;
  const activeAcs = classrooms.filter(c => c.currentState.acMode !== 'OFF').length;
  const systemFaults = classrooms.filter(c => c.faultLatched).length;

  return (
    <div className="space-y-8 py-6 select-none">
      {/* 1. Dashboard Overview Title Block */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <LayoutGrid className="h-5.5 w-5.5 text-primary" />
            System Cockpit Overview
          </h2>
          <p className="text-xs text-slate-400 font-medium mt-1">Realtime monitoring and controls of smart rooms</p>
        </div>

        <div className="flex gap-2">
          <button
            onClick={fetchClassrooms}
            className="p-2.5 rounded-xl border border-slate-700 bg-slate-800/40 text-slate-300 hover:bg-slate-800 hover:text-white transition-all cursor-pointer"
            title="Refresh Grid"
          >
            <RefreshCw className={`h-4.5 w-4.5 ${isLoading ? 'animate-spin' : ''}`} />
          </button>
          <button
            onClick={() => setIsModalOpen(true)}
            className="px-4 py-2.5 rounded-xl bg-primary hover:bg-primary-hover text-white text-xs font-bold shadow-lg shadow-primary-glowing transition-all flex items-center gap-2 cursor-pointer"
          >
            <Plus className="h-4 w-4" />
            Add Room Node
          </button>
        </div>
      </div>

      {/* 2. Grid Statistics Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4.5">
        <div className="glass-panel rounded-2xl p-4.5 flex items-center gap-4 relative overflow-hidden">
          <div className="h-10 w-10 rounded-xl bg-primary-glowing text-primary flex items-center justify-center shrink-0 border border-primary/20">
            <School className="h-5 w-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-500 block uppercase">Smart Rooms</span>
            <h3 className="text-2xl font-extrabold text-slate-100 mt-0.5">{totalClassrooms}</h3>
          </div>
        </div>

        <div className="glass-panel rounded-2xl p-4.5 flex items-center gap-4 relative overflow-hidden">
          <div className="h-10 w-10 rounded-xl bg-success-glowing text-success flex items-center justify-center shrink-0 border border-success/20">
            <Cpu className="h-5 w-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-500 block uppercase">Online Nodes</span>
            <h3 className="text-2xl font-extrabold text-success mt-0.5">{onlineDevices}</h3>
          </div>
        </div>

        <div className="glass-panel rounded-2xl p-4.5 flex items-center gap-4 relative overflow-hidden">
          <div className="h-10 w-10 rounded-xl bg-warning-glowing text-warning flex items-center justify-center shrink-0 border border-warning/20">
            <Thermometer className="h-5 w-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-500 block uppercase">Active ACs</span>
            <h3 className="text-2xl font-extrabold text-slate-100 mt-0.5">{activeAcs}</h3>
          </div>
        </div>

        <div className={`glass-panel rounded-2xl p-4.5 flex items-center gap-4 relative overflow-hidden ${
          systemFaults > 0 ? 'border-danger/30 bg-danger-glowing/5' : ''
        }`}>
          <div className={`h-10 w-10 rounded-xl flex items-center justify-center shrink-0 border ${
            systemFaults > 0 
              ? 'bg-danger/20 text-danger border-danger/30 animate-pulse' 
              : 'bg-slate-800 text-slate-500 border-slate-700/20'
          }`}>
            <AlertTriangle className="h-5 w-5" />
          </div>
          <div>
            <span className="text-[10px] font-bold text-slate-500 block uppercase">Active Alarms</span>
            <h3 className={`text-2xl font-extrabold mt-0.5 ${systemFaults > 0 ? 'text-danger animate-pulse' : 'text-slate-100'}`}>
              {systemFaults}
            </h3>
          </div>
        </div>
      </div>

      {/* 3. Classrooms Listing Grid */}
      {isLoading && classrooms.length === 0 ? (
        <div className="h-64 flex items-center justify-center text-xs text-slate-500 font-medium">
          <RefreshCw className="h-6 w-6 animate-spin mr-2 text-primary" />
          Synchronizing classroom registry...
        </div>
      ) : classrooms.length === 0 ? (
        <div className="glass-panel rounded-3xl p-12 text-center space-y-4">
          <School className="h-12 w-12 text-slate-600 mx-auto" />
          <h3 className="text-lg font-bold text-slate-300">No Classroom Nodes Provisioned</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto leading-relaxed">
            There are no room nodes mapped to this dashboard. Click "Add Room Node" to bootstrap your first IoT smart classroom.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          {classrooms.map((room) => (
            <ClassroomCard
              key={room.id}
              classroom={room}
              onClick={() => handleCardClick(room)}
            />
          ))}
        </div>
      )}

      {/* 4. MODAL: Create New Classroom */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="glass-panel rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden relative border border-slate-800 animate-in fade-in zoom-in duration-200">
            <div className="flex items-center justify-between px-6 py-4.5 border-b border-slate-800/80">
              <h3 className="font-bold text-slate-100">Provision New Room Node</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="h-8 w-8 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition-all flex items-center justify-center cursor-pointer"
              >
                <X className="h-4.5 w-4.5" />
              </button>
            </div>

            <form onSubmit={handleCreateClassroom} className="p-6 space-y-4.5">
              {error && (
                <div className="p-3.5 rounded-xl border border-danger/30 bg-danger-glowing text-danger text-xs font-semibold">
                  {error}
                </div>
              )}

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Classroom Name *</label>
                  <input
                    type="text"
                    required
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="e.g. Room 302"
                    className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 focus:border-primary text-slate-200 text-sm font-semibold transition-all focus:outline-none focus:ring-1 focus:ring-primary"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Building Name *</label>
                  <input
                    type="text"
                    required
                    value={building}
                    onChange={(e) => setBuilding(e.target.value)}
                    placeholder="e.g. C1"
                    className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 focus:border-primary text-slate-200 text-sm font-semibold transition-all focus:outline-none focus:ring-1 focus:ring-primary"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Device Hardware ID *</label>
                  <input
                    type="text"
                    required
                    value={deviceId}
                    onChange={(e) => setDeviceId(e.target.value)}
                    placeholder="e.g. esp32_node_01"
                    className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 focus:border-primary text-slate-200 text-sm font-semibold transition-all focus:outline-none focus:ring-1 focus:ring-primary"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Gateway Category</label>
                  <select
                    value={deviceType}
                    onChange={(e) => setDeviceType(e.target.value)}
                    className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 focus:border-primary text-slate-400 text-sm font-semibold transition-all focus:outline-none focus:ring-1 focus:ring-primary select-none cursor-pointer"
                  >
                    <option value="SMART_ROOM_GATEWAY">Smart Room Gateway</option>
                    <option value="SMART_CLIMATE_GATEWAY">Smart Climate Gateway</option>
                    <option value="HEMS_SENSOR_HUB">HEMS Sensor Hub</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 border-t border-slate-850/60 pt-4">
                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Light Relays Count</label>
                  <input
                    type="number"
                    min={1}
                    max={8}
                    value={lightsCount}
                    onChange={(e) => setLightsCount(parseInt(e.target.value) || 2)}
                    className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 focus:border-primary text-slate-200 text-sm font-semibold transition-all focus:outline-none"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Fan Relays Count</label>
                  <input
                    type="number"
                    min={1}
                    max={8}
                    value={fansCount}
                    onChange={(e) => setFansCount(parseInt(e.target.value) || 2)}
                    className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 focus:border-primary text-slate-200 text-sm font-semibold transition-all focus:outline-none"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-3 border-t border-slate-800/80 pt-4 mt-4">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-bold border border-slate-700 bg-slate-800/30 text-slate-300 hover:text-white transition-all cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-primary hover:bg-primary-hover text-white text-xs font-bold shadow-lg shadow-primary-glowing transition-all cursor-pointer"
                >
                  Register Node
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
