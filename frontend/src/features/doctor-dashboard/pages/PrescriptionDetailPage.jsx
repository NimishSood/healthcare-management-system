import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { getPrescriptionById } from '../../../services/doctorService';

export default function PrescriptionDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [prescription, setPrescription] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getPrescriptionById(id)
      .then(data => {
        setPrescription(data);
      })
      .catch(() => {
        toast.error('Failed to load prescription');
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="p-6">Loading...</div>;
  if (!prescription) return <div className="p-6 text-red-500">Prescription not found.</div>;

  const formatDate = dt => (dt ? new Date(dt).toLocaleDateString() : 'N/A');
  const patient = prescription.patient;

  return (
    <div className="max-w-xl mx-auto bg-white dark:bg-gray-800 rounded-2xl shadow p-6 mt-8">
      <button
        className="mb-4 flex items-center text-blue-600 dark:text-blue-400 hover:underline text-sm"
        onClick={() => navigate(-1)}
      >
        &larr; Back
      </button>
      <h2 className="text-xl font-semibold mb-4">Prescription Details</h2>
      <div className="space-y-2">
        <div>
          <span className="font-semibold">Medication:</span> {prescription.medicationName}
        </div>
        <div>
          <span className="font-semibold">Dosage:</span> {prescription.dosage}
        </div>
        <div>
          <span className="font-semibold">Instructions:</span> {prescription.instructions}
        </div>
        <div>
          <span className="font-semibold">Date Issued:</span> {formatDate(prescription.dateIssued)}
        </div>
        <div>
          <span className="font-semibold">Refills Left:</span> {prescription.refillsLeft}
        </div>
        {patient && (
          <div>
            <span className="font-semibold">Patient:</span> {patient.firstName} {patient.lastName} &mdash; {patient.email}
            {patient.phoneNumber ? ` • ${patient.phoneNumber}` : ''}
          </div>
        )}
        {prescription.appointment && (
          <div>
            <span className="font-semibold">Appointment ID:</span> {prescription.appointment.id}
          </div>
        )}
      </div>
    </div>
  );
}