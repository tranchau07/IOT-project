import React from 'react';
import { SensorReading } from '../../types';
import { 
  ResponsiveContainer, 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  Tooltip, 
  Legend, 
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
    <div className="glass-panel rounded-2xl p-5 space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider">LIVE TREND LINES</h4>
          <h3 className="font-bold text-slate-200 mt-0.5">Environmental Fluctuations</h3>
        </div>
        <div className="flex gap-4 text-xs font-semibold">
          <div className="flex items-center gap-1.5 text-danger">
            <span className="h-2 w-2 rounded-full bg-danger" />
            <span>Temp (°C)</span>
          </div>
          <div className="flex items-center gap-1.5 text-primary">
            <span className="h-2 w-2 rounded-full bg-primary" />
            <span>Humidity (%)</span>
          </div>
        </div>
      </div>

      <div className="h-72 w-full pt-2">
        {chartData.length === 0 ? (
          <div className="h-full w-full flex items-center justify-center text-xs text-slate-500 italic">
            Gathering telemetry data to compute curves...
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData} margin={{ top: 5, right: 5, left: -20, bottom: 5 }}>
              <CartesianGrid stroke="#1e293b" strokeDasharray="3 3" vertical={false} />
              <XAxis 
                dataKey="time" 
                stroke="#475569" 
                fontSize={9} 
                tickLine={false} 
                axisLine={false} 
              />
              <YAxis 
                yAxisId="left" 
                stroke="#ef4444" 
                fontSize={9} 
                tickLine={false} 
                axisLine={false} 
                domain={['auto', 'auto']}
              />
              <YAxis 
                yAxisId="right" 
                orientation="right"
                stroke="#3b82f6" 
                fontSize={9} 
                tickLine={false} 
                axisLine={false} 
                domain={[0, 100]}
              />
              <Tooltip 
                contentStyle={{ backgroundColor: '#131c2e', borderColor: '#1e293b', borderRadius: '8px' }}
                labelStyle={{ fontSize: '10px', fontWeight: 'bold', color: '#94a3b8' }}
                itemStyle={{ fontSize: '11px', fontWeight: 'semibold' }}
              />
              <Line 
                yAxisId="left"
                type="monotone" 
                dataKey="temperature" 
                stroke="#ef4444" 
                strokeWidth={2.5}
                dot={false}
                activeDot={{ r: 4 }}
                name="Temperature"
              />
              <Line 
                yAxisId="right"
                type="monotone" 
                dataKey="humidity" 
                stroke="#3b82f6" 
                strokeWidth={2.5}
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
