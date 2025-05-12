import React from 'react'
import { Link } from 'react-router-dom'

export default function NotFoundPage() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center">
      <h1 className="text-4xl mb-4">404</h1>
      <p className="mb-4">Page not found.</p>
      <Link to="/" className="text-blue-600 hover:underline">Go Home</Link>
    </div>
  )
}
