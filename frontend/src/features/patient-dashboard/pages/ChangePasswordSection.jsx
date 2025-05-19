import React, { useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import { KeyIcon } from "@heroicons/react/24/outline";
import PasswordInput from "../components/PasswordInput"; // <-- Adjust if folder differs!

export default function ChangePasswordSection() {
  const [current, setCurrent] = useState("");
  const [next, setNext] = useState("");
  const [confirm, setConfirm] = useState("");
  const [show, setShow] = useState({ current: false, next: false, confirm: false });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function validate(newPwd, currPwd, confirmPwd) {
    if (!currPwd || !newPwd || !confirmPwd)
      return "All fields are required.";
    if (newPwd.length < 8)
      return "Password must be at least 8 characters.";
    if (!/[A-Z]/.test(newPwd))
      return "Must include an uppercase letter.";
    if (!/[a-z]/.test(newPwd))
      return "Must include a lowercase letter.";
    if (!/[0-9]/.test(newPwd))
      return "Must include a number.";
    if (!/[^A-Za-z0-9]/.test(newPwd))
      return "Must include a special character.";
    if (newPwd === currPwd)
      return "New password must be different.";
    if (newPwd !== confirmPwd)
      return "Passwords do not match.";
    return "";
  }

  async function handleChangePassword(e) {
    e.preventDefault();
    setError("");
    const validation = validate(next, current, confirm);
    if (validation) return setError(validation);

    setLoading(true);
    try {
      await axios.post("/patient/profile/change-password", {
        oldPassword: current,
        newPassword: next,
        confirmPassword: confirm,
      });
      toast.success("Password updated!");
      setCurrent(""); setNext(""); setConfirm("");
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data ||
        err.message ||
        "Failed to change password"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="bg-white mt-10 rounded-xl shadow p-6 max-w-lg mx-auto border border-blue-100">
      <div className="flex items-center mb-3">
        <KeyIcon className="h-6 w-6 text-blue-400 mr-2" />
        <h3 className="text-lg font-bold text-blue-700">Change Password</h3>
      </div>
      <form className="space-y-5" onSubmit={handleChangePassword}>
        <div>
          <label className="block mb-1 font-medium text-gray-600">Current Password</label>
          <PasswordInput
            value={current}
            onChange={e => setCurrent(e.target.value)}
            show={show.current}
            toggle={() => setShow(s => ({ ...s, current: !s.current }))}
            placeholder="Current password"
          />
        </div>
        <div>
          <label className="block mb-1 font-medium text-gray-600">New Password</label>
          <PasswordInput
            value={next}
            onChange={e => setNext(e.target.value)}
            show={show.next}
            toggle={() => setShow(s => ({ ...s, next: !s.next }))}
            placeholder="New password"
          />
        </div>
        <div>
          <label className="block mb-1 font-medium text-gray-600">Confirm New Password</label>
          <PasswordInput
            value={confirm}
            onChange={e => setConfirm(e.target.value)}
            show={show.confirm}
            toggle={() => setShow(s => ({ ...s, confirm: !s.confirm }))}
            placeholder="Confirm new password"
          />
        </div>
        {error && <div className="text-red-600 text-sm">{error}</div>}
        <div className="flex gap-4 mt-2">
          <button
            type="submit"
            className="bg-blue-600 text-white px-6 py-2 rounded font-semibold hover:bg-blue-700 transition"
            disabled={loading}
          >
            {loading ? "Saving..." : "Update Password"}
          </button>
        </div>
      </form>
    </div>
  );
}
