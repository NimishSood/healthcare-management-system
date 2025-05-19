import React, { useEffect, useState, useContext } from "react";
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
  MoonIcon,
  SunIcon,
  ArrowRightOnRectangleIcon,
} from "@heroicons/react/24/outline";
import { useTheme } from "/src/context/ThemeContext";
import { AuthContext } from "/src/context/AuthContext";

export default function DoctorProfilePage() {
  const { theme, toggleTheme } = useTheme();
  const { logout } = useContext(AuthContext);

  const [user, setUser] = useState(null);
  const [form, setForm] = useState({});
  const [editMode, setEditMode] = useState(false);
  const [showDelete, setShowDelete] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState("");
  const [loading, setLoading] = useState(false);
  const [initialLoad, setInitialLoad] = useState(true);

  useEffect(() => {
    async function fetchProfile() {
      setInitialLoad(true);
      try {
        const { data } = await axios.get("/doctor/profile");
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

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSave = async () => {
    setLoading(true);
    try {
      const payload = {
        firstName: form.firstName,
        lastName: form.lastName,
        phoneNumber: form.phoneNumber,
      };
      await axios.put("/doctor/profile", payload);
      setUser({ ...user, ...payload });
      setEditMode(false);
      toast.success("Profile updated!");
    } catch (err) {
      toast.error("Failed to update profile");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setForm(user);
    setEditMode(false);
  };

  const handleDelete = async () => {
    setLoading(true);
    try {
      await axios.delete("/doctor/delete-account");
      toast.success("Account deleted!");
      setShowDelete(false);
      logout(); // Log the doctor out after deleting account!
    } catch (err) {
      toast.error("Failed to delete account");
    } finally {
      setLoading(false);
    }
  };

  if (initialLoad) {
    return (
      <div className="flex justify-center items-center h-60">
        {/* loading spinner here */}
        <svg className="animate-spin h-10 w-10 text-blue-500" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
        </svg>
        <span className="ml-4 text-gray-600 text-lg">Loading profile…</span>
      </div>
    );
  }

  if (!user) {
    return <div className="text-center text-gray-500 mt-10">No profile data found.</div>;
  }

  return (
    <div className="max-w-5xl mx-auto p-8 w-full bg-gray-50 dark:bg-gray-900 min-h-screen transition-colors relative">
      {/* Theme toggle button */}
      <button
        className="absolute top-4 right-8 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-full p-2 shadow transition z-20"
        aria-label="Toggle theme"
        onClick={toggleTheme}
      >
        {theme === "dark" ? (
          <SunIcon className="h-5 w-5 text-yellow-400" />
        ) : (
          <MoonIcon className="h-5 w-5 text-blue-400" />
        )}
      </button>

      <div className="flex flex-col md:flex-row md:gap-12">
        {/* LEFT: Profile Info */}
        <div className="flex-1 bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-10 flex flex-col items-center relative mb-10 md:mb-0 transition hover:shadow-[0_8px_32px_0_rgba(31,41,55,0.12)] duration-200">
          {!editMode && (
            <button
              onClick={() => setEditMode(true)}
              className="absolute top-4 right-4 flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-600 rounded hover:bg-blue-200 transition"
              aria-label="Edit Profile"
            >
              <PencilSquareIcon className="h-5 w-5" /> Edit
            </button>
          )}
          <div className="mb-6">
            <span className="inline-flex items-center justify-center h-24 w-24 rounded-full bg-blue-100 border-4 border-blue-400 text-4xl font-bold text-blue-700 shadow-2xl ring-2 ring-blue-200">
              {user.firstName?.[0]}{user.lastName?.[0]}
            </span>
          </div>
          <h1 className="text-2xl font-bold mb-2 dark:text-gray-100">{user.firstName} {user.lastName}</h1>
          <p className="text-gray-500 dark:text-gray-400 mb-6">Doctor Profile</p>
          <div className="w-full grid grid-cols-1 gap-y-5">
            <ProfileField label="First Name" value={form.firstName || ""} icon={<UserCircleIcon className="h-5 w-5" />} editable={editMode} name="firstName" onChange={handleChange} />
            <ProfileField label="Last Name" value={form.lastName || ""} icon={<UserCircleIcon className="h-5 w-5" />} editable={editMode} name="lastName" onChange={handleChange} />
            <ProfileField label="Email" value={user.email || ""} icon={<EnvelopeIcon className="h-5 w-5" />} editable={false} name="email" onChange={handleChange} />
            <ProfileField label="Phone" value={form.phoneNumber || ""} icon={<PhoneIcon className="h-5 w-5" />} editable={editMode} name="phoneNumber" onChange={handleChange} />
            <ProfileField label="License Number" value={user.licenseNumber || ""} icon={<LockClosedIcon className="h-5 w-5" />} editable={false} name="licenseNumber" onChange={handleChange} />
            <ProfileField label="Specialty" value={user.specialty || ""} icon={<LockClosedIcon className="h-5 w-5" />} editable={false} name="specialty" onChange={handleChange} />
          </div>
          {editMode && (
            <div className="flex gap-4 mt-8">
              <button
                className="bg-blue-600 text-white px-6 py-2 rounded font-semibold hover:bg-blue-700 transition"
                onClick={handleSave}
                disabled={loading}
                aria-label="Save Profile"
              >
                {loading ? "Saving..." : "Save"}
              </button>
              <button
                className="bg-gray-200 text-gray-700 px-6 py-2 rounded font-semibold hover:bg-gray-300 transition"
                onClick={handleCancel}
                disabled={loading}
                aria-label="Cancel Edit"
              >
                Cancel
              </button>
            </div>
          )}
        </div>
        {/* RIGHT: Security/Actions */}
        <div className="flex-1 flex flex-col gap-8">
          <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-10 transition hover:shadow-[0_8px_32px_0_rgba(31,41,55,0.12)] duration-200">
            <div className="flex items-center mb-6">
              <LockClosedIcon className="h-5 w-5 text-gray-400 mr-2" />
              <h2 className="text-lg font-semibold text-gray-700 dark:text-gray-200">Account Security</h2>
            </div>
            <ChangePasswordSection />
          </div>
          {/* Account Actions (Logout + Delete) */}
          <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-10 transition hover:shadow-[0_8px_32px_0_rgba(239,68,68,0.08)] duration-200">
            <div className="flex flex-col gap-3">
              <button
                onClick={logout}
                className="flex items-center gap-2 bg-blue-50 text-blue-700 px-4 py-2 rounded hover:bg-blue-100 font-semibold transition w-full justify-center"
                aria-label="Logout"
              >
                <ArrowRightOnRectangleIcon className="h-5 w-5" /> Logout
              </button>
              <button
                className="flex items-center gap-2 text-red-600 bg-red-50 dark:bg-red-800 px-4 py-2 rounded hover:bg-red-100 dark:hover:bg-red-700 font-semibold transition w-full justify-center"
                onClick={() => setShowDelete(true)}
                aria-label="Delete Account"
              >
                <TrashIcon className="h-5 w-5" /> Delete Account
              </button>
            </div>
          </div>
        </div>
      </div>
      {/* Delete Modal */}
      {showDelete && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50" onClick={() => setShowDelete(false)}>
          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-8 max-w-sm mx-auto relative" onClick={e => e.stopPropagation()}>
            <button className="absolute top-4 right-4" onClick={() => setShowDelete(false)}>
              <XCircleIcon className="h-6 w-6 text-gray-400 hover:text-gray-600" />
            </button>
            <div className="flex flex-col items-center">
              <TrashIcon className="h-10 w-10 text-red-500 mb-4" />
              <h3 className="text-xl font-semibold mb-2 text-gray-900 dark:text-gray-100">Delete Account</h3>
              <p className="mb-4 text-center text-gray-600 dark:text-gray-300">
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

function ProfileField({ label, value, icon, editable, name, onChange }) {
  return (
    <div className="flex items-center mb-3">
      <div className="mr-2">{icon}</div>
      <label className="w-32 font-medium text-gray-700 dark:text-gray-300">{label}:</label>
      {editable ? (
        <input
          name={name}
          value={value}
          onChange={onChange}
          className="flex-1 border px-3 py-1 rounded bg-blue-50 dark:bg-gray-700 focus:bg-white focus:dark:bg-gray-600 focus:border-blue-400 transition ml-2 text-gray-900 dark:text-gray-100"
        />
      ) : (
        <span className="ml-2 text-gray-900 dark:text-gray-100">{value}</span>
      )}
    </div>
  );
}
