import React from 'react'

export default function LoadingSpinner() {
  return (
    <div className="flex justify-center items-center py-10">
      <div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full"></div>
    </div>
  )
}
