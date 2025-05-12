// src/pages/HomePage.jsx
import React from 'react'
import { Link } from 'react-router-dom'
import TailwindSmoke from '../components/TailwindSmoke'
export default function HomePage() {
  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-gray-100 p-6">
      <TailwindSmoke />
      <h1 className="text-4xl font-extrabold mb-8 text-blue-800"> 
        Welcome to HealthCare App Red
      </h1>
      
      <div className="flex space-x-4">
        <Link
          to="/login"
          className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-md shadow"
        >
          Log In
        </Link>
        <Link
          to="/register"
          className="px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-md shadow"
        >
          Sign Up
        </Link>
      </div>
    </div>
  )
}
