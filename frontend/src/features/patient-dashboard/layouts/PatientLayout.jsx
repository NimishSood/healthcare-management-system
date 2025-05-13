// src/features/patient-dashboard/layouts/PatientLayout.jsx
import React from 'react'
import { NavLink, Outlet } from 'react-router-dom'
import {
  HomeIcon,
  CalendarDaysIcon,
  ClipboardDocumentIcon,
  UserCircleIcon
} from '@heroicons/react/24/outline'

export default function PatientLayout() {
  const navItems = [
    { name: 'Home',          path: '/patient',              icon: HomeIcon },
    { name: 'Appointments',  path: '/patient/appointments',  icon: CalendarDaysIcon },
    { name: 'Prescriptions', path: '/patient/prescriptions', icon: ClipboardDocumentIcon },
    { name: 'Profile',       path: '/patient/profile',       icon: UserCircleIcon }
  ]

  return (
    <div className="flex h-screen bg-gray-50">
      <aside className="w-64 bg-white shadow-md">
        <div className="p-4 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-blue-800">Patient Portal</h2>
        </div>
        <nav className="p-4 space-y-2">
          {navItems.map(({ name, path, icon: Icon }) => (
            <NavLink
              key={path}
              to={path}
              className={({ isActive }) =>
                `flex items-center p-3 rounded-lg transition-colors ${
                  isActive ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100 text-gray-700'
                }`
              }
            >
              <Icon className="h-5 w-5 mr-3" />
              {name}
            </NavLink>
          ))}
        </nav>
      </aside>

      <main className="flex-1 overflow-auto p-8">
        <Outlet />
      </main>
    </div>
  )
}
