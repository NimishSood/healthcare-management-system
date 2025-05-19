// src/components/messaging/mockConversations.js

export const mockUsers = [
  {
    id: 2,
    firstName: "John",
    lastName: "Doe",
    role: "DOCTOR"
  },
  {
    id: 3,
    firstName: "Priya",
    lastName: "Patel",
    role: "ADMIN"
  },
  {
    id: 4,
    firstName: "Emily",
    lastName: "Wong",
    role: "PATIENT"
  }
];

export const mockThreads = [
  {
    user: mockUsers[0],
    lastMessage: "See you at your appointment.",
    unread: 1
  },
  {
    user: mockUsers[1],
    lastMessage: "I have updated your prescription.",
    unread: 0
  },
  {
    user: mockUsers[2],
    lastMessage: "Thank you for your help!",
    unread: 2
  }
];

export const mockMessages = {
  2: [
    {
      id: 101,
      senderId: 1, // you
      content: "Hello Dr. Doe, I have a question.",
      timestamp: "2025-05-17T11:05:00",
      isRead: true
    },
    {
      id: 102,
      senderId: 2, // Dr. Doe
      content: "Sure! How can I help?",
      timestamp: "2025-05-17T11:06:00",
      isRead: true
    },
    {
      id: 103,
      senderId: 1,
      content: "Will my test results be ready this week?",
      timestamp: "2025-05-17T11:07:00",
      isRead: false
    }
  ],
  3: [
    {
      id: 201,
      senderId: 3,
      content: "Please fill out the feedback form.",
      timestamp: "2025-05-17T10:12:00",
      isRead: true
    },
    {
      id: 202,
      senderId: 1,
      content: "Done! Thank you.",
      timestamp: "2025-05-17T10:15:00",
      isRead: true
    }
  ],
  4: [
    {
      id: 301,
      senderId: 4,
      content: "Can you help me schedule an appointment?",
      timestamp: "2025-05-17T09:40:00",
      isRead: false
    }
  ]
};
