// src/components/ColorTest.jsx
import React from 'react'

export default function ColorTest() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50 space-y-6">
      <div className="text-4xl font-bold text-red-500">🔴 Red Text</div>
      <div className="bg-green-500 text-white px-6 py-4 rounded-lg">
        🟢 Green Background
      </div>
      <button className="bg-blue-600 hover:bg-blue-800 text-white font-semibold px-8 py-3 rounded-full">
        🔵 Blue Button
      </button>
    </div>
  )
}
