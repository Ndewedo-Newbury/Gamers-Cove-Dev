import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Home, Compass, BookOpen, Calendar, Award, Bell, Mail, User, Gamepad2 } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

const navItems = [
  { icon: Home, label: 'Home', path: '/' },
  { icon: Compass, label: 'Discover', path: '/discover' },
  { icon: BookOpen, label: 'Forums', path: '/forums' },
  { icon: Calendar, label: 'Events', path: '/events' },
  { icon: Award, label: 'Leaderboards', path: '/leaderboards' },
];

export default function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  return (
    <>
      {/* Desktop Navbar */}
      <nav className="bg-white/5 backdrop-blur-xl border-b border-white/10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <Link to="/" className="flex items-center">
                <div className="w-10 h-10 bg-gradient-to-br from-cyan-400 to-blue-500 rounded-xl flex items-center justify-center text-white mr-3">
                  <Gamepad2 className="w-5 h-5" />
                </div>
                <span className="text-xl font-bold bg-gradient-to-r from-cyan-400 via-blue-400 to-purple-400 bg-clip-text text-transparent">
                  Gamers Cove
                </span>
              </Link>
              <div className="hidden md:ml-10 md:flex md:space-x-8">
                {navItems.map((item) => (
                  <Link
                    key={item.path}
                    to={item.path}
                    className="text-gray-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium flex items-center"
                  >
                    <item.icon className="w-5 h-5 mr-2" />
                    {item.label}
                  </Link>
                ))}
              </div>
            </div>
            
            <div className="hidden md:flex items-center space-x-4">
              <button className="p-2 rounded-full text-gray-400 hover:text-white hover:bg-white/10">
                <Bell className="h-5 w-5" />
              </button>
              <button className="p-2 rounded-full text-gray-400 hover:text-white hover:bg-white/10">
                <Mail className="h-5 w-5" />
              </button>
              
              {currentUser ? (
                <div className="ml-4 flex items-center">
                  <div className="relative">
                    <button 
                      onClick={() => navigate('/profile')}
                      className="flex items-center space-x-2 text-sm rounded-full focus:outline-none"
                    >
                      <div className="w-8 h-8 rounded-full bg-gradient-to-br from-cyan-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold">
                        {currentUser.username?.substring(0, 2).toUpperCase() || 'U'}
                      </div>
                    </button>
                  </div>
                </div>
              ) : (
                <button 
                  onClick={() => navigate('/login')}
                  className="ml-4 px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-500 text-white text-sm font-medium rounded-md hover:opacity-90"
                >
                  Sign In
                </button>
              )}
            </div>
            
            {/* Mobile menu button */}
            <div className="-mr-2 flex items-center md:hidden">
              <button
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-white hover:bg-white/10 focus:outline-none"
              >
                <span className="sr-only">Open main menu</span>
                {isMenuOpen ? (
                  <svg className="block h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                ) : (
                  <svg className="block h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                  </svg>
                )}
              </button>
            </div>
          </div>
        </div>
        
        {/* Mobile menu */}
        {isMenuOpen && (
          <div className="md:hidden">
            <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
              {navItems.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  className="text-gray-300 hover:bg-white/10 hover:text-white block px-3 py-2 rounded-md text-base font-medium"
                >
                  <div className="flex items-center">
                    <item.icon className="w-5 h-5 mr-2" />
                    {item.label}
                  </div>
                </Link>
              ))}
              
              {!currentUser && (
                <button 
                  onClick={() => navigate('/login')}
                  className="w-full text-left px-3 py-2 rounded-md text-base font-medium text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:opacity-90"
                >
                  Sign In
                </button>
              )}
            </div>
            
            {currentUser && (
              <div className="pt-4 pb-3 border-t border-white/10">
                <div className="flex items-center px-5">
                  <div className="w-10 h-10 rounded-full bg-gradient-to-br from-cyan-400 to-blue-500 flex items-center justify-center text-white text-sm font-bold">
                    {currentUser.username?.substring(0, 2).toUpperCase() || 'U'}
                  </div>
                  <div className="ml-3">
                    <div className="text-base font-medium text-white">{currentUser.username || 'User'}</div>
                    <div className="text-sm font-medium text-cyan-400">View Profile</div>
                  </div>
                </div>
                <div className="mt-3 px-2 space-y-1">
                  <Link
                    to="/profile"
                    className="block px-3 py-2 rounded-md text-base font-medium text-gray-400 hover:text-white hover:bg-white/10"
                  >
                    Your Profile
                  </Link>
                  <Link
                    to="/settings"
                    className="block px-3 py-2 rounded-md text-base font-medium text-gray-400 hover:text-white hover:bg-white/10"
                  >
                    Settings
                  </Link>
                  <button
                    onClick={() => {}}
                    className="w-full text-left block px-3 py-2 rounded-md text-base font-medium text-gray-400 hover:text-white hover:bg-white/10"
                  >
                    Sign out
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </nav>
    </>
  );
}
