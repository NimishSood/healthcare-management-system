import React from 'react'
import { Link } from 'react-router-dom'

export default function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center h-screen">
      <h1 className="text-4xl mb-4">404 – Not Found</h1>
      <Link to="/" className="text-blue-600">Go Home</Link>
    </div>
  )
}
