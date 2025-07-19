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

/**
 * Fetch a prescription by its ID.
 * @param {number} id 
 * @returns {Promise<Object>} prescription object
 */
export async function getPrescriptionById(id) {
  const { data } = await axios.get(`/prescriptions/${id}`);
  return data;
}

/**
 * Request a refill for a prescription.
 * @param {number} prescriptionId 
 * @returns {Promise<string>} confirmation message
 */
export async function requestRefill(prescriptionId) {
  const { data } = await axios.post(`/prescriptions/${prescriptionId}/refill-request`);
  return data;
}

/**
 * Upload a document for the authenticated patient.
 * @param {File} file - file to upload
 * @returns {Promise<Object>} saved document metadata
 */
export async function uploadDocument(file) {
  const formData = new FormData();
  formData.append('file', file);
  const { data } = await axios.post('/documents/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return data;
}

/**
 * Retrieve all documents uploaded by the authenticated patient.
 * @returns {Promise<Array>} list of document objects
 */
export async function getDocuments() {
  const { data } = await axios.get('/documents/mine');
  return data;
}

/**
 * Download a specific document.
 * @param {number} id - document id
 * @returns {Promise<Blob>} file contents as blob
 */
export async function downloadDocument(id) {
  const { data } = await axios.get(`/documents/${id}`, { responseType: 'blob' });
  return data;
}


/**
 * Delete a document by ID.
 * @param {number} id - document id
 * @returns {Promise<void>} confirmation from backend
 */
export async function deleteDocument(id) {
  const { data } = await axios.delete(`/documents/${id}`);
  return data;
}
