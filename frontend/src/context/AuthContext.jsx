import React, { createContext, useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

// 1. Create context first
export const AuthContext = createContext(null);

// 2. Create provider component
export function AuthProvider({ children }) {
  const nav = useNavigate();
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // Centralized auth response handler
  const handleAuthResponse = useCallback((data) => {
    const userData = {
      id: data.user?.id || data.userId,
      email: data.user?.email || data.email,
      role: data.role,
      firstName: data.user?.firstName || data.firstName,
      lastName: data.user?.lastName || data.lastName
    };

    setToken(data.token);
    setUser(userData);
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(userData));
    
    const dashboardPath = `/${data.role.toLowerCase()}-dashboard`;
    nav(dashboardPath, { replace: true });
  }, [nav]);

  // Session verification on mount
  useEffect(() => {
    const verifySession = async () => {
      try {
        const storedToken = localStorage.getItem('token');
        if (!storedToken) {
          setIsLoading(false);
          return;
        }

        const { data } = await axios.get('/api/auth/verify', {
          headers: { Authorization: `Bearer ${storedToken}` }
        });

        handleAuthResponse(data);
      } catch (error) {
        logout();
      } finally {
        setIsLoading(false);
      }
    };

    verifySession();
  }, [handleAuthResponse]);

  // Attach token to axios headers
  useEffect(() => {
    if (token) {
      axios.defaults.headers.common.Authorization = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common.Authorization;
    }
  }, [token]);

  const login = async (email, password) => {
    setError(null);
    try {
      const { data } = await axios.post('/api/auth/login', { email, password });
      handleAuthResponse(data);
    } catch (e) {
      setError(e.response?.data?.message || 'Login failed');
      throw e;
    }
  };

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    nav('/login', { replace: true });
  }, [nav]);

  const value = {
    token,
    user,
    error,
    isLoading,
    login,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {!isLoading && children}
    </AuthContext.Provider>
  );
}