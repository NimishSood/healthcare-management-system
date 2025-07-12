import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth }   from '../hooks/useAuth'

export default function PrivateRoute({ children, allowedRoles = [] }) {
  const { token, user } = useAuth()

  if (!token) {
    return <Navigate to="/login" replace />
  }

  if (
    allowedRoles.length > 0 &&
    (!user?.role || !allowedRoles.includes(user.role))
  ) {
    return <Navigate to="/" replace />
  }

  return children
}
