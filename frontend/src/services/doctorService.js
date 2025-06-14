// src/services/doctorService.js
import axios from "axios"

/**
 * Fetch upcoming appointments for the authenticated doctor.
 * @returns {Promise<Array>} list of appointments
 */
export async function getUpcomingAppointments() {
  const { data } = await axios.get("/doctor/appointments/upcoming");
  return data;
}

/**
 * Fetch past appointment history for the authenticated doctor.
 * @returns {Promise<Array>} list of past appointments
 */
export async function getPastAppointments() {
  const { data } = await axios.get("/doctor/appointments/history");
  return data;
}

/**
 * Mark an appointment as completed.
 * @param {number} appointmentId
 * @returns {Promise<string>} confirmation message
 */
export async function markAppointmentComplete(appointmentId) {
  const { data } = await axios.put(
    `/doctor/appointments/${appointmentId}/mark-complete`,
  );
  return data;
}

/**
 * Cancel an appointment if the backend route is supported.
 * @param {number} appointmentId
 * @returns {Promise<string>} confirmation message
 */
export async function cancelAppointment(appointmentId) {
  const { data } = await axios.delete(
    `/doctor/appointments/${appointmentId}/cancel`,
  );
  return data;
}

/**
 * Fetch cancelled appointments for the authenticated doctor.
 * @returns {Promise<Array>} list of cancelled appointments
 */
export async function getCancelledAppointments() {
  const { data } = await axios.get("/doctor/appointments/cancelled");
  return data;
}

// Get just the next upcoming appointment
export async function getNextAppointment() {
  const { data } = await axios.get("/doctor/appointments/upcoming", {
    params: { limit: 1 },
  });
  return data[0] || null;
}

// Count pending prescription refill requests for this doctor
export async function countPendingPrescriptions() {
  const { data } = await axios.get("/doctor/prescriptions/pending/count");
  return data;
}

// Count unread messages for this doctor
export async function countUnreadMessages() {
  const { data } = await axios.get("/doctor/messages/unread/count");
  return data;
}

// Fetch list of patients for this doctor
export async function getPatients() {
  const { data } = await axios.get("/doctor/patients");
  return data;
}
// Fetch a single patient profile by ID
export async function getPatient(id) {
  const { data } = await axios.get(`/doctor/patients/${id}`);
  return data;
}