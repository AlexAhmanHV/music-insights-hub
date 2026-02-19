INSERT INTO app_users (id, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', now(), now())
ON CONFLICT DO NOTHING;

INSERT INTO spotify_accounts (id, app_user_id, spotify_user_id, display_name, email, country, created_at, updated_at)
VALUES (
  '22222222-2222-2222-2222-222222222222',
  '11111111-1111-1111-1111-111111111111',
  'mock_user_001',
  'Mock Listener',
  'mock@example.com',
  'SE',
  now(),
  now()
)
ON CONFLICT DO NOTHING;
