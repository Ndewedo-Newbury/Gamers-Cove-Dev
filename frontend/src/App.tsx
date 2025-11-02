import { Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './contexts/AuthContext';
import HomePage from './pages/HomePage';
import AuthTestPage from './pages/AuthTestPage';
import DebugPage from './pages/DebugPage';
import GamesPage from './pages/GamesPage';
import Layout from './components/layout/Layout';
import Toaster from './components/ui/toaster/Toaster';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<HomePage />} />
            <Route path="auth-test" element={<AuthTestPage />} />
            <Route path="debug" element={<DebugPage />} />
            <Route path="games" element={<GamesPage />} />
            {/* Add more routes here as needed */}
          </Route>
        </Routes>
        <Toaster />
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;
