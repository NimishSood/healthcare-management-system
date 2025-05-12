import React, { createContext, useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

export const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const nav = useNavigate()
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [user,  setUser ] = useState(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })
  const [error, setError] = useState(null)

  // Attach token to all outgoing API calls
  useEffect(() => {
    if (token) axios.defaults.headers.common.Authorization = `Bearer ${token}`
    else delete axios.defaults.headers.common.Authorization
  }, [token])

  const login = async (email, password) => {
    setError(null)
    try {
      const { data } = await axios.post('/api/auth/login', { email, password })
      // assuming response = { token: "...", user: { id, email, role, ... } }
      setToken(data.token)
      setUser(data.user)
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
      nav(`/${data.user.role.toLowerCase()}/inbox`, { replace: true })
    } catch (e) {
      setError(e.response?.data?.message || 'Login failed')
      throw e
    }
  }

  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    nav('/login', { replace: true })
  }

  return (
    <AuthContext.Provider value={{ token, user, error, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}
