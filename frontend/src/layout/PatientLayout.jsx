import React from 'react'
import { Outlet, Link } from 'react-router-dom'

export default function PatientLayout() {
  return (
    <div className="min-h-screen p-4 bg-gray-100">
      <header className="mb-4">
        <nav className="space-x-4">
          <Link to="/patient/inbox" className="text-blue-600 hover:underline">Inbox</Link>
          <button onClick={() => {/* later: call logout */}}>Log Out</button>
        </nav>
      </header>
      <main>
        {/* nested pages will render here */}
        <Outlet />
      </main>
    </div>
  )
}
