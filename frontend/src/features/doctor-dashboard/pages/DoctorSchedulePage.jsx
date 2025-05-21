import React, { useEffect, useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import DoctorScheduleCalendar from "../components/Schedule/DoctorScheduleCalendar";
import BreaksSection from "../components/Schedule/BreaksSection"; // <--- Use imported!

function getCurrentWeekDates(currentDate) {
  const startOfWeek = new Date(currentDate);
  startOfWeek.setDate(currentDate.getDate() - currentDate.getDay());
  return Array.from({ length: 7 }).map((_, i) => {
    const d = new Date(startOfWeek);
    d.setDate(startOfWeek.getDate() + i);
    return d;
  });
}

function mapDoctorScheduleToEvents(schedule, currentWeekStartDate) {
  const events = [];
  const weekDates = getCurrentWeekDates(currentWeekStartDate);

  // Recurring slots
  schedule.recurringSlots.forEach(slot => {
    weekDates.forEach(day => {
      const dayName = day.toLocaleString('en-US', { weekday: 'long' }).toUpperCase();
      if (dayName === slot.dayOfWeek) {
        events.push({
          id: `recurring-${slot.id}-${day.toDateString()}`,
          title: 'Working Slot',
          start: `${day.toISOString().slice(0, 10)}T${slot.startTime}`,
          end:   `${day.toISOString().slice(0, 10)}T${slot.endTime}`,
          backgroundColor: '#34d399',
        });
      }
    });
  });

  // One-time slots
  schedule.oneTimeSlots.forEach(slot => {
    events.push({
      id: `onetime-${slot.id}`,
      title: slot.available ? 'One-Time Slot' : 'Unavailable Slot',
      start: `${slot.date}T${slot.startTime}`,
      end:   `${slot.date}T${slot.endTime}`,
      backgroundColor: slot.available ? '#2563eb' : '#a1a1aa',
      textColor: '#fff'
    });
  });

  // Recurring breaks
  schedule.recurringBreaks.forEach(brk => {
    weekDates.forEach(day => {
      const dayName = day.toLocaleString('en-US', { weekday: 'long' }).toUpperCase();
      if (dayName === brk.dayOfWeek) {
        events.push({
          id: `break-${brk.id}-${day.toDateString()}`,
          title: 'Break',
          start: `${day.toISOString().slice(0, 10)}T${brk.startTime}`,
          end:   `${day.toISOString().slice(0, 10)}T${brk.endTime}`,
          backgroundColor: '#ef4444',
        });
      }
    });
  });

  return events;
}

export default function DoctorSchedulePage() {
  const [schedule, setSchedule] = useState(null);
  const [loading, setLoading] = useState(true);

  // ---- Add slot modal state ----
  const [showAddModal, setShowAddModal] = useState(false);
  const [newSlot, setNewSlot] = useState({ dayOfWeek: '', startTime: '', endTime: '' });
  const [adding, setAdding] = useState(false);

  // Unified refresh method for breaks/slots
  const refreshSchedule = async () => {
    setLoading(true);
    try {
      const { data } = await axios.get("/doctor/schedule/full");
      setSchedule(data);
    } catch (err) {
      toast.error("Failed to load schedule");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refreshSchedule();
  }, []);

  const today = new Date();
  const events = schedule ? mapDoctorScheduleToEvents(schedule, today) : [];

  // ---- Add Slot Handler ----
  function handleAddSlot(e) {
    e.preventDefault();
    setAdding(true);
    axios.post('/doctor/schedule/recurring', newSlot)
      .then(() => {
        toast.success('Slot added!');
        setShowAddModal(false);
        setNewSlot({ dayOfWeek: '', startTime: '', endTime: '' });
        return refreshSchedule();
      })
      .catch(() => toast.error('Failed to add slot'))
      .finally(() => setAdding(false));
  }

  if (loading) return <div className="p-10 text-center">Loading schedule...</div>;
  if (!schedule) return <div className="p-10 text-center text-gray-400">No schedule found.</div>;

  return (
  <div className="max-w-4xl mx-auto py-8">
    {/* Header and Add Working Slot Button on the same line */}
    <div className="flex items-center justify-between mb-6">
      <h1 className="text-2xl font-bold">My Schedule (Beta)</h1>
      <button
        onClick={() => setShowAddModal(true)}
        className="bg-blue-600 text-white px-4 py-2 rounded font-semibold hover:bg-blue-700 focus:ring-2 focus:ring-blue-300 transition"
      >
        + Add Working Slot
      </button>
    </div>

    {/* Add Working Slot Modal */}
    {showAddModal && (
      <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
        <form
          className="bg-white dark:bg-gray-900 rounded-2xl p-8 shadow-2xl w-96 flex flex-col gap-4"
          onSubmit={handleAddSlot}
        >
          <h2 className="text-xl font-semibold mb-2">Add Recurring Slot</h2>
          <label className="font-medium">Day of Week
            <select
              className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
              required
              value={newSlot.dayOfWeek}
              onChange={e => setNewSlot(s => ({ ...s, dayOfWeek: e.target.value }))}
            >
              <option value="">Select day</option>
              {["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"].map(d => (
                <option key={d} value={d}>{d.charAt(0) + d.slice(1).toLowerCase()}</option>
              ))}
            </select>
          </label>
          <label className="font-medium">Start Time
            <input
              type="time"
              className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
              required
              value={newSlot.startTime}
              onChange={e => setNewSlot(s => ({ ...s, startTime: e.target.value }))}
            />
          </label>
          <label className="font-medium">End Time
            <input
              type="time"
              className="mt-1 w-full border px-3 py-2 rounded focus:ring focus:ring-blue-300"
              required
              value={newSlot.endTime}
              onChange={e => setNewSlot(s => ({ ...s, endTime: e.target.value }))}
            />
          </label>
          <div className="flex gap-2 mt-2">
            <button
              type="submit"
              className="bg-blue-600 text-white px-4 py-2 rounded font-semibold hover:bg-blue-700 focus:ring-2 focus:ring-blue-300 transition w-full"
              disabled={adding}
            >
              {adding ? "Adding..." : "Add"}
            </button>
            <button
              type="button"
              onClick={() => setShowAddModal(false)}
              className="bg-gray-200 text-gray-700 px-4 py-2 rounded font-semibold hover:bg-gray-300 focus:ring-2 focus:ring-gray-400 transition w-full"
              disabled={adding}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    )}

      {/* Breaks Section (imported, improved) */}
      <BreaksSection
        breaks={schedule.recurringBreaks || []}
        refresh={refreshSchedule}
      />

      <DoctorScheduleCalendar events={events} />
    </div>
  );
}
