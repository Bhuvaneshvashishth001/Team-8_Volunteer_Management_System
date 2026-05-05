import React from 'react'
import { AuthProvider } from './context/AuthContext'
import { NotificationProvider } from './context/NotificationContext'
import { NotificationContainer } from './components/Notifications'
import Dashboard from './components/Dashboard'

function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <NotificationContainer />
        <Dashboard />
      </NotificationProvider>
    </AuthProvider>
  )
}

export default App
