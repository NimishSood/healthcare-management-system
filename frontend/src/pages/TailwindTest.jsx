// src/pages/TailwindTest.jsx
import React from 'react'

export default function TailwindTest() {
  return (
    // full-screen centered light-blue background
    <div className="min-h-screen flex items-center justify-center bg-blue-100">
      {/* big, bold, dark-blue text */}
      <h1 className="text-5xl font-extrabold text-blue-800">
        🎉 Tailwind is working!
      </h1>
    </div>
  )
}
