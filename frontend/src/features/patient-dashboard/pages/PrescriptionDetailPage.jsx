// src/features/patient-dashboard/pages/PrescriptionDetailPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getPrescriptionById, requestRefill } from "../../../services/patientService";
import toast from "react-hot-toast";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";

const CLINIC_NAME = "Sunrise Health Clinic";
const CLINIC_ADDRESS = "123 Wellness Rd, Charlottetown, PE";
const CLINIC_CONTACT = "Tel: (902) 555-0101 | www.sunrisehealth.ca";

export default function PrescriptionDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [prescription, setPrescription] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refillLoading, setRefillLoading] = useState(false);

  // Ref for PDF section
  const pdfRef = useRef(null);

  // Fetch prescription
  const fetchPrescription = () =>
    getPrescriptionById(id)
      .then((data) => {
        setPrescription(data);
        setLoading(false);
      })
      .catch((error) => {
        toast.error("Could not load prescription. You may not have access.");
        setLoading(false);
        setTimeout(() => navigate("/patient/prescriptions"), 2000);
      });

  useEffect(() => {
    fetchPrescription();
    // eslint-disable-next-line
  }, [id]);

  if (loading) {
    return <div className="flex justify-center py-10">Loading...</div>;
  }

  if (!prescription) {
    return null; // Already handled with toast + redirect
  }

  function statusBadge() {
    if (prescription.isDeleted) {
      return (
        <span className="bg-red-100 text-red-800 text-xs px-2 py-1 rounded ml-2" title="Cancelled">
          Cancelled
        </span>
      );
    }
    if (prescription.refillsLeft === 0) {
      return (
        <span className="bg-gray-200 text-gray-700 text-xs px-2 py-1 rounded ml-2" title="No refills left">
          Expired
        </span>
      );
    }
    return (
      <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded ml-2" title="Active">
        Active
      </span>
    );
  }

  // PDF Download Handler
  const handleDownloadPDF = async () => {
    const input = pdfRef.current;
    if (!input) return;

    // Scroll to top and wait to render
    window.scrollTo({ top: 0 });
    await new Promise((res) => setTimeout(res, 150));

    // Use a fixed width to prevent cropping
    const canvas = await html2canvas(input, {
      scale: 2,
      backgroundColor: "#fff",
      useCORS: true,
      width: 440, // match our wrapper
      height: input.offsetHeight,
    });
    const imgData = canvas.toDataURL("image/png");
    const pdf = new jsPDF({
      orientation: "portrait",
      unit: "px",
      format: [canvas.width, canvas.height],
    });
    pdf.addImage(imgData, "PNG", 0, 0, canvas.width, canvas.height);
    pdf.save(`Prescription_${prescription.medicationName}_${id}.pdf`);
  };

  // Format date utility
  const formatDate = (dt) => (dt ? new Date(dt).toLocaleDateString() : "N/A");

  // Patient info
  const patientName = prescription.patient?.firstName
    ? `${prescription.patient?.firstName} ${prescription.patient?.lastName || ""}`
    : "Patient";
  const patientId = prescription.patient?.id || prescription.patientId || "—";

  // Doctor info
  const doctorName = prescription.doctor
    ? `Dr. ${prescription.doctor.firstName} ${prescription.doctor.lastName || ""}`
    : "Doctor";

  // Show "Request Refill" button only if eligible
  const showRefillButton =
    !prescription.isDeleted &&
    prescription.refillsLeft > 0 &&
    (!prescription.refillStatus || prescription.refillStatus === "DENIED");

  // Status color for refill
  const refillStatusColor =
    prescription.refillStatus === "PENDING"
      ? "text-yellow-600"
      : prescription.refillStatus === "APPROVED"
      ? "text-green-600"
      : prescription.refillStatus === "DENIED"
      ? "text-red-600"
      : "";

  return (
    <div className="max-w-xl mx-auto bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 mt-8 relative">
      {/* Back Button */}
      <button
        className="mb-4 flex items-center text-blue-600 dark:text-blue-400 hover:underline text-sm focus:outline-none"
        onClick={() => navigate("/patient/prescriptions")}
      >
        <svg className="h-5 w-5 mr-1" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
        </svg>
        Back to Prescriptions
      </button>

      {/* Download as PDF Button */}
      <button
        className="absolute top-6 right-6 px-3 py-1 bg-blue-500 hover:bg-blue-600 text-white text-xs rounded shadow transition"
        onClick={handleDownloadPDF}
      >
        Download as PDF
      </button>

      {/* Request Refill Button */}
      {showRefillButton && (
        <button
          className="absolute top-16 right-6 px-3 py-1 bg-green-500 hover:bg-green-600 text-white text-xs rounded shadow transition"
          onClick={async () => {
            setRefillLoading(true);
            try {
              await requestRefill(prescription.id);
              toast.success("Refill request sent!");
              // Refetch to update UI
              fetchPrescription();
            } catch (err) {
              toast.error("Failed to request refill.");
            }
            setRefillLoading(false);
          }}
          disabled={refillLoading}
        >
          {refillLoading ? "Requesting..." : "Request Refill"}
        </button>
      )}

      {/* PDF-ONLY SECTION */}
      <div
        ref={pdfRef}
        className="bg-white mx-auto p-6 rounded-2xl text-gray-900"
        style={{
          width: 440, // fixed width for PDF area
          border: "1px solid #c0c0c0", // visual debug, remove later
          boxSizing: "border-box",
          marginTop: 12,
        }}
      >
        {/* Header with clinic name/logo */}
        <div className="flex items-center mb-2">
          <div>
            <h2 className="font-bold text-2xl text-blue-700 tracking-wide">{CLINIC_NAME}</h2>
            <div className="text-xs text-gray-500">
              {CLINIC_ADDRESS} | {CLINIC_CONTACT}
            </div>
          </div>
        </div>
        <hr className="my-2" />

        {/* Patient/Doctor/Date/Status */}
        <div className="flex flex-wrap justify-between text-sm mb-4">
          <div>
            <span className="font-semibold">Patient:</span> {patientName} <span className="mx-2">|</span>
            <span className="font-semibold">Patient ID:</span> {patientId}
          </div>
          <div>
            <span className="font-semibold">Prescribing Doctor:</span> {doctorName}
          </div>
        </div>

        {/* Prescription Info */}
        <div className="mb-2">
          <span className="font-bold text-lg">{prescription.medicationName}</span>
          {statusBadge()}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Dosage:</span> {prescription.dosage}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Instructions:</span> {prescription.instructions}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Date Issued:</span> {formatDate(prescription.dateIssued)}
        </div>
        <div className="mb-2">
          <span className="font-semibold">Refills Left:</span> {prescription.refillsLeft}
        </div>
        {prescription.appointment && (
          <div className="mb-2">
            <span className="font-semibold">Appointment ID:</span> {prescription.appointment.id}
          </div>
        )}

        {/* Refill Status */}
        {prescription.refillStatus && prescription.refillStatus !== "NONE" && (
          <div className="mb-2 mt-4">
            <span className="font-semibold">Refill status: </span>
            <span className={refillStatusColor}>{prescription.refillStatus}</span>
          </div>
        )}

        {/* Signature line */}
        <div className="mt-6 mb-2">
          <span className="font-semibold">Signature:</span>
          <span className="ml-10 border-b-2 border-gray-300 w-40 inline-block"></span>
        </div>
        <hr className="my-2" />

        {/* Footer */}
        <div className="text-xs text-gray-500 text-right">
          Generated by Healthcare Management System &nbsp;|&nbsp; {new Date().toLocaleString()}
          <br />
          <span className="italic">This is a computer-generated document. No signature required.</span>
        </div>
      </div>
    </div>
  );
}
