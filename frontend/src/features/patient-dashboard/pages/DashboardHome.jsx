// src/features/patient-dashboard/pages/DashboardHome.jsx
import React from 'react'
import { useAuth } from '../../../context/AuthContext'
import { ClockIcon, BellIcon, CalendarDaysIcon } from '@heroicons/react/24/outline'
import { AppointmentList } from '../components/Appointments/AppointmentList'

export default function DashboardHome() {
  const { user } = useAuth()

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="bg-white p-6 rounded-xl shadow-sm">
        <h1 className="text-2xl font-bold text-gray-800">
          Welcome back, {user?.firstName}!
        </h1>
        <p className="text-gray-600 mt-2">
          Here's what's happening with your healthcare today
        </p>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard
          icon={ClockIcon}
          title="Next Appointment"
          value="Tomorrow, 10:30 AM"
          color="bg-blue-100 text-blue-700"
        />
        <StatCard
          icon={BellIcon}
          title="Pending Prescriptions"
          value="2 Renewals Needed"
          color="bg-amber-100 text-amber-700"
        />
        <StatCard
          icon={CalendarDaysIcon}
          title="Recent Visits"
          value="5 in last 6 months"
          color="bg-green-100 text-green-700"
        />
      </div>

      {/* Upcoming Appointments Section */}
      <AppointmentList />
    </div>
  )
}

// Reusable Stat Card Component
const StatCard = ({ icon: Icon, title, value, color }) => (
  <div className={`p-5 rounded-xl ${color.split(' ')[0]} ${color.split(' ')[1]}`}>
    <div className="flex items-center">
      <Icon className="h-8 w-8 mr-3" />
      <div>
        <p className="text-sm font-medium">{title}</p>
        <p className="text-xl font-semibold">{value}</p>
      </div>
    </div>
  </div>
)
