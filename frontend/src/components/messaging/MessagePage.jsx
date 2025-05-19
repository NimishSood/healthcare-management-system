import React, { useState, useEffect } from "react";
import ConversationList from "./ConversationList";
import ChatWindow from "./ChatWindow";
import * as messageApi from "../../services/messageApi";
import { useAuth } from "../../context/AuthContext";

export default function MessagePage() {
  const [activeUser, setActiveUser] = useState(null);
  const [threads, setThreads] = useState([]);
  const [messages, setMessages] = useState([]);
  const [loadingThreads, setLoadingThreads] = useState(true);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [sending, setSending] = useState(false);
  const { user } = useAuth();
  const myId = user?.id;

  // For "New Conversation"
  const [showNewConv, setShowNewConv] = useState(false);
  const [partners, setPartners] = useState([]);
  const [loadingPartners, setLoadingPartners] = useState(false);

  // Load threads (conversation list) -- NOW just fetch from backend!
  const fetchThreads = () => {
    setLoadingThreads(true);
    messageApi
      .getThreads()
      .then(setThreads)
      .catch(() => setThreads([]))
      .finally(() => setLoadingThreads(false));
  };

  useEffect(() => {
    fetchThreads();
  }, []);

  // Fetch messages for the active conversation
  useEffect(() => {
    if (!activeUser) {
      setMessages([]);
      return;
    }
    setLoadingMessages(true);
    messageApi.markConversationAsRead(activeUser.id).then(() => {
      messageApi
        .getMessagesWithUser(activeUser.id)
        .then(setMessages)
        .catch(() => setMessages([]))
        .finally(() => setLoadingMessages(false));
      setThreads((prev) =>
        prev.map((thread) =>
          thread.user.id === activeUser.id ? { ...thread, unread: 0 } : thread
        )
      );
    });
  }, [activeUser]);

  // Sending a message
  const handleSendMessage = async (content) => {
    if (!activeUser) return;
    setSending(true);
    try {
      const msg = await messageApi.sendMessage(activeUser.id, content);
      setMessages((prev) => [...prev, msg]);
      // After sending, refresh threads to ensure sidebar is up-to-date
      fetchThreads();
    } finally {
      setSending(false);
    }
  };

  // "New Conversation" Modal Logic
  const handleOpenNewConv = () => {
    setShowNewConv(true);
    setLoadingPartners(true);
    messageApi
      .getMessagingPartners()
      .then(setPartners)
      .catch(() => setPartners([]))
      .finally(() => setLoadingPartners(false));
  };

  return (
    <div className="flex h-[80vh] bg-gray-50 dark:bg-gray-900 rounded-2xl shadow-2xl overflow-hidden max-w-5xl mx-auto mt-8">
      {/* Sidebar */}
      <div className="w-1/3 min-w-[260px] border-r bg-white dark:bg-gray-800">
        {/* Header + New Conversation */}
        <div className="flex items-center justify-between p-4 border-b">
          <span className="text-lg font-bold text-blue-700">Conversations</span>
          <button
            onClick={handleOpenNewConv}
            className="px-3 py-1 bg-blue-600 text-white rounded-xl hover:bg-blue-700 text-sm"
          >
            + New
          </button>
        </div>
        {loadingThreads ? (
          <div className="flex justify-center items-center h-full text-blue-600">
            Loading...
          </div>
        ) : (
          <ConversationList
            activeUser={activeUser}
            setActiveUser={setActiveUser}
            threads={threads}
          />
        )}
      </div>
      {/* Chat Window */}
      <div className="flex-1">
        <ChatWindow
          activeUser={activeUser}
          messages={messages}
          loading={loadingMessages}
          onSend={handleSendMessage}
          sending={sending}
        />
      </div>

      {/* New Conversation Modal */}
      {showNewConv && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center">
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-lg w-80 max-w-full p-6">
            <div className="font-bold text-lg mb-4">
              Start New Conversation
            </div>
            {loadingPartners ? (
              <div className="text-blue-500">Loading...</div>
            ) : (
              <div className="flex flex-col gap-2">
                {partners.map((partner) => (
                  <button
                    key={partner.id}
                    onClick={() => {
                      setActiveUser(partner);
                      setShowNewConv(false);
                    }}
                    className="flex items-center gap-2 px-4 py-2 rounded-xl hover:bg-blue-100 dark:hover:bg-gray-700"
                  >
                    <span className="h-8 w-8 rounded-full bg-blue-200 text-blue-800 flex items-center justify-center font-bold">
                      {partner.firstName[0]}
                      {partner.lastName[0]}
                    </span>
                    <span>
                      {partner.firstName} {partner.lastName}
                      <span className="ml-2 text-xs bg-blue-50 text-blue-600 px-2 rounded">
                        {partner.role}
                      </span>
                    </span>
                  </button>
                ))}
                {partners.length === 0 && (
                  <div className="text-gray-400 text-sm text-center">
                    No available contacts.
                  </div>
                )}
              </div>
            )}
            <button
              onClick={() => setShowNewConv(false)}
              className="mt-4 px-4 py-1 bg-gray-300 dark:bg-gray-700 rounded-xl text-gray-700 dark:text-gray-100 text-sm w-full"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
