// src/features/doctor-dashboard/components/Schedule/RecurringSlotsSection.jsx
import React, { useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";

export default function RecurringSlotsSection({ slots, refresh }) {
  const [form, setForm] = useState({ dayOfWeek: "", startTime: "", endTime: "" });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleAdd = async () => {
    if (!form.dayOfWeek || !form.startTime || !form.endTime) return toast.error("Fill all fields!");
    setLoading(true);
    try {
      await axios.post("/doctor/schedule/recurring", form);
      toast.success("Recurring slot added!");
      setForm({ dayOfWeek: "", startTime: "", endTime: "" });
      refresh();
    } catch {
      toast.error("Failed to add slot");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this slot?")) return;
    try {
      await axios.delete(`/doctor/schedule/recurring/${id}`);
      toast.success("Slot deleted!");
      refresh();
    } catch {
      toast.error("Failed to delete");
    }
  };

  return (
    <div>
      <h2 className="text-lg font-semibold mb-2">Weekly Recurring Slots</h2>
      {/* Add slot form */}
      <div className="flex gap-2 mb-4">
        <select name="dayOfWeek" value={form.dayOfWeek} onChange={handleChange} className="border rounded px-2 py-1">
          <option value="">Day</option>
          {["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"].map(d =>
            <option key={d} value={d}>{d}</option>
          )}
        </select>
        <input type="time" name="startTime" value={form.startTime} onChange={handleChange} className="border rounded px-2 py-1"/>
        <input type="time" name="endTime" value={form.endTime} onChange={handleChange} className="border rounded px-2 py-1"/>
        <button className="bg-blue-600 text-white px-3 rounded" onClick={handleAdd} disabled={loading}>Add</button>
      </div>
      {/* List slots */}
      <table className="w-full mb-4">
        <thead>
          <tr>
            <th className="text-left">Day</th><th className="text-left">Start</th><th className="text-left">End</th><th></th>
          </tr>
        </thead>
        <tbody>
          {slots.map(slot => (
            <tr key={slot.id}>
              <td>{slot.dayOfWeek}</td>
              <td>{slot.startTime}</td>
              <td>{slot.endTime}</td>
              <td>
                <button className="text-red-500" onClick={() => handleDelete(slot.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
