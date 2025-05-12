import { Outlet, Link } from 'react-router-dom';
import { UserCircleIcon, CalendarIcon, ClipboardDocumentIcon, ChatBubbleLeftIcon } from '@heroicons/react/24/outline';

export default function PatientLayout() {
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Sidebar */}
      <aside className="fixed inset-y-0 left-0 w-64 bg-white shadow-lg p-4">
        <div className="flex items-center space-x-2 mb-8 p-2 bg-blue-50 rounded">
          <UserCircleIcon className="h-8 w-8 text-blue-600" />
          <h2 className="text-xl font-semibold">Patient Portal</h2>
        </div>
        <nav className="space-y-1">
          <Link to="/patient/dashboard" className="flex items-center space-x-2 p-2 hover:bg-gray-100 rounded">
            <CalendarIcon className="h-5 w-5" />
            <span>Appointments</span>
          </Link>
          <Link to="/patient/prescriptions" className="flex items-center space-x-2 p-2 hover:bg-gray-100 rounded">
            <ClipboardDocumentIcon className="h-5 w-5" />
            <span>Prescriptions</span>
          </Link>
          <Link to="/patient/messages" className="flex items-center space-x-2 p-2 hover:bg-gray-100 rounded">
            <ChatBubbleLeftIcon className="h-5 w-5" />
            <span>Messages</span>
          </Link>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="ml-64 p-8">
        <Outlet />
      </main>
    </div>
  );
}