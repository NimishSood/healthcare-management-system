// src/utils/dateUtils.js
export function isOneTimeSlotPast(slot) {
  if (!slot.date || !slot.endTime) return false;
  const slotEnd = new Date(`${slot.date}T${slot.endTime}`);
  return slotEnd < new Date();
}

export function isRecurringPast(slot) {
  if (!slot.dayOfWeek || !slot.endTime) return false;
  const WEEK_DAYS = ["SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"];
  const today = new Date();
  const slotDayIdx = WEEK_DAYS.indexOf(slot.dayOfWeek.toUpperCase());
  const todayIdx = today.getDay();
  if (slotDayIdx < todayIdx) return true;
  if (slotDayIdx > todayIdx) return false;
  // Same day: compare end time (in minutes)
  const now = today.getHours() * 60 + today.getMinutes();
  const [h, m] = slot.endTime.split(':').map(Number);
  const slotEndMins = h * 60 + m;
  return now > slotEndMins;
}
