import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './components/PrivateRoute'

import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import NotFoundPage from './pages/NotFoundPage'

import PatientLayout from "./layout/PatientLayout.jsx"
import DoctorLayout  from './layout/DoctorLayout'
import AdminLayout   from './layout/AdminLayout'
import OwnerLayout   from './layout/OwnerLayout'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login"    element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route path="/" element={<Navigate to="/patient/inbox" replace />} />

          <Route path="/patient/*" element={
            <PrivateRoute><PatientLayout/></PrivateRoute>
          }/>
          <Route path="/doctor/*" element={
            <PrivateRoute><DoctorLayout/></PrivateRoute>
          }/>
          <Route path="/admin/*" element={
            <PrivateRoute><AdminLayout/></PrivateRoute>
          }/>
          <Route path="/owner/*" element={
            <PrivateRoute><OwnerLayout/></PrivateRoute>
          }/>

          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
