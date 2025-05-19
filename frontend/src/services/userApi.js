// src/services/userApi.js

import { mockUsers } from "../components/messaging/mockConversations";

// Simulate a network delay
const wait = (ms) => new Promise(res => setTimeout(res, ms));

// Get all users (for new conversation/search)
export async function getAllUsers() {
  await wait(700);
  // For real API: fetch("/api/users")
  return mockUsers;
}

// Get user by ID (for chat header/details)
export async function getUserById(id) {
  await wait(400);
  // For real API: fetch(`/api/users/${id}`)
  return mockUsers.find(u => u.id === id) || null;
}
