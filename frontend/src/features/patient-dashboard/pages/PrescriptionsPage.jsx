// src/features/patient-dashboard/pages/PrescriptionsPage.jsx
import React, { useEffect, useState } from 'react'
import { getPrescriptions } from '../../../services/patientService'
import { PrescriptionCard } from '../components/Prescriptions/PrescriptionCard'

export default function PrescriptionsPage() {
  const [medications, setMedications] = useState([])
  const [search, setSearch] = useState('')

  useEffect(() => {
    getPrescriptions()
      .then(data => setMedications(data))
      .catch(err => console.error('Failed to load prescriptions:', err))
  }, [])

  // Filter and sort
  const filtered = medications
    .filter(med =>
      med.medicationName.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) =>
      new Date(b.dateIssued) - new Date(a.dateIssued)
    )

  return (
    <div className="space-y-6 p-6 bg-gray-50 min-h-screen">
      <h1 className="text-2xl font-semibold">Your Prescriptions</h1>
      <input
        type="text"
        className="border rounded px-3 py-2 w-full max-w-sm focus:outline-none focus:ring"
        placeholder="Search by medication name..."
        value={search}
        onChange={e => setSearch(e.target.value)}
      />
      {filtered.length > 0 ? (
        <div className="grid gap-4 mt-4">
          {filtered.map(med => (
            <PrescriptionCard key={med.id} medication={med} />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center py-16">
          <img
            src="/no-data.svg"
            alt="No prescriptions"
            className="w-32 opacity-60 mb-4"
            style={{ filter: 'grayscale(1)' }}
          />
          <p className="text-gray-500 text-lg">No prescriptions found.</p>
        </div>
      )}
    </div>
  )
}
