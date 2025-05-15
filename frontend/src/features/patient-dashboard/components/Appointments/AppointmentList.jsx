// src/features/patient-dashboard/components/Appointments/AppointmentList.jsx
import React, { useState, useEffect } from 'react'
import axios from 'axios'
import toast from 'react-hot-toast'
import { format } from 'date-fns'
import { Link } from 'react-router-dom'
import {
  XCircleIcon,
  PlusCircleIcon,
} from '@heroicons/react/24/outline'
import { Tabs } from './Tabs'  // <-- adjust import path if needed

export function AppointmentList() {
  // — Data states —
  const [upcoming,   setUpcoming]   = useState([])
  const [cancelled,  setCancelled]  = useState([])
  const [history,    setHistory]    = useState([])
  const [loading,    setLoading]    = useState(true)

  // — UI states —
  const [activeTab,     setActiveTab]     = useState('upcoming')
  const [selected,      setSelected]      = useState(null)
  const [cancellingId,  setCancellingId]  = useState(null)

  // — Fetch all three lists, always use this for updates! —
  const load = async () => {
    setLoading(true)
    try {
      const [upRes, pastRes, cancelledRes] = await Promise.all([
        axios.get('/patient/appointments/upcoming'),
        axios.get('/patient/appointments/history'),
        axios.get('/patient/appointments/cancelled'),
      ])
      setUpcoming(upRes.data.filter(a => a.status !== 'CANCELLED'))
      setHistory(pastRes.data.filter(a => a.status !== 'CANCELLED'))
      setCancelled(cancelledRes.data)
    } catch (err) {
      toast.error('Failed to load appointments')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  // — On mount: load all data
  useEffect(() => {
    load()
  }, [])

  // — Cancel handler —
  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) return
    setCancellingId(id)
    try {
      await axios.delete('/patient/appointments/cancel', { params: { appointmentId: id } })
      toast.success('Appointment cancelled.')
      await load() // Always reload ALL data after cancellation
    } catch (err) {
      console.error('Cancel failed:', err)
      toast.error(
        err.response?.data?.message ||
        err.response?.data ||
        err.message ||
        'Failed to cancel'
      )
    } finally {
      setCancellingId(null)
    }
  }

  // — Status badge classes —
  const statusClass = status => {
    switch (status) {
      case 'CANCELLED': return 'bg-red-100 text-red-800'
      case 'COMPLETED':
      case 'CONFIRMED': return 'bg-green-100 text-green-800'
      default:          return 'bg-amber-100 text-amber-800'
    }
  }

  // — Tabs config —
  const tabs = [
    { id: 'upcoming',  label: 'Upcoming',   count: upcoming.length },
    { id: 'cancelled', label: 'Cancelled',  count: cancelled.length },
    { id: 'past',      label: 'Past',       count: history.length },
  ]

  const sections = {
    upcoming:  { data: upcoming,  allowCancel: true  },
    cancelled: { data: cancelled, allowCancel: false },
    past:      { data: history,   allowCancel: false },
  }

  // — Loading spinner —
  if (loading) {
    return (
      <div className="text-center py-10">
        <svg className="animate-spin h-8 w-8 mx-auto text-gray-500" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
        </svg>
        <p className="mt-2 text-gray-600">Loading appointments…</p>
      </div>
    )
  }

  return (
    <div className="space-y-6 p-6">
      {/* Book New */}
      <div className="flex justify-end">
        <Link
          to="/patient/appointments/book"
          className="flex items-center bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          <PlusCircleIcon className="h-5 w-5 mr-2"/> Book New
        </Link>
      </div>

      {/* Tabs */}
      <Tabs
        tabs={tabs}
        activeTab={activeTab}
        onChange={setActiveTab}
      />

      {/* Current Section */}
      <div className="bg-white p-6 rounded-xl shadow-sm">
        <h2 className="text-xl font-semibold mb-4">
          {tabs.find(t => t.id === activeTab).label} Appointments
        </h2>
        {sections[activeTab].data.length > 0 ? (
          <ul className="space-y-4">
            {sections[activeTab].data.map(appt => (
              <li key={appt.id} className="flex justify-between items-center border-b pb-4 last:border-none">
                <div>
                  <p className="font-medium">{appt.doctorName}</p>
                  <p className="text-gray-600">{appt.specialty}</p>
                  <p className="text-sm text-gray-500">
                    {format(new Date(appt.appointmentTime), 'PPpp')}
                  </p>
                </div>
                <div className="flex items-center space-x-4">
                  <span className={`px-2 py-1 text-xs rounded-full ${statusClass(appt.status)}`}>
                    {appt.status}
                  </span>
                  <button
                    onClick={() => setSelected(appt)}
                    className="text-blue-600 hover:underline text-sm"
                  >
                    View Details
                  </button>
                  {sections[activeTab].allowCancel && appt.status === 'BOOKED' && (
                    <button
                      onClick={() => handleCancel(appt.id)}
                      disabled={cancellingId === appt.id}
                      className="text-red-600 hover:underline text-sm disabled:opacity-50"
                    >
                      {cancellingId === appt.id ? 'Cancelling…' : 'Cancel'}
                    </button>
                  )}
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-gray-500">No {tabs.find(t => t.id === activeTab).label.toLowerCase()}.</p>
        )}
      </div>

      {/* Detail Modal */}
      {selected && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
          onClick={() => setSelected(null)}
        >
          <div
            className="bg-white rounded-lg p-6 w-11/12 max-w-lg relative"
            onClick={e => e.stopPropagation()}
          >
            <button
              onClick={() => setSelected(null)}
              className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
            >
              <XCircleIcon className="h-6 w-6"/>
            </button>
            <h3 className="text-2xl font-semibold mb-4">Appointment Details</h3>
            <dl className="grid grid-cols-1 gap-y-3 text-sm">
              <div><dt className="font-medium">Doctor</dt><dd>{selected.doctorName}</dd></div>
              <div><dt className="font-medium">Specialty</dt><dd>{selected.specialty}</dd></div>
              <div><dt className="font-medium">Contact</dt><dd>{selected.doctorContact}</dd></div>
              <div><dt className="font-medium">When</dt><dd>{format(new Date(selected.appointmentTime), 'PPpp')}</dd></div>
              <div><dt className="font-medium">Booked On</dt><dd>{format(new Date(selected.createdAt), 'PPpp')}</dd></div>
              <div><dt className="font-medium">Status</dt><dd>{selected.status}</dd></div>
              {selected.location && <div><dt className="font-medium">Location</dt><dd>{selected.location}</dd></div>}
              {selected.notes    && <div><dt className="font-medium">Notes</dt><dd>{selected.notes}</dd></div>}
            </dl>
            <div className="mt-6 text-right">
              <button
                onClick={() => setSelected(null)}
                className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
