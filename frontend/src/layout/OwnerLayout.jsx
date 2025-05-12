// DoctorLayout.jsx
import { Outlet } from "react-router-dom";

export default function OwnerLayout() {
  return (
    <div>
      <header>
        <h1>Owner Dashboard</h1>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}
