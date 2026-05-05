export function Header() {
  return (
    <header className="bg-gradient-to-r from-indigo-600 to-blue-600 text-white shadow-lg">
      <div className="container mx-auto px-4 py-6">
        <div className="flex justify-between items-center">
          <div>
            <p className="text-indigo-200 text-sm font-semibold">Volunteer Management</p>
            <h1 className="text-4xl font-bold">VolunteerHub</h1>
          </div>
          <div className="text-right">
            <p className="text-indigo-100">Discover • Apply • Check-in • Earn</p>
          </div>
        </div>
      </div>
    </header>
  )
}

export default Header
