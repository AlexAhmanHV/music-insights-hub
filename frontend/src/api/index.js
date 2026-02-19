import { apiFetch } from './client';
export const api = {
    me: () => apiFetch('/me'),
    startSpotifyAuth: () => apiFetch('/auth/spotify/start'),
    logout: () => apiFetch('/auth/logout', { method: 'POST' }),
    getTopInsights: (timeRange) => apiFetch(`/insights/top?timeRange=${timeRange}`),
    createSnapshot: (timeRange) => apiFetch('/insights/snapshots', {
        method: 'POST',
        body: JSON.stringify({ timeRange }),
    }),
    listSnapshots: () => apiFetch('/insights/snapshots'),
    getTrends: (latestSnapshotId) => apiFetch(`/insights/trends?latestSnapshotId=${latestSnapshotId}`),
    createTopTracksPlaylist: (payload) => apiFetch('/playlists/top-tracks', {
        method: 'POST',
        body: JSON.stringify(payload),
    }),
};
