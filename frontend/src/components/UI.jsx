import React from 'react'
import { AlertCircle } from 'lucide-react'

export function Badge({ children, variant = 'primary', size = 'md' }) {
  const variants = {
    primary: 'bg-indigo-100 text-indigo-800',
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    danger: 'bg-red-100 text-red-800'
  }

  const sizes = {
    sm: 'px-2 py-1 text-xs',
    md: 'px-3 py-1 text-sm',
    lg: 'px-4 py-2 text-base'
  }

  return (
    <span className={`inline-block rounded-full font-semibold ${variants[variant]} ${sizes[size]}`}>
      {children}
    </span>
  )
}

export function Card({ children, className = '' }) {
  return (
    <div className={`bg-white rounded-lg shadow hover:shadow-lg transition-shadow ${className}`}>
      {children}
    </div>
  )
}

export function Button({ children, variant = 'primary', size = 'md', isLoading = false, ...props }) {
  const variants = {
    primary: 'bg-indigo-600 hover:bg-indigo-700 text-white',
    secondary: 'bg-gray-200 hover:bg-gray-300 text-gray-800',
    success: 'bg-green-600 hover:bg-green-700 text-white',
    danger: 'bg-red-600 hover:bg-red-700 text-white',
    ghost: 'hover:bg-gray-100 text-gray-800'
  }

  const sizes = {
    sm: 'px-3 py-1 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg'
  }

  return (
    <button
      className={`font-medium rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed ${variants[variant]} ${sizes[size]}`}
      disabled={isLoading}
      {...props}
    >
      {isLoading ? '...' : children}
    </button>
  )
}

export function EmptyState({ icon: Icon, title, description, action }) {
  return (
    <div className="text-center py-12">
      <div className="flex justify-center mb-4">
        {Icon ? <Icon className="w-16 h-16 text-gray-300" /> : <AlertCircle className="w-16 h-16 text-gray-300" />}
      </div>
      <h3 className="text-lg font-semibold text-gray-600 mb-2">{title}</h3>
      <p className="text-gray-500 mb-4">{description}</p>
      {action}
    </div>
  )
}

export function StatCard({ label, value, icon: Icon, color = 'blue' }) {
  const colors = {
    blue: 'from-blue-50 to-blue-100 text-blue-600',
    green: 'from-green-50 to-green-100 text-green-600',
    purple: 'from-purple-50 to-purple-100 text-purple-600',
    orange: 'from-orange-50 to-orange-100 text-orange-600'
  }

  return (
    <div className={`bg-gradient-to-br ${colors[color]} rounded-lg p-6 shadow hover:shadow-lg transition`}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-gray-700">{label}</p>
          <p className="text-3xl font-bold mt-2">{value}</p>
        </div>
        {Icon && <Icon className="w-8 h-8 opacity-30" />}
      </div>
    </div>
  )
}
