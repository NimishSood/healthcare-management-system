import React from 'react'
import { Outlet, Link } from 'react-router-dom'

export default function DoctorLayout() {
  return (
    <div className="min-h-screen p-4 bg-gray-50">
      <header className="mb-4">
        <nav className="space-x-4">
          <Link to="/doctor/inbox" className="text-green-600 hover:underline">Doctor Inbox</Link>
        </nav>
      </header>
      <Outlet />
    </div>
  )
}
