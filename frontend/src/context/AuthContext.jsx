import React, { createContext, useState, useEffect } from 'react'
import authService from '../services/authService'

export const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [user, setUser]   = useState(null)

  useEffect(() => {
    if (token) {
      authService
        .me()
        .then(u => setUser(u))
        .catch(() => logout())
    }
  }, [token])

  function login(email, password) {
    return authService.login(email, password)
      .then(({ token, role }) => {
        setToken(token)
        localStorage.setItem('token', token)
      })
  }

  function logout() {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
  }

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}
