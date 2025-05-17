// src/features/patient-dashboard/pages/ProfilePage.jsx
import React, { useEffect, useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import ChangePasswordSection from "./ChangePasswordSection";
import {
  UserCircleIcon,
  EnvelopeIcon,
  PhoneIcon,
  PencilSquareIcon,
  TrashIcon,
  LockClosedIcon,
  XCircleIcon,
  KeyIcon,
  EyeIcon,
  EyeSlashIcon,
} from "@heroicons/react/24/outline";

// --- Change Password Section ---


// --- Main ProfilePage ---
export default function ProfilePage() {
  const [user, setUser] = useState(null);
  const [form, setForm] = useState({});
  const [editMode, setEditMode] = useState(false);
  const [showDelete, setShowDelete] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState("");
  const [loading, setLoading] = useState(false);
  const [initialLoad, setInitialLoad] = useState(true);

  // Load profile from backend on mount
  useEffect(() => {
    async function fetchProfile() {
      setInitialLoad(true);
      try {
        const { data } = await axios.get("/patient/profile");
        setUser(data);
        setForm(data);
      } catch (err) {
        toast.error("Failed to load profile.");
      } finally {
        setInitialLoad(false);
      }
    }
    fetchProfile();
  }, []);

  // Change handlers
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  // Save
  const handleSave = async () => {
    setLoading(true);
    try {
      const { data } = await axios.put("/patient/profile", form);
      setUser(data);
      setEditMode(false);
      toast.success("Profile updated!");
    } catch (err) {
      toast.error(
        err.response?.data?.message ||
        err.response?.data ||
        err.message ||
        "Failed to update profile"
      );
    } finally {
      setLoading(false);
    }
  };

  // Cancel edit
  const handleCancel = () => {
    setForm(user);
    setEditMode(false);
  };

  // Delete account
  const handleDelete = async () => {
    setLoading(true);
    try {
      await axios.delete("/patient/profile");
      toast.success("Account deleted!");
      setShowDelete(false);
      // Optionally, redirect to logout/login page here
    } catch (err) {
      toast.error("Failed to delete account");
    } finally {
      setLoading(false);
    }
  };

  // Handle initials or avatar
  const initials = user
    ? ((user.firstName?.[0] || "") + (user.lastName?.[0] || "")).toUpperCase()
    : "--";

  // Loading state
  if (initialLoad) {
    return (
      <div className="flex justify-center items-center h-60">
        <svg className="animate-spin h-10 w-10 text-blue-500" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
        </svg>
        <span className="ml-4 text-gray-600 text-lg">Loading profile…</span>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="text-center text-gray-500 mt-10">No profile data found.</div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <div className="bg-white rounded-2xl shadow-xl p-8 flex flex-col items-center relative">
        {/* Edit icon */}
        {!editMode && (
          <button
            onClick={() => setEditMode(true)}
            className="absolute top-4 right-4 flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-600 rounded hover:bg-blue-200 transition"
          >
            <PencilSquareIcon className="h-5 w-5" /> Edit
          </button>
        )}

        {/* Avatar */}
        <div className="mb-6">
          <span className="inline-flex items-center justify-center h-24 w-24 rounded-full bg-blue-100 border-4 border-blue-400 text-4xl font-bold text-blue-700 shadow-lg">
            {initials}
          </span>
        </div>
        <h1 className="text-2xl font-bold mb-2">{user.firstName} {user.lastName}</h1>
        <p className="text-gray-500 mb-6">Patient Profile</p>

        {/* Profile Info */}
        <div className="w-full grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-5">
          <ProfileField
            label="First Name"
            value={form.firstName || ""}
            icon={<UserCircleIcon className="h-5 w-5" />}
            editable={editMode}
            name="firstName"
            onChange={handleChange}
          />
          <ProfileField
            label="Last Name"
            value={form.lastName || ""}
            icon={<UserCircleIcon className="h-5 w-5" />}
            editable={editMode}
            name="lastName"
            onChange={handleChange}
          />
          <ProfileField
            label="Email"
            value={form.email || ""}
            icon={<EnvelopeIcon className="h-5 w-5" />}
            editable={false}
            name="email"
            onChange={handleChange}
          />
          <ProfileField
            label="Phone"
            value={form.phoneNumber || ""}
            icon={<PhoneIcon className="h-5 w-5" />}
            editable={editMode}
            name="phoneNumber"
            onChange={handleChange}
          />
        </div>

        {/* Save/Cancel in Edit Mode */}
        {editMode && (
          <div className="flex gap-4 mt-8">
            <button
              className="bg-blue-600 text-white px-6 py-2 rounded font-semibold hover:bg-blue-700 transition"
              onClick={handleSave}
              disabled={loading}
            >
              {loading ? "Saving..." : "Save"}
            </button>
            <button
              className="bg-gray-200 text-gray-700 px-6 py-2 rounded font-semibold hover:bg-gray-300 transition"
              onClick={handleCancel}
              disabled={loading}
            >
              Cancel
            </button>
          </div>
        )}

        {/* Divider */}
        <div className="border-t w-full my-8"></div>

        {/* Security Section */}
        <div className="w-full flex items-center mb-6">
          <LockClosedIcon className="h-5 w-5 text-gray-400 mr-2" />
          <h2 className="text-lg font-semibold text-gray-700">Account Security</h2>
        </div>
        <button
          className="text-red-600 flex items-center gap-2 bg-red-50 px-4 py-2 rounded hover:bg-red-100 font-semibold transition"
          onClick={() => setShowDelete(true)}
        >
          <TrashIcon className="h-5 w-5" /> Delete Account
        </button>

        {/* Change Password Section */}
        <ChangePasswordSection />

      </div>

      {/* Delete Modal */}
      {showDelete && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50" onClick={() => setShowDelete(false)}>
          <div className="bg-white rounded-xl shadow-lg p-8 max-w-sm mx-auto relative" onClick={e => e.stopPropagation()}>
            <button className="absolute top-4 right-4" onClick={() => setShowDelete(false)}>
              <XCircleIcon className="h-6 w-6 text-gray-400 hover:text-gray-600" />
            </button>
            <div className="flex flex-col items-center">
              <TrashIcon className="h-10 w-10 text-red-500 mb-4" />
              <h3 className="text-xl font-semibold mb-2 text-gray-900">Delete Account</h3>
              <p className="mb-4 text-center text-gray-600">
                Are you sure you want to delete your account? <br />
                This action cannot be undone.
              </p>
              <input
                type="text"
                className="w-full px-3 py-2 border rounded mb-4"
                placeholder='Type "DELETE" to confirm'
                value={deleteConfirm}
                onChange={e => setDeleteConfirm(e.target.value)}
                disabled={loading}
              />
              <button
                className="bg-red-600 text-white px-6 py-2 rounded font-semibold hover:bg-red-700 transition w-full"
                onClick={handleDelete}
                disabled={loading || deleteConfirm !== "DELETE"}
              >
                {loading ? "Deleting..." : "Delete"}
              </button>
              <button
                className="text-gray-500 mt-3 underline text-sm"
                onClick={() => setShowDelete(false)}
                disabled={loading}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// Field subcomponent
function ProfileField({ label, value, icon, editable, name, onChange }) {
  return (
    <div className="flex items-center">
      <div className="mr-2">{icon}</div>
      <label className="w-32 font-medium text-gray-700">{label}:</label>
      {editable ? (
        <input
          name={name}
          value={value}
          onChange={onChange}
          className="flex-1 border px-3 py-1 rounded bg-blue-50 focus:bg-white focus:border-blue-400 transition ml-2"
        />
      ) : (
        <span className="ml-2">{value}</span>
      )}
    </div>
  );
}
