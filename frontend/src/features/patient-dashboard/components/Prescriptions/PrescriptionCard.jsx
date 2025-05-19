// src/features/patient-dashboard/components/Prescriptions/PrescriptionCard.jsx
import { Link } from "react-router-dom";

// Helper for badge color and label
function getStatusBadge(med) {
  if (med.isDeleted)
    return <span className="bg-red-100 text-red-800 text-xs px-2 py-1 rounded ml-2">Cancelled</span>
  if (med.refillsLeft === 0)
    return <span className="bg-gray-200 text-gray-700 text-xs px-2 py-1 rounded ml-2">Expired</span>
  return <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded ml-2">Active</span>
}

export function PrescriptionCard({ medication }) {
  return (
    <Link to={`/prescriptions/${medication.id}`} className="block hover:shadow-md transition-shadow rounded-lg">
      <div className="bg-white p-4 rounded-lg shadow-sm border-l-4 border-blue-500 mb-2 dark:bg-gray-800">
        <div className="flex items-center justify-between">
          <span className="text-lg font-semibold">{medication.medicationName}</span>
          {getStatusBadge(medication)}
        </div>
        <div className="flex flex-wrap text-sm text-gray-600 dark:text-gray-300 mt-1 mb-1 gap-x-4">
          <span>Dosage: {medication.dosage}</span>
          <span>Refills: {medication.refillsLeft}</span>
        </div>
        <div className="flex justify-between text-xs text-gray-500 dark:text-gray-400">
          <span>
            Doctor: {medication.doctor?.firstName} {medication.doctor?.lastName}
          </span>
          <span>
            Issued: {medication.dateIssued ? new Date(medication.dateIssued).toLocaleDateString() : "N/A"}
          </span>
        </div>
      </div>
    </Link>
  )
}
