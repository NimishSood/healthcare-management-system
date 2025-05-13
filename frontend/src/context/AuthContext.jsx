// src/context/AuthContext.jsx

import axios from 'axios'
import React, {
  createContext,
  useState,
  useEffect,
  useCallback,
  useContext
} from 'react'
import { useNavigate } from 'react-router-dom'

// === 0) Point axios at your Spring Boot API ===
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.headers.common['Authorization'] = `Bearer ${localStorage.getItem('token')}`

// === 1) Create context ===
export const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const nav = useNavigate()

  // preserve token & user from localStorage
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [user, setUser]   = useState(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })
  const [error, setError]       = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  // === 2) logout() must come _before_ the effect below ===
  const logout = useCallback(() => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    delete axios.defaults.headers.common.Authorization
    nav('/login', { replace: true })
  }, [nav])

  // === 3) Handle a successful login ===
  const handleAuthResponse = useCallback((data) => {
    const userData = {
      id:        data.user?.id || data.userId,
      email:     data.user?.email || data.email,
      role:      data.role,
      firstName: data.user?.firstName || data.firstName,
      lastName:  data.user?.lastName  || data.lastName
    }

    setToken(data.token)
    setUser(userData)
    localStorage.setItem('token', data.token)
    localStorage.setItem('user',  JSON.stringify(userData))
    axios.defaults.headers.common.Authorization = `Bearer ${data.token}`

    const base = userData.role.toLowerCase()
    nav(`/${base}`, { replace: true })
  }, [nav])

  // === 4) Verify session on mount (no redirect) ===
  useEffect(() => {
    const verifySession = async () => {
      try {
        const storedToken = localStorage.getItem('token')
        if (!storedToken) {
          setIsLoading(false)
          return
        }
        const { data } = await axios.get('/api/auth/verify', {
          headers: { Authorization: `Bearer ${storedToken}` }
        })

        const userData = {
          id:        data.user?.id || data.userId,
          email:     data.user?.email || data.email,
          role:      data.role,
          firstName: data.user?.firstName || data.firstName,
          lastName:  data.user?.lastName  || data.lastName
        }
        setToken(data.token)
        setUser(userData)
        localStorage.setItem('token', data.token)
        localStorage.setItem('user',  JSON.stringify(userData))
        axios.defaults.headers.common.Authorization = `Bearer ${data.token}`

      } catch (err) {
        logout()
      } finally {
        setIsLoading(false)
      }
    }

    verifySession()
  }, [logout])

  // === 5) login() wrapper ===
  const login = async (email, password) => {
    setError(null)
    try {
      const { data } = await axios.post('/api/auth/login', { email, password })
      handleAuthResponse(data)
    } catch (e) {
      setError(e.response?.data?.message || 'Login failed')
      throw e
    }
  }

  const value = { token, user, error, isLoading, login, logout }

  return (
    <AuthContext.Provider value={value}>
      {!isLoading && children}
    </AuthContext.Provider>
  )
}

// === 6) Convenience hook ===
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
