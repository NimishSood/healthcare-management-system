// src/services/doctorService.js
import axios from 'axios'

/**
 * Fetch upcoming appointments for the authenticated doctor.
 * @returns {Promise<Array>} list of appointments
 */
export async function getUpcomingAppointments() {
  const { data } = await axios.get('/doctor/appointments/upcoming')
  return data
}

/**
 * Fetch past appointment history for the authenticated doctor.
 * @returns {Promise<Array>} list of past appointments
 */
export async function getPastAppointments() {
  const { data } = await axios.get('/doctor/appointments/history')
  return data
}

/**
 * Mark an appointment as completed.
 * @param {number} appointmentId
 * @returns {Promise<string>} confirmation message
 */
export async function markAppointmentComplete(appointmentId) {
  const { data } = await axios.put(`/doctor/appointments/${appointmentId}/mark-complete`)
  return data
}

/**
 * Cancel an appointment if the backend route is supported.
 * @param {number} appointmentId
 * @returns {Promise<string>} confirmation message
 */
export async function cancelAppointment(appointmentId) {
  const { data } = await axios.delete(`/doctor/appointments/${appointmentId}/cancel`)
  return data
}
