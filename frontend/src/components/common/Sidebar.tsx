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
     <aside className="w-64 border-r border-slate-200/80 bg-white flex flex-col justify-between shrink-0 select-none">
       <div className="flex-1 py-6 px-4 space-y-6">
         <div className="space-y-1">
           <p className="px-3 text-[10px] font-bold text-slate-400 tracking-wider uppercase">MAIN MENU</p>
           <nav className="space-y-1.5">
             {navItems
               .filter((item) => !item.roles || item.roles.some((r) => roles.includes(r) || (r === 'ADMIN' && isAdmin)))
               .map((item) => (
                 <NavLink
                   key={item.to}
                   to={item.to}
                   className={({ isActive }) =>
                     `flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-bold transition-all group ${
                       isActive
                         ? 'bg-blue-50 text-blue-600 shadow-sm shadow-blue-500/5'
                         : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                     }`
                   }
                 >
                   {({ isActive }) => (
                     <>
                       <item.icon className={`h-4.5 w-4.5 shrink-0 ${isActive ? 'text-blue-600' : 'text-slate-400 group-hover:text-blue-600'}`} />
                       <span>{item.label}</span>
                     </>
                   )}
                 </NavLink>
               ))}
           </nav>
         </div>
 
         <div className="border-t border-slate-100 pt-6 space-y-1">
           <p className="px-3 text-[10px] font-bold text-slate-400 tracking-wider uppercase">PREFERENCES</p>
           <NavLink
             to="/settings"
             className={({ isActive }) =>
               `flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-bold transition-all ${
                 isActive ? 'bg-slate-100 text-slate-800' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
               }`
             }
           >
             <Settings className="h-4.5 w-4.5 text-slate-400" />
             <span>Settings</span>
           </NavLink>
           <div className="flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-bold text-slate-400 hover:text-slate-950 hover:bg-slate-50 cursor-not-allowed">
             <HelpCircle className="h-4.5 w-4.5 text-slate-350" />
             <span>Help Guide</span>
           </div>
         </div>
       </div>
 
       {/* System info footer - minimalized for clean professional product evaluation */}
       <div className="p-4 border-t border-slate-100 bg-slate-50/50 text-center">
         <p className="text-[10px] text-slate-400 font-bold tracking-wider uppercase">HEMS Smart Room</p>
         <p className="text-[9px] text-slate-400 mt-0.5 font-medium">Core Version 1.0.0</p>
       </div>
     </aside>
   );
 };
 
 export default Sidebar;

