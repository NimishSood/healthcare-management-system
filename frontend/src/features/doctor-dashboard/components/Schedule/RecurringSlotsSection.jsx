import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import { isRecurringPast } from "../../../../utils/dateUtils.js";
import { getMySlotRemovalRequests, submitSlotRemovalRequest } from "../../../../services/slotRemovalApi";

// Reusable error toast helper
function showApiErrorToast(err, fallback = "Something went wrong") {
  toast.error(
    err?.response?.data?.message ||
    err?.response?.data ||
    fallback
  );
}

export default function RecurringSlotsSection({ slots, refresh }) {
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  const [form, setForm] = useState({ dayOfWeek: "", startTime: "", endTime: "" });
  const [editForm, setEditForm] = useState({ id: null, dayOfWeek: "", startTime: "", endTime: "" });
  const [loading, setLoading] = useState(false);

  // Slot removal requests
  const [removalRequests, setRemovalRequests] = useState([]);
  const [showRemovalModal, setShowRemovalModal] = useState(false);
  const [removalSlot, setRemovalSlot] = useState(null);
  const [removalReason, setRemovalReason] = useState("");
  const [removalLoading, setRemovalLoading] = useState(false);

  // Autofocus for Add modal
  const dayRef = useRef();
  useEffect(() => {
    if (showAddModal && dayRef.current) dayRef.current.focus();
  }, [showAddModal]);

  // Autofocus for Edit modal
  const editDayRef = useRef();
  useEffect(() => {
    if (showEditModal && editDayRef.current) editDayRef.current.focus();
  }, [showEditModal]);

  // Load removal requests
  useEffect(() => {
    getMySlotRemovalRequests().then(setRemovalRequests).catch(() => setRemovalRequests([]));
  }, [refresh]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });
  const handleEditChange = (e) => setEditForm({ ...editForm, [e.target.name]: e.target.value });

  // Add slot
  const handleAdd = async (e) => {
    e.preventDefault();
    if (!form.dayOfWeek || !form.startTime || !form.endTime)
      return toast.error("Fill all fields!");
    if (isRecurringPast({ dayOfWeek: form.dayOfWeek, endTime: form.endTime }))
      return toast.error("Cannot add a recurring slot in the past!");
    setLoading(true);
    try {
      await axios.post("/doctor/schedule/recurring", form);
      toast.success("Recurring slot added!");
      setForm({ dayOfWeek: "", startTime: "", endTime: "" });
      setShowAddModal(false);
      refresh();
    } catch (err) {
      showApiErrorToast(err, "Failed to add slot");
    } finally {
      setLoading(false);
    }
  };

  // Open Edit modal
  const openEdit = (slot) => {
    setEditForm(slot);
    setShowEditModal(true);
  };

  // Save Edit
  const handleEdit = async (e) => {
    e.preventDefault();
    if (!editForm.dayOfWeek || !editForm.startTime || !editForm.endTime)
      return toast.error("Fill all fields!");
    setLoading(true);
    try {
      await axios.put(`/doctor/schedule/recurring/${editForm.id}`, editForm);
      toast.success("Slot updated!");
      setShowEditModal(false);
      refresh();
    } catch (err) {
      showApiErrorToast(err, "Failed to update slot");
    } finally {
      setLoading(false);
    }
  };

  // Delete slot
  const handleDelete = async (id) => {
    if (!window.confirm("Delete this slot?")) return;
    try {
      await axios.delete(`/doctor/schedule/recurring/${id}`);
      toast.success("Slot deleted!");
      refresh();
    } catch (err) {
      showApiErrorToast(err, "Failed to delete");
    }
  };

  // Request removal logic
  const openRemovalModal = (slot) => {
    setRemovalSlot(slot);
    setRemovalReason("");
    setShowRemovalModal(true);
  };

  const handleSubmitRemoval = async (e) => {
    e.preventDefault();
    setRemovalLoading(true);
    try {
      await submitSlotRemovalRequest({
        slotType: "RECURRING",
        slotId: removalSlot.id,
        reason: removalReason,
      });
      toast.success("Removal request sent!");
      setShowRemovalModal(false);
      setRemovalSlot(null);
      setRemovalReason("");
      // Refresh requests after submission
      getMySlotRemovalRequests().then(setRemovalRequests).catch(() => {});
    } catch (err) {
      showApiErrorToast(err, "Failed to send removal request");
    } finally {
      setRemovalLoading(false);
    }
  };

  return (
    <div className="mb-8">
      <div className="flex items-center justify-between mb-2">
        <h2 className="text-lg font-bold">Weekly Recurring Slots</h2>
        <button
          onClick={() => setShowAddModal(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded font-semibold hover:bg-blue-700 focus:ring-2 focus:ring-blue-300 transition"
        >
          + Add
        </button>
      </div>

      {/* Add Slot Modal */}
      {showAddModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
          <form
            className="bg-white dark:bg-gray-900 rounded-2xl p-8 shadow-2xl w-96 flex flex-col gap-4"
            onSubmit={handleAdd}
          >
            <h2 className="text-xl font-semibold mb-2">Add Recurring Slot</h2>
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

      {/* Edit Slot Modal */}
      {showEditModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
          <form
            className="bg-white dark:bg-gray-900 rounded-2xl p-8 shadow-2xl w-96 flex flex-col gap-4"
            onSubmit={handleEdit}
          >
            <h2 className="text-xl font-semibold mb-2">Edit Recurring Slot</h2>
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

      {/* Slot Removal Request Modal */}
      {showRemovalModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
          <form
            className="bg-white dark:bg-gray-900 rounded-2xl p-8 shadow-2xl w-96 flex flex-col gap-4"
            onSubmit={handleSubmitRemoval}
          >
            <h2 className="text-xl font-semibold mb-2">Request Slot Removal</h2>
            <p>Reason for removal:</p>
            <textarea
              className="border rounded w-full p-2 min-h-[60px] dark:bg-gray-800"
              value={removalReason}
              onChange={e => setRemovalReason(e.target.value)}
              required
              maxLength={1000}
              placeholder="Describe why you want to remove this slot."
            />
            <div className="flex gap-2 mt-2">
              <button
                type="submit"
                className="bg-yellow-600 text-white px-4 py-2 rounded font-semibold hover:bg-yellow-700 focus:ring-2 focus:ring-yellow-300 transition w-full"
                disabled={removalLoading || !removalReason.trim()}
              >
                {removalLoading ? "Submitting..." : "Submit"}
              </button>
              <button
                type="button"
                className="bg-gray-200 text-gray-700 px-4 py-2 rounded font-semibold hover:bg-gray-300 focus:ring-2 focus:ring-gray-400 transition w-full"
                onClick={() => setShowRemovalModal(false)}
                disabled={removalLoading}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Table Container */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-4 mb-4">
        <table className="w-full">
          <thead>
            <tr className="bg-gray-100 dark:bg-gray-800">
              <th className="text-left px-3 py-2">Day</th>
              <th className="text-left px-3 py-2">Start</th>
              <th className="text-left px-3 py-2">End</th>
              <th className="text-left px-3 py-2"></th>
            </tr>
          </thead>
          <tbody>
            {slots.map(slot => {
              const isPast = isRecurringPast(slot);
              const pendingRequest = removalRequests.find(
                req => req.slotType === "RECURRING" && req.slotId === slot.id && req.status === "PENDING"
              );
              return (
                <tr key={slot.id} className="hover:bg-gray-50 dark:hover:bg-gray-900 transition">
                  <td className="px-3 py-2">{slot.dayOfWeek}</td>
                  <td className="px-3 py-2">{slot.startTime}</td>
                  <td className="px-3 py-2">{slot.endTime}</td>
                  <td className="px-3 py-2 flex gap-2">
                    <button
                      className={`text-blue-600 hover:underline font-medium cursor-pointer ${isPast ? "opacity-40 cursor-not-allowed" : ""}`}
                      onClick={() => {
                        if (isPast) toast("Cannot edit past slots.", { icon: "⚠️" });
                        else openEdit(slot);
                      }}
                      disabled={isPast}
                      title={isPast ? "Cannot edit past slots" : "Edit"}
                    >
                      Edit
                    </button>
                    <button
                      className={`text-red-500 hover:underline font-medium cursor-pointer ${isPast ? "opacity-40 cursor-not-allowed" : ""}`}
                      onClick={() => {
                        if (isPast) toast("Cannot delete past slots.", { icon: "⚠️" });
                        else handleDelete(slot.id);
                      }}
                      disabled={isPast}
                      title={isPast ? "Cannot delete past slots" : "Delete"}
                    >
                      Delete
                    </button>
                    <button
                      className={`text-yellow-600 hover:underline font-medium cursor-pointer ${pendingRequest ? "opacity-40 cursor-not-allowed" : ""}`}
                      onClick={() => {
                        if (pendingRequest) toast("Request already pending.");
                        else openRemovalModal(slot);
                      }}
                      disabled={!!pendingRequest}
                      title={
                        pendingRequest
                          ? "Removal request pending"
                          : "Request Removal"
                      }
                    >
                      {pendingRequest ? "Pending…" : "Request Remove"}
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
