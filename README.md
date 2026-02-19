# Music Insights Hub

Music Insights Hub is a monorepo for a web app with:
- `backend/`: Java 21, Spring Boot 3, PostgreSQL, Flyway
- `frontend/`: React, TypeScript, Vite, MUI, Recharts, Framer Motion

This README explains exactly how to run it locally in both mock mode and real Spotify mode.

## 1) Prerequisites
- Java 21
- Node.js 20+
- PostgreSQL running locally

## 2) Create local databases
Run in psql:

```sql
CREATE DATABASE music_insights_dev;
CREATE DATABASE music_insights_test;
```

## 3) Recommended local host setup
Use `127.0.0.1` consistently for frontend + backend + Spotify redirect.
Do not mix `localhost` and `127.0.0.1` during OAuth.

## 4) Environment files
Examples exist in:
- `.env.example` (root)
- `backend/.env.example`
- `frontend/.env.example`

### Backend (.env or terminal env)
Use this as a baseline:

```env
MUSICHUB_DB_URL=jdbc:postgresql://127.0.0.1:5432/music_insights_dev
MUSICHUB_DB_USER=postgres
MUSICHUB_DB_PASS=postgres

MUSICHUB_TEST_DB_URL=jdbc:postgresql://127.0.0.1:5432/music_insights_test
MUSICHUB_TEST_DB_USER=postgres
MUSICHUB_TEST_DB_PASS=postgres

APP_BASE_URL=http://127.0.0.1:5173
JWT_SECRET=replace-with-a-long-random-secret

SPOTIFY_MOCK_MODE=true
SPOTIFY_CLIENT_ID=
SPOTIFY_CLIENT_SECRET=
SPOTIFY_REDIRECT_URI=http://127.0.0.1:8080/auth/spotify/callback

CACHE_TTL_MINUTES=20
SESSION_COOKIE_SECURE=false
```

### Frontend (`frontend/.env`)

```env
VITE_API_BASE_URL=http://127.0.0.1:8080
```

## 5) Run in mock mode (no real Spotify login)

### Start backend
```powershell
cd e:\music-insights-hub\backend
$env:SPRING_PROFILES_ACTIVE="local"
$env:SPOTIFY_MOCK_MODE="true"
$env:SESSION_COOKIE_SECURE="false"
.\mvnw.cmd spring-boot:run
```

### Start frontend (new terminal)
```powershell
cd e:\music-insights-hub\frontend
npm install
npm run dev -- --host 127.0.0.1 --port 5173
```

Open: `http://127.0.0.1:5173`

## 6) Run with real Spotify OAuth

1. Create/open app in Spotify Developer Dashboard.
2. Add this Redirect URI in Spotify settings:
   - `http://127.0.0.1:8080/auth/spotify/callback`
3. Set backend env:

```powershell
cd e:\music-insights-hub\backend
$env:SPRING_PROFILES_ACTIVE="local"
$env:SPOTIFY_MOCK_MODE="false"
$env:SPOTIFY_CLIENT_ID="YOUR_CLIENT_ID"
$env:SPOTIFY_CLIENT_SECRET="YOUR_CLIENT_SECRET"
$env:SPOTIFY_REDIRECT_URI="http://127.0.0.1:8080/auth/spotify/callback"
$env:APP_BASE_URL="http://127.0.0.1:5173"
$env:SESSION_COOKIE_SECURE="false"
.\mvnw.cmd spring-boot:run
```

4. Start frontend:
```powershell
cd e:\music-insights-hub\frontend
npm run dev -- --host 127.0.0.1 --port 5173
```

5. Clear cookies for both `localhost` and `127.0.0.1`, then login again.

## 7) What should work
- Connect with Spotify button
- Dashboard top artists/tracks and genre chart
- Create snapshot
- Trends view (requires at least 2 snapshots)
- Playlist builder with returned playlist URL
- Settings + logout

## 8) Quick verification
Backend health:
```powershell
curl http://127.0.0.1:8080/health
```

Backend tests:
```powershell
cd e:\music-insights-hub\backend
.\mvnw.cmd test
```

Frontend build:
```powershell
cd e:\music-insights-hub\frontend
npm run build
```

## 9) Common issues

### `INVALID_STATE` / OAuth state mismatch
- Use only `127.0.0.1` across frontend, backend, Spotify redirect.
- Ensure `SESSION_COOKIE_SECURE=false` locally.
- Clear old cookies and retry.

### Redirect works but app returns to landing page
- Check `/me` response body. If `connected=false`, session cookie was not set/sent.
- Verify frontend calls `http://127.0.0.1:8080/me` (not localhost).

### `INVALID_CLIENT`
- Wrong `SPOTIFY_CLIENT_ID` or `SPOTIFY_CLIENT_SECRET`.
- Regenerate client secret in Spotify Dashboard and restart backend.

## 10) API summary
Auth/session:
- `GET /auth/spotify/start`
- `GET /auth/spotify/callback`
- `POST /auth/logout`
- `GET /me`

Insights:
- `GET /insights/top?timeRange=short_term|medium_term|long_term`
- `POST /insights/snapshots`
- `GET /insights/snapshots`
- `GET /insights/trends?latestSnapshotId=...`

Playlists:
- `POST /playlists/top-tracks`

Debug:
- `GET /health`
- `GET /swagger-ui/index.html`

## 11) Security notes
- Secrets must come from environment variables.
- OAuth uses PKCE + state.
- Session cookie is `HttpOnly` and `SameSite=Lax`.
- In local HTTP dev, keep `SESSION_COOKIE_SECURE=false`.
- Spotify tokens are stored in plain text in this demo app.
  For production, encrypt tokens at rest.

## 12) Render deployment note (backend)
If Render does not provide Java runtime in your current setup, deploy backend as a Docker Web Service:
- Root directory: `backend`
- Dockerfile: `backend/Dockerfile`
- No separate build/start command needed (Docker handles it)

Make sure backend env vars are configured in Render and that:
- `APP_BASE_URL` points to frontend URL
- `SPOTIFY_REDIRECT_URI` points to backend callback URL
