// DoctorLayout.jsx
import { Outlet } from "react-router-dom";

export default function AdminLayout() {
  return (
    <div>
      <header>
        <h1>Admin Dashboard</h1>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}
