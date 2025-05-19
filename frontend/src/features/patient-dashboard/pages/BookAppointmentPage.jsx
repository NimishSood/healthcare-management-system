import React, { useState, useEffect } from 'react'
import axios from 'axios'
import toast from 'react-hot-toast'
import { useNavigate } from 'react-router-dom'
import {
  CalendarIcon,
  XCircleIcon,
} from '@heroicons/react/24/outline'

export default function BookAppointmentPage() {
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState({ doctorId: '', dateTime: '' })
  const [errors, setErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)
  const navigate = useNavigate()

  // Load doctor list
  useEffect(() => {
    axios.get('/patient/doctors')
      .then(res => setDoctors(res.data))
      .catch(err => {
        toast.error('Failed to load doctors')
        console.error(err)
      })
      .finally(() => setLoading(false))
  }, [])

  const handleSubmit = () => {
    const errs = {}
    if (!form.doctorId) errs.doctorId = 'Please select a doctor.'
    if (!form.dateTime) errs.dateTime = 'Please choose date & time.'
    setErrors(errs)
    if (Object.keys(errs).length) return

    setSubmitting(true)
    toast.promise(
      axios.post('/patient/appointments/book', null, {
        params: {
          doctorId: form.doctorId,
          appointmentTime: form.dateTime,
        }
      }),
      {
        loading: 'Booking appointment…',
        success: () => {
          navigate('/patient/appointments', { replace: true })
          return 'Appointment booked!'
        },
        error: err => err.response?.data || 'Failed to book appointment'
      }
    ).finally(() => setSubmitting(false))
  }

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-xl shadow">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold flex items-center">
          <CalendarIcon className="h-5 w-5 mr-2"/> Book Appointment
        </h2>
        <button
          onClick={() => navigate('/patient/appointments')}
          className="text-gray-500 hover:text-gray-700"
        >
          <XCircleIcon className="h-6 w-6"/>
        </button>
      </div>

      {loading ? (
        <p>Loading doctors…</p>
      ) : (
        <div className="space-y-4 text-sm">
          {/* Doctor */}
          <div>
            <label className="block font-medium mb-1">Doctor</label>
            <select
              className="w-full border rounded p-2"
              value={form.doctorId}
              onChange={e => setForm({ ...form, doctorId: e.target.value })}
            >
              <option value="">— Select Doctor —</option>
              {doctors.map(d => (
                <option key={d.id} value={d.id}>
                  {d.firstName} {d.lastName} — {d.specialty}
                </option>
              ))}
            </select>
            {errors.doctorId && (
              <p className="text-red-600 text-xs mt-1">{errors.doctorId}</p>
            )}
          </div>

          {/* Date & Time */}
          <div>
            <label className="block font-medium mb-1">Date &amp; Time</label>
            <input
              type="datetime-local"
              min={new Date().toISOString().slice(0,16)}
              className="w-full border rounded p-2"
              value={form.dateTime}
              onChange={e => setForm({ ...form, dateTime: e.target.value })}
            />
            {errors.dateTime && (
              <p className="text-red-600 text-xs mt-1">{errors.dateTime}</p>
            )}
          </div>

          {/* Actions */}
          <div className="mt-6 flex justify-end space-x-3">
            <button
              onClick={() => navigate('/patient/appointments')}
              disabled={submitting}
              className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300"
            >
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              disabled={submitting}
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
            >
              {submitting ? 'Booking…' : 'Book Appointment'}
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
