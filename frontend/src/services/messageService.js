import api from './api'

export function sendMessage(receiverId, content) {
  return api.post('/messages/send', { receiverId, content })
    .then(res => res.data)
}

export function getInbox() {
  return api.get('/messages/inbox').then(res => res.data)
}

export function getSent() {
  return api.get('/messages/sent').then(res => res.data)
}

export function markAsRead(id) {
  return api.put(`/messages/${id}/read`)
}

export default { sendMessage, getInbox, getSent, markAsRead }
