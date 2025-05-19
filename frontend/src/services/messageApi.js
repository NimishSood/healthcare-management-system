import axios from "axios";

const API = axios.create({
  baseURL: "/api/messages",
  // Optionally add interceptors or auth headers if you want
});

API.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// **NEW: Fetch conversation threads from backend**
export async function getThreads() {
  const res = await API.get("/threads");
  return res.data;
}

// Fetch all messages with a specific user
export function getMessagesWithUser(userId) {
  return API.get(`/with/${userId}`).then(res => res.data);
}

// Send a message to a user
export function sendMessage(receiverId, content) {
  return API.post('/send', { receiverId, content }).then(res => res.data);
}

// Mark all messages from a conversation as read
export function markConversationAsRead(userId) {
  return API.put(`/with/${userId}/read`);
}

// If you still use this elsewhere (e.g., for new chat partners)
export async function getMessagingPartners() {
  const res = await API.get("/partners");
  return res.data;
}

// Fetch user info (for displaying names/avatars etc)
export async function getUser(userId) {
  const res = await axios.get(`/api/users/${userId}`);
  return res.data;
}
