import React from 'react'
import { Link } from 'react-router-dom'

export default function RegisterPage() {
  return (
    <div className="container">
      <h1>Sign Up</h1>
      <form>
        <label>Email</label>
        <input type="email" placeholder="you@example.com" />
        <label>Password</label>
        <input type="password" placeholder="••••••••" />
        <button type="submit">Register</button>
      </form>
      <p>
        Already have an account? <Link to="/login">Log In</Link>
      </p>
    </div>
  )
}
