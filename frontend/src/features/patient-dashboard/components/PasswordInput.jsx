//src/features/patient-dashboard/components/PasswordInput.jsx
import React from "react";
import { EyeIcon, EyeSlashIcon } from "@heroicons/react/24/outline";

export default function PasswordInput({ value, onChange, show, toggle, placeholder }) {
  return (
    <div className="flex items-center border rounded bg-gray-50 focus-within:bg-white px-2">
      <input
        type={show ? "text" : "password"}
        value={value}
        onChange={onChange}
        className="w-full px-2 py-2 bg-transparent focus:outline-none"
        placeholder={placeholder}
        autoComplete="new-password"
      />
      <button type="button" onClick={toggle} tabIndex={-1} className="p-1 focus:outline-none">
        {show
          ? <EyeSlashIcon className="h-5 w-5 text-gray-400" />
          : <EyeIcon className="h-5 w-5 text-gray-400" />}
      </button>
    </div>
  );
}
