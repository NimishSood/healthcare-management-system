import React, { useState } from "react";

export default function MessageInput({ onSend, disabled }) {
  const [value, setValue] = useState("");

  const handleSend = (e) => {
    e.preventDefault();
    if (!value.trim()) return;
    onSend(value.trim());
    setValue(""); // Clear input after send
  };

  return (
    <form className="flex gap-2" onSubmit={handleSend}>
      <input
        className="flex-1 px-4 py-2 rounded-xl border bg-gray-50 dark:bg-gray-900 focus:outline-none"
        placeholder="Type a message…"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        disabled={disabled}
        autoComplete="off"
      />
      <button
        type="submit"
        className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-2 rounded-xl transition disabled:opacity-50"
        disabled={disabled || !value.trim()}
      >
        Send
      </button>
    </form>
  );
}
