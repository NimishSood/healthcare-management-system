// src/utils/dateUtils.js
export function isOneTimeSlotPast({ date, endTime }) {
  if (!date || !endTime) return false;
  const end = new Date(`${date}T${endTime}`);
  return end < new Date();
}

export function isRecurringPast({ dayOfWeek, endTime }) {
  if (!dayOfWeek || !endTime) return false;
  const days = ["SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"];
  const dayIndex = typeof dayOfWeek === "string" ? days.indexOf(dayOfWeek.toUpperCase()) : dayOfWeek;
  if (dayIndex === -1) return false;
  const now = new Date();
  const todayIndex = now.getDay();
  const offset = (dayIndex - todayIndex + 7) % 7; // days until next occurrence
  const nextDate = new Date(now);
  nextDate.setDate(now.getDate() + offset);
  const isoDate = nextDate.toISOString().slice(0, 10);
  const end = new Date(`${isoDate}T${endTime}`);
  return end < now;
}