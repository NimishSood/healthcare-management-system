import React, { useEffect, useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import RecurringSlotsSection from "../components/Schedule/RecurringSlotsSection";
import OneTimeSlotsSection from "../components/Schedule/OneTimeSlotsSection";
import BreaksSection from "../components/Schedule/BreaksSection";

export default function DoctorSchedulePage() {
  const [schedule, setSchedule] = useState(null);
  const [loading, setLoading] = useState(true);
  const [tab, setTab] = useState("recurring"); // 'recurring' | 'onetime' | 'breaks'

  // Fetch schedule
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

  const refresh = () => {
    setLoading(true);
    axios.get("/doctor/schedule/full")
      .then(({ data }) => setSchedule(data))
      .catch(() => toast.error("Failed to refresh schedule"))
      .finally(() => setLoading(false));
  };

  if (loading) return <div className="p-10 text-center">Loading schedule...</div>;
  if (!schedule) return <div className="p-10 text-center text-gray-400">No schedule found.</div>;

  return (
    <div className="max-w-4xl mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">My Schedule</h1>
      {/* Tabs */}
      <div className="flex gap-4 mb-6">
        <button className={tab === "recurring" ? "font-bold underline" : ""} onClick={() => setTab("recurring")}>Weekly Slots</button>
        <button className={tab === "onetime" ? "font-bold underline" : ""} onClick={() => setTab("onetime")}>One-Time Slots</button>
        <button className={tab === "breaks" ? "font-bold underline" : ""} onClick={() => setTab("breaks")}>Breaks</button>
      </div>

      {/* Sections */}
      {tab === "recurring" && <RecurringSlotsSection slots={schedule.recurringSlots} refresh={refresh} />}
      {tab === "onetime" && <OneTimeSlotsSection slots={schedule.oneTimeSlots} refresh={refresh} />}
      {tab === "breaks" && <BreaksSection breaks={schedule.recurringBreaks} refresh={refresh} />}
    </div>
  );
}
