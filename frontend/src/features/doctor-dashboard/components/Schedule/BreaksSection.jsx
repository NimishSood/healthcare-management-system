// src/features/doctor-dashboard/components/Schedule/BreaksSection.jsx
import React, { useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";

export default function BreaksSection({ breaks, refresh }) {
  const [form, setForm] = useState({ dayOfWeek: "", startTime: "", endTime: "" });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleAdd = async () => {
    if (!form.dayOfWeek || !form.startTime || !form.endTime) return toast.error("Fill all fields!");
    setLoading(true);
    try {
      await axios.post("/doctor/schedule/break", form);
      toast.success("Break added!");
      setForm({ dayOfWeek: "", startTime: "", endTime: "" });
      refresh();
    } catch {
      toast.error("Failed to add break");
    } finally {
      setLoading(false);
    }
  };

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
    <div>
      <h2 className="text-lg font-semibold mb-2">Weekly Breaks</h2>
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
      <table className="w-full mb-4">
        <thead>
          <tr>
            <th className="text-left">Day</th><th className="text-left">Start</th><th className="text-left">End</th><th></th>
          </tr>
        </thead>
        <tbody>
          {breaks.map(brk => (
            <tr key={brk.id}>
              <td>{brk.dayOfWeek}</td>
              <td>{brk.startTime}</td>
              <td>{brk.endTime}</td>
              <td>
                <button className="text-red-500" onClick={() => handleDelete(brk.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
