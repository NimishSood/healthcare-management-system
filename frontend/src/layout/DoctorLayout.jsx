// DoctorLayout.jsx
import { Outlet } from "react-router-dom";

export default function DoctorLayout() {
  return (
    <div>
      <header>
        <h1>Doctor Dashboard</h1>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}
