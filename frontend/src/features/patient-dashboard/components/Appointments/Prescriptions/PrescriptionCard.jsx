// features/patient-dashboard/components/Prescriptions/PrescriptionCard.jsx
export function PrescriptionCard({ medication }) {
  return (
    <div className="bg-white p-4 rounded-lg shadow-sm border-l-4 border-blue-500">
      <div className="flex justify-between">
        <div>
          <h3 className="font-bold">{medication.name}</h3>
          <p className="text-sm text-gray-600">{medication.dosage}</p>
        </div>
        <span className={`px-2 py-1 text-xs rounded-full ${
          medication.status === 'active' 
            ? 'bg-green-100 text-green-800' 
            : 'bg-red-100 text-red-800'
        }`}>
          {medication.status}
        </span>
      </div>
      <div className="mt-3 flex justify-between text-sm">
        <p>Refills: {medication.refillsLeft}</p>
        <p>Expires: {medication.expiryDate}</p>
      </div>
    </div>
  );
}