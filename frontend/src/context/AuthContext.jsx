import React, { createContext, useState, useContext, useCallback } from 'react'

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(false)

  const login = useCallback(async (email, password) => {
    setLoading(true)
    try {
      const userData = {
        id: 'user-' + Math.random().toString(36).substr(2, 9),
        email,
        name: email.split('@')[0],
        role: 'VOLUNTEER'
      }
      setUser(userData)
      localStorage.setItem('user', JSON.stringify(userData))
      return userData
    } catch (err) {
      throw err
    } finally {
      setLoading(false)
    }
  }, [])

  const logout = useCallback(() => {
    setUser(null)
    localStorage.removeItem('user')
  }, [])

  const value = { user, loading, login, logout }
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
