import axios from 'axios'

export async function getPatients() {
  const { data } = await axios.get('/admin/view-patients')
  return data
}