import React, { useEffect, useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import DoctorScheduleCalendar from "../components/Schedule/DoctorScheduleCalendar";
import BreaksSection from "../components/Schedule/BreaksSection";
import RecurringSlotsSection from "../components/Schedule/RecurringSlotsSection";

// Used for mapping days between frontend and backend
const WEEK_DAYS = ["SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"];

// Format date to YYYY-MM-DD in **LOCAL TIME** (not UTC!)
function toLocalYMD(date) {
  // Gets local year, month (0-based), day; pads as needed
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, '0'),
    String(date.getDate()).padStart(2, '0')
  ].join('-');
}

// Returns array of Dates (week starts Sunday)
function getCurrentWeekDates(currentDate) {
  // Always start from Sunday (JavaScript default)
  const startOfWeek = new Date(currentDate);
  startOfWeek.setHours(0,0,0,0);
  startOfWeek.setDate(currentDate.getDate() - currentDate.getDay());
  const weekDates = Array.from({ length: 7 }).map((_, i) => {
    const d = new Date(startOfWeek);
    d.setDate(startOfWeek.getDate() + i);
    d.setHours(0,0,0,0);
    return d;
  });
  // Debug printout for sanity
  console.log("=== Week Dates Mapping ===");
  weekDates.forEach((date, idx) => {
    console.log(`weekDates[${idx}]: ${toLocalYMD(date)} (${WEEK_DAYS[idx]})`);
  });
  return weekDates;
}

function mapDoctorScheduleToEvents(schedule, currentWeekStartDate) {
  const events = [];
  const weekDates = getCurrentWeekDates(currentWeekStartDate);

  // Recurring slots
  schedule.recurringSlots.forEach(slot => {
    const dayIdx = WEEK_DAYS.findIndex(d => d === slot.dayOfWeek.toUpperCase());
    if (dayIdx !== -1) {
      const day = weekDates[dayIdx];
      if (day) {
        console.log(`[SLOT] id:${slot.id}, dayOfWeek:${slot.dayOfWeek}, dayIdx:${dayIdx}, date:${toLocalYMD(day)}`);
        events.push({
          id: `recurring-${slot.id}-${day.toDateString()}`,
          title: 'Working Slot',
          start: `${toLocalYMD(day)}T${slot.startTime}`,
          end:   `${toLocalYMD(day)}T${slot.endTime}`,
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
      end:   `${slot.date}T${slot.endTime}`,
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
        console.log(`[BREAK] id:${brk.id}, dayOfWeek:${brk.dayOfWeek}, dayIdx:${dayIdx}, date:${toLocalYMD(day)}`);
        events.push({
          id: `break-${brk.id}-${day.toDateString()}`,
          title: 'Break',
          start: `${toLocalYMD(day)}T${brk.startTime}`,
          end:   `${toLocalYMD(day)}T${brk.endTime}`,
          backgroundColor: '#ef4444',
        });
      }
    }
  });

  // Final debug output
  console.log("=== EVENTS SENT TO CALENDAR ===");
  events.forEach(ev =>
    console.log(
      `[${ev.title}] ${ev.start} to ${ev.end} (bg:${ev.backgroundColor})`
    )
  );

  return events;
}

export default function DoctorSchedulePage() {
  const [schedule, setSchedule] = useState(null);
  const [loading, setLoading] = useState(true);

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

  if (loading) return <div className="p-10 text-center">Loading schedule...</div>;
  if (!schedule) return <div className="p-10 text-center text-gray-400">No schedule found.</div>;

  return (
    <div className="max-w-4xl mx-auto py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">My Schedule (Beta)</h1>
      </div>

      {/* Recurring Working Slots (card layout) */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-4 mb-8">
        <RecurringSlotsSection
          slots={schedule.recurringSlots || []}
          refresh={refreshSchedule}
        />
      </div>

      {/* Breaks Section (card layout) */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-4 mb-8">
        <BreaksSection
          breaks={schedule.recurringBreaks || []}
          refresh={refreshSchedule}
        />
      </div>

      {/* Calendar (card layout) */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-4 mb-8">
        <DoctorScheduleCalendar events={events} />
      </div>
    </div>
  );
}
