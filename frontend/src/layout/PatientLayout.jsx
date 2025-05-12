import { Outlet } from "react-router-dom";

export default function PatientLayout() {
  return (
    <div>
      <header>
        <h1>Patient Dashboard</h1>
        {/* You can add a navbar here later */}
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}
