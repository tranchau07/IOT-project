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
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-success-glowing text-success border border-success/20">
            <CheckCircle2 className="h-3 w-3" />
            SUCCESS
          </span>
        );
      case 'FAILED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-danger-glowing text-danger border border-danger/20 animate-pulse">
            <XCircle className="h-3 w-3" />
            FAILED
          </span>
        );
      case 'SENT':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-primary-glowing text-primary border border-primary/20 animate-pulse">
            <Send className="h-3 w-3 animate-pulse" />
            TRANSMITTED
          </span>
        );
      case 'CREATE':
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-warning-glowing text-warning border border-warning/20">
            <Hourglass className="h-3 w-3 animate-spin" />
            PENDING
          </span>
        );
    }
  };

  const formatCommandSummary = (cmd: ControlLog['command']) => {
    const parts = [];
    if (cmd.power === 'CLEAR_FAULT') {
      return 'RESET HARDWARE FAULT LATITUDE';
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
    <div className="glass-panel rounded-2xl p-5 space-y-4">
      <div>
        <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider">COMMAND FEED</h4>
        <h3 className="font-bold text-slate-200 mt-0.5">Control Logs & Operations</h3>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs border-collapse">
          <thead>
            <tr className="border-b border-slate-800 text-slate-400 font-semibold h-10 select-none">
              <th className="pb-3 pr-4">OPERATOR & MODE</th>
              <th className="pb-3 pr-4">COMMAND SPECIFICATION</th>
              <th className="pb-3 pr-4">TIME STAMP</th>
              <th className="pb-3 text-right">STATUS</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/40">
            {logs.length === 0 ? (
              <tr>
                <td colSpan={4} className="py-8 text-center text-slate-500 italic">
                  No control logs logged in the past 24 hours.
                </td>
              </tr>
            ) : (
              logs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-900/10 transition-colors h-14">
                  <td className="pr-4 py-3">
                    <div className="flex items-center gap-2">
                      <div className={`p-1.5 rounded-lg ${log.mode === 'AUTO' ? 'bg-primary-glowing text-primary' : 'bg-warning-glowing text-warning'}`}>
                        {log.mode === 'AUTO' ? <Cpu className="h-3.5 w-3.5" /> : <UserCog className="h-3.5 w-3.5" />}
                      </div>
                      <div>
                        <p className="font-bold text-slate-200">{log.reason.replace(/_/g, ' ')}</p>
                        <p className="text-[10px] text-slate-400 font-medium">Mode: {log.mode}</p>
                      </div>
                    </div>
                  </td>
                  <td className="pr-4 py-3 text-slate-300 font-medium">
                    {formatCommandSummary(log.command)}
                  </td>
                  <td className="pr-4 py-3 text-slate-400 font-medium">
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
