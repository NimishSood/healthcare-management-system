import React, { useEffect, useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import DoctorScheduleCalendar from "../components/Schedule/DoctorScheduleCalendar";
import BreaksSection from "../components/Schedule/BreaksSection";
import RecurringSlotsSection from "../components/Schedule/RecurringSlotsSection";
import OneTimeSlotsSection from "../components/Schedule/OneTimeSlotsSection";

const WEEK_DAYS = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];

function toLocalYMD(date) {
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, '0'),
    String(date.getDate()).padStart(2, '0')
  ].join('-');
}

function getCurrentWeekDates(currentDate) {
  const startOfWeek = new Date(currentDate);
  startOfWeek.setHours(0, 0, 0, 0);
  startOfWeek.setDate(currentDate.getDate() - currentDate.getDay());
  return Array.from({ length: 7 }).map((_, i) => {
    const d = new Date(startOfWeek);
    d.setDate(startOfWeek.getDate() + i);
    d.setHours(0, 0, 0, 0);
    return d;
  });
}

function mapDoctorScheduleToEvents(schedule, currentWeekStartDate) {
  const events = [];
  const weekDates = getCurrentWeekDates(currentWeekStartDate);

  schedule.recurringSlots.forEach(slot => {
    const dayIdx = WEEK_DAYS.findIndex(d => d === slot.dayOfWeek.toUpperCase());
    if (dayIdx !== -1) {
      const day = weekDates[dayIdx];
      if (day) {
        events.push({
          id: String(slot.id),
          title: 'Working Slot',
          start: `${toLocalYMD(day)}T${slot.startTime}`,
          end: `${toLocalYMD(day)}T${slot.endTime}`,
          backgroundColor: '#34d399',
          type: "RECURRING",
        });
      }
    }
  });

  schedule.oneTimeSlots.forEach(slot => {
    events.push({
      id: String(slot.id),
      title: slot.available ? 'One-Time Slot' : 'Unavailable Slot',
      start: `${slot.date}T${slot.startTime}`,
      end: `${slot.date}T${slot.endTime}`,
      backgroundColor: slot.available ? '#2563eb' : '#a1a1aa',
      textColor: '#fff',
      type: "ONE_TIME",
      available: slot.available,
    });
  });

  schedule.recurringBreaks.forEach(brk => {
    const dayIdx = WEEK_DAYS.findIndex(d => d === brk.dayOfWeek.toUpperCase());
    if (dayIdx !== -1) {
      const day = weekDates[dayIdx];
      if (day) {
        events.push({
          id: String(brk.id),
          title: 'Break',
          start: `${toLocalYMD(day)}T${brk.startTime}`,
          end: `${toLocalYMD(day)}T${brk.endTime}`,
          backgroundColor: '#ef4444',
          type: "BREAK",
        });
      }
    }
  });

  return events;
}

export default function DoctorSchedulePage() {
  const [schedule, setSchedule] = useState(null);
  const [loading, setLoading] = useState(true);

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
    <div className="max-w-6xl mx-auto py-8 px-2">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold pl-1">My Schedule (Beta)</h1>
      </div>
      {/* Responsive grid: 2 columns on md+, stacked on mobile */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-start">
        {/* Left: All slot sections */}
        <div className="flex flex-col gap-6 min-h-[700px]">
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-6">
            <div className="flex items-center justify-between mb-2">
              
            </div>
            <RecurringSlotsSection slots={schedule.recurringSlots || []} refresh={refreshSchedule} />
          </div>
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-6">
            
            <BreaksSection breaks={schedule.recurringBreaks || []} refresh={refreshSchedule} />
          </div>
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-6">
            
            <OneTimeSlotsSection slots={schedule.oneTimeSlots || []} refresh={refreshSchedule} />
          </div>
        </div>
        {/* Right: Calendar with color legend */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl shadow p-6 md:sticky md:top-8 h-fit min-h-[700px] flex flex-col">
          <DoctorScheduleCalendar events={events} refresh={refreshSchedule} />
          {/* Color legend below calendar */}
          <div className="flex gap-6 mt-4 justify-center">
            <span className="flex items-center gap-2 text-sm">
              <span className="inline-block w-3 h-3 rounded-full bg-green-400"></span> Working Slot
            </span>
            <span className="flex items-center gap-2 text-sm">
              <span className="inline-block w-3 h-3 rounded-full bg-red-500"></span> Break
            </span>
            <span className="flex items-center gap-2 text-sm">
              <span className="inline-block w-3 h-3 rounded-full bg-blue-600"></span> One-Time Slot
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
