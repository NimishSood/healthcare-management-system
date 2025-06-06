// src/services/slotRemovalApi.js
import axios from "axios";

export async function getMySlotRemovalRequests() {
  const res = await axios.get("/doctor/schedule/removal-requests");
  return res.data;
}

export async function submitSlotRemovalRequest({ slotType, slotId, reason }) {
  const res = await axios.post("/doctor/schedule/removal-request", {
    slotType, slotId, reason
  });
  return res.data;
}
