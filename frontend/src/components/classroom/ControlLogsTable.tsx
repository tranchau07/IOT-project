import React from 'react';
import { ControlLog } from '../../types';
import { 
  CheckCircle2, 
  XCircle, 
  Send, 
  Hourglass, 
  Cpu, 
  UserCog 
} from 'lucide-react';

interface ControlLogsTableProps {
  logs: ControlLog[];
}

export const ControlLogsTable: React.FC<ControlLogsTableProps> = ({ logs }) => {
  const getStatusBadge = (status: ControlLog['status']) => {
    switch (status) {
      case 'SUCCESS':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-600 border border-emerald-100">
            <CheckCircle2 className="h-3 w-3" />
            SUCCESS
          </span>
        );
      case 'FAILED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-rose-50 text-rose-600 border border-rose-100 animate-pulse">
            <XCircle className="h-3 w-3" />
            FAILED
          </span>
        );
      case 'SENT':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-blue-50 text-blue-600 border border-blue-100 animate-pulse">
            <Send className="h-3 w-3" />
            TRANSMITTED
          </span>
        );
      case 'CREATE':
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-amber-50 text-amber-600 border border-amber-100">
            <Hourglass className="h-3 w-3 animate-spin" />
            PENDING
          </span>
        );
    }
  };

  const formatCommandSummary = (cmd: ControlLog['command']) => {
    const parts = [];
    if (cmd.power === 'CLEAR_FAULT') {
      return 'RESET HARDWARE FAULT STATE';
    }
    if (cmd.acMode !== 'OFF') {
      parts.push(`AC ${cmd.acMode} @ ${cmd.acTemp}°C`);
    } else {
      parts.push('AC OFF');
    }
    const lightsActive = cmd.lightStates.filter(s => s === 1).length;
    if (lightsActive > 0) {
      parts.push(`${lightsActive} Lights On`);
    }
    const activeFans = cmd.fanSpeed.filter(s => s > 0).length;
    if (activeFans > 0) {
      parts.push(`${activeFans} Fans On`);
    }
    return parts.join(' • ');
  };

  return (
    <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-4 shadow-sm">
      <div>
        <h4 className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider">COMMAND FEED</h4>
        <h3 className="font-bold text-slate-900 mt-0.5">Control Logs & Operations</h3>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs border-collapse">
          <thead>
            <tr className="border-b border-slate-100 text-slate-500 font-semibold h-10 select-none">
              <th className="pb-3 pr-4">OPERATOR & MODE</th>
              <th className="pb-3 pr-4">COMMAND SPECIFICATION</th>
              <th className="pb-3 pr-4">TIME STAMP</th>
              <th className="pb-3 text-right">STATUS</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {logs.length === 0 ? (
              <tr>
                <td colSpan={4} className="py-8 text-center text-slate-400 italic">
                  No control logs logged in the past 24 hours.
                </td>
              </tr>
            ) : (
              logs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-50/50 transition-colors h-14">
                  <td className="pr-4 py-3">
                    <div className="flex items-center gap-2">
                      <div className={`p-1.5 rounded-lg border ${log.mode === 'AUTO' ? 'bg-blue-50 text-blue-600 border-blue-100/50' : 'bg-amber-50 text-amber-600 border-amber-100/50'}`}>
                        {log.mode === 'AUTO' ? <Cpu className="h-3.5 w-3.5" /> : <UserCog className="h-3.5 w-3.5" />}
                      </div>
                      <div>
                        <p className="font-bold text-slate-800">{log.reason.replace(/_/g, ' ')}</p>
                        <p className="text-[9px] text-slate-400 font-semibold">Mode: {log.mode}</p>
                      </div>
                    </div>
                  </td>
                  <td className="pr-4 py-3 text-slate-700 font-medium">
                    {formatCommandSummary(log.command)}
                  </td>
                  <td className="pr-4 py-3 text-slate-500 font-medium">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                  <td className="py-3 text-right">
                    {getStatusBadge(log.status)}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ControlLogsTable;

