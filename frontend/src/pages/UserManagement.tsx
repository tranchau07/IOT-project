import React, { useEffect, useState } from 'react';
import { userService, roleService } from '../services/api';
import { UserResponse, Role } from '../types';
import { Users, Plus, Pencil, Trash2, Shield, X, ShieldAlert } from 'lucide-react';

export const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [roles, setRoles] = useState<Role[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserResponse | null>(null);

  // Form states
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const userList = await userService.getAll();
      const roleList = await roleService.getAll();
      setUsers(userList);
      setRoles(roleList);
    } catch (err: any) {
      console.error(err);
      setError('Failed to fetch account registry. Verify admin rights.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleOpenCreate = () => {
    setEditingUser(null);
    setUsername('');
    setPassword('');
    setPhone('');
    setEmail('');
    setSelectedRoles([]);
    setIsModalOpen(true);
  };

  const handleOpenEdit = (user: UserResponse) => {
    setEditingUser(user);
    setUsername(user.username);
    setPassword(''); // leave blank for no change in editing
    setPhone(user.phone || '');
    setEmail(user.email || '');
    setSelectedRoles(user.roles.map(r => r.name));
    setIsModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || (!editingUser && !password)) {
      setError('Username and password are required.');
      return;
    }

    setError(null);
    const payload = {
      username,
      ...(password ? { password } : {}), // only send password if filled
      phone,
      email,
      roles: selectedRoles,
    };

    try {
      if (editingUser) {
        await userService.update(editingUser.id, payload);
      } else {
        await userService.create(payload);
      }
      setIsModalOpen(false);
      loadData();
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Transaction failed.');
    }
  };

  const handleDelete = async (id: string) => {
    const confirm = window.confirm('Are you sure you want to delete this user? This cannot be undone.');
    if (!confirm) return;

    try {
      await userService.delete(id);
      loadData();
    } catch (err: any) {
      console.error(err);
      alert(err.response?.data?.message || 'Failed to delete user.');
    }
  };

  const handleRoleCheckbox = (roleName: string) => {
    if (selectedRoles.includes(roleName)) {
      setSelectedRoles(selectedRoles.filter(r => r !== roleName));
    } else {
      setSelectedRoles([...selectedRoles, roleName]);
    }
  };

  return (
    <div className="space-y-6 py-6 select-none">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-extrabold tracking-tight text-slate-900 flex items-center gap-2">
            <Users className="h-5.5 w-5.5 text-blue-600" />
            Users Control Deck
          </h2>
          <p className="text-xs text-slate-500 font-semibold mt-1">Manage accounts, configure passwords, and assign RBAC roles</p>
        </div>

        <button
          onClick={handleOpenCreate}
          className="px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-md shadow-blue-500/10 transition-all flex items-center gap-2 cursor-pointer"
        >
          <Plus className="h-4 w-4" />
          Create User
        </button>
      </div>

      {error && !isModalOpen && (
        <div className="p-3.5 rounded-xl border border-rose-200 bg-rose-50 text-rose-700 text-xs font-semibold flex items-center gap-2">
          <ShieldAlert className="h-4.5 w-4.5" />
          <span>{error}</span>
        </div>
      )}

      {/* Users table */}
      <div className="bg-white border border-slate-200/80 rounded-2xl p-5 shadow-sm">
        {isLoading && users.length === 0 ? (
          <div className="py-12 text-center text-xs text-slate-400 font-semibold">
            Fetching user directory from core Relational Database...
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="border-b border-slate-100 text-slate-500 font-semibold h-10 select-none">
                  <th className="pb-3 pr-4">USERNAME</th>
                  <th className="pb-3 pr-4">EMAIL ADDRESS</th>
                  <th className="pb-3 pr-4">CONTACT NUMBER</th>
                  <th className="pb-3 pr-4">ASSIGNED SECURITY ROLES</th>
                  <th className="pb-3 text-right">OPERATIONS</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {users.map((user) => (
                  <tr key={user.id} className="hover:bg-slate-50/50 transition-colors h-14">
                    <td className="pr-4 py-3 font-bold text-slate-800">{user.username}</td>
                    <td className="pr-4 py-3 text-slate-600 font-semibold">{user.email || 'Not Provided'}</td>
                    <td className="pr-4 py-3 text-slate-600 font-semibold">{user.phone || 'Not Provided'}</td>
                    <td className="pr-4 py-3 font-semibold">
                      <div className="flex flex-wrap gap-1.5">
                        {user.roles.map(r => (
                          <span key={r.name} className="inline-flex items-center gap-1 px-2 py-0.5 rounded bg-blue-50 text-blue-600 border border-blue-100 text-[9px] font-bold">
                            <Shield className="h-2.5 w-2.5" />
                            {r.name}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="py-3 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => handleOpenEdit(user)}
                          className="p-1.5 rounded-lg border border-slate-200 bg-white text-slate-500 hover:bg-slate-50 hover:text-slate-800 transition-all cursor-pointer shadow-sm"
                          title="Edit User"
                        >
                          <Pencil className="h-3.5 w-3.5" />
                        </button>
                        <button
                          onClick={() => handleDelete(user.id)}
                          className="p-1.5 rounded-lg border border-rose-200 bg-rose-50 text-rose-600 hover:bg-rose-600 hover:text-white transition-all cursor-pointer shadow-sm"
                          title="Delete User"
                        >
                          <Trash2 className="h-3.5 w-3.5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* CREATE/EDIT MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-md shadow-2xl overflow-hidden relative border border-slate-100 animate-in fade-in zoom-in duration-200">
            <div className="flex items-center justify-between px-6 py-4.5 border-b border-slate-100">
              <h3 className="font-extrabold text-slate-950">{editingUser ? 'Edit User Credentials' : 'Create User Credentials'}</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="h-8 w-8 rounded-lg bg-slate-50 border border-slate-100 text-slate-400 hover:text-slate-900 hover:bg-slate-100 transition-all flex items-center justify-center cursor-pointer"
              >
                <X className="h-4.5 w-4.5" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              {error && (
                <div className="p-3.5 rounded-xl border border-rose-200 bg-rose-50 text-rose-700 text-xs font-semibold">
                  {error}
                </div>
              )}

              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Username *</label>
                <input
                  type="text"
                  required
                  disabled={!!editingUser}
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-white border border-slate-200 text-slate-800 text-sm font-semibold focus:outline-none focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">
                  {editingUser ? 'New Password (leave empty to keep current)' : 'Password *'}
                </label>
                <input
                  type="password"
                  required={!editingUser}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-white border border-slate-200 text-slate-800 text-sm font-semibold focus:outline-none focus:border-blue-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Phone Number</label>
                  <input
                    type="text"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    className="w-full px-3.5 py-2 rounded-xl bg-white border border-slate-200 text-slate-800 text-sm font-semibold focus:outline-none focus:border-blue-500"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Email Address</label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-3.5 py-2 rounded-xl bg-white border border-slate-200 text-slate-800 text-sm font-semibold focus:outline-none focus:border-blue-500"
                  />
                </div>
              </div>

              {/* Roles checkboxes list */}
              <div className="space-y-2 border-t border-slate-100 pt-4">
                <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Assign Roles</span>
                <div className="grid grid-cols-2 gap-2">
                  {roles.map(r => {
                    const isChecked = selectedRoles.includes(r.name);
                    return (
                      <div
                        key={r.name}
                        onClick={() => handleRoleCheckbox(r.name)}
                        className={`flex items-center gap-2 p-2.5 rounded-xl border cursor-pointer select-none transition-all duration-150 ${
                          isChecked
                            ? 'bg-blue-50 border-blue-200 text-blue-600 font-bold'
                            : 'bg-white border-slate-200 text-slate-500 hover:bg-slate-50'
                        }`}
                      >
                        <Shield className="h-4 w-4" />
                        <span className="text-[11px] font-semibold">{r.name}</span>
                      </div>
                    );
                  })}
                </div>
              </div>

              <div className="flex justify-end gap-3 border-t border-slate-100 pt-4 mt-4">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-bold border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 hover:text-slate-900 transition-all cursor-pointer shadow-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-md shadow-blue-500/10 transition-all cursor-pointer"
                >
                  {editingUser ? 'Save Changes' : 'Register User'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserManagement;

