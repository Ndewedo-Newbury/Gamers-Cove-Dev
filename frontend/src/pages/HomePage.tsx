import { Link } from 'react-router-dom';

export default function HomePage() {
  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto text-center">
        <h1 className="text-5xl font-bold mb-6 text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 via-blue-400 to-purple-400">
          Welcome to Gamers Cove
        </h1>
        <p className="text-xl text-gray-300 mb-8">
          Join our community of passionate gamers, share your experiences, and discover new games.
        </p>
        
        <div className="flex flex-col sm:flex-row justify-center gap-4">
          <Link 
            to="/auth-test" 
            className="px-8 py-4 bg-gradient-to-r from-cyan-500 to-blue-500 hover:from-cyan-600 hover:to-blue-600 text-white font-medium rounded-lg text-lg transition-colors"
          >
            Test Authentication
          </Link>
          <Link 
            to="/games" 
            className="px-8 py-4 bg-transparent border-2 border-gray-600 text-white hover:bg-gray-800/50 font-medium rounded-lg text-lg transition-colors"
          >
            Browse Games
          </Link>
        </div>
      </div>
    </div>
  );
}
