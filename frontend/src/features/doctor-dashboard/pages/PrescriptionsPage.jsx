import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { Link } from 'react-router-dom'
import {
  getPendingRefillRequests,
  respondToRefill,
  getPatients,
  issuePrescription,
  getPrescriptions
} from '../../../services/doctorService'

export default function DoctorPrescriptionsPage() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [patients, setPatients] = useState([])
  const [prescriptions, setPrescriptions] = useState([])
  const [prescLoading, setPrescLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ patientId: '', medicationName: '', dosage: '', instructions: '', refillsLeft: 0 })
  const [saving, setSaving] = useState(false)

  const loadRequests = () => {
    setLoading(true)
    getPendingRefillRequests()
      .then(setRequests)
      .catch(() => toast.error('Failed to load requests'))
      .finally(() => setLoading(false))
  }
  const loadPrescriptions = () => {
    setPrescLoading(true)
    getPrescriptions()
      .then(setPrescriptions)
      .catch(() => toast.error('Failed to load prescriptions'))
      .finally(() => setPrescLoading(false))
  }

  useEffect(() => {
    loadRequests()
    getPatients().then(setPatients).catch(() => {})
    loadPrescriptions()
  }, [])

  const handleAction = (id, approve) => {
    respondToRefill(id, approve)
      .then(() => {
        toast.success(approve ? 'Refill approved' : 'Refill denied')
        loadRequests()
      })
      .catch(() => toast.error('Action failed'))
  }

  const handleCreate = async () => {
    if (!form.patientId || !form.medicationName || !form.dosage) {
      toast.error('Please fill all required fields')
      return
    }
    setSaving(true)
    try {
      await issuePrescription({
        patientId: form.patientId,
        appointmentId: null,
        medicationName: form.medicationName,
        dosage: form.dosage,
        instructions: form.instructions,
        refillsLeft: Number(form.refillsLeft)
      })
      toast.success('Prescription issued')
      setShowForm(false)
      setForm({ patientId: '', medicationName: '', dosage: '', instructions: '', refillsLeft: 0 })
    } catch (err) {
      toast.error('Failed to issue prescription')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-8 p-6">
      <h1 className="text-2xl font-semibold">Prescriptions</h1>

      <section>
        <h2 className="text-xl font-semibold mb-2">Pending Refill Requests</h2>
        {loading ? (
          <p>Loading…</p>
        ) : requests.length ? (
          <ul className="space-y-4">
            {requests.map(r => (
              <li key={r.id} className="bg-white p-4 rounded shadow">
                <p className="font-medium">
                  {r.medicationName} — {r.patient?.firstName} {r.patient?.lastName}
                </p>
                <p className="text-sm text-gray-500">Refills left: {r.refillsLeft}</p>
                <div className="mt-2 space-x-3">
                  <button onClick={() => handleAction(r.id, true)} className="px-3 py-1 bg-green-600 text-white rounded">
                    Approve
                  </button>
                  <button onClick={() => handleAction(r.id, false)} className="px-3 py-1 bg-red-600 text-white rounded">
                    Deny
                  </button>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-gray-500">No pending requests.</p>
        )}
      </section>

      <section>
        <div className="flex items-center justify-between mb-2">
          <h2 className="text-xl font-semibold">Issue New Prescription</h2>
          <button onClick={() => setShowForm(v => !v)} className="px-4 py-2 bg-blue-600 text-white rounded">
            {showForm ? 'Close' : 'New Prescription'}
          </button>
        </div>
        {showForm && (
          <div className="space-y-3 bg-white p-4 rounded shadow">
            <select
              className="border p-2 w-full"
              value={form.patientId}
              onChange={e => setForm({ ...form, patientId: e.target.value })}
            >
              <option value="">Select patient</option>
              {patients.map(p => (
                <option key={p.id} value={p.id}>
                  {p.firstName} {p.lastName}
                </option>
              ))}
            </select>
            <input
              type="text"
              placeholder="Medication name"
              className="border p-2 w-full"
              value={form.medicationName}
              onChange={e => setForm({ ...form, medicationName: e.target.value })}
            />
            <input
              type="text"
              placeholder="Dosage"
              className="border p-2 w-full"
              value={form.dosage}
              onChange={e => setForm({ ...form, dosage: e.target.value })}
            />
            <textarea
              placeholder="Instructions"
              className="border p-2 w-full"
              value={form.instructions}
              onChange={e => setForm({ ...form, instructions: e.target.value })}
            />
            <input
              type="number"
              placeholder="Refills"
              className="border p-2 w-full"
              value={form.refillsLeft}
              onChange={e => setForm({ ...form, refillsLeft: e.target.value })}
            />
            <button
              onClick={handleCreate}
              disabled={saving}
              className="px-4 py-2 bg-blue-600 text-white rounded"
            >
              {saving ? 'Saving…' : 'Submit'}
            </button>
          </div>
        )}
        
        <section>
        <h2 className="text-xl font-semibold mb-2">Issued Prescriptions</h2>
        {prescLoading ? (
          <p>Loading…</p>
        ) : prescriptions.length ? (
          <ul className="space-y-4">
            {prescriptions
              .slice()
              .sort((a, b) => new Date(b.dateIssued) - new Date(a.dateIssued))
              .map(p => (
                <li key={p.id}>
                  <Link
                    to={`/doctor/prescriptions/${p.id}`}
                    className="block bg-white p-4 rounded shadow hover:shadow-md"
                  >
                    <p className="font-medium">
                      {p.medicationName} — {p.patient?.firstName}{' '}
                      {p.patient?.lastName}
                    </p>
                    <p className="text-sm text-gray-500">
                      Issued:{' '}
                      {p.dateIssued
                        ? new Date(p.dateIssued).toLocaleDateString()
                        : 'N/A'}
                    </p>
                  </Link>
                </li>
              ))}
          </ul>
        ) : (
          <p className="text-gray-500">No prescriptions issued.</p>
        )}
      </section>
      </section>
    </div>
  )
}