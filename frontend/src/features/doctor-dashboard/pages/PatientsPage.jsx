import React, { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Link } from 'react-router-dom';
import { getPatients } from '../../../services/doctorService';

export default function DoctorPatientsPage() {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getPatients()
      .then(setPatients)
      .catch(() => toast.error('Failed to load patients'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading patients…</p>;

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-semibold">My Patients</h1>
      <ul className="space-y-4">
        {patients.map(p => (
          <li key={p.id} className="bg-white p-4 rounded shadow">
            <Link to={`/doctor/patients/${p.id}`}
              className="block hover:underline">
              <p className="font-medium">{p.firstName} {p.lastName}</p>
              <p className="text-sm">{p.email} • {p.phoneNumber}</p>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}