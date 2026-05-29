import React, { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import Header from './components/common/Header';
import Sidebar from './components/common/Sidebar';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import ClassroomDetail from './pages/ClassroomDetail';
import UserManagement from './pages/UserManagement';
import RolePermissionManagement from './pages/RolePermissionManagement';

// Protected Route Component to restrict unauthenticated access
const ProtectedRoute: React.FC<{ children: React.ReactNode; requiredRole?: string }> = ({ children, requiredRole }) => {
  const { isAuthenticated, isLoading, roles } = useAuthStore();

  if (isLoading) {
    return (
      <div className="h-screen w-screen flex items-center justify-center bg-background text-slate-500 font-medium">
        Loading secure shell...
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Handle role restrictions if specified
  if (requiredRole) {
    const hasRole = roles.includes(requiredRole) || roles.includes('ADMIN') || roles.includes('ROLE_ADMIN');
    if (!hasRole) {
      return <Navigate to="/" replace />;
    }
  }

  return <>{children}</>;
};

// Layout wrapper for authenticated pages
const AuthenticatedLayout: React.FC = () => {
  return (
    <div className="flex flex-col h-screen overflow-hidden bg-background">
      <Header />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-y-auto px-8 bg-[#080c14]">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/classrooms" element={<Dashboard />} />
            <Route path="/classrooms/:id" element={<ClassroomDetail />} />
            
            {/* Admin only routes matching backend Spring security rules */}
            <Route 
              path="/users" 
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <UserManagement />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/roles" 
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <RolePermissionManagement />
                </ProtectedRoute>
              } 
            />
            
            <Route path="/settings" element={
              <div className="py-12 text-slate-500 italic text-xs">
                Theme and Notification controls managed via local system environment settings.
              </div>
            } />
            
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </div>
  );
};

export const App: React.FC = () => {
  const { initialize } = useAuthStore();

  useEffect(() => {
    initialize();
  }, [initialize]);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        
        {/* All other routes are protected and wrapped in Header/Sidebar Layout */}
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <AuthenticatedLayout />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
