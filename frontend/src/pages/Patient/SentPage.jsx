import React, { useEffect, useState } from 'react'
import messageService from '../../services/messageService'
import LoadingSpinner from '../../components/LoadingSpinner'

export default function SentPage() {
  const [msgs, setMsgs] = useState(null)

  useEffect(() => {
    messageService.getSent().then(setMsgs)
  }, [])

  if (!msgs) return <LoadingSpinner />

  return (
    <div>
      <h2 className="text-xl mb-4">Sent Messages</h2>
      <ul className="space-y-3">
        {msgs.map(m => (
          <li key={m.id} className="border p-3 rounded">
            <p>{m.content}</p>
            <small>To {m.receiverId} • {new Date(m.timestamp).toLocaleString()}</small>
          </li>
        ))}
      </ul>
    </div>
  )
}
