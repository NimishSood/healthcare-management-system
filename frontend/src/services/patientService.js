// src/services/patientService.js
import axios from 'axios'

/**
 * Fetch the authenticated patient's upcoming appointments.
 * @returns {Promise<Array>} list of appointment objects
 */
export async function getUpcomingAppointments() {
  const { data } = await axios.get('/patient/appointments/upcoming')
  return data
}

/**
 * Fetch the authenticated patient's past appointment history.
 * @returns {Promise<Array>} list of past appointment objects
 */
export async function getPastAppointments() {
  const { data } = await axios.get('/patient/appointments/history')
  return data
}

/**
 * Cancel a specific appointment.
 * @param {number} appointmentId 
 * @returns {Promise<string>} backend confirmation message
 */
export async function cancelAppointment(appointmentId) {
  const { data } = await axios.delete('/patient/appointments/cancel', {
    params: { appointmentId }
  })
  return data
}

/**
 * Fetch the authenticated patient's prescriptions.
 * @returns {Promise<Array>} list of prescription objects
 */
export async function getPrescriptions() {
  const { data } = await axios.get('/prescriptions/mine');
  return data;
}


export async function getPrescriptionById(id) {
  const { data } = await axios.get(`/prescriptions/${id}`);
  return data;
}


