import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'

export default function RegisterPage() {
  const [form, setForm] = useState({ firstName:'', lastName:'', email:'', password:'', phoneNumber:'' })
  const navigate = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    await api.post('/api/auth/register', form)
    navigate('/login')
  }

  return (
    <div className="max-w-md mx-auto mt-20 p-6 bg-white shadow">
      <h1 className="text-2xl mb-4">Register</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        {['firstName','lastName','email','password','phoneNumber'].map(key => (
          <input
            key={key}
            type={key==='email'?'email': key==='password'?'password':'text'}
            placeholder={key.charAt(0).toUpperCase()+key.slice(1)}
            value={form[key]}
            onChange={e=>setForm({...form,[key]:e.target.value})}
            className="w-full border p-2 rounded"
          />
        ))}
        <button type="submit" className="w-full bg-green-600 text-white p-2 rounded">
          Register
        </button>
      </form>
    </div>
  )
}
