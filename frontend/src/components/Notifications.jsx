import React from 'react'
import { AlertCircle, CheckCircle, InfoIcon, AlertTriangle, X } from 'lucide-react'
import { useNotification } from '../context/NotificationContext'

function Notification({ notification }) {
  const { removeNotification } = useNotification()
  
  const icons = {
    error: <AlertCircle className="w-5 h-5" />,
    success: <CheckCircle className="w-5 h-5" />,
    info: <InfoIcon className="w-5 h-5" />,
    warning: <AlertTriangle className="w-5 h-5" />
  }

  const colors = {
    error: 'bg-red-50 border-red-200 text-red-800',
    success: 'bg-green-50 border-green-200 text-green-800',
    info: 'bg-blue-50 border-blue-200 text-blue-800',
    warning: 'bg-yellow-50 border-yellow-200 text-yellow-800'
  }

  const iconColors = {
    error: 'text-red-600',
    success: 'text-green-600',
    info: 'text-blue-600',
    warning: 'text-yellow-600'
  }

  return (
    <div className={`border-l-4 ${colors[notification.type]} p-4 rounded flex items-center gap-3 animate-in slide-in-from-top-5 duration-300`}>
      <div className={iconColors[notification.type]}>
        {icons[notification.type]}
      </div>
      <p className="flex-1 text-sm font-medium">{notification.message}</p>
      <button
        onClick={() => removeNotification(notification.id)}
        className="opacity-50 hover:opacity-100 transition"
      >
        <X className="w-4 h-4" />
      </button>
    </div>
  )
}

export function NotificationContainer() {
  const { notifications } = useNotification()

  return (
    <div className="fixed top-4 right-4 z-50 space-y-3 max-w-md">
      {notifications.map(notification => (
        <Notification key={notification.id} notification={notification} />
      ))}
    </div>
  )
}
