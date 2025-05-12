import React from 'react'
import { Outlet, Link } from 'react-router-dom'

export default function AdminLayout() {
  return (
    <div className="min-h-screen p-4 bg-white">
      <header className="mb-4">
        <nav className="space-x-4">
          <Link to="/admin/dashboard" className="text-purple-600 hover:underline">Dashboard</Link>
        </nav>
      </header>
      <Outlet />
    </div>
  )
}
