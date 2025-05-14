// src/features/patient-dashboard/pages/DashboardHome.jsx
import React, { useEffect, useState } from 'react'
import { useAuth } from '../../../context/AuthContext'
import { ClockIcon, BellIcon, CalendarDaysIcon } from '@heroicons/react/24/outline'
import { AppointmentList } from '../components/Appointments/AppointmentList'
import axios from 'axios'
import { format } from 'date-fns'

export default function DashboardHome() {
  const { user } = useAuth()

  // STAT STATES
  const [nextAppt, setNextAppt] = useState(null)
  const [pendingCount, setPendingCount] = useState(0)
  const [recentVisits, setRecentVisits] = useState(0)
  const [statsLoading, setStatsLoading] = useState(true)

  useEffect(() => {
    async function loadStats() {
      try {
        // 1) Next appointment (limit=1)
        const upRes = await axios.get('/patient/appointments/upcoming', { params: { limit: 1 } })
        setNextAppt(upRes.data[0] || null)

        // 2) Pending prescriptions count
        const presRes = await axios.get('/patient/prescriptions/pending/count')
        // if your API returns { count: X }, use presRes.data.count
        setPendingCount(typeof presRes.data === 'number' ? presRes.data : presRes.data.count)

        // 3) Recent visits (past 6 months)
        const sinceDate = new Date()
        sinceDate.setMonth(sinceDate.getMonth() - 6)
        const histRes = await axios.get('/patient/appointments/history', {
          params: { since: sinceDate.toISOString() }
        })
        setRecentVisits(histRes.data.length)
      } catch (err) {
        console.error('Error loading dashboard stats', err)
      } finally {
        setStatsLoading(false)
      }
    }
    loadStats()
  }, [])

  const stats = [
    {
      icon: ClockIcon,
      title: 'Next Appointment',
      value: statsLoading
        ? 'Loading…'
        : nextAppt
          ? format(new Date(nextAppt.appointmentTime), 'MMMM d, yyyy, h:mm a')
          : 'None scheduled',
      color: 'bg-blue-100 text-blue-700'
    },
    {
      icon: BellIcon,
      title: 'Pending Prescriptions',
      value: statsLoading
        ? 'Loading…'
        : `${pendingCount} Renewal${pendingCount === 1 ? '' : 's'} Needed`,
      color: 'bg-amber-100 text-amber-700'
    },
    {
      icon: CalendarDaysIcon,
      title: 'Recent Visits',
      value: statsLoading
        ? 'Loading…'
        : `${recentVisits} in last 6 months`,
      color: 'bg-green-100 text-green-700'
    },
  ]

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

      {/* Dynamic Stat Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {stats.map(({ icon: Icon, title, value, color }) => (
          <StatCard
            key={title}
            icon={Icon}
            title={title}
            value={value}
            color={color}
          />
        ))}
      </div>

      {/* Reuse our AppointmentList for the rest */}
      <AppointmentList />
    </div>
  )
}

// Reusable Stat Card
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
