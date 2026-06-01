import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { authService } from '../services/api';
import { KeyRound, User, Radio, ArrowRight, ShieldAlert, Sparkles } from 'lucide-react';

export const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const { login, isAuthenticated } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      setError('Please provide username and password.');
      return;
    }

    setError(null);
    setIsLoading(true);

    try {
      const token = await authService.login(username, password);
      login(token);
      navigate('/');
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Connection failed. Please check backend is running.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-screen flex items-center justify-center bg-slate-50 relative overflow-hidden px-4 select-none">
      <div className="w-full max-w-md relative z-10 space-y-6">
        <div className="text-center space-y-2">
          <div className="inline-flex h-12 w-12 items-center justify-center rounded-2xl bg-blue-600 shadow-md shadow-blue-500/10 border border-blue-500/20 mb-2">
            <Radio className="h-6 w-6 text-white animate-pulse" />
          </div>
          <h1 className="text-xl font-extrabold tracking-tight text-slate-900">Smart Room Control Center</h1>
          <p className="text-xs text-slate-500 font-semibold max-w-xs mx-auto">
            Authorized hardware grid operations. Enter your security credentials.
          </p>
        </div>

        <div className="bg-white border border-slate-200/80 rounded-3xl p-8 shadow-xl shadow-slate-200/40 relative">
          <form onSubmit={handleSubmit} className="space-y-5">
            {error && (
              <div className="p-3.5 rounded-xl border border-rose-250 bg-rose-50 text-rose-700 text-xs font-semibold flex items-center gap-2">
                <ShieldAlert className="h-4.5 w-4.5 shrink-0" />
                <span>{error}</span>
              </div>
            )}

            {/* Username Field */}
            <div className="space-y-1.5">
              <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Username</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 pl-3.5 flex items-center text-slate-400">
                  <User className="h-4.5 w-4.5" />
                </span>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:border-blue-500 text-slate-800 text-sm font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-blue-500/10"
                  placeholder="e.g. admin"
                  disabled={isLoading}
                />
              </div>
            </div>

            {/* Password Field */}
            <div className="space-y-1.5">
              <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">Password</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 pl-3.5 flex items-center text-slate-400">
                  <KeyRound className="h-4.5 w-4.5" />
                </span>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-white border border-slate-200 focus:border-blue-500 text-slate-800 text-sm font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-blue-500/10"
                  placeholder="••••••••"
                  disabled={isLoading}
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-3 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs shadow-md shadow-blue-500/10 transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50 disabled:pointer-events-none"
            >
              <span>{isLoading ? 'Verifying Credentials...' : 'Authenticate'}</span>
              {!isLoading && <ArrowRight className="h-4.5 w-4.5" />}
            </button>
          </form>
        </div>

        {/* Demo credentials notification */}
        <div className="bg-blue-50/50 border border-blue-100/60 rounded-2xl p-4.5 text-left relative overflow-hidden">
          <div className="absolute top-0 right-0 p-2">
            <Sparkles className="h-3.5 w-3.5 text-blue-500 opacity-60 animate-pulse" />
          </div>
          <p className="text-[10px] font-bold text-blue-600 uppercase">DEVELOPER SANDBOX SEED</p>
          <p className="text-[11px] text-slate-500 mt-1 font-semibold leading-relaxed">
            If no accounts exist, the backend auto-seeds the system. You can connect using:
            <br />
            <span className="text-slate-800 font-bold">Username:</span> <code className="bg-blue-100/50 px-1 py-0.5 rounded text-blue-750 font-bold text-[10px]">admin</code>
            <span className="mx-2">|</span>
            <span className="text-slate-800 font-bold">Password:</span> <code className="bg-blue-100/50 px-1 py-0.5 rounded text-blue-750 font-bold text-[10px]">admin</code>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;

