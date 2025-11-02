import { useState, useEffect } from 'react';
import { Search, Bell, Mail, Users, Gamepad2, Home, Compass, BookOpen, Calendar, Award, Plus, ChevronRight, MessageSquare, Star } from 'lucide-react';
import { Game, Thread, User as UserType } from '../types';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function GamesPage() {
  const [games, setGames] = useState<Game[]>([]);
  const [threads, setThreads] = useState<Thread[]>([]);
  const [loading, setLoading] = useState(true);
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch games and threads from your API
    const fetchData = async () => {
      try {
        // Replace with actual API calls
        // const gamesResponse = await fetch('/api/games');
        // const threadsResponse = await fetch('/api/threads');
        // const gamesData = await gamesResponse.json();
        // const threadsData = await threadsResponse.json();
        
        // Temporary mock data - replace with actual API calls
        const gamesData: Game[] = [
          { id: 'game1', title: 'Elden Ring', emoji: '🏰', rating: 9.5, players: '2.3M', genre: 'Action RPG', color: 'from-amber-500 to-orange-600' },
          { id: 'game2', title: 'Baldur\'s Gate 3', emoji: '🎲', rating: 9.7, players: '1.8M', genre: 'RPG', color: 'from-purple-500 to-pink-600' },
          { id: 'game3', title: 'Cyberpunk 2077', emoji: '🌃', rating: 8.9, players: '1.5M', genre: 'RPG', color: 'from-cyan-500 to-blue-600' },
          { id: 'game4', title: 'Hades II', emoji: '⚔️', rating: 9.2, players: '890K', genre: 'Roguelike', color: 'from-red-500 to-rose-600' },
        ];
        
        const threadsData: Thread[] = [
          { id: 'thread1', title: 'Best RPGs of 2024', author: 'RPGMaster', avatar: '👑', replies: 234, views: 5420, time: '5m ago', category: 'Discussion', categoryColor: 'bg-blue-500' },
          { id: 'thread2', title: 'Elden Ring DLC Guide', author: 'Tarnished', avatar: '⚔️', replies: 156, views: 3210, time: '1h ago', category: 'Guide', categoryColor: 'bg-green-500' },
          { id: 'thread3', title: 'Speedrunning Tips', author: 'Speedster', avatar: '🏃', replies: 89, views: 1890, time: '2h ago', category: 'Tips', categoryColor: 'bg-yellow-500' },
        ];
        
        setGames(gamesData);
        setThreads(threadsData);
      } catch (error) {
        console.error('Error fetching data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center">
        <div className="text-white text-xl">Loading...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Top Navigation */}
      <nav className="bg-white/5 backdrop-blur-xl border-b border-white/10">
        <div className="max-w-7xl mx-auto px-6">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center
            ">
              <div className="flex items-center">
                <Gamepad2 className="h-8 w-8 text-cyan-400" />
                <span className="ml-2 text-xl font-bold text-white">GamersCove</span>
              </div>
              <div className="hidden md:block">
                <div className="ml-10 flex items-baseline space-x-4">
                  {[
                    { name: 'Home', icon: Home, href: '/', current: true },
                    { name: 'Discover', icon: Compass, href: '/discover', current: false },
                    { name: 'Forums', icon: BookOpen, href: '/forums', current: false },
                    { name: 'Events', icon: Calendar, href: '/events', current: false },
                    { name: 'Leaderboards', icon: Award, href: '/leaderboards', current: false },
                  ].map((item) => (
                    <a
                      key={item.name}
                      href={item.href}
                      className={`${
                        item.current
                          ? 'bg-gray-900 text-white'
                          : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                      } px-3 py-2 rounded-md text-sm font-medium flex items-center`}
                    >
                      <item.icon className="h-4 w-4 mr-1" />
                      {item.name}
                    </a>
                  ))}
                </div>
              </div>
            </div>
            
            {/* Search Bar */}
            <div className="flex-1 max-w-xl px-4">
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Search className="h-4 w-4 text-gray-400" />
                </div>
                <input
                  type="text"
                  className="block w-full pl-10 pr-3 py-2 border border-transparent rounded-md leading-5 bg-gray-700 text-gray-300 placeholder-gray-400 focus:outline-none focus:bg-white focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-cyan-500 focus:text-gray-900 sm:text-sm"
                  placeholder="Search games, threads, users..."
                />
              </div>
            </div>

            {/* User Menu */}
            <div className="flex items-center">
              <button className="p-1 rounded-full text-gray-400 hover:text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-white">
                <Bell className="h-6 w-6" />
              </button>
              <button className="ml-4 p-1 rounded-full text-gray-400 hover:text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-white">
                <Mail className="h-6 w-6" />
              </button>
              <div className="ml-4 flex items-center">
                <div className="relative">
                  <button className="flex items-center text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-white">
                    <span className="sr-only">Open user menu</span>
                    <div className="h-8 w-8 rounded-full bg-gradient-to-r from-cyan-500 to-blue-500 flex items-center justify-center text-white font-semibold">
                      {(currentUser as UserType)?.username?.charAt(0) || 'U'}
                    </div>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        {/* Games Section */}
        <section className="mb-12">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-white">Trending Games</h2>
            <button className="flex items-center text-cyan-400 hover:text-cyan-300 text-sm font-medium">
              View All
              <ChevronRight className="ml-1 h-4 w-4" />
            </button>
          </div>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {games.map((game) => (
              <div key={game.id} className="bg-gray-800 rounded-xl overflow-hidden shadow-lg hover:shadow-2xl transition-shadow duration-300">
                <div className={`h-48 ${game.color} flex items-center justify-center text-6xl`}>
                  {game.emoji}
                </div>
                <div className="p-4">
                  <h3 className="text-lg font-bold text-white mb-1">{game.title}</h3>
                  <p className="text-sm text-gray-400 mb-3">{game.genre}</p>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <Star className="h-4 w-4 text-yellow-400 mr-1" />
                      <span className="text-sm font-medium text-white">{game.rating}</span>
                    </div>
                    <div className="flex items-center">
                      <Users className="h-4 w-4 text-gray-400 mr-1" />
                      <span className="text-sm text-gray-400">{game.players}</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Threads Section */}
        <section>
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-white">Latest Discussions</h2>
            <div className="flex space-x-3">
              <button 
                onClick={() => navigate('/new-thread')}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-cyan-600 hover:bg-cyan-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-cyan-500"
              >
                <Plus className="h-4 w-4 mr-2" />
                New Thread
              </button>
            </div>
          </div>

          <div className="bg-gray-800 rounded-xl shadow overflow-hidden">
            <ul className="divide-y divide-gray-700">
              {threads.map((thread) => (
                <li key={thread.id} className="hover:bg-gray-700 transition-colors duration-150">
                  <a href={`/threads/${thread.id}`} className="block px-6 py-4">
                    <div className="flex items-center">
                      <div className="flex-shrink-0 h-10 w-10 rounded-full bg-gray-700 flex items-center justify-center text-xl">
                        {thread.avatar}
                      </div>
                      <div className="ml-4 flex-1">
                        <div className="flex items-center justify-between">
                          <p className="text-sm font-medium text-white">{thread.title}</p>
                          <div className="flex space-x-2">
                            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-500 text-white">
                              {thread.category}
                            </span>
                          </div>
                        </div>
                        <div className="mt-1 flex items-center text-sm text-gray-400">
                          <span>by {thread.author}</span>
                          <span className="mx-1">•</span>
                          <span>{thread.time}</span>
                          <span className="mx-1">•</span>
                          <div className="flex items-center">
                            <MessageSquare className="h-3.5 w-3.5 mr-1" />
                            <span>{thread.replies}</span>
                          </div>
                          <span className="mx-1">•</span>
                          <div className="flex items-center">
                            <Users className="h-3.5 w-3.5 mr-1" />
                            <span>{thread.views}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </a>
                </li>
              ))}
            </ul>
          </div>

          {/* Pagination */}
          <div className="mt-6 flex justify-center">
            <nav className="inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
              <button className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-700 bg-gray-700 text-sm font-medium text-gray-300 hover:bg-gray-600">
                <span className="sr-only">Previous</span>
                <ChevronRight className="h-5 w-5 transform rotate-180" />
              </button>
              {[1, 2, 3].map((page) => (
                <button
                  key={page}
                  className={`relative inline-flex items-center px-4 py-2 border text-sm font-medium ${
                    page === 1
                      ? 'bg-cyan-600 border-cyan-600 text-white'
                      : 'bg-gray-700 border-gray-700 text-gray-300 hover:bg-gray-600'
                  }`}
                >
                  {page}
                </button>
              ))}
              <button className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-700 bg-gray-700 text-sm font-medium text-gray-300 hover:bg-gray-600">
                <span className="sr-only">Next</span>
                <ChevronRight className="h-5 w-5" />
              </button>
            </nav>
          </div>
        </section>
      </main>
    </div>
  );
}
