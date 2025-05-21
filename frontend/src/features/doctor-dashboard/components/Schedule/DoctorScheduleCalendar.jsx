import React from 'react';
import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';

export default function DoctorScheduleCalendar({ events, onEventChange }) {
  return (
    <FullCalendar
      plugins={[timeGridPlugin, interactionPlugin]}
      initialView="timeGridWeek"
      editable={true}
      selectable={true}
      events={events}
      eventDrop={onEventChange}      // Drag/move
      eventResize={onEventChange}    // Resize
      height="auto"
      slotMinTime="07:00:00"
      slotMaxTime="21:00:00"
    />
  );
}
