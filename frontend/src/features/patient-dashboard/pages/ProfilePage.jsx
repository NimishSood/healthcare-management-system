// src/features/patient-dashboard/pages/ProfilePage.jsx
import React, { useState, useEffect } from 'react'
import axios from 'axios'
import { useAuth } from '../../../context/AuthContext'
import {
  UserCircleIcon,
  EnvelopeIcon,
  PhoneIcon,
  LockClosedIcon,
  EyeIcon,
  EyeSlashIcon,
  CheckCircleIcon,
  XCircleIcon,
  ArrowRightOnRectangleIcon,
  PencilSquareIcon,
  XMarkIcon
} from '@heroicons/react/24/outline'
import { useNavigate } from 'react-router-dom'

export default function ProfilePage() {
  const { logout } = useAuth()
  const navigate = useNavigate()

  // ─── Profile state ─────────────────────────────────────────────────────────
  const [original, setOriginal] = useState(null)
  const [profile, setProfile]   = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    insuranceProvider: ''
  })
  const [editing, setEditing]       = useState(false)
  const [profileMsg, setProfileMsg] = useState('')
  const [loading, setLoading]       = useState(true)

  // ─── Password state ─────────────────────────────────────────────────────────
  const [pwData, setPwData]       = useState({
    oldPassword: '',
    newPassword: '',
    confirmNew: ''
  })
  const [showPw, setShowPw]       = useState(false)
  const [pwValid, setPwValid]     = useState({
    minLength: false,
    hasUpper: false,
    hasLower: false,
    hasNumber: false,
    hasSpecial: false,
    matchesConfirm: false
  })
  const [pwMsg, setPwMsg]         = useState('')
  const [changingPw, setChangingPw] = useState(false)

  const specialChars = /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]+/

  // ─── Load profile ───────────────────────────────────────────────────────────
  useEffect(() => {
    axios.get('/patient/profile')
      .then(res => {
        const d = res.data
        const norm = {
          firstName:        d.firstName || '',
          lastName:         d.lastName  || '',
          email:            d.email     || '',
          phoneNumber:      d.phoneNumber      || '',
          insuranceProvider:d.insuranceProvider || ''
        }
        setOriginal(norm)
        setProfile(norm)
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  // ─── Validate new password ──────────────────────────────────────────────────
  useEffect(() => {
    const p = pwData.newPassword
    setPwValid({
      minLength:      p.length >= 8,
      hasUpper:       /[A-Z]/.test(p),
      hasLower:       /[a-z]/.test(p),
      hasNumber:      /\d/.test(p),
      hasSpecial:     specialChars.test(p),
      matchesConfirm: p === pwData.confirmNew && p !== ''
    })
  }, [pwData.newPassword, pwData.confirmNew])

  // ─── Handlers ───────────────────────────────────────────────────────────────
  const startEdit = () => {
    setProfileMsg('')
    setEditing(true)
  }
  const cancelEdit = () => {
    setProfile(original)
    setProfileMsg('')
    setEditing(false)
  }
  const saveProfile = async () => {
    setProfileMsg('')
    try {
      await axios.put('/patient/profile', profile)
      setOriginal(profile)
      setProfileMsg('Profile updated successfully.')
      setEditing(false)
    } catch (e) {
      setProfileMsg(e.response?.data || 'Failed to update profile.')
    }
  }

  const changePassword = async () => {
    if (!Object.values(pwValid).every(Boolean)) {
      setPwMsg('Please meet all password requirements.')
      return
    }
    setPwMsg(''); setChangingPw(true)
    try {
      await axios.post('/patient/change-password', {
        oldPassword: pwData.oldPassword,
        newPassword: pwData.newPassword
      })
      setPwMsg('Password changed. Please log in again.')
      logout()
      navigate('/login')
    } catch (e) {
      setPwMsg(e.response?.data || 'Failed to change password.')
    } finally {
      setChangingPw(false)
    }
  }

  const deleteAccount = async () => {
    if (!window.confirm('Delete your account? This cannot be undone.')) return
    try {
      await axios.delete('/patient/delete-account')
      logout()
      navigate('/')
    } catch (e) {
      console.error('Delete account failed', e)
    }
  }

  if (loading) return <div className="p-6">Loading…</div>

  // Avatar initials
  const initials = `${profile.firstName[0] || ''}${profile.lastName[0] || ''}`.toUpperCase()

  return (
    <div className="space-y-8 p-6">
      {/* ─── Profile Card ───────────────────────────────────────────── */}
      <div className="bg-white p-6 rounded-xl shadow">
        <div className="flex justify-between items-center mb-4">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-blue-500 text-white rounded-full flex items-center justify-center text-lg">
              {initials}
            </div>
            <h2 className="text-2xl font-semibold">Your Profile</h2>
          </div>
          <div className="space-x-2">
            <button
              onClick={logout}
              className="inline-flex items-center px-3 py-1 border rounded hover:bg-gray-50"
            >
              <ArrowRightOnRectangleIcon className="h-5 w-5 mr-1"/> Logout
            </button>
            {editing
              ? <button
                  onClick={cancelEdit}
                  className="inline-flex items-center px-3 py-1 border rounded hover:bg-gray-50"
                >
                  <XMarkIcon className="h-5 w-5 mr-1"/> Cancel
                </button>
              : <button
                  onClick={startEdit}
                  className="inline-flex items-center px-3 py-1 border rounded hover:bg-gray-50"
                >
                  <PencilSquareIcon className="h-5 w-5 mr-1"/> Edit Profile
                </button>
            }
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* First Name */}
          <div>
            <label className="block text-sm font-medium mb-1">First Name</label>
            <div className="relative">
              <UserCircleIcon className="h-5 w-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                type="text"
                disabled={!editing}
                className={`w-full pl-10 py-2 border rounded-lg ${
                  editing ? 'focus:ring focus:ring-blue-200' : 'bg-gray-100 text-gray-600'
                }`}
                value={profile.firstName}
                onChange={e => setProfile({ ...profile, firstName: e.target.value })}
              />
            </div>
          </div>

          {/* Last Name */}
          <div>
            <label className="block text-sm font-medium mb-1">Last Name</label>
            <div className="relative">
              <UserCircleIcon className="h-5 w-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                type="text"
                disabled={!editing}
                className={`w-full pl-10 py-2 border rounded-lg ${
                  editing ? 'focus:ring focus:ring-blue-200' : 'bg-gray-100 text-gray-600'
                }`}
                value={profile.lastName}
                onChange={e => setProfile({ ...profile, lastName: e.target.value })}
              />
            </div>
          </div>

          {/* Email */}
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <div className="relative">
              <EnvelopeIcon className="h-5 w-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                type="email"
                disabled={!editing}
                className={`w-full pl-10 py-2 border rounded-lg ${
                  editing ? 'focus:ring focus:ring-blue-200' : 'bg-gray-100 text-gray-600'
                }`}
                value={profile.email}
                onChange={e => setProfile({ ...profile, email: e.target.value })}
              />
            </div>
          </div>

          {/* Phone */}
          <div>
            <label className="block text-sm font-medium mb-1">Phone</label>
            <div className="relative">
              <PhoneIcon className="h-5 w-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                type="tel"
                disabled={!editing}
                className={`w-full pl-10 py-2 border rounded-lg ${
                  editing ? 'focus:ring focus:ring-blue-200' : 'bg-gray-100 text-gray-600'
                }`}
                value={profile.phoneNumber}
                onChange={e => setProfile({ ...profile, phoneNumber: e.target.value })}
              />
            </div>
          </div>

          {/* Insurance Provider */}
          <div className="md:col-span-2">
            <label className="block text-sm font-medium mb-1">Insurance Provider</label>
            <input
              type="text"
                disabled={!editing}
                className={`w-full py-2 border rounded-lg ${
                  editing ? 'focus:ring focus:ring-blue-200' : 'bg-gray-100 text-gray-600'
                }`}
                value={profile.insuranceProvider}
                onChange={e => setProfile({ ...profile, insuranceProvider: e.target.value })}
            />
          </div>
        </div>

        {editing && (
          <button
            onClick={saveProfile}
            className="mt-6 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
          >
            Save Changes
          </button>
        )}

        {profileMsg && (
          <p className="mt-4 text-sm text-green-700">{profileMsg}</p>
        )}
      </div>

      {/* ─── Change Password Card ─────────────────────────────────────── */}
      <div className="bg-white p-6 rounded-xl shadow">
        <h2 className="text-2xl font-semibold mb-4">Change Password</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Current Password */}
          <div>
            <label className="block text-sm font-medium mb-1">Current Password</label>
            <div className="relative">
              <LockClosedIcon className="h-5 w-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                type={showPw ? 'text' : 'password'}
                className="w-full pl-10 pr-10 py-2 border rounded-lg focus:ring focus:ring-blue-200"
                value={pwData.oldPassword}
                onChange={e => setPwData({ ...pwData, oldPassword: e.target.value })}
              />
              <button
                type="button"
                onClick={() => setShowPw(!showPw)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
              >
                {showPw ? <EyeSlashIcon className="h-5 w-5"/> : <EyeIcon className="h-5 w-5"/>}
              </button>
            </div>
          </div>

          {/* New Password */}
          <div>
            <label className="block text-sm font-medium mb-1">New Password</label>
            <div className="relative">
              <LockClosedIcon className="h-5 w-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                type={showPw ? 'text' : 'password'}
                className="w-full pl-10 pr-10 py-2 border rounded-lg focus:ring focus:ring-blue-200"
                value={pwData.newPassword}
                onChange={e => setPwData({ ...pwData, newPassword: e.target.value })}
              />
            </div>
          </div>

          {/* Confirm New Password */}
          <div>
            <label className="block text-sm font-medium mb-1">Confirm New Password</label>
            <div className="relative">
              <LockClosedIcon className="h-5 w-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                type={showPw ? 'text' : 'password'}
                className="w-full pl-10 pr-10 py-2 border rounded-lg focus:ring focus:ring-blue-200"
                value={pwData.confirmNew}
                onChange={e => setPwData({ ...pwData, confirmNew: e.target.value })}
              />
            </div>
          </div>
        </div>

        {/* Password Rules */}
        <ul className="mt-4 grid grid-cols-2 gap-2 text-sm">
          {Object.entries(pwValid).map(([k,v]) => (
            <li key={k} className="flex items-center space-x-2">
              {v
                ? <CheckCircleIcon className="h-4 w-4 text-green-600"/>
                : <XCircleIcon className="h-4 w-4 text-red-600"/>}
              <span className={v ? 'text-green-700' : 'text-gray-600'}>
                {{
                  minLength:      'At least 8 characters',
                  hasUpper:       'One uppercase letter',
                  hasLower:       'One lowercase letter',
                  hasNumber:      'One number',
                  hasSpecial:     'One special character',
                  matchesConfirm: 'Matches confirmation'
                }[k]}
              </span>
            </li>
          ))}
        </ul>

        <button
          onClick={changePassword}
          disabled={changingPw}
          className={`mt-6 w-full md:w-auto bg-blue-600 text-white px-6 py-2 rounded-lg ${
            changingPw ? 'opacity-50 cursor-not-allowed' : 'hover:bg-blue-700'
          } transition`}
        >
          {changingPw ? 'Changing…' : 'Change Password'}
        </button>

        {pwMsg && (
          <p className={`mt-4 text-sm ${
            pwMsg.startsWith('Password changed') ? 'text-green-700' : 'text-red-600'
          }`}>
            {pwMsg}
          </p>
        )}
      </div>

      {/* ─── Delete Account ───────────────────────────────────────────── */}
      <div className="text-center">
        <button
          onClick={deleteAccount}
          className="text-red-600 hover:underline"
        >
          Delete My Account
        </button>
      </div>
    </div>
  )
}
