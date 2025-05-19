// src/components/Tabs.jsx
import React from 'react'

/**
 * Props:
 *  - tabs: [{ id: string, label: string, count?: number }]
 *  - active: string
 *  - onChange: (tabId: string) => void
 */
export function Tabs({ tabs, active, onChange }) {
  return (
    <nav className="border-b mb-4">
      <ul className="-mb-px flex space-x-8">
        {tabs.map(tab => (
          <li key={tab.id}>
            <button
              onClick={() => onChange(tab.id)}
              className={
                `py-4 px-1 border-b-2 font-medium text-sm focus:outline-none ` +
                (active === tab.id
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300')
              }
            >
              {tab.label}
              {typeof tab.count === 'number' && (
                <span className="ml-2 inline-block min-w-[1.3em] px-2 rounded-full text-xs bg-gray-200 text-gray-700">
                  {tab.count}
                </span>
              )}
            </button>
          </li>
        ))}
      </ul>
    </nav>
  )
}
