import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'

import HomePage      from './pages/HomePage'
import LoginPage     from './pages/LoginPage'
import RegisterPage  from './pages/RegisterPage'
import NotFoundPage  from './pages/NotFoundPage'
import PrivateRoute  from './components/PrivateRoute'

import PatientLayout from './layouts/PatientLayout'
import DoctorLayout  from './layouts/DoctorLayout'
import AdminLayout   from './layouts/AdminLayout'
import OwnerLayout   from './layouts/OwnerLayout'

import TailwindTest from './pages/TailwindTest'

import ColorTest from './components/ColorTest'

export default function App() {
  return (
    <Routes>
      {/* public */}
      <Route path="/"         element={<HomePage />} />
      <Route path="/login"    element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route path="/twtest" element={<TailwindTest />} />

      <Route path="/colortest" element={<ColorTest />} />

      {/* protected */}
      <Route
        path="/patient/*"
        element={<PrivateRoute><PatientLayout/></PrivateRoute>}
      />
      <Route
        path="/doctor/*"
        element={<PrivateRoute><DoctorLayout/></PrivateRoute>}
      />
      <Route
        path="/admin/*"
        element={<PrivateRoute><AdminLayout/></PrivateRoute>}
      />
      <Route
        path="/owner/*"
        element={<PrivateRoute><OwnerLayout/></PrivateRoute>}
      />

      {/* catch-all */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
