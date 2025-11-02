import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getTestToken, exchangeToken } from '../services/authService';
import { useAuth } from '../contexts/AuthContext';

const AuthTestPage = () => {
  const [testResult, setTestResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const { currentUser, isAuthenticated, login } = useAuth();

  const handleTestAuth = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      // Step 1: Get test token
      const tokenData = await getTestToken();
      console.log('Test token response:', tokenData);
      
      if (!tokenData.customToken) {
        throw new Error('No custom token received');
      }
      
      // Step 2: Exchange for ID token
      const authData = await exchangeToken(tokenData.customToken);
      console.log('Exchange token response:', authData);
      
      if (authData.idToken) {
        // Step 3: Login with the ID token
        login(authData.idToken);
        setTestResult({
          message: 'Authentication successful!',
          user: authData,
        });
      } else {
        throw new Error('No ID token received');
      }
    } catch (err) {
      console.error('Auth test failed:', err);
      setError(err instanceof Error ? err.message : 'An unknown error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6 bg-white/5 rounded-xl border border-white/10 backdrop-blur-sm">
      <h1 className="text-2xl font-bold text-white mb-6">Authentication Test</h1>
      
      <div className="space-y-6">
        <div className="p-4 bg-slate-800/50 rounded-lg">
          <h2 className="text-lg font-semibold text-white mb-2">Current Auth Status</h2>
          <pre className="text-sm bg-slate-900/50 p-3 rounded overflow-auto text-green-400">
            {JSON.stringify(
              {
                isAuthenticated,
                currentUser,
              },
              null,
              2
            )}
          </pre>
        </div>

        <div className="p-4 bg-slate-800/50 rounded-lg">
          <h2 className="text-lg font-semibold text-white mb-4">Test Authentication</h2>
          <button
            onClick={handleTestAuth}
            disabled={isLoading}
            className={`px-4 py-2 rounded-md font-medium ${
              isLoading
                ? 'bg-gray-500 cursor-not-allowed'
                : 'bg-gradient-to-r from-cyan-500 to-blue-500 hover:from-cyan-600 hover:to-blue-600'
            } text-white`}
          >
            {isLoading ? 'Testing...' : 'Test Authentication'}
          </button>
        </div>

        {testResult && (
          <div className="p-4 bg-green-900/20 border border-green-500/30 rounded-lg">
            <h3 className="text-lg font-semibold text-green-400 mb-2">Test Result</h3>
            <pre className="text-sm bg-black/30 p-3 rounded overflow-auto">
              {JSON.stringify(testResult, null, 2)}
            </pre>
          </div>
        )}

        {error && (
          <div className="p-4 bg-red-900/20 border border-red-500/30 rounded-lg">
            <h3 className="text-lg font-semibold text-red-400 mb-2">Error</h3>
            <div className="text-red-300">{error}</div>
            <details className="mt-2">
              <summary className="text-sm text-red-400 cursor-pointer">Show details</summary>
              <pre className="text-xs bg-black/30 p-3 mt-2 rounded overflow-auto">
                {error}
              </pre>
            </details>
          </div>
        )}

        {isAuthenticated && (
          <div className="p-4 bg-blue-900/20 border border-blue-500/30 rounded-lg">
            <h3 className="text-lg font-semibold text-blue-400 mb-2">Test Protected Endpoint</h3>
            <button
              onClick={async () => {
                try {
                  const response = await fetch('/api/auth-test/test-user-data');
                  const data = await response.json();
                  console.log('Test user data:', data);
                  setTestResult({
                    message: 'Protected endpoint response',
                    data,
                  });
                } catch (err) {
                  console.error('Failed to fetch protected data:', err);
                  setError(err instanceof Error ? err.message : 'Failed to fetch protected data');
                }
              }}
              className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-md font-medium"
            >
              Test Protected Endpoint
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuthTestPage;
