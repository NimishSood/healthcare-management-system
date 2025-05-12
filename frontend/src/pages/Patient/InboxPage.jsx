import React, { useEffect, useState } from 'react'
import messageService from '../../services/messageService'
import LoadingSpinner from '../../components/LoadingSpinner'

export default function InboxPage() {
  const [msgs, setMsgs] = useState(null)

  useEffect(() => {
    messageService.getInbox().then(setMsgs)
  }, [])

  if (!msgs) return <LoadingSpinner />

  return (
    <div>
      <h2 className="text-xl mb-4">Inbox</h2>
      <ul className="space-y-3">
        {msgs.map(m => (
          <li key={m.id} className="border p-3 rounded">
            <p>{m.content}</p>
            <small>From {m.senderId} • {new Date(m.timestamp).toLocaleString()}</small>
          </li>
        ))}
      </ul>
    </div>
  )
}
