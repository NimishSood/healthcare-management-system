import React, { useState } from 'react'
import axios from 'axios'

export default function AiQueryPage() {
  const [query, setQuery] = useState('')
  const [response, setResponse] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)
    setResponse('')
    try {
      const { data } = await axios.post('/api/ai/query/admin', { query })
      setResponse(data)
    } catch (err) {
      setResponse(err.response?.data || 'Error sending request')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-semibold">Admin AI Query</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <textarea
          className="w-full p-3 border rounded"
          rows="4"
          placeholder="Ask the AI..."
          value={query}
          onChange={e => setQuery(e.target.value)}
        />
        <button
          type="submit"
          className="px-4 py-2 bg-purple-600 text-white rounded disabled:opacity-50"
          disabled={loading || !query.trim()}
        >
          {loading ? 'Sending...' : 'Send'}
        </button>
      </form>
      {response && (
        <div className="border p-4 rounded bg-gray-50 whitespace-pre-wrap">
          {response}
        </div>
      )}
    </div>
  )
}