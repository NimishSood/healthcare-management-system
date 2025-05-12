import React from 'react'
import { NavLink } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

export default function Navbar() {
  const { user, logout } = useAuth()

  return (
    <nav className="bg-white shadow px-6 py-4 flex justify-between">
      <div className="space-x-4">
        {user?.role === 'PATIENT' && (
          <>
            <NavLink to="/patient/inbox">Inbox</NavLink>
            <NavLink to="/patient/sent">Sent</NavLink>
            <NavLink to="/patient/book">Book</NavLink>
          </>
        )}
        {/* similarly for doctor/admin/owner */}
      </div>
      <div>
        <span>{user?.firstName}</span>
        <button onClick={logout} className="ml-4 text-red-500">Logout</button>
      </div>
    </nav>
  )
}
