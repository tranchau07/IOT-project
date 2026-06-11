import React, { useState, useEffect } from 'react';
import { Classroom, Schedule, Config } from '../../types';
import { useClassroomStore } from '../../store/classroomStore';
import { 
  Settings, 
  Calendar, 
  Clock, 
  Plus, 
  Trash2, 
  Save, 
  CheckCircle2, 
  AlertCircle 
} from 'lucide-react';

interface AutoConfigPanelProps {
  classroom: Classroom;
}

const DAYS_OF_WEEK = [
  { value: 1, label: 'Monday' },
  { value: 2, label: 'Tuesday' },
  { value: 3, label: 'Wednesday' },
  { value: 4, label: 'Thursday' },
  { value: 5, label: 'Friday' },
  { value: 6, label: 'Saturday' },
  { value: 7, label: 'Sunday' }
];

export const AutoConfigPanel: React.FC<AutoConfigPanelProps> = ({ classroom }) => {
  const { updateClassroomConfig } = useClassroomStore();
  
  // local state for configs
  const [autoOff, setAutoOff] = useState(true);
  const [maxTemp, setMaxTemp] = useState(28);
  const [minOcc, setMinOcc] = useState(1);
  
  // local state for schedules
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  
  // form helper for adding schedule
  const [newDay, setNewDay] = useState(1);
  const [newStart, setNewStart] = useState('08:00');
  const [newEnd, setNewEnd] = useState('10:00');
  
  // status messages
  const [status, setStatus] = useState<{ type: 'success' | 'error' | null; message: string }>({
    type: null,
    message: ''
  });
  
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    // Populate form with current classroom data
    if (classroom.config) {
      setAutoOff(classroom.config.autoTurnOffFanAndLightWhenEmpty ?? true);
      setMaxTemp(classroom.config.maxTemperature ?? 28);
      setMinOcc(classroom.config.minOccupancyToTurnOnAC ?? 1);
    }
    if (classroom.schedules) {
      setSchedules(classroom.schedules);
    } else {
      setSchedules([]);
    }
    setStatus({ type: null, message: '' });
  }, [classroom]);

  const handleAddSchedule = () => {
    if (!newStart || !newEnd) {
      setStatus({ type: 'error', message: 'Please provide start and end times.' });
      return;
    }
    
    // Simple validation
    if (newStart >= newEnd) {
      setStatus({ type: 'error', message: 'Start time must be before end time.' });
      return;
    }

    const newSched: Schedule = {
      dayOfWeek: newDay,
      startTime: newStart,
      endTime: newEnd
    };

    // Check duplicate schedule (exact same day and start/end time)
    const exists = schedules.some(
      s => s.dayOfWeek === newSched.dayOfWeek && 
           s.startTime === newSched.startTime && 
           s.endTime === newSched.endTime
    );

    if (exists) {
      setStatus({ type: 'error', message: 'This schedule already exists.' });
      return;
    }

    setSchedules([...schedules, newSched].sort((a, b) => {
      if (a.dayOfWeek !== b.dayOfWeek) return a.dayOfWeek - b.dayOfWeek;
      return a.startTime.localeCompare(b.startTime);
    }));
    setStatus({ type: null, message: '' });
  };

  const handleRemoveSchedule = (index: number) => {
    const updated = schedules.filter((_, i) => i !== index);
    setSchedules(updated);
  };

  const handleSaveConfig = async () => {
    setIsSubmitting(true);
    setStatus({ type: null, message: '' });
    
    const configData: Config = {
      autoTurnOffFanAndLightWhenEmpty: autoOff,
      maxTemperature: maxTemp,
      minOccupancyToTurnOnAC: minOcc
    };

    try {
      await updateClassroomConfig(classroom.id, schedules, configData);
      setStatus({ type: 'success', message: 'Classroom automation configuration saved successfully!' });
    } catch (err: any) {
      console.error(err);
      setStatus({ type: 'error', message: err.response?.data?.message || 'Failed to save configuration.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-6 shadow-sm">
      {/* Title */}
      <div className="flex items-center justify-between border-b border-slate-100 pb-3">
        <div className="flex items-center gap-2.5">
          <div className="h-8 w-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center border border-blue-100/50">
            <Settings className="h-4.5 w-4.5" />
          </div>
          <div>
            <h4 className="text-sm font-bold text-slate-900">Automation & Scheduling Cockpit</h4>
            <p className="text-[10px] text-slate-500 font-semibold mt-0.5">Define schedule triggers and energy-saving rules</p>
          </div>
        </div>
      </div>

      {/* Status Alert */}
      {status.type && (
        <div className={`p-3.5 rounded-xl border flex items-center gap-2 text-xs font-semibold ${
          status.type === 'success' 
            ? 'border-emerald-200 bg-emerald-50 text-emerald-700' 
            : 'border-rose-200 bg-rose-50 text-rose-700'
        }`}>
          {status.type === 'success' ? (
            <CheckCircle2 className="h-4.5 w-4.5 shrink-0" />
          ) : (
            <AlertCircle className="h-4.5 w-4.5 shrink-0" />
          )}
          <span>{status.message}</span>
        </div>
      )}

      {/* Row 1: Energy Saving Config */}
      <div className="space-y-4">
        <h5 className="text-[11px] font-extrabold text-slate-400 uppercase tracking-wider">Energy Saving & Climate Settings</h5>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Toggle Auto Off */}
          <div className="flex items-center justify-between p-3.5 rounded-xl border border-slate-100 bg-slate-50/50">
            <div className="space-y-0.5">
              <span className="text-xs font-bold text-slate-700 block">Auto Turn Off Devices</span>
              <span className="text-[10px] text-slate-400 block font-medium">Turn off lights & fans after 10 mins empty</span>
            </div>
            <label className="relative inline-flex items-center cursor-pointer select-none">
              <input 
                type="checkbox" 
                checked={autoOff} 
                onChange={(e) => setAutoOff(e.target.checked)}
                className="sr-only peer" 
              />
              <div className="w-9 h-5 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-blue-600"></div>
            </label>
          </div>

          {/* AC Trigger occupancy */}
          <div className="grid grid-cols-2 gap-2">
            <div className="space-y-1">
              <label className="text-[10px] font-bold text-slate-450 block uppercase tracking-wider">AC Temp Threshold</label>
              <input
                type="number"
                min={16}
                max={35}
                value={maxTemp}
                onChange={(e) => setMaxTemp(parseFloat(e.target.value) || 28)}
                className="w-full px-3 py-1.5 rounded-xl bg-white border border-slate-200 text-slate-800 text-xs font-semibold focus:outline-none"
              />
            </div>
            <div className="space-y-1">
              <label className="text-[10px] font-bold text-slate-450 block uppercase tracking-wider">Min Occupancy AC</label>
              <input
                type="number"
                min={1}
                max={100}
                value={minOcc}
                onChange={(e) => setMinOcc(parseInt(e.target.value) || 1)}
                className="w-full px-3 py-1.5 rounded-xl bg-white border border-slate-200 text-slate-800 text-xs font-semibold focus:outline-none"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Row 2: Schedules Section */}
      <div className="space-y-4 pt-2 border-t border-slate-100">
        <h5 className="text-[11px] font-extrabold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
          <Calendar className="h-3.5 w-3.5" />
          Scheduled Class Hours
        </h5>

        {/* Existing Schedules list */}
        <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
          {schedules.length === 0 ? (
            <p className="text-xs text-slate-400 italic py-3 text-center border border-dashed border-slate-200 rounded-xl bg-slate-50/50">
              No schedules defined. Room auto-cooling will not trigger.
            </p>
          ) : (
            schedules.map((s, idx) => (
              <div key={idx} className="flex items-center justify-between p-2.5 rounded-xl border border-slate-150 bg-white hover:border-slate-350 transition-colors">
                <div className="flex items-center gap-3">
                  <div className="text-xs font-bold text-blue-600 bg-blue-50 px-2 py-0.5 rounded-lg border border-blue-100/50 min-w-[90px] text-center">
                    {DAYS_OF_WEEK.find(d => d.value === s.dayOfWeek)?.label || 'Weekday'}
                  </div>
                  <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-650">
                    <Clock className="h-3.5 w-3.5 text-slate-400" />
                    <span>{s.startTime} - {s.endTime}</span>
                  </div>
                </div>
                <button
                  type="button"
                  onClick={() => handleRemoveSchedule(idx)}
                  className="p-1 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-all cursor-pointer"
                  title="Remove Schedule slot"
                >
                  <Trash2 className="h-3.5 w-3.5" />
                </button>
              </div>
            ))
          )}
        </div>

        {/* Add Schedule Form */}
        <div className="p-3 bg-slate-50 border border-slate-150 rounded-xl space-y-3">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Add New Class Schedule Block</span>
          <div className="grid grid-cols-3 gap-2">
            <div>
              <select
                value={newDay}
                onChange={(e) => setNewDay(parseInt(e.target.value))}
                className="w-full px-2 py-1.5 rounded-lg border border-slate-200 bg-white text-slate-800 text-xs font-semibold cursor-pointer"
              >
                {DAYS_OF_WEEK.map(d => (
                  <option key={d.value} value={d.value} className="text-slate-800 bg-white">{d.label}</option>
                ))}
              </select>
            </div>
            <div>
              <input
                type="time"
                value={newStart}
                onChange={(e) => setNewStart(e.target.value)}
                className="w-full px-2 py-1.5 rounded-lg border border-slate-200 bg-white text-slate-800 text-xs font-semibold"
              />
            </div>
            <div>
              <input
                type="time"
                value={newEnd}
                onChange={(e) => setNewEnd(e.target.value)}
                className="w-full px-2 py-1.5 rounded-lg border border-slate-200 bg-white text-slate-800 text-xs font-semibold"
              />
            </div>
          </div>
          <button
            type="button"
            onClick={handleAddSchedule}
            className="w-full py-1.5 rounded-lg bg-slate-200 hover:bg-slate-300 text-slate-700 text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer shadow-sm border border-slate-200/50"
          >
            <Plus className="h-3.5 w-3.5" />
            Add Slot to List
          </button>
        </div>
      </div>

      {/* Row 3: Submit / Save Configurations button */}
      <div className="pt-4 border-t border-slate-100 flex justify-end">
        <button
          onClick={handleSaveConfig}
          disabled={isSubmitting}
          className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white text-xs font-bold transition-all shadow-md shadow-blue-500/10 flex items-center gap-2 cursor-pointer"
        >
          <Save className="h-4 w-4" />
          <span>{isSubmitting ? 'Saving Configuration...' : 'Save Automation Rules'}</span>
        </button>
      </div>
    </div>
  );
};
