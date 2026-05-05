import React, { useState } from 'react'
import { api } from '../services/api'
import { useNotification } from '../context/NotificationContext'
import { Plus, RefreshCw, Users, ClipboardList, Clock, Eye } from 'lucide-react'
import { Button, StatCard, Badge } from './UI'

function AdminPanel({ events, tasks, shifts, setEvents, setTasks, setShifts, onRefresh }) {
  const { addNotification } = useNotification()
  const [isCreatingEvent, setIsCreatingEvent] = useState(false)
  const [isCreatingTask, setIsCreatingTask] = useState(false)
  const [isCreatingShift, setIsCreatingShift] = useState(false)

  const [eventForm, setEventForm] = useState({
    name: '',
    cause: '',
    location: '',
    checkInCode: '',
    description: ''
  })

  const [taskForm, setTaskForm] = useState({
    eventId: '',
    title: '',
    department: '',
    location: '',
    cause: '',
    targetVolunteers: 5,
    requiredSkills: '',
    description: ''
  })

  const [shiftForm, setShiftForm] = useState({
    taskId: '',
    startTime: '',
    endTime: '',
    capacity: 5
  })

  async function handleCreateEvent(e) {
    e.preventDefault()
    setIsCreatingEvent(true)
    try {
      await api.post('/events', eventForm)
      setEventForm({ name: '', cause: '', location: '', checkInCode: '', description: '' })
      onRefresh()
      addNotification('Event created successfully! 🎉', 'success')
    } catch (err) {
      addNotification('Error creating event: ' + err.message, 'error')
    } finally {
      setIsCreatingEvent(false)
    }
  }

  async function handleCreateTask(e) {
    e.preventDefault()
    setIsCreatingTask(true)
    try {
      const body = {
        ...taskForm,
        requiredSkills: taskForm.requiredSkills.split(',').map(s => s.trim()).filter(Boolean)
      }
      await api.post('/tasks', body)
      setTaskForm({
        eventId: '',
        title: '',
        department: '',
        location: '',
        cause: '',
        targetVolunteers: 5,
        requiredSkills: '',
        description: ''
      })
      onRefresh()
      addNotification('Task created successfully! 🎯', 'success')
    } catch (err) {
      addNotification('Error creating task: ' + err.message, 'error')
    } finally {
      setIsCreatingTask(false)
    }
  }

  async function handleCreateShift(e) {
    e.preventDefault()
    setIsCreatingShift(true)
    try {
      await api.post('/shifts', shiftForm)
      setShiftForm({ taskId: '', startTime: '', endTime: '', capacity: 5 })
      onRefresh()
      addNotification('Shift created successfully! ⏰', 'success')
    } catch (err) {
      addNotification('Error creating shift: ' + err.message, 'error')
    } finally {
      setIsCreatingShift(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard label="Total Events" value={events.length} icon={Users} color="blue" />
        <StatCard label="Tasks Created" value={tasks.length} icon={ClipboardList} color="green" />
        <StatCard label="Available Shifts" value={shifts.length} icon={Clock} color="purple" />
        <button
          onClick={onRefresh}
          className="flex items-center justify-center gap-2 bg-gradient-to-br from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white rounded-lg shadow hover:shadow-lg transition font-semibold"
        >
          <RefreshCw size={20} />
          Refresh
        </button>
      </div>

      {/* Create Event */}
      <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-blue-600">
        <h2 className="text-3xl font-bold text-gray-800 mb-6 flex items-center gap-2">
          <Plus size={24} /> Create Event
        </h2>
        <form onSubmit={handleCreateEvent} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Event Name</label>
            <input
              type="text"
              placeholder="Community Clean-up Day"
              value={eventForm.name}
              onChange={(e) => setEventForm({ ...eventForm, name: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Cause</label>
            <input
              type="text"
              placeholder="Environment"
              value={eventForm.cause}
              onChange={(e) => setEventForm({ ...eventForm, cause: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Location</label>
            <input
              type="text"
              placeholder="Central Park"
              value={eventForm.location}
              onChange={(e) => setEventForm({ ...eventForm, location: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Check-in Code</label>
            <input
              type="text"
              placeholder="CLEAN-2024"
              value={eventForm.checkInCode}
              onChange={(e) => setEventForm({ ...eventForm, checkInCode: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          <textarea
            placeholder="Event description..."
            value={eventForm.description}
            onChange={(e) => setEventForm({ ...eventForm, description: e.target.value })}
            className="md:col-span-2 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            rows="3"
          />
          <Button
            type="submit"
            variant="success"
            size="lg"
            isLoading={isCreatingEvent}
            className="md:col-span-2 w-full"
          >
            Create Event
          </Button>
        </form>
      </div>

      {/* Create Task */}
      <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-green-600">
        <h2 className="text-3xl font-bold text-gray-800 mb-6 flex items-center gap-2">
          <Plus size={24} /> Create Task
        </h2>
        <form onSubmit={handleCreateTask} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Select Event</label>
            <select
              value={taskForm.eventId}
              onChange={(e) => setTaskForm({ ...taskForm, eventId: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
            >
              <option value="">Choose an event...</option>
              {events.map(e => (
                <option key={e.id} value={e.id}>{e.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Task Title</label>
            <input
              type="text"
              placeholder="Trash Collection"
              value={taskForm.title}
              onChange={(e) => setTaskForm({ ...taskForm, title: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Department</label>
            <input
              type="text"
              placeholder="Operations"
              value={taskForm.department}
              onChange={(e) => setTaskForm({ ...taskForm, department: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Location</label>
            <input
              type="text"
              placeholder="South Entrance"
              value={taskForm.location}
              onChange={(e) => setTaskForm({ ...taskForm, location: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Cause</label>
            <input
              type="text"
              placeholder="Environment"
              value={taskForm.cause}
              onChange={(e) => setTaskForm({ ...taskForm, cause: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Target Volunteers</label>
            <input
              type="number"
              placeholder="5"
              min="1"
              value={taskForm.targetVolunteers}
              onChange={(e) => setTaskForm({ ...taskForm, targetVolunteers: parseInt(e.target.value) })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            />
          </div>
          <input
            type="text"
            placeholder="Required skills (comma separated)"
            value={taskForm.requiredSkills}
            onChange={(e) => setTaskForm({ ...taskForm, requiredSkills: e.target.value })}
            className="md:col-span-2 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
          />
          <textarea
            placeholder="Task description..."
            value={taskForm.description}
            onChange={(e) => setTaskForm({ ...taskForm, description: e.target.value })}
            className="md:col-span-2 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            rows="3"
          />
          <Button
            type="submit"
            variant="success"
            size="lg"
            isLoading={isCreatingTask}
            className="md:col-span-2 w-full"
          >
            Create Task
          </Button>
        </form>
      </div>

      {/* Create Shift */}
      <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-purple-600">
        <h2 className="text-3xl font-bold text-gray-800 mb-6 flex items-center gap-2">
          <Plus size={24} /> Create Shift
        </h2>
        <form onSubmit={handleCreateShift} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Select Task</label>
            <select
              value={shiftForm.taskId}
              onChange={(e) => setShiftForm({ ...shiftForm, taskId: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              required
            >
              <option value="">Choose a task...</option>
              {tasks.map(t => (
                <option key={t.id} value={t.id}>{t.title}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Capacity</label>
            <input
              type="number"
              placeholder="5"
              min="1"
              value={shiftForm.capacity}
              onChange={(e) => setShiftForm({ ...shiftForm, capacity: parseInt(e.target.value) })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Start Time</label>
            <input
              type="datetime-local"
              value={shiftForm.startTime}
              onChange={(e) => setShiftForm({ ...shiftForm, startTime: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">End Time</label>
            <input
              type="datetime-local"
              value={shiftForm.endTime}
              onChange={(e) => setShiftForm({ ...shiftForm, endTime: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              required
            />
          </div>
          <Button
            type="submit"
            variant="success"
            size="lg"
            isLoading={isCreatingShift}
            className="md:col-span-2 w-full"
          >
            Create Shift
          </Button>
        </form>
      </div>

      {/* Events List */}
      {events.length > 0 && (
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h2 className="text-3xl font-bold text-gray-800 mb-6 flex items-center gap-2">
            <Eye size={24} /> All Events
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {events.map(event => (
              <div key={event.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-lg transition">
                <h3 className="text-lg font-semibold text-gray-800">{event.name}</h3>
                <div className="mt-3 space-y-2">
                  <p className="text-sm"><span className="font-medium">Cause:</span> {event.cause}</p>
                  <p className="text-sm"><span className="font-medium">Location:</span> {event.location}</p>
                  <p className="text-sm"><span className="font-medium">Code:</span> <Badge variant="primary">{event.checkInCode}</Badge></p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminPanel
