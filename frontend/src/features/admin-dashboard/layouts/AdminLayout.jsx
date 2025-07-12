import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import {
  HomeIcon,
  UsersIcon,
  UserCircleIcon,
  SparklesIcon,
} from '@heroicons/react/24/outline';

export default function AdminLayout() {
  const navItems = [
    { name: 'Dashboard', path: '/admin', icon: HomeIcon },
    { name: 'Users', path: '/admin/users', icon: UsersIcon },
    { name: 'Profile', path: '/admin/profile', icon: UserCircleIcon },
    {
      name: 'AI Features',
      path: '/admin/ai',
      icon: SparklesIcon,
      end: true,
    },
    { name: 'AI Query', path: '/admin/ai/query', icon: SparklesIcon },
  ];

  return (
    <div className="flex h-screen bg-gray-50 dark:bg-gray-900 transition-colors">
      <aside className="w-64 bg-white dark:bg-gray-800 shadow-md transition-colors">
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-xl font-semibold text-blue-800 dark:text-blue-200">Admin Portal</h2>
        </div>
        <nav className="p-4 space-y-2">
           {navItems.map(({ name, path, icon: Icon, end }) => (
            <NavLink
              key={path}
              to={path}
              {...(end ? { end: true } : {})}
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
      <main className="flex-1 overflow-auto p-8 bg-gray-50 dark:bg-gray-900 transition-colors">
        <Outlet />
      </main>
    </div>
  );
}
