-- ===========================================
-- GamersCove Database Initialization (V1)
-- Full Schema + Triggers + Sample Seed Data
-- ===========================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===========================================
-- Trigger Function for Auto-Updating updated_at
-- ===========================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ===========================================
-- USERS TABLE
-- ===========================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    firebase_uid VARCHAR(128) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    avatar_url TEXT,
    bio TEXT,
    preferred_platforms TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    email VARCHAR(255),
    password_hash TEXT,
    display_name VARCHAR(100),
    last_login_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN', 'MODERATOR')),
    email_verified BOOLEAN DEFAULT FALSE,
    profile_visibility VARCHAR(20) DEFAULT 'PUBLIC' CHECK (profile_visibility IN ('PUBLIC','FRIENDS_ONLY','PRIVATE')),
    favorite_genres TEXT[],
    favorite_games TEXT[],
    timezone VARCHAR(50),
    locale VARCHAR(10),
    last_active_at TIMESTAMPTZ,
    status_message TEXT,
    favorite_game_ids TEXT,
    gamertags_visibility VARCHAR(10) DEFAULT 'FRIENDS'
);

CREATE INDEX idx_users_firebase_uid ON users(firebase_uid);
CREATE INDEX idx_users_username ON users(username);

CREATE TRIGGER update_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE SEQUENCE IF NOT EXISTS game_sequence START 1 INCREMENT 1;
-- ===========================================
-- GAMES TABLE
-- ===========================================
CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,
    external_api_id VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    developer VARCHAR(100),
    publisher VARCHAR(100),
    cover_image_url TEXT,
    background_image_url TEXT,
    website_url TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    average_rating NUMERIC(3,2) DEFAULT 0.0,
    total_ratings INT DEFAULT 0,
    platforms TEXT,
    genres TEXT,
    tags TEXT[],
    esrb_rating VARCHAR(50),
    metacritic_score INT,
    playtime INT,
    is_multiplayer BOOLEAN DEFAULT FALSE,
    is_online BOOLEAN DEFAULT FALSE,
    is_free_to_play BOOLEAN DEFAULT FALSE,
    price NUMERIC(10,2),
    discount_percent INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_early_access BOOLEAN DEFAULT FALSE,
    is_indie BOOLEAN DEFAULT FALSE,
    title VARCHAR(200) NOT NULL DEFAULT 'Untitled Game'
);

CREATE INDEX idx_games_external_api_id ON games(external_api_id);

CREATE TRIGGER update_games_updated_at
BEFORE UPDATE ON games
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ===========================================
-- REVIEWS TABLE
-- ===========================================
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id BIGINT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_public BOOLEAN DEFAULT TRUE,
    playtime_hours INT,
    platform_played_on VARCHAR(50),
    is_spoiler BOOLEAN DEFAULT FALSE,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    is_helpful_count INT DEFAULT 0,
    is_not_helpful_count INT DEFAULT 0,
    is_edited BOOLEAN DEFAULT FALSE,
    last_edited_at TIMESTAMPTZ,
    CONSTRAINT unique_user_game_review UNIQUE (user_id, game_id)
);

CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_game_id ON reviews(game_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

CREATE TRIGGER update_reviews_updated_at
BEFORE UPDATE ON reviews
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ===========================================
-- FRIENDSHIPS TABLE
-- ===========================================
CREATE TABLE friendships (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','ACCEPTED','DECLINED')),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_friendship UNIQUE (requester_id, receiver_id),
    CONSTRAINT check_self_friendship CHECK (requester_id <> receiver_id)
);

CREATE INDEX idx_friendships_requester_id ON friendships(requester_id);
CREATE INDEX idx_friendships_receiver_id ON friendships(receiver_id);
CREATE INDEX idx_friendships_status ON friendships(status);

CREATE TRIGGER update_friendships_updated_at
BEFORE UPDATE ON friendships
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ===========================================
-- USER_GAMERTAGS TABLE
-- ===========================================
CREATE TABLE user_gamertags (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    gamertag TEXT,
    platform TEXT
);

-- ===========================================
-- SEED DATA (Sample Entries)
-- ===========================================
INSERT INTO users (firebase_uid, username, email, display_name, password_hash, role)
VALUES ('demo-uid-123', 'DemoUser', 'demo@example.com', 'Demo User', 'hashedpassword123', 'USER');

INSERT INTO users (firebase_uid, username, email, display_name, password_hash, role)
VALUES ('demo-uid-456', 'TestFriend', 'friend@example.com', 'Test Friend', 'hashedpassword456', 'USER');

INSERT INTO games (external_api_id, name, description, developer, publisher, price)
VALUES ('ext-001', 'Demo Game', 'A sample demo game for testing.', 'Demo Devs', 'Demo Studio', 19.99);

INSERT INTO reviews (user_id, game_id, rating, content)
VALUES (1, 1, 5, 'Amazing demo experience!');

INSERT INTO friendships (requester_id, receiver_id, status)
VALUES (1, 2, 'PENDING');
