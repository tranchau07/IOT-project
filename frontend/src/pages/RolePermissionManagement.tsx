import React, { useEffect, useState } from 'react';
import { roleService, permissionService } from '../services/api';
import { Role, Permission } from '../types';
import { KeyRound, ShieldAlert, Plus, Trash2, Key, Shield, CheckSquare, Square, X } from 'lucide-react';

export const RolePermissionManagement: React.FC = () => {
  const [roles, setRoles] = useState<Role[]>([]);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // New Role Form States
  const [newRoleName, setNewRoleName] = useState('');
  const [newRoleDesc, setNewRoleDesc] = useState('');
  const [selectedPermissions, setSelectedPermissions] = useState<string[]>([]);
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);

  // New Permission Form States
  const [newPermName, setNewPermName] = useState('');
  const [newPermDesc, setNewPermDesc] = useState('');
  const [isPermModalOpen, setIsPermModalOpen] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const roleList = await roleService.getAll();
      const permList = await permissionService.getAll();
      setRoles(roleList);
      setPermissions(permList);
    } catch (err: any) {
      console.error(err);
      setError('Failed to fetch role/permission matrices. Verify admin credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateRole = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newRoleName) return;
    setError(null);

    try {
      await roleService.create({
        name: newRoleName.toUpperCase(),
        description: newRoleDesc,
        permissions: selectedPermissions,
      });
      setIsRoleModalOpen(false);
      setNewRoleName('');
      setNewRoleDesc('');
      setSelectedPermissions([]);
      loadData();
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to create role.');
    }
  };

  const handleCreatePermission = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPermName) return;
    setError(null);

    try {
      await permissionService.create({
        name: newPermName.toUpperCase(),
        description: newPermDesc,
      });
      setIsPermModalOpen(false);
      setNewPermName('');
      setNewPermDesc('');
      loadData();
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to create permission.');
    }
  };

  const handleDeleteRole = async (name: string) => {
    const confirm = window.confirm(`Are you sure you want to delete role ${name}? Active users will lose this role.`);
    if (!confirm) return;

    try {
      await roleService.delete(name);
      loadData();
    } catch (err: any) {
      console.error(err);
      alert('Failed to delete role.');
    }
  };

  const handleDeletePermission = async (name: string) => {
    const confirm = window.confirm(`Are you sure you want to delete permission ${name}?`);
    if (!confirm) return;

    try {
      await permissionService.delete(name);
      loadData();
    } catch (err: any) {
      console.error(err);
      alert('Failed to delete permission.');
    }
  };

  const togglePermSelection = (permName: string) => {
    if (selectedPermissions.includes(permName)) {
      setSelectedPermissions(selectedPermissions.filter(p => p !== permName));
    } else {
      setSelectedPermissions([...selectedPermissions, permName]);
    }
  };

  return (
    <div className="space-y-8 py-6 select-none">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <KeyRound className="h-5.5 w-5.5 text-primary" />
            Roles & Keys Matrices
          </h2>
          <p className="text-xs text-slate-400 font-medium mt-1">Configure fine-grained access policies and credentials</p>
        </div>

        <div className="flex gap-2.5">
          <button
            onClick={() => setIsPermModalOpen(true)}
            className="px-3.5 py-2.5 rounded-xl border border-slate-700 bg-slate-800/40 text-slate-300 hover:bg-slate-800 hover:text-white transition-all text-xs font-semibold flex items-center gap-2 cursor-pointer"
          >
            <Plus className="h-4 w-4" />
            New Key / Permission
          </button>
          <button
            onClick={() => setIsRoleModalOpen(true)}
            className="px-4 py-2.5 rounded-xl bg-primary hover:bg-primary-hover text-white text-xs font-bold shadow-lg shadow-primary-glowing transition-all flex items-center gap-2 cursor-pointer"
          >
            <Plus className="h-4 w-4" />
            New Role
          </button>
        </div>
      </div>

      {error && (
        <div className="p-3.5 rounded-xl border border-danger/30 bg-danger-glowing text-danger text-xs font-semibold flex items-center gap-2">
          <ShieldAlert className="h-4.5 w-4.5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 columns: Roles cards grid */}
        <div className="lg:col-span-2 space-y-4">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider">Registered System Roles</h3>
          
          {isLoading && roles.length === 0 ? (
            <div className="py-12 text-center text-xs text-slate-500 font-medium">Syncing Roles from SQL backend...</div>
          ) : roles.length === 0 ? (
            <div className="glass-panel rounded-2xl p-12 text-center text-xs text-slate-500 italic">No roles mapped.</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {roles.map((role) => (
                <div key={role.name} className="glass-panel rounded-2xl p-5 space-y-4 relative overflow-hidden group">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-2.5">
                      <div className="p-2 rounded-lg bg-primary-glowing text-primary">
                        <Shield className="h-4.5 w-4.5" />
                      </div>
                      <div>
                        <h4 className="font-extrabold text-slate-100">{role.name}</h4>
                        <p className="text-[10px] text-slate-400 font-semibold mt-0.5">{role.description || 'No Description'}</p>
                      </div>
                    </div>

                    <button
                      onClick={() => handleDeleteRole(role.name)}
                      className="p-1 rounded-lg border border-danger/20 text-danger bg-danger-glowing/10 hover:bg-danger hover:text-white transition-all cursor-pointer opacity-0 group-hover:opacity-100"
                      title="Delete Role"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                  </div>

                  <div className="space-y-1.5 border-t border-slate-800/60 pt-4">
                    <span className="text-[9px] font-bold text-slate-500 block uppercase">Permissions / Auth Keys</span>
                    <div className="flex flex-wrap gap-1">
                      {role.permissions.length === 0 ? (
                        <span className="text-[10px] text-slate-500 italic">No permission keys linked.</span>
                      ) : (
                        role.permissions.map(p => (
                          <span key={p.name} className="px-1.5 py-0.5 rounded text-[8px] font-extrabold bg-slate-900 text-slate-400 border border-slate-800">
                            {p.name}
                          </span>
                        ))
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Right 1 column: Permissions directory */}
        <div className="lg:col-span-1 space-y-4">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider">Auth Keys Directory</h3>
          
          <div className="glass-panel rounded-2xl p-5 space-y-4">
            {isLoading && permissions.length === 0 ? (
              <div className="py-6 text-center text-xs text-slate-500">Syncing keys...</div>
            ) : permissions.length === 0 ? (
              <div className="py-6 text-center text-xs text-slate-500 italic">No keys recorded.</div>
            ) : (
              <div className="space-y-3 max-h-[500px] overflow-y-auto pr-1">
                {permissions.map((perm) => (
                  <div key={perm.name} className="flex items-center justify-between p-3 rounded-xl bg-slate-900/30 border border-slate-850/60 group">
                    <div className="flex items-center gap-2.5">
                      <Key className="h-4 w-4 text-primary shrink-0" />
                      <div>
                        <p className="font-extrabold text-[11px] text-slate-200">{perm.name}</p>
                        <p className="text-[9px] text-slate-400 font-semibold">{perm.description || 'No description'}</p>
                      </div>
                    </div>

                    <button
                      onClick={() => handleDeletePermission(perm.name)}
                      className="p-1 rounded-lg border border-danger/20 text-danger bg-danger-glowing/10 hover:bg-danger hover:text-white transition-all cursor-pointer opacity-0 group-hover:opacity-100 shrink-0"
                      title="Delete Permission Key"
                    >
                      <Trash2 className="h-3 w-3" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* MODAL 1: Create Role */}
      {isRoleModalOpen && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="glass-panel rounded-3xl w-full max-w-md shadow-2xl overflow-hidden relative border border-slate-800 animate-in fade-in zoom-in duration-200">
            <div className="flex items-center justify-between px-6 py-4.5 border-b border-slate-800/80">
              <h3 className="font-bold text-slate-100">Create Security Role</h3>
              <button
                onClick={() => setIsRoleModalOpen(false)}
                className="h-8 w-8 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition-all flex items-center justify-center cursor-pointer"
              >
                <X className="h-4.5 w-4.5" />
              </button>
            </div>

            <form onSubmit={handleCreateRole} className="p-6 space-y-4">
              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Role Unique Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. ROLE_OPERATOR"
                  value={newRoleName}
                  onChange={(e) => setNewRoleName(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 text-slate-200 text-sm font-semibold focus:outline-none focus:border-primary"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Description</label>
                <input
                  type="text"
                  placeholder="Classroom operator credentials..."
                  value={newRoleDesc}
                  onChange={(e) => setNewRoleDesc(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 text-slate-200 text-sm font-semibold focus:outline-none focus:border-primary"
                />
              </div>

              {/* Checkboxes list of permissions */}
              <div className="space-y-2 border-t border-slate-850/60 pt-4">
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Link Auth Keys</span>
                <div className="grid grid-cols-2 gap-2 max-h-40 overflow-y-auto pr-1">
                  {permissions.map(p => {
                    const isChecked = selectedPermissions.includes(p.name);
                    return (
                      <div
                        key={p.name}
                        onClick={() => togglePermSelection(p.name)}
                        className={`flex items-center gap-2 p-2 rounded-lg border cursor-pointer transition-all ${
                          isChecked
                            ? 'bg-primary-glowing/10 border-primary/40 text-primary font-bold'
                            : 'bg-slate-900/30 border-slate-850/40 text-slate-400 hover:bg-slate-800/30'
                        }`}
                      >
                        {isChecked ? <CheckSquare className="h-3.5 w-3.5 shrink-0" /> : <Square className="h-3.5 w-3.5 shrink-0" />}
                        <span className="text-[10px] truncate">{p.name}</span>
                      </div>
                    );
                  })}
                </div>
              </div>

              <div className="flex justify-end gap-3 border-t border-slate-800/80 pt-4 mt-4">
                <button
                  type="button"
                  onClick={() => setIsRoleModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-bold border border-slate-700 bg-slate-800/30 text-slate-300 hover:text-white transition-all cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-primary hover:bg-primary-hover text-white text-xs font-bold shadow-lg shadow-primary-glowing transition-all cursor-pointer"
                >
                  Create Role
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL 2: Create Permission */}
      {isPermModalOpen && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="glass-panel rounded-3xl w-full max-w-md shadow-2xl overflow-hidden relative border border-slate-800 animate-in fade-in zoom-in duration-200">
            <div className="flex items-center justify-between px-6 py-4.5 border-b border-slate-800/80">
              <h3 className="font-bold text-slate-100">Create Permission Key</h3>
              <button
                onClick={() => setIsPermModalOpen(false)}
                className="h-8 w-8 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition-all flex items-center justify-center cursor-pointer"
              >
                <X className="h-4.5 w-4.5" />
              </button>
            </div>

            <form onSubmit={handleCreatePermission} className="p-6 space-y-4">
              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Key Code Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. OVERRIDE_POWER"
                  value={newPermName}
                  onChange={(e) => setNewPermName(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 text-slate-200 text-sm font-semibold focus:outline-none focus:border-primary"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Description</label>
                <input
                  type="text"
                  placeholder="Allows manual hardware override controls..."
                  value={newPermDesc}
                  onChange={(e) => setNewPermDesc(e.target.value)}
                  className="w-full px-3.5 py-2 rounded-xl bg-slate-950/60 border border-slate-850 text-slate-200 text-sm font-semibold focus:outline-none focus:border-primary"
                />
              </div>

              <div className="flex justify-end gap-3 border-t border-slate-800/80 pt-4 mt-4">
                <button
                  type="button"
                  onClick={() => setIsPermModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-bold border border-slate-700 bg-slate-800/30 text-slate-300 hover:text-white transition-all cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-primary hover:bg-primary-hover text-white text-xs font-bold shadow-lg shadow-primary-glowing transition-all cursor-pointer"
                >
                  Create Key
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default RolePermissionManagement;
