import React from 'react'
import { Outlet, Link } from 'react-router-dom'

export default function OwnerLayout() {
  return (
    <div className="min-h-screen p-4 bg-gray-200">
      <header className="mb-4">
        <nav className="space-x-4">
          <Link to="/owner/profile" className="text-red-600 hover:underline">Profile</Link>
        </nav>
      </header>
      <Outlet />
    </div>
  )
}
