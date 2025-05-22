import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import toast from "react-hot-toast";

export default function BreaksSection({ breaks, refresh }) {
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [form, setForm] = useState({ dayOfWeek: "", startTime: "", endTime: "" });
  const [editForm, setEditForm] = useState({ id: null, dayOfWeek: "", startTime: "", endTime: "" });
  const [loading, setLoading] = useState(false);

  const dayRef = useRef();

  // Autofocus first field when modal opens
  useEffect(() => {
    if (showAddModal && dayRef.current) dayRef.current.focus();
  }, [showAddModal]);

  // Autofocus for edit modal
  const editDayRef = useRef();
  useEffect(() => {
    if (showEditModal && editDayRef.current) editDayRef.current.focus();
  }, [showEditModal]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });
  const handleEditChange = (e) => setEditForm({ ...editForm, [e.target.name]: e.target.value });

  // Add Break
  const handleAdd = async (e) => {
    e.preventDefault();
    if (!form.dayOfWeek || !form.startTime || !form.endTime)
      return toast.error("Fill all fields!");
    setLoading(true);
    try {
      await axios.post("/doctor/schedule/break", form);
      toast.success("Break added!");
      setForm({ dayOfWeek: "", startTime: "", endTime: "" });
      setShowAddModal(false);
      refresh();
    } catch {
      toast.error("Failed to add break");
    } finally {
      setLoading(false);
    }
  };

  // Edit Break (open modal)
  const openEdit = (brk) => {
    setEditForm(brk);
    setShowEditModal(true);
  };

  // Save Edit
  const handleEdit = async (e) => {
    e.preventDefault();
    if (!editForm.dayOfWeek || !editForm.startTime || !editForm.endTime)
      return toast.error("Fill all fields!");
    setLoading(true);
    try {
      await axios.put(`/doctor/schedule/break/${editForm.id}`, editForm);
      toast.success("Break updated!");
      setShowEditModal(false);
      refresh();
    } catch {
      toast.error("Failed to update break");
    } finally {
      setLoading(false);
    }
  };

  // Delete Break
  const handleDelete = async (id) => {
    if (!window.confirm("Delete this break?")) return;
    try {
      await axios.delete(`/doctor/schedule/break/${id}`);
      toast.success("Break deleted!");
      refresh();
    } catch {
      toast.error("Failed to delete");
    }
  };

  return (
    <div className="mb-8">
      {/* Header and Add Break Button on the same line */}
      <div className="flex items-center justify-between mb-2">
        <h2 className="text-lg font-semibold">Weekly Breaks</h2>
        <button
          onClick={() => setShowAddModal(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded font-semibold hover:bg-blue-700 focus:ring-2 focus:ring-blue-300 transition"
        >
          + Add Break
        </button>
      </div>

      {/* Add Break Modal */}
      {showAddModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
          <form
            className="bg-white dark:bg-gray-900 rounded-2xl p-8 shadow-2xl w-96 flex flex-col gap-4"
            onSubmit={handleAdd}
          >
            <h2 className="text-xl font-semibold mb-2">Add Weekly Break</h2>
            <label className="font-medium">Day of Week
              <select
                name="dayOfWeek"
                className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
                required
                value={form.dayOfWeek}
                onChange={handleChange}
                ref={dayRef}
              >
                <option value="">Select day</option>
                {["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"].map(d =>
                  <option key={d} value={d}>{d.charAt(0) + d.slice(1).toLowerCase()}</option>
                )}
              </select>
            </label>
            <label className="font-medium">Start Time
              <input
                type="time"
                name="startTime"
                className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
                required
                value={form.startTime}
                onChange={handleChange}
              />
            </label>
            <label className="font-medium">End Time
              <input
                type="time"
                name="endTime"
                className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
                required
                value={form.endTime}
                onChange={handleChange}
              />
            </label>
            <div className="flex gap-2 mt-2">
              <button
                type="submit"
                className="bg-blue-600 text-white px-4 py-2 rounded font-semibold hover:bg-blue-700 focus:ring-2 focus:ring-blue-300 transition w-full"
                disabled={loading}
              >
                {loading ? "Adding..." : "Add"}
              </button>
              <button
                type="button"
                onClick={() => setShowAddModal(false)}
                className="bg-gray-200 text-gray-700 px-4 py-2 rounded font-semibold hover:bg-gray-300 focus:ring-2 focus:ring-gray-400 transition w-full"
                disabled={loading}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Edit Break Modal */}
      {showEditModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
          <form
            className="bg-white dark:bg-gray-900 rounded-2xl p-8 shadow-2xl w-96 flex flex-col gap-4"
            onSubmit={handleEdit}
          >
            <h2 className="text-xl font-semibold mb-2">Edit Break</h2>
            <label className="font-medium">Day of Week
              <select
                name="dayOfWeek"
                className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
                required
                value={editForm.dayOfWeek}
                onChange={handleEditChange}
                ref={editDayRef}
              >
                <option value="">Select day</option>
                {["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"].map(d =>
                  <option key={d} value={d}>{d.charAt(0) + d.slice(1).toLowerCase()}</option>
                )}
              </select>
            </label>
            <label className="font-medium">Start Time
              <input
                type="time"
                name="startTime"
                className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
                required
                value={editForm.startTime}
                onChange={handleEditChange}
              />
            </label>
            <label className="font-medium">End Time
              <input
                type="time"
                name="endTime"
                className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
                required
                value={editForm.endTime}
                onChange={handleEditChange}
              />
            </label>
            <div className="flex gap-2 mt-2">
              <button
                type="submit"
                className="bg-blue-600 text-white px-4 py-2 rounded font-semibold hover:bg-blue-700 focus:ring-2 focus:ring-blue-300 transition w-full"
                disabled={loading}
              >
                {loading ? "Saving..." : "Save"}
              </button>
              <button
                type="button"
                onClick={() => setShowEditModal(false)}
                className="bg-gray-200 text-gray-700 px-4 py-2 rounded font-semibold hover:bg-gray-300 focus:ring-2 focus:ring-gray-400 transition w-full"
                disabled={loading}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Breaks Table */}
      <table className="w-full mb-4 border rounded shadow-sm">
        <thead>
          <tr className="bg-gray-100 dark:bg-gray-800">
            <th className="text-left px-3 py-2">Day</th>
            <th className="text-left px-3 py-2">Start</th>
            <th className="text-left px-3 py-2">End</th>
            <th className="text-left px-3 py-2"></th>
          </tr>
        </thead>
        <tbody>
          {breaks.map(brk => (
            <tr
              key={brk.id}
              className="hover:bg-gray-50 dark:hover:bg-gray-900 transition"
            >
              <td className="px-3 py-2">{brk.dayOfWeek}</td>
              <td className="px-3 py-2">{brk.startTime}</td>
              <td className="px-3 py-2">{brk.endTime}</td>
              <td className="px-3 py-2 flex gap-2">
                <button
                  className="text-blue-600 hover:underline font-medium cursor-pointer"
                  onClick={() => openEdit(brk)}
                >
                  Edit
                </button>
                <button
                  className="text-red-500 hover:underline font-medium cursor-pointer"
                  onClick={() => handleDelete(brk.id)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
