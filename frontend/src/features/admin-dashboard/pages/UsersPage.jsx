import React, { useEffect, useState } from 'react'
import axios from 'axios'

export default function UsersPage() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    axios.get('/api/users')
      .then(res => setUsers(res.data))
      .catch(err => setError(err.response?.data || 'Failed to load users'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p>Loading users…</p>
  if (error) return <p className='text-red-600'>Error: {error}</p>

  return (
    <div className='p-6'>
      <h1 className='text-xl font-semibold mb-4'>Users</h1>
      <div className='overflow-x-auto'>
        <table className='min-w-full bg-white'>
          <thead>
            <tr className='bg-gray-100'>
              <th className='text-left px-4 py-2'>Name</th>
              <th className='text-left px-4 py-2'>Email</th>
              <th className='text-left px-4 py-2'>Role</th>
              <th className='text-left px-4 py-2'>Status</th>
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id} className='border-t hover:bg-gray-50'>
                <td className='px-4 py-2'>{u.firstName} {u.lastName}</td>
                <td className='px-4 py-2'>{u.email}</td>
                <td className='px-4 py-2'>{u.role}</td>
                <td className='px-4 py-2'>{u.accountStatus}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}