// src/pages/HomePage.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import { HeartIcon, UserCircleIcon, DocumentTextIcon, ClockIcon } from '@heroicons/react/24/outline';

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-teal-50 dark:from-gray-900 dark:to-gray-800">
      {/* Hero Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="text-center">
          <div className="flex justify-center mb-8">
            <div className="bg-white dark:bg-gray-800 p-4 rounded-full shadow-lg">
              <HeartIcon className="h-16 w-16 text-red-600" />
            </div>
          </div>
          
          <h1 className="text-4xl md:text-6xl font-bold text-gray-900 dark:text-gray-100 mb-4">
            Welcome to <span className="text-blue-800">HealthCare</span>Pro
          </h1>
          <p className="text-xl text-gray-600 dark:text-gray-400 mb-8 max-w-3xl mx-auto">
            Your trusted partner in comprehensive healthcare management. 
            Connect with medical professionals, manage appointments, and 
            take control of your health journey.
          </p>

          <div className="flex flex-col sm:flex-row justify-center gap-4 mb-16">
            <Link
              to="/login"
              className="flex items-center justify-center px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg shadow-md transition-all duration-300 transform hover:scale-105"
            >
              <UserCircleIcon className="h-5 w-5 mr-2" />
              Patient Login
            </Link>
            <Link
              to="/register"
              className="flex items-center justify-center px-8 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg shadow-md transition-all duration-300 transform hover:scale-105"
            >
              <DocumentTextIcon className="h-5 w-5 mr-2" />
              New Patient Signup
            </Link>
          </div>

          {/* Quick Features Grid */}
          <div className="grid md:grid-cols-3 gap-8 mb-16">
            <div className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-sm hover:shadow-md transition-shadow">
              <ClockIcon className="h-12 w-12 text-blue-600 mb-4" />
              <h3 className="text-xl font-semibold mb-2">24/7 Access</h3>
              <p className="text-gray-600 dark:text-gray-400">Manage your health records and appointments anytime, anywhere</p>
            </div>
            <div className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-sm hover:shadow-md transition-shadow">
              <HeartIcon className="h-12 w-12 text-red-500 mb-4" />
              <h3 className="text-xl font-semibold mb-2">Expert Care</h3>
              <p className="text-gray-600 dark:text-gray-400">Connect with certified healthcare professionals</p>
            </div>
            <div className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-sm hover:shadow-md transition-shadow">
              <DocumentTextIcon className="h-12 w-12 text-emerald-600 mb-4" />
              <h3 className="text-xl font-semibold mb-2">Digital Records</h3>
              <p className="text-gray-600 dark:text-gray-400">Secure, HIPAA-compliant medical records management</p>
            </div>
          </div>

          {/* Provider Section */}
          <div className="bg-white dark:bg-gray-800 rounded-2xl p-8 shadow-lg">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">Healthcare Providers</h2>
            <div className="flex flex-col sm:flex-row justify-center gap-4">
              <Link
                to="/login?role=doctor"
                className="px-6 py-2 bg-sky-100 dark:bg-gray-700 dark:text-sky-200 hover:bg-sky-200 dark:hover:bg-gray-600 text-sky-800 rounded-lg transition-colors"
              >
                Doctor Portal
              </Link>
              <Link
                to="/login?role=admin"
                className="px-6 py-2 bg-purple-100 dark:bg-gray-700 dark:text-purple-200 hover:bg-purple-200 dark:hover:bg-gray-600 text-purple-800 rounded-lg transition-colors"
              >
                Admin Portal
              </Link>
              <Link
                to="/login?role=staff"
                className="px-6 py-2 bg-amber-100 dark:bg-gray-700 dark:text-amber-200 hover:bg-amber-200 dark:hover:bg-gray-600 text-amber-800 rounded-lg transition-colors"
              >
                Staff Portal
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 mt-20">
        <div className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8 text-center text-sm text-gray-500 dark:text-gray-400">
          <p>© 2024 HealthCarePro. All rights reserved.</p>
          <div className="mt-2">
            <Link to="/privacy" className="hover:text-blue-600 dark:hover:text-blue-400 mx-2">Privacy Policy</Link>
            <Link to="/terms" className="hover:text-blue-600 dark:hover:text-blue-400 mx-2">Terms of Service</Link>
          </div>
        </div>
      </footer>
    </div>
  );
}