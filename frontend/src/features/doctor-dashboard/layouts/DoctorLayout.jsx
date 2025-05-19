import React from 'react'
import { NavLink, Outlet } from 'react-router-dom'
import {
  HomeIcon,
  CalendarDaysIcon,
  ClipboardDocumentIcon,
  UserCircleIcon,
  ChatBubbleLeftRightIcon
} from '@heroicons/react/24/outline'

export default function DoctorLayout() {
  const navItems = [
    { name: 'Home', path: '/doctor', icon: HomeIcon },
    { name: 'Appointments', path: '/doctor/appointments', icon: CalendarDaysIcon },
    { name: 'Prescriptions', path: '/doctor/prescriptions', icon: ClipboardDocumentIcon },
    { name: 'Profile', path: '/doctor/profile', icon: UserCircleIcon },
    { name: 'Messages', path: '/doctor/messages', icon: ChatBubbleLeftRightIcon },
  ];

  return (
    <div className="flex h-screen bg-gray-50 dark:bg-gray-900 transition-colors">
      {/* Sidebar */}
      <aside className="w-64 bg-white dark:bg-gray-800 shadow-md transition-colors">
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-xl font-semibold text-blue-800 dark:text-blue-200">
            Doctor Portal
          </h2>
        </div>
        <nav className="p-4 space-y-2">
          {navItems.map(({ name, path, icon: Icon }) => (
            <NavLink
              key={path}
              to={path}
              className={({ isActive }) =>
                `flex items-center p-3 rounded-lg transition-colors ${
                  isActive
                    ? 'bg-blue-100 dark:bg-gray-700 text-blue-700 dark:text-blue-200'
                    : 'hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-200'
                }`
              }
            >
              <Icon className="h-5 w-5 mr-3" />
              {name}
            </NavLink>
          ))}
        </nav>
      </aside>

      {/* Main content area */}
      <main className="flex-1 overflow-auto p-8 bg-gray-50 dark:bg-gray-900 transition-colors">
        <Outlet />
      </main>
    </div>
  );
}
