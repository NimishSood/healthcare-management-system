// src/services/patientService.js
import axios from 'axios'

/**
 * Fetch the authenticated patient's prescriptions.
 * @returns {Promise<Array>} list of prescription objects
 */
export async function getPrescriptions() {
  const { data } = await axios.get('/patient/prescriptions')
  return data
}
