export interface Game {
  id: string;
  title: string;
  emoji: string;
  rating: number;
  players: string;
  genre: string;
  color: string;
}

export interface Thread {
  id: string;
  title: string;
  author: string;
  avatar: string;
  replies: number;
  views: number;
  time: string;
  category: string;
  categoryColor: string;
}

export interface User {
  id: string;
  username: string;
  displayName: string;
  email: string;
  avatar?: string;
  level?: number;
  notifications?: number;
  messages?: number;
}
