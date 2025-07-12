// src/App.jsx
import React from 'react'
import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import HomePage      from './pages/HomePage'
import LoginPage     from './pages/LoginPage'
import RegisterPage  from './pages/RegisterPage'
import NotFoundPage  from './pages/NotFoundPage'
import PrivateRoute  from './components/PrivateRoute'
import MessagePage from './components/messaging/MessagePage'

import PatientLayout    from './features/patient-dashboard/layouts/PatientLayout'
import PatientDashboardHome    from './features/patient-dashboard/pages/DashboardHome'
import { AppointmentList } from './features/patient-dashboard/components/Appointments/AppointmentList'
import PrescriptionsPage from './features/patient-dashboard/pages/PrescriptionsPage'
import PatientProfilePage      from './features/patient-dashboard/pages/ProfilePage'

import DoctorLayout     from './features/doctor-dashboard/layouts/DoctorLayout'
import DoctorDashboardHome from './features/doctor-dashboard/pages/DashboardHome'
import DoctorAppointmentsPage from './features/doctor-dashboard/pages/AppointmentsPage'
import DoctorPrescriptionsPage from './features/doctor-dashboard/pages/PrescriptionsPage'
import DoctorProfilePage from './features/doctor-dashboard/pages/ProfilePage'
import DoctorSchedulePage from './features/doctor-dashboard/pages/DoctorSchedulePage'
import DoctorPatientsPage from './features/doctor-dashboard/pages/PatientsPage'
import DoctorPatientDetailPage from './features/doctor-dashboard/pages/DoctorPatientDetailPage'
import DoctorPrescriptionDetailPage from './features/doctor-dashboard/pages/PrescriptionDetailPage'

import AdminLayout      from './features/admin-dashboard/layouts/AdminLayout'
import AdminDashboardHome from './features/admin-dashboard/pages/DashboardHome'
import UsersPage from './features/admin-dashboard/pages/UsersPage'
import AdminProfilePage from './features/admin-dashboard/pages/ProfilePage'
import AiFeaturesPage from './features/admin-dashboard/pages/AiFeaturesPage'
import AiQueryPage from './features/admin-dashboard/pages/AiQueryPage'



import OwnerLayout      from './features/owner-dashboard/layouts/OwnerLayout'

import { Toaster } from 'react-hot-toast'
import BookAppointmentPage from './features/patient-dashboard/pages/BookAppointmentPage'
import TestPasswordPage from './features/patient-dashboard/pages/TestPasswordPage'
import { ThemeProvider } from './context/ThemeContext'
import PrescriptionDetailPage from './features/patient-dashboard/pages/PrescriptionDetailPage'
import AppHeader from './components/AppHeader'



export default function App() {
  return (
    <AuthProvider>
      <ThemeProvider>
        <AppHeader />
        {/* Toast container for react-hot-toast */}
        <Toaster position="top-right" />

        <Routes>
          {/* public */}
          <Route path="/"         element={<HomePage />} />
          <Route path="/login"    element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/test"     element={<TestPasswordPage />} />
          <Route path="/prescriptions/:id" element={<PrescriptionDetailPage />} />
        

          {/* protected: patient */}
          <Route
            path="/patient/*"
            element={
              <PrivateRoute allowedRoles={['PATIENT']}>
                <PatientLayout />
              </PrivateRoute>
            }
          >
            <Route index                element={<PatientDashboardHome />} />
            <Route path="appointments"  element={<AppointmentList />} />
            <Route path="appointments/book"  element={<BookAppointmentPage />} />
            <Route path="prescriptions" element={<PrescriptionsPage />} />
            <Route path="profile"       element={<PatientProfilePage />} />
            <Route path="messages" element={<MessagePage role="patient" />} />
            



          </Route>

          {/* protected: doctor */}
          <Route
            path="/doctor/*"
            element={
              <PrivateRoute allowedRoles={['DOCTOR']}>
                <DoctorLayout />
              </PrivateRoute>
            }
          >
            <Route index            element={<DoctorDashboardHome />} />
            <Route path="appointments"  element={<DoctorAppointmentsPage />} />
            <Route path="prescriptions" element={<DoctorPrescriptionsPage />} />
            <Route path="prescriptions/:id" element={<DoctorPrescriptionDetailPage />} />
            <Route path="patients" element={<DoctorPatientsPage />} />
            <Route path="patients/:id" element={<DoctorPatientDetailPage />} />
            <Route path="profile"       element={<DoctorProfilePage />} />
            <Route path="messages"      element={<MessagePage role="doctor" />} />
            <Route path="schedule" element={<DoctorSchedulePage />} />


            {/* You can replace the above <div> with actual imported components when ready */}
          </Route>


          {/* protected: admin */}
          <Route
            path="/admin/*"
            element={
              <PrivateRoute allowedRoles={['ADMIN']}>
                <AdminLayout />
              </PrivateRoute>
            }
            >
            <Route index element={<AdminDashboardHome />} />
            <Route path="users" element={<UsersPage />} />
            <Route path="profile" element={<AdminProfilePage />} />
            <Route path="ai" element={<AiFeaturesPage />} />
            <Route path="ai/query" element={<AiQueryPage />} />
          </Route>
          

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
      </ThemeProvider>
    </AuthProvider>
  )
}
