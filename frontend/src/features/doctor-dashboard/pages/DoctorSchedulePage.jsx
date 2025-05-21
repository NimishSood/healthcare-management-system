import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import DoctorScheduleCalendar from "../components/Schedule/DoctorScheduleCalendar";

// Helper: Get current week dates (Sunday to Saturday)
function getCurrentWeekDates(currentDate) {
  const startOfWeek = new Date(currentDate);
  startOfWeek.setDate(currentDate.getDate() - currentDate.getDay());
  return Array.from({ length: 7 }).map((_, i) => {
    const d = new Date(startOfWeek);
    d.setDate(startOfWeek.getDate() + i);
    return d;
  });
}

// Helper: Map backend data to calendar events
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

  useEffect(() => {
    async function fetchSchedule() {
      try {
        setLoading(true);
        const { data } = await axios.get("/doctor/schedule/full");
        setSchedule(data);
      } catch (err) {
        toast.error("Failed to load schedule");
      } finally {
        setLoading(false);
      }
    }
    fetchSchedule();
  }, []);

  // The only change: derive events array from API response
  const today = new Date();
  const events = schedule ? mapDoctorScheduleToEvents(schedule, today) : [];

  if (loading) return <div className="p-10 text-center">Loading schedule...</div>;
  if (!schedule) return <div className="p-10 text-center text-gray-400">No schedule found.</div>;

  return (
    <div className="max-w-4xl mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">My Schedule (Beta)</h1>
      <DoctorScheduleCalendar events={events} />
      {/* Later: Add buttons to add/edit/remove */}
    </div>
  );
}