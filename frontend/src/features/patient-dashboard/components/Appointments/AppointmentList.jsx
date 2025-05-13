// src/features/patient-dashboard/components/Appointments/AppointmentList.jsx
import React, { useState, useEffect } from 'react'
import axios from 'axios'

export function AppointmentList() {
  const [appointments, setAppointments] = useState([])

  useEffect(() => {
    // API call to fetch appointments
    axios.get('/patient/appointments/upcoming')
      .then(res => setAppointments(res.data))
      .catch(err => {
        console.error('Failed to load appointments:', err)
      })
  }, [])

  return (
    <div className="bg-white p-6 rounded-xl shadow-sm">
      <h2 className="text-xl font-semibold mb-4">Upcoming Appointments</h2>
      <div className="space-y-4">
        {appointments.map(appt => (
          <div key={appt.id} className="border-b border-gray-100 pb-4 last:border-0">
            <div className="flex justify-between">
              <div>
                <p className="font-medium">{appt.doctorName}</p>
                <p className="text-gray-600">{appt.specialty}</p>
              </div>
              <div className="text-right">
                <p className="font-medium">{new Date(appt.date).toLocaleString()}</p>
                <p className={`text-sm ${
                  appt.status === 'confirmed' ? 'text-green-600' : 'text-amber-600'
                }`}>
                  {appt.status}
                </p>
              </div>
            </div>
            <button className="mt-2 text-sm text-blue-600 hover:text-blue-800">
              View Details
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
