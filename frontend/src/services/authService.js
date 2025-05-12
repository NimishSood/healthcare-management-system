import api from './api'

export function login(email, password) {
  return api.post('/api/auth/login', { email, password })
    .then(res => res.data)
}

export function me() {
  // backend should expose a /api/auth/me that returns the current user
  return api.get('/api/auth/me').then(res => res.data)
}

export default { login, me }
