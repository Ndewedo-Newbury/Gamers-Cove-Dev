import api from './api';

interface LoginCredentials {
  email: string;
  password: string;
}

interface AuthResponse {
  token: string;
  user: {
    id: string;
    username: string;
    email: string;
  };
}

export const login = async (credentials: LoginCredentials): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/login', credentials);
  return response.data;
};

export const register = async (userData: {
  username: string;
  email: string;
  password: string;
}): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/register', userData);
  return response.data;
};

export const getCurrentUser = async (): Promise<{
  id: string;
  username: string;
  email: string;
  avatar?: string;
}> => {
  const response = await api.get('/auth/me');
  return response.data;
};

export const logout = async (): Promise<void> => {
  try {
    await api.post('/auth/logout');
  } finally {
    localStorage.removeItem('token');
  }
};

export const getTestToken = async () => {
  const response = await api.get('/auth-test/test-token');
  return response.data;
};

export const exchangeToken = async (customToken: string) => {
  const response = await api.post('/auth-test/exchange-token', { customToken });
  return response.data;
};
