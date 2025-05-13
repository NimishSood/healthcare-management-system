// features/patient-dashboard/components/MedicalRecords/RecordSection.jsx
export function RecordSection() {
  const [records, setRecords] = useState([]);

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="font-semibold">Your Medical History</h3>
        <button className="text-sm text-blue-600 hover:text-blue-800">
          Download All
        </button>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {records.map(record => (
          <div key={record.id} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
            <p className="font-medium">{record.type}</p>
            <p className="text-sm text-gray-500">{record.date}</p>
            <p className="text-sm mt-2">{record.description}</p>
            <button className="mt-3 text-sm text-blue-600 hover:text-blue-800">
              View Details
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}