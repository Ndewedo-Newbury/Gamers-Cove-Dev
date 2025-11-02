import { useState } from 'react';

export default function DebugPage() {
  const [response, setResponse] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const testEndpoint = async (endpoint: string) => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await fetch(`/api${endpoint}`);
      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.message || 'Request failed');
      }
      setResponse(data);
    } catch (err) {
      console.error(`Error testing ${endpoint}:`, err);
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6 bg-white/5 rounded-xl border border-white/10 backdrop-blur-sm">
      <h1 className="text-2xl font-bold text-white mb-6">API Debugger</h1>
      
      <div className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button
            onClick={() => testEndpoint('/auth-test/test-token')}
            disabled={isLoading}
            className="p-3 bg-blue-600 hover:bg-blue-700 text-white rounded-md font-medium disabled:opacity-50"
          >
            Test /auth-test/test-token
          </button>
          <button
            onClick={() => testEndpoint('/auth-test/exchange-token')}
            disabled={isLoading}
            className="p-3 bg-blue-600 hover:bg-blue-700 text-white rounded-md font-medium disabled:opacity-50"
          >
            Test /auth-test/exchange-token
          </button>
          <button
            onClick={async () => {
              try {
                setIsLoading(true);
                // First get a token
                const tokenRes = await fetch('/api/auth-test/test-token');
                const tokenData = await tokenRes.json();
                
                if (!tokenRes.ok) throw new Error('Failed to get token');
                
                // Then exchange it
                const exchangeRes = await fetch('/api/auth-test/exchange-token', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ customToken: tokenData.customToken })
                });
                const exchangeData = await exchangeRes.json();
                
                if (!exchangeRes.ok) throw new Error('Failed to exchange token');
                
                // Then try to access protected endpoint
                const protectedRes = await fetch('/api/auth-test/test-user-data', {
                  headers: {
                    'Authorization': `Bearer ${exchangeData.idToken}`
                  }
                });
                const protectedData = await protectedRes.json();
                
                if (!protectedRes.ok) throw new Error(protectedData.message || 'Failed to access protected endpoint');
                
                setResponse(protectedData);
              } catch (err) {
                console.error('Error in test flow:', err);
                setError(err instanceof Error ? err.message : 'Unknown error occurred');
              } finally {
                setIsLoading(false);
              }
            }}
            disabled={isLoading}
            className="p-3 bg-green-600 hover:bg-green-700 text-white rounded-md font-medium disabled:opacity-50"
          >
            Test Full Auth Flow
          </button>
        </div>

        {isLoading && (
          <div className="p-4 bg-blue-900/20 border border-blue-500/30 rounded-lg">
            <div className="flex items-center space-x-2 text-blue-400">
              <div className="w-4 h-4 border-2 border-blue-400 border-t-transparent rounded-full animate-spin"></div>
              <span>Loading...</span>
            </div>
          </div>
        )}

        {error && (
          <div className="p-4 bg-red-900/20 border border-red-500/30 rounded-lg">
            <h3 className="text-lg font-semibold text-red-400 mb-2">Error</h3>
            <div className="text-red-300">{error}</div>
          </div>
        )}

        {response && (
          <div className="p-4 bg-green-900/20 border border-green-500/30 rounded-lg overflow-auto">
            <h3 className="text-lg font-semibold text-green-400 mb-2">Response</h3>
            <pre className="text-sm bg-black/30 p-3 rounded">
              {JSON.stringify(response, null, 2)}
            </pre>
          </div>
        )}
      </div>
    </div>
  );
}
