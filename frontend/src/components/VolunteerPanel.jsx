import React, { useState, useEffect } from 'react'
import { api } from '../services/api'
import { useNotification } from '../context/NotificationContext'
import { Heart, MapPin, Briefcase, Award, Clock } from 'lucide-react'
import { Badge, StatCard, Button } from './UI'

function VolunteerPanel({
  volunteers,
  selectedVolunteerId,
  setSelectedVolunteerId,
  tasks,
  events,
  setTasks,
  onRefresh
}) {
  const { addNotification } = useNotification()
  const [profile, setProfile] = useState({
    name: '',
    email: '',
    location: '',
    skills: ''
  })
  const [recommendations, setRecommendations] = useState([])
  const [dashboard, setDashboard] = useState(null)
  const [filterSkill, setFilterSkill] = useState('')
  const [filterLocation, setFilterLocation] = useState('')
  const [isSaving, setIsSaving] = useState(false)

  const currentVolunteer = volunteers.find(v => v.id === selectedVolunteerId)

  useEffect(() => {
    if (currentVolunteer) {
      setProfile({
        name: currentVolunteer.name || '',
        email: currentVolunteer.email || '',
        location: currentVolunteer.location || '',
        skills: (currentVolunteer.skills || []).join(', ')
      })
      loadDashboard()
    }
  }, [selectedVolunteerId])

  async function loadDashboard() {
    try {
      const data = await api.get(`/volunteers/${selectedVolunteerId}/dashboard`)
      setDashboard(data)
    } catch (err) {
      console.error(err)
    }
  }

  async function handleSaveProfile(e) {
    e.preventDefault()
    setIsSaving(true)
    try {
      const body = {
        name: profile.name,
        email: profile.email,
        role: 'VOLUNTEER',
        location: profile.location,
        skills: profile.skills.split(',').map(s => s.trim()).filter(Boolean)
      }
      
      if (selectedVolunteerId) {
        await api.put(`/volunteers/${selectedVolunteerId}`, body)
      } else {
        await api.post('/volunteers', body)
      }
      addNotification('Profile saved successfully!', 'success')
      onRefresh()
    } catch (err) {
      addNotification('Error saving profile: ' + err.message, 'error')
    } finally {
      setIsSaving(false)
    }
  }

  async function handleFindTasks() {
    try {
      const params = new URLSearchParams()
      if (filterSkill) params.append('skill', filterSkill)
      if (filterLocation) params.append('location', filterLocation)
      
      const data = await api.get(`/volunteers/${selectedVolunteerId}/recommendations?${params}`)
      setRecommendations(data)
      addNotification(`Found ${data.length} matching tasks!`, 'success')
    } catch (err) {
      addNotification('Error searching tasks: ' + err.message, 'error')
    }
  }

  async function handleApply(shiftId) {
    try {
      await api.post('/applications', {
        volunteerId: selectedVolunteerId,
        shiftId: shiftId
      })
      addNotification('Application submitted successfully!', 'success')
      loadDashboard()
    } catch (err) {
      addNotification('Error applying: ' + err.message, 'error')
    }
  }

  return (
    <div className="space-y-6">
      {/* Profile Section */}
      <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-indigo-600">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-3xl font-bold text-gray-800">My Profile</h2>
          <select
            value={selectedVolunteerId || ''}
            onChange={(e) => setSelectedVolunteerId(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
          >
            {volunteers.map(v => (
              <option key={v.id} value={v.id}>{v.name}</option>
            ))}
          </select>
        </div>

        <form onSubmit={handleSaveProfile} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Full Name</label>
            <input
              type="text"
              placeholder="John Doe"
              value={profile.name}
              onChange={(e) => setProfile({ ...profile, name: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
            <input
              type="email"
              placeholder="john@example.com"
              value={profile.email}
              onChange={(e) => setProfile({ ...profile, email: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Location</label>
            <input
              type="text"
              placeholder="New York"
              value={profile.location}
              onChange={(e) => setProfile({ ...profile, location: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Skills</label>
            <input
              type="text"
              placeholder="Teaching, First Aid, Coding"
              value={profile.skills}
              onChange={(e) => setProfile({ ...profile, skills: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            />
          </div>
          <Button
            type="submit"
            variant="success"
            size="lg"
            isLoading={isSaving}
            className="md:col-span-2 w-full"
          >
            Save Profile
          </Button>
        </form>
      </div>

      {/* Find Tasks Section */}
      <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-green-600">
        <h2 className="text-3xl font-bold text-gray-800 mb-6">🎯 Find Tasks</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Filter by Skill</label>
            <input
              type="text"
              placeholder="e.g., Teaching"
              value={filterSkill}
              onChange={(e) => setFilterSkill(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Filter by Location</label>
            <input
              type="text"
              placeholder="e.g., Manhattan"
              value={filterLocation}
              onChange={(e) => setFilterLocation(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            />
          </div>
          <div className="flex items-end">
            <Button
              onClick={handleFindTasks}
              variant="success"
              size="lg"
              className="w-full"
            >
              🔍 Search Tasks
            </Button>
          </div>
        </div>

        {recommendations.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {recommendations.map(rec => (
              <div key={rec.id} className="border border-gray-200 rounded-lg p-5 hover:shadow-lg hover:border-indigo-400 transition">
                <div className="flex justify-between items-start mb-3">
                  <h3 className="text-lg font-semibold text-gray-800">{rec.title}</h3>
                  <Badge variant="primary" size="sm">{rec.department}</Badge>
                </div>
                <div className="space-y-2 mb-4">
                  <div className="flex items-center gap-2 text-gray-600">
                    <MapPin size={18} className="text-gray-400" />
                    <span>{rec.location}</span>
                  </div>
                  <div className="flex items-center gap-2 text-gray-600">
                    <Briefcase size={18} className="text-gray-400" />
                    <span>{rec.department}</span>
                  </div>
                </div>
                <p className="text-gray-600 text-sm mb-4">{rec.description}</p>
                <Button
                  onClick={() => handleApply(rec.id)}
                  variant="primary"
                  size="md"
                  className="w-full"
                >
                  Apply for Shift
                </Button>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-center py-8">Search for tasks to see available opportunities</p>
        )}
      </div>

      {/* Dashboard Section */}
      {dashboard && (
        <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-purple-600">
          <h2 className="text-3xl font-bold text-gray-800 mb-6">📊 My Dashboard</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <StatCard
              label="Hours Served"
              value={dashboard.hoursServed || 0}
              icon={Clock}
              color="blue"
            />
            <StatCard
              label="Badges Earned"
              value={dashboard.badges?.length || 0}
              icon={Award}
              color="green"
            />
            <StatCard
              label="Applications"
              value={dashboard.applications?.length || 0}
              icon={Heart}
              color="purple"
            />
          </div>

          {dashboard.badges && dashboard.badges.length > 0 && (
            <div className="mt-6">
              <h3 className="text-lg font-semibold text-gray-800 mb-3">🏆 Badges</h3>
              <div className="flex flex-wrap gap-2">
                {dashboard.badges.map(badge => (
                  <Badge key={badge.id} variant="success">
                    {badge.name}
                  </Badge>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default VolunteerPanel
