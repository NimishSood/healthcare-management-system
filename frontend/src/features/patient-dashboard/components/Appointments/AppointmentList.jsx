// src/features/patient-dashboard/components/Appointments/AppointmentList.jsx

import React, { useState, useEffect } from 'react'
import axios from 'axios'

export function AppointmentList() {
  const [upcoming, setUpcoming] = useState([])
  const [history, setHistory]   = useState([])
  const [loading, setLoading]   = useState(true)

  const [selected, setSelected] = useState(null)  // the appointment to show in modal

  useEffect(() => {
    async function load() {
      try {
        const [upRes, pastRes] = await Promise.all([
          axios.get('/patient/appointments/upcoming'),
          axios.get('/patient/appointments/history'),
        ])
        setUpcoming(upRes.data)
        setHistory(pastRes.data)
      } catch (e) {
        console.error('Failed to load appointments:', e)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  if (loading) return <p>Loading appointments…</p>

  const sections = [
    { title: 'Upcoming Appointments', data: upcoming },
    { title: 'Past Appointments',     data: history  },
  ]

  return (
    <div className="space-y-8">
      {sections.map(({ title, data }) => (
        <div key={title} className="bg-white p-6 rounded-xl shadow-sm">
          <h2 className="text-xl font-semibold mb-4">{title}</h2>
          {data.length ? (
            <ul className="space-y-4">
              {data.map(appt => (
                <li
                  key={appt.id}
                  className="flex justify-between items-center border-b border-gray-100 pb-4 last:border-0"
                >
                  <div>
                    <p className="font-medium">{appt.doctorName}</p>
                    <p className="text-gray-600">{appt.specialty}</p>
                    <p className="text-sm text-gray-500">
                      {new Date(appt.appointmentTime).toLocaleString()}
                    </p>
                  </div>
                  <div className="flex items-center space-x-4">
                    <span className={`px-2 py-1 text-xs rounded-full ${
                      appt.status === 'CONFIRMED'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-amber-100 text-amber-800'
                    }`}>
                      {appt.status}
                    </span>
                    <button
                      onClick={() => setSelected(appt)}
                      className="text-blue-600 hover:underline text-sm"
                    >
                      View Details
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500">No {title.toLowerCase()}.</p>
          )}
        </div>
      ))}

      {/* Modal */}
      {selected && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
          onClick={() => setSelected(null)}
        >
          <div
            className="bg-white rounded-lg p-6 w-11/12 max-w-lg"
            onClick={e => e.stopPropagation()}
          >
            <h3 className="text-2xl font-semibold mb-4">
              Appointment Details
            </h3>
            <dl className="grid grid-cols-1 gap-y-2 text-sm">
              <div>
                <dt className="font-medium">Doctor</dt>
                <dd>{selected.doctorName}</dd>
              </div>
              <div>
                <dt className="font-medium">Specialty</dt>
                <dd>{selected.specialty}</dd>
              </div>
              <div>
                <dt className="font-medium">When</dt>
                <dd>{new Date(selected.appointmentTime).toLocaleString()}</dd>
              </div>
              <div>
                <dt className="font-medium">Booked On</dt>
                <dd>{new Date(selected.createdAt).toLocaleString()}</dd>
              </div>
              <div>
                <dt className="font-medium">Last Updated</dt>
                <dd>{new Date(selected.updatedAt).toLocaleString()}</dd>
              </div>
              <div>
                <dt className="font-medium">Status</dt>
                <dd>{selected.status}</dd>
              </div>
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
