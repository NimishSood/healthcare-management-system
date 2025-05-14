// src/App.jsx
import React from 'react'
import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import HomePage      from './pages/HomePage'
import LoginPage     from './pages/LoginPage'
import RegisterPage  from './pages/RegisterPage'
import NotFoundPage  from './pages/NotFoundPage'
import PrivateRoute  from './components/PrivateRoute'

import PatientLayout    from './features/patient-dashboard/layouts/PatientLayout'
import DashboardHome    from './features/patient-dashboard/pages/DashboardHome'
import { AppointmentList } from './features/patient-dashboard/components/Appointments/AppointmentList'
import PrescriptionsPage from './features/patient-dashboard/pages/PrescriptionsPage'
import ProfilePage      from './features/patient-dashboard/pages/ProfilePage'

import DoctorLayout     from './features/doctor-dashboard/layouts/DoctorLayout'
import AdminLayout      from './features/admin-dashboard/layouts/AdminLayout'
import OwnerLayout      from './features/owner-dashboard/layouts/OwnerLayout'

import { Toaster } from 'react-hot-toast'
import BookAppointmentPage from './features/patient-dashboard/pages/BookAppointmentPage'
export default function App() {
  return (
    <AuthProvider>
      {/* Toast container for react-hot-toast */}
      <Toaster position="top-right" />

      <Routes>
        {/* public */}
        <Route path="/"         element={<HomePage />} />
        <Route path="/login"    element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* protected: patient */}
        <Route
          path="/patient/*"
          element={
            <PrivateRoute allowedRoles={['PATIENT']}>
              <PatientLayout />
            </PrivateRoute>
          }
        >
          <Route index                element={<DashboardHome />} />
          <Route path="appointments"  element={<AppointmentList />} />
          <Route path="appointments/book"  element={<BookAppointmentPage />} />
          <Route path="prescriptions" element={<PrescriptionsPage />} />
          <Route path="profile"       element={<ProfilePage />} />
        </Route>

        {/* protected: doctor */}
        <Route
          path="/doctor/*"
          element={
            <PrivateRoute allowedRoles={['DOCTOR']}>
              <DoctorLayout />
            </PrivateRoute>
          }
        />

        {/* protected: admin */}
        <Route
          path="/admin/*"
          element={
            <PrivateRoute allowedRoles={['ADMIN']}>
              <AdminLayout />
            </PrivateRoute>
          }
        />

        {/* protected: owner */}
        <Route
          path="/owner/*"
          element={
            <PrivateRoute allowedRoles={['OWNER']}>
              <OwnerLayout />
            </PrivateRoute>
          }
        />

        {/* catch-all */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AuthProvider>
  )
}
