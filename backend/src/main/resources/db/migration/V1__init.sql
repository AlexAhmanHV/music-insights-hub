CREATE TABLE app_users (
  id uuid PRIMARY KEY,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL
);

CREATE TABLE spotify_accounts (
  id uuid PRIMARY KEY,
  app_user_id uuid NOT NULL UNIQUE REFERENCES app_users(id),
  spotify_user_id text NOT NULL UNIQUE,
  display_name text,
  email text,
  country text,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL
);

CREATE TABLE spotify_tokens (
  id uuid PRIMARY KEY,
  spotify_account_id uuid NOT NULL UNIQUE REFERENCES spotify_accounts(id),
  access_token text NOT NULL,
  refresh_token text NOT NULL,
  expires_at timestamp NOT NULL,
  scopes text NOT NULL,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL
);

CREATE TABLE snapshots (
  id uuid PRIMARY KEY,
  spotify_account_id uuid NOT NULL REFERENCES spotify_accounts(id),
  type text NOT NULL,
  captured_at timestamp NOT NULL,
  time_range text NOT NULL,
  top_artists jsonb NOT NULL,
  top_tracks jsonb NOT NULL,
  genre_breakdown jsonb NOT NULL
);

CREATE INDEX idx_snapshots_account_captured_at ON snapshots (spotify_account_id, captured_at DESC);

CREATE TABLE cache_entries (
  id uuid PRIMARY KEY,
  spotify_account_id uuid NOT NULL REFERENCES spotify_accounts(id),
  cache_key text NOT NULL,
  payload jsonb NOT NULL,
  expires_at timestamp NOT NULL,
  UNIQUE (spotify_account_id, cache_key)
);

CREATE TABLE oauth_states (
  state text PRIMARY KEY,
  code_verifier text NOT NULL,
  created_at timestamp NOT NULL,
  expires_at timestamp NOT NULL
);

CREATE INDEX idx_oauth_states_expires_at ON oauth_states (expires_at);
