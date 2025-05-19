// src/features/patient-dashboard/pages/DoctorsPage.jsx
import React, { useEffect, useState } from 'react'
import axios from 'axios'
import toast from 'react-hot-toast'
import { useNavigate } from 'react-router-dom'

export default function DoctorsPage() {
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    axios.get('/patient/doctors')
      .then(res => setDoctors(res.data))
      .catch(err => toast.error('Failed to load doctors'))
      .finally(() => setLoading(false))
  }, [])

  const book = (docId) => {
    // for simplicity, navigate to appointment route with doctor pre-selected
    navigate(`/patient/appointments?doctorId=${docId}`)
  }

  if (loading) return <p>Loading doctors…</p>
  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-semibold">Choose a Doctor</h1>
      <ul className="space-y-4">
        {doctors.map(d => (
          <li key={d.id} className="flex justify-between items-center bg-white p-4 rounded shadow">
            <div>
              <p className="font-medium">{d.firstName} {d.lastName}</p>
              <p className="text-gray-600">{d.specialty}</p>
              <p className="text-sm">{d.email} • {d.phoneNumber}</p>
            </div>
            <button
              onClick={() => book(d.id)}
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
              Book
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}
