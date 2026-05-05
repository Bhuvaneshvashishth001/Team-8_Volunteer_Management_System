import React, { useState, useEffect } from 'react'
import Header from './Header'
import { LoginForm, UserMenu } from './Auth'
import VolunteerPanel from './VolunteerPanel'
import AdminPanel from './AdminPanel'
import { useAuth } from '../context/AuthContext'
import { useNotification } from '../context/NotificationContext'
import { api } from '../services/api'

function Dashboard() {
  const { user } = useAuth()
  const { addNotification } = useNotification()
  const [volunteers, setVolunteers] = useState([])
  const [events, setEvents] = useState([])
  const [tasks, setTasks] = useState([])
  const [shifts, setShifts] = useState([])
  const [selectedVolunteerId, setSelectedVolunteerId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [activeTab, setActiveTab] = useState('volunteer')

  useEffect(() => {
    loadBootstrap()
  }, [])

  async function loadBootstrap() {
    try {
      setLoading(true)
      const data = await api.get('/bootstrap')
      setVolunteers(data.volunteers || [])
      setEvents(data.events || [])
      setTasks(data.tasks || [])
      setShifts(data.shifts || [])
      setSelectedVolunteerId(data.volunteers?.[0]?.id)
      setError(null)
      addNotification('Data loaded successfully', 'success')
    } catch (err) {
      setError('Failed to load data. Make sure backend is running.')
      addNotification('Failed to load data', 'error')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  if (!user) {
    return <LoginForm />
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-4 border-indigo-200 border-t-indigo-600"></div>
          <p className="mt-4 text-gray-600">Loading VolunteerHub...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-gradient-to-r from-indigo-600 to-blue-600 text-white shadow-lg">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold">VolunteerHub</h1>
          </div>
          <UserMenu />
        </div>
      </header>

      {error && (
        <div className="bg-red-50 border-l-4 border-red-400 p-4 m-4">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <div className="flex gap-2 px-4 py-4 border-b bg-white sticky top-0 z-40">
        <button
          onClick={() => setActiveTab('volunteer')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            activeTab === 'volunteer'
              ? 'bg-indigo-600 text-white shadow'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          📋 Volunteer
        </button>
        <button
          onClick={() => setActiveTab('admin')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            activeTab === 'admin'
              ? 'bg-indigo-600 text-white shadow'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          👨‍💼 Organizer
        </button>
      </div>

      <div className="container mx-auto p-4 max-w-7xl">
        {activeTab === 'volunteer' ? (
          <VolunteerPanel
            volunteers={volunteers}
            selectedVolunteerId={selectedVolunteerId}
            setSelectedVolunteerId={setSelectedVolunteerId}
            tasks={tasks}
            events={events}
            setTasks={setTasks}
            onRefresh={loadBootstrap}
          />
        ) : (
          <AdminPanel
            events={events}
            tasks={tasks}
            shifts={shifts}
            setEvents={setEvents}
            setTasks={setTasks}
            setShifts={setShifts}
            onRefresh={loadBootstrap}
          />
        )}
      </div>
    </div>
  )
}

export default Dashboard
