import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { 
  LayoutDashboard, 
  School, 
  Users, 
  ShieldAlert, 
  Settings,
  HelpCircle
} from 'lucide-react';

export const Sidebar: React.FC = () => {
  const { roles } = useAuthStore();
  const isAdmin = roles.includes('ADMIN') || roles.includes('ROLE_ADMIN');

  const navItems = [
    {
      to: '/',
      label: 'Dashboard',
      icon: LayoutDashboard,
      roles: ['USER', 'ADMIN', 'ROLE_ADMIN'],
    },
    {
      to: '/classrooms',
      label: 'Smart Rooms',
      icon: School,
      roles: ['USER', 'ADMIN', 'ROLE_ADMIN'],
    },
    {
      to: '/users',
      label: 'Users Control',
      icon: Users,
      roles: ['ADMIN', 'ROLE_ADMIN'], // Admin Only
    },
    {
      to: '/roles',
      label: 'Roles & Keys',
      icon: ShieldAlert,
      roles: ['ADMIN', 'ROLE_ADMIN'], // Admin Only
    },
  ];

  return (
    <aside className="w-64 border-r border-border bg-[#0a0f1d] flex flex-col justify-between shrink-0 select-none">
      <div className="flex-1 py-6 px-4 space-y-6">
        <div className="space-y-1">
          <p className="px-3 text-[10px] font-bold text-slate-500 tracking-wider uppercase">MAIN MENU</p>
          <nav className="space-y-1">
            {navItems
              .filter((item) => !item.roles || item.roles.some((r) => roles.includes(r) || (r === 'ADMIN' && isAdmin)))
              .map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold transition-all group ${
                      isActive
                        ? 'bg-primary text-white shadow-md shadow-primary-glowing'
                        : 'text-slate-400 hover:text-white hover:bg-slate-800/40'
                    }`
                  }
                >
                  {({ isActive }) => (
                    <>
                      <item.icon className={`h-4.5 w-4.5 shrink-0 ${isActive ? 'text-white' : 'text-slate-400 group-hover:text-primary'}`} />
                      <span>{item.label}</span>
                    </>
                  )}
                </NavLink>
              ))}
          </nav>
        </div>

        <div className="border-t border-slate-800/60 pt-6 space-y-1">
          <p className="px-3 text-[10px] font-bold text-slate-500 tracking-wider uppercase">PREFERENCES</p>
          <NavLink
            to="/settings"
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold transition-all text-slate-400 hover:text-white hover:bg-slate-800/40 ${
                isActive ? 'bg-slate-800 text-white' : ''
              }`
            }
          >
            <Settings className="h-4.5 w-4.5" />
            <span>Settings</span>
          </NavLink>
          <div className="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold text-slate-400 hover:text-white hover:bg-slate-800/40 cursor-not-allowed">
            <HelpCircle className="h-4.5 w-4.5" />
            <span>Help Guide</span>
          </div>
        </div>
      </div>

      {/* System info footer */}
      <div className="p-4 border-t border-slate-800 bg-slate-950/40">
        <div className="rounded-xl border border-slate-800/60 bg-slate-900/30 p-3">
          <p className="text-[10px] font-bold text-slate-500">SYSTEM ARCHITECTURE</p>
          <div className="flex items-center justify-between mt-1">
            <span className="text-[11px] text-slate-300 font-semibold">Spring Security</span>
            <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-success-glowing text-success border border-success/20">Active</span>
          </div>
          <div className="flex items-center justify-between mt-1">
            <span className="text-[11px] text-slate-300 font-semibold">Nimbus JWT</span>
            <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-primary-glowing text-primary border border-primary/20">HS512</span>
          </div>
          <div className="flex items-center justify-between mt-1">
            <span className="text-[11px] text-slate-300 font-semibold">MQTT Broker</span>
            <span className="text-[9px] text-slate-500 font-mono">QoS 1</span>
          </div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
