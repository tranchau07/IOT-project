import React from 'react';
import { SensorReading } from '../../types';
import { 
  ResponsiveContainer, 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  Tooltip, 
  CartesianGrid 
} from 'recharts';

interface LiveTelemetryChartProps {
  readings: SensorReading[];
}

export const LiveTelemetryChart: React.FC<LiveTelemetryChartProps> = ({ readings }) => {
  // Format the chart data dynamically
  const chartData = readings.map((r) => ({
    time: new Date(r.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
    temperature: r.environment.temperature,
    humidity: r.environment.humidity,
    light: r.environment.lightLevel,
  }));

  return (
    <div className="bg-white border border-slate-200/80 rounded-2xl p-5 space-y-4 shadow-sm">
      <div className="flex items-center justify-between">
        <div>
          <h4 className="text-[10px] font-extrabold text-slate-400 uppercase tracking-wider">LIVE TREND LINES</h4>
          <h3 className="font-bold text-slate-900 mt-0.5">Environmental Fluctuations</h3>
        </div>
        <div className="flex gap-4 text-xs font-semibold">
          <div className="flex items-center gap-1.5 text-rose-500">
            <span className="h-2 w-2 rounded-full bg-rose-500" />
            <span>Temp (°C)</span>
          </div>
          <div className="flex items-center gap-1.5 text-blue-600">
            <span className="h-2 w-2 rounded-full bg-blue-600" />
            <span>Humidity (%)</span>
          </div>
        </div>
      </div>

      <div className="h-72 w-full pt-2">
        {chartData.length === 0 ? (
          <div className="h-full w-full flex items-center justify-center text-xs text-slate-400 italic">
            Gathering telemetry data to compute curves...
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData} margin={{ top: 5, right: 5, left: -20, bottom: 5 }}>
              <CartesianGrid stroke="#f1f5f9" strokeDasharray="3 3" vertical={false} />
              <XAxis 
                dataKey="time" 
                stroke="#94a3b8" 
                fontSize={9} 
                tickLine={false} 
                axisLine={false} 
              />
              <YAxis 
                yAxisId="left" 
                stroke="#f43f5e" 
                fontSize={9} 
                tickLine={false} 
                axisLine={false} 
                domain={['auto', 'auto']}
              />
              <YAxis 
                yAxisId="right" 
                orientation="right"
                stroke="#2563eb" 
                fontSize={9} 
                tickLine={false} 
                axisLine={false} 
                domain={[0, 100]}
              />
              <Tooltip 
                contentStyle={{ backgroundColor: '#ffffff', borderColor: '#e2e8f0', borderRadius: '8px', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)' }}
                labelStyle={{ fontSize: '10px', fontWeight: 'bold', color: '#64748b' }}
                itemStyle={{ fontSize: '11px', fontWeight: '600' }}
              />
              <Line 
                yAxisId="left"
                type="monotone" 
                dataKey="temperature" 
                stroke="#f43f5e" 
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 4 }}
                name="Temperature"
              />
              <Line 
                yAxisId="right"
                type="monotone" 
                dataKey="humidity" 
                stroke="#2563eb" 
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 4 }}
                name="Humidity"
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  );
};

export default LiveTelemetryChart;

