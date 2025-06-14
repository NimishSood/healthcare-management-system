import React, { useEffect, useState } from "react";
import { useAuth } from "../../../context/AuthContext";
import {
  ClockIcon,
  BellIcon,
  ChatBubbleLeftRightIcon,
} from "@heroicons/react/24/outline";
import { format } from "date-fns";
import DoctorAppointmentList from "../components/Appointments/DoctorAppointmentList";
import {
  getNextAppointment,
  countPendingPrescriptions,
  countUnreadMessages,
} from "../../../services/doctorService";

export default function DoctorDashboardHome() {
  const { user } = useAuth();

  const [nextAppt, setNextAppt] = useState(null);
  const [pendingCount, setPendingCount] = useState(0);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const [appt, pending, unread] = await Promise.all([
          getNextAppointment(),
          countPendingPrescriptions(),
          countUnreadMessages(),
        ]);
        setNextAppt(appt);
        setPendingCount(pending);
        setUnreadCount(unread);
      } catch (err) {
        console.error("Failed to load dashboard stats", err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  const stats = [
    {
      icon: ClockIcon,
      title: "Next Appointment",
      value: loading
        ? "Loading…"
        : nextAppt
          ? format(new Date(nextAppt.appointmentTime), "MMMM d, yyyy, h:mm a")
          : "None scheduled",
      color: "bg-blue-100 text-blue-700",
    },
    {
      icon: BellIcon,
      title: "Pending Refills",
      value: loading
        ? "Loading…"
        : `${pendingCount} Request${pendingCount === 1 ? "" : "s"}`,
      color: "bg-amber-100 text-amber-700",
    },
    {
      icon: ChatBubbleLeftRightIcon,
      title: "Unread Messages",
      value: loading ? "Loading…" : unreadCount,
      color: "bg-green-100 text-green-700",
    },
  ];

  return (
    <div className="space-y-8">
      <div className="bg-white p-6 rounded-xl shadow-sm">
        <h1 className="text-2xl font-bold text-gray-800">
          Welcome back, Dr. {user?.lastName}!
        </h1>
        <p className="text-gray-600 mt-2">
          Here is a snapshot of your practice.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {stats.map(({ icon: Icon, title, value, color }) => (
          <StatCard
            key={title}
            icon={Icon}
            title={title}
            value={value}
            color={color}
          />
        ))}
      </div>

      <DoctorAppointmentList />
    </div>
  );
}

const StatCard = ({ icon: Icon, title, value, color }) => (
  <div
    className={`p-5 rounded-xl ${color.split(" ")[0]} ${color.split(" ")[1]}`}
  >
    <div className="flex items-center">
      <Icon className="h-8 w-8 mr-3" />
      <div>
        <p className="text-sm font-medium">{title}</p>
        <p className="text-xl font-semibold">{value}</p>
      </div>
    </div>
  </div>
);