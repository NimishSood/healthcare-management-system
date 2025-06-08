import React from 'react';
import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import axios from "axios";
import toast from "react-hot-toast";

export default function DoctorScheduleCalendar({ events, refresh }) {
  // Handler for event drag/drop/resize
  const handleEventChange = async (info) => {
    // info.event contains new start/end, id, extendedProps
    const { id, start, end, extendedProps } = info.event;
    const type = info.event.type || extendedProps.type;
    let apiUrl, payload;

    // 1. Prevent moving/resizing to the past
    const now = new Date();
    // End must be after start, and both after now
    if (start < now || end < now) {
      toast.error("Cannot move or resize events into the past.");
      info.revert();
      return;
    }

    try {
      if (type === "RECURRING") {
        apiUrl = `/doctor/schedule/recurring/${id}`;
        payload = {
          id,
          dayOfWeek: start.toLocaleDateString("en-US", { weekday: "long" }).toUpperCase(),
          startTime: start.toTimeString().slice(0,5),
          endTime: end.toTimeString().slice(0,5),
        };
      } else if (type === "ONE_TIME") {
        apiUrl = `/doctor/schedule/onetime/${id}`;
        payload = {
          id,
          date: start.toISOString().slice(0,10),
          startTime: start.toTimeString().slice(0,5),
          endTime: end.toTimeString().slice(0,5),
          available: info.event.available ?? extendedProps.available ?? true,
        };
      } else if (type === "BREAK") {
        apiUrl = `/doctor/schedule/break/${id}`;
        payload = {
          id,
          dayOfWeek: start.toLocaleDateString("en-US", { weekday: "long" }).toUpperCase(),
          startTime: start.toTimeString().slice(0,5),
          endTime: end.toTimeString().slice(0,5),
        };
      } else {
        toast.error("Unknown slot type. No changes saved.");
        info.revert();
        return;
      }
      await axios.put(apiUrl, payload);
      toast.success("Updated!");
      refresh();
    } catch (err) {
      toast.error(
        err?.response?.data?.message ||
        "Conflict or invalid update. No changes saved."
      );
      info.revert(); // Revert the drag/resize on the calendar!
    }
  };

  return (
    <FullCalendar
      plugins={[timeGridPlugin, interactionPlugin]}
      initialView="timeGridWeek"
      editable={true}
      selectable={true}
      events={events}
      eventDrop={handleEventChange}
      eventResize={handleEventChange}
      height="auto"
      slotMinTime="07:00:00"
      slotMaxTime="21:00:00"
    />
  );
}
