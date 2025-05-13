// src/features/patient-dashboard/pages/PrescriptionsPage.jsx
import React, { useEffect, useState } from 'react'
import { getPrescriptions } from '../../../services/patientService'
import { PrescriptionCard } from '../components/Appointments/Prescriptions/PrescriptionCard'

export default function PrescriptionsPage() {
  const [medications, setMedications] = useState([])

  useEffect(() => {
    getPrescriptions()
      .then(data => setMedications(data))
      .catch(err => console.error('Failed to load prescriptions:', err))
  }, [])

  return (
    <div className="space-y-6 p-6 bg-gray-50 min-h-screen">
      <h1 className="text-2xl font-semibold">Your Prescriptions</h1>
      {medications.length > 0 ? (
        <div className="grid gap-4">
          {medications.map(med => (
            <PrescriptionCard key={med.id} medication={med} />
          ))}
        </div>
      ) : (
        <p className="text-gray-500">No active prescriptions.</p>
      )}
    </div>
  )
}
