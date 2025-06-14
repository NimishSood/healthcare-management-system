import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { getPatient } from '../../../services/doctorService';

export default function DoctorPatientDetailPage() {
  const { id } = useParams();
  const [patient, setPatient] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getPatient(id)
      .then(setPatient)
      .catch(() => toast.error('Failed to load patient'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <p>Loading patient…</p>;
  if (!patient) return <p className="text-red-500">Patient not found.</p>;

  return (
    <div className="space-y-4 p-6">
      <h1 className="text-2xl font-semibold">Patient Details</h1>
      <div className="bg-white p-4 rounded shadow space-y-2">
        <div>
          <span className="font-medium">First Name: </span>
          {patient.firstName}
        </div>
        <div>
          <span className="font-medium">Last Name: </span>
          {patient.lastName}
        </div>
        <div>
          <span className="font-medium">Email: </span>
          {patient.email}
        </div>
        <div>
          <span className="font-medium">Phone: </span>
          {patient.phoneNumber}
        </div>
        <div>
          <span className="font-medium">Insurance Provider: </span>
          {patient.insuranceProvider}
        </div>
      </div>
    </div>
  );
}