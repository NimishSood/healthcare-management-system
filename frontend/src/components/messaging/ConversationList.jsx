import React from "react";

export default function ConversationList({ activeUser, setActiveUser, threads }) {
  return (
    <div className="p-4 pt-2"> {/* Slight top padding to separate from header */}
      <div className="flex flex-col gap-2">
        {threads.map(({ user, lastMessage, unread }) => (
          <button
            key={user.id}
            onClick={() => setActiveUser(user)}
            className={`flex items-center justify-between rounded-xl px-4 py-3 transition ${
              activeUser && activeUser.id === user.id
                ? "bg-blue-100 dark:bg-blue-900"
                : "hover:bg-gray-100 dark:hover:bg-gray-800"
            }`}
          >
            <div className="flex items-center gap-3">
              {/* Avatar badge */}
              <span className="inline-flex items-center justify-center h-10 w-10 rounded-full bg-blue-200 text-blue-800 font-bold text-lg">
                {user.firstName[0]}
                {user.lastName[0]}
              </span>
              <div>
                <div className="font-semibold text-left">
                  {user.firstName} {user.lastName}
                  <span className="ml-2 text-xs px-2 py-0.5 rounded bg-blue-50 text-blue-600 border border-blue-200">
                    {user.role}
                  </span>
                </div>
                <div className="text-xs text-gray-500 truncate max-w-[120px]">{lastMessage}</div>
              </div>
            </div>
            {unread > 0 && (
              <span className="ml-2 bg-red-500 text-white rounded-full px-2 py-0.5 text-xs font-bold">
                {unread}
              </span>
            )}
          </button>
        ))}
      </div>
    </div>
  );
}
