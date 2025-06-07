import React, { useEffect, useState } from 'react'
import { format } from 'date-fns'
import { Tabs } from '../../../patient-dashboard/components/Appointments/Tabs'
import {
  getUpcomingAppointments,
  getPastAppointments,
  markAppointmentComplete,
  cancelAppointment
} from '../../../../services/doctorService'

export default function DoctorAppointmentList() {
  const [upcoming, setUpcoming] = useState([])
  const [past, setPast] = useState([])
  const [loading, setLoading] = useState(true)
  const [activeTab, setActiveTab] = useState('upcoming')
  const [markingId, setMarkingId] = useState(null)
  const [selected, setSelected] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const [upRes, pastRes] = await Promise.all([
        getUpcomingAppointments(),
        getPastAppointments()
      ])
      setUpcoming(upRes)
      setPast(pastRes)
    } catch (err) {
      console.error('Failed to load appointments', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const handleComplete = async (id) => {
    setMarkingId(id)
    try {
      await markAppointmentComplete(id)
      await load()
    } catch (err) {
      console.error('Failed to mark complete', err)
    } finally {
      setMarkingId(null)
    }
  }

  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) return
    try {
      await cancelAppointment(id)
      await load()
    } catch (err) {
      console.error('Failed to cancel appointment', err)
    }
  }

  const statusClass = status => {
    switch (status) {
      case 'CANCELLED': return 'bg-red-100 text-red-800'
      case 'COMPLETED':
      case 'CONFIRMED': return 'bg-green-100 text-green-800'
      default:          return 'bg-amber-100 text-amber-800'
    }
  }

  const tabs = [
    { id: 'upcoming', label: 'Upcoming', count: upcoming.length },
    { id: 'past', label: 'Past', count: past.length }
  ]

  const sections = {
    upcoming,
    past
  }

  if (loading) {
    return (
      <div className="text-center py-10">
        <svg className="animate-spin h-8 w-8 mx-auto text-gray-500" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
        </svg>
        <p className="mt-2 text-gray-600">Loading appointments…</p>
      </div>
    )
  }

  return (
    <div className="space-y-6 p-6">
      <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
      <div className="bg-white p-6 rounded-xl shadow-sm">
        <h2 className="text-xl font-semibold mb-4">
          {tabs.find(t => t.id === activeTab).label} Appointments
        </h2>
        {sections[activeTab].length > 0 ? (
          <ul className="space-y-4">
            {sections[activeTab].map(appt => (
              <li key={appt.id} className="flex justify-between items-center border-b pb-4 last:border-none">
                <div>
                  <p className="font-medium">{appt.patientName || `${appt.patient?.firstName} ${appt.patient?.lastName}`}</p>
                  <p className="text-sm text-gray-500">{format(new Date(appt.appointmentTime), 'PPpp')}</p>
                </div>
                <div className="flex items-center space-x-4">
                  <span className={`px-2 py-1 text-xs rounded-full ${statusClass(appt.status)}`}>{appt.status}</span>
                  <button
                    onClick={() => setSelected(appt)}
                    className="text-blue-600 hover:underline text-sm"
                  >
                    View Details
                  </button>
                  {appt.status !== 'COMPLETED' && (
                    <button
                      onClick={() => handleComplete(appt.id)}
                      disabled={markingId === appt.id}
                      className="text-green-600 hover:underline text-sm disabled:opacity-50"
                    >
                      {markingId === appt.id ? 'Marking…' : 'Mark Complete'}
                    </button>
                  )}
                  {appt.status === 'BOOKED' && (
                    <button
                      onClick={() => handleCancel(appt.id)}
                      className="text-red-600 hover:underline text-sm"
                    >
                      Cancel
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
              ×
            </button>
            <h3 className="text-2xl font-semibold mb-4">Appointment Details</h3>
            <dl className="grid grid-cols-1 gap-y-3 text-sm">
              <div><dt className="font-medium">Patient</dt><dd>{selected.patientName || `${selected.patient?.firstName} ${selected.patient?.lastName}`}</dd></div>
              <div><dt className="font-medium">When</dt><dd>{format(new Date(selected.appointmentTime), 'PPpp')}</dd></div>
              <div><dt className="font-medium">Status</dt><dd>{selected.status}</dd></div>
              {selected.location && <div><dt className="font-medium">Location</dt><dd>{selected.location}</dd></div>}
              {selected.notes && <div><dt className="font-medium">Notes</dt><dd>{selected.notes}</dd></div>}
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