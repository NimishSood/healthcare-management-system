import React, { useEffect, useRef } from "react";
import MessageInput from "./MessageInput";
import { useAuth } from "../../context/AuthContext"; 
export default function ChatWindow({ activeUser, messages, loading, onSend, sending }) {
  const { user } = useAuth();
  const myId = user?.id;

  // Auto-scroll to bottom
  const messagesEndRef = useRef(null);
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, activeUser, loading]);

  if (!activeUser) {
    return (
      <div className="flex flex-col h-full items-center justify-center text-gray-400">
        <span className="text-2xl mb-2">💬</span>
        <span>Select a conversation to start messaging.</span>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="p-4 border-b bg-gray-100 dark:bg-gray-700 text-blue-700 font-semibold flex items-center gap-3">
        <span className="inline-flex items-center justify-center h-9 w-9 rounded-full bg-blue-200 text-blue-800 font-bold text-base">
          {activeUser.firstName[0]}
          {activeUser.lastName[0]}
        </span>
        {activeUser.firstName} {activeUser.lastName}
        <span className="ml-2 text-xs px-2 py-0.5 rounded bg-blue-50 text-blue-600 border border-blue-200">
          {activeUser.role}
        </span>
      </div>
      {/* Messages */}
      <div className="flex-1 p-4 space-y-3 overflow-y-auto bg-gray-50 dark:bg-gray-900">
        {loading ? (
          <div className="text-blue-500">Loading messages...</div>
        ) : messages.length === 0 ? (
          <div className="text-gray-400 mt-8">No messages yet.</div>
        ) : (
          messages.map((msg) => (
            <div
              key={msg.id}
              className={`flex ${msg.senderId === myId ? "justify-end" : "justify-start"}`}
            >
              <div
                className={`max-w-xs px-4 py-2 rounded-2xl shadow
                  ${msg.senderId === myId
                    ? "bg-blue-600 text-white rounded-br-none"
                    : "bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-gray-100 rounded-bl-none"
                  }`}
              >
                <div>{msg.content}</div>
                <div className="text-[10px] text-gray-200 mt-1 text-right">
                  {new Date(msg.timestamp).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                </div>
              </div>
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </div>
      {/* Message input */}
      <div className="p-4 border-t bg-white dark:bg-gray-800">
        <MessageInput onSend={onSend} disabled={sending || loading} />
      </div>
    </div>
  );
}
