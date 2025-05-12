import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from "../hooks/useAuth.jsx"

export default function LoginPage() {
  const [email, setEmail]       = useState('')
  const [password, setPassword] = useState('')
  const { login }               = useAuth()
  const navigate                = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    await login(email, password)
    navigate('/')
  }

  return (
    <div className="max-w-md mx-auto mt-20 p-6 bg-white shadow">
      <h1 className="text-2xl mb-4">Login</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          className="w-full border p-2 rounded"
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          className="w-full border p-2 rounded"
        />
        <button type="submit" className="w-full bg-blue-600 text-white p-2 rounded">
          Login
        </button>
      </form>
    </div>
  )
}
