// src/features/doctor-dashboard/components/Schedule/OneTimeSlotsSection.jsx
import React, { useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";

export default function OneTimeSlotsSection({ slots, refresh }) {
  const [form, setForm] = useState({ date: "", startTime: "", endTime: "", available: true });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(f => ({ ...f, [name]: type === "checkbox" ? checked : value }));
  };

  const handleAdd = async () => {
    if (!form.date || !form.startTime || !form.endTime) return toast.error("Fill all fields!");
    setLoading(true);
    try {
      await axios.post("/doctor/schedule/onetime", form);
      toast.success("One-time slot added!");
      setForm({ date: "", startTime: "", endTime: "", available: true });
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
      await axios.delete(`/doctor/schedule/onetime/${id}`);
      toast.success("Slot deleted!");
      refresh();
    } catch {
      toast.error("Failed to delete");
    }
  };

  return (
    <div>
      <h2 className="text-lg font-semibold mb-2">One-Time Slots</h2>
      <div className="flex gap-2 mb-4">
        <input type="date" name="date" value={form.date} onChange={handleChange} className="border rounded px-2 py-1"/>
        <input type="time" name="startTime" value={form.startTime} onChange={handleChange} className="border rounded px-2 py-1"/>
        <input type="time" name="endTime" value={form.endTime} onChange={handleChange} className="border rounded px-2 py-1"/>
        <label className="flex items-center">
          <input type="checkbox" name="available" checked={form.available} onChange={handleChange} />
          <span className="ml-1">Available</span>
        </label>
        <button className="bg-blue-600 text-white px-3 rounded" onClick={handleAdd} disabled={loading}>Add</button>
      </div>
      <table className="w-full mb-4">
        <thead>
          <tr>
            <th className="text-left">Date</th><th className="text-left">Start</th><th className="text-left">End</th><th>Available</th><th></th>
          </tr>
        </thead>
        <tbody>
          {slots.map(slot => (
            <tr key={slot.id}>
              <td>{slot.date}</td>
              <td>{slot.startTime}</td>
              <td>{slot.endTime}</td>
              <td>{slot.available ? "Yes" : "No"}</td>
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
