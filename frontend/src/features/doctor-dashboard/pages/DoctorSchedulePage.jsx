import React, { useEffect, useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import DoctorScheduleCalendar from "../components/Schedule/DoctorScheduleCalendar";
import BreaksSection from "../components/Schedule/BreaksSection";

// JS weekday order
const WEEK_DAYS = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];

// Return 7 dates starting from most recent Sunday (calendar's first column!)
function getCurrentWeekDates(currentDate) {
  // Create a new date to avoid mutation bugs
  const today = new Date(currentDate);
  const dayOfWeek = today.getDay(); // 0=Sun, 1=Mon, ...
  const sunday = new Date(today);
  sunday.setHours(0,0,0,0); // Remove time part for clarity
  sunday.setDate(today.getDate() - dayOfWeek); // Always roll back to Sunday

  // DEBUG: Print the week starting date and check it matches your calendar's first column
  console.log("=== Week Dates Mapping ===");
  console.log("calendar column 1 (should match):", sunday.toISOString().slice(0,10));

  // Build array of 7 dates (Sunday to Saturday)
  const weekDates = Array.from({ length: 7 }).map((_, i) => {
    const d = new Date(sunday);
    d.setDate(sunday.getDate() + i);
    return d;
  });
  // Print mapping for verification
  weekDates.forEach((d, idx) => {
    console.log(`weekDates[${idx}]: ${d.toISOString().slice(0,10)} (${WEEK_DAYS[d.getDay()]})`);
  });
  return weekDates;
}

// Main mapping logic (always match slot dayOfWeek to weekDates index)
function mapDoctorScheduleToEvents(schedule, currentWeekStartDate) {
  const events = [];
  const weekDates = getCurrentWeekDates(currentWeekStartDate);

  // Recurring slots (map Java dayOfWeek string to weekDates index)
  schedule.recurringSlots.forEach(slot => {
    const dayIdx = WEEK_DAYS.findIndex(d => d === slot.dayOfWeek.toUpperCase());
    if (dayIdx !== -1) {
      const day = weekDates[dayIdx];
      if (day) {
        console.log(`[SLOT] id:${slot.id}, dayOfWeek:${slot.dayOfWeek}, dayIdx:${dayIdx}, date:${day.toISOString().slice(0,10)}`);
        events.push({
          id: `recurring-${slot.id}-${day.toDateString()}`,
          title: 'Working Slot',
          start: `${day.toISOString().slice(0, 10)}T${slot.startTime}`,
          end: `${day.toISOString().slice(0, 10)}T${slot.endTime}`,
          backgroundColor: '#34d399',
        });
      }
    }
  });

  // One-time slots
  schedule.oneTimeSlots.forEach(slot => {
    console.log(`[ONETIME SLOT] id:${slot.id}, date:${slot.date}, startTime:${slot.startTime}, endTime:${slot.endTime}`);
    events.push({
      id: `onetime-${slot.id}`,
      title: slot.available ? 'One-Time Slot' : 'Unavailable Slot',
      start: `${slot.date}T${slot.startTime}`,
      end: `${slot.date}T${slot.endTime}`,
      backgroundColor: slot.available ? '#2563eb' : '#a1a1aa',
      textColor: '#fff'
    });
  });

  // Recurring breaks
  schedule.recurringBreaks.forEach(brk => {
    const dayIdx = WEEK_DAYS.findIndex(d => d === brk.dayOfWeek.toUpperCase());
    if (dayIdx !== -1) {
      const day = weekDates[dayIdx];
      if (day) {
        console.log(`[BREAK] id:${brk.id}, dayOfWeek:${brk.dayOfWeek}, dayIdx:${dayIdx}, date:${day.toISOString().slice(0,10)}`);
        events.push({
          id: `break-${brk.id}-${day.toDateString()}`,
          title: 'Break',
          start: `${day.toISOString().slice(0, 10)}T${brk.startTime}`,
          end: `${day.toISOString().slice(0, 10)}T${brk.endTime}`,
          backgroundColor: '#ef4444',
        });
      }
    }
  });

  // Final events log
  console.log("=== EVENTS SENT TO CALENDAR ===");
  events.forEach(ev => {
    console.log(`[${ev.title}] ${ev.start} to ${ev.end} (bg:${ev.backgroundColor})`);
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

      {/* Breaks Section */}
      <BreaksSection
        breaks={schedule.recurringBreaks || []}
        refresh={refreshSchedule}
      />

      <DoctorScheduleCalendar events={events} />
    </div>
  );
}
