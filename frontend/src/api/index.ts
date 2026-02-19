import { apiFetch } from './client';
import { MeResponse, SnapshotSummary, TimeRange, TopInsightsResponse, TrendsResponse } from '../types/api';

export const api = {
  me: () => apiFetch<MeResponse>('/me'),
  startSpotifyAuth: () => apiFetch<{ authorizeUrl: string }>('/auth/spotify/start'),
  logout: () => apiFetch<void>('/auth/logout', { method: 'POST' }),

  getTopInsights: (timeRange: TimeRange) => apiFetch<TopInsightsResponse>(`/insights/top?timeRange=${timeRange}`),
  createSnapshot: (timeRange: TimeRange) => apiFetch<SnapshotSummary>('/insights/snapshots', {
    method: 'POST',
    body: JSON.stringify({ timeRange }),
  }),
  listSnapshots: () => apiFetch<SnapshotSummary[]>('/insights/snapshots'),
  getTrends: (latestSnapshotId: string) => apiFetch<TrendsResponse>(`/insights/trends?latestSnapshotId=${latestSnapshotId}`),

  createTopTracksPlaylist: (payload: { timeRange: TimeRange; limit: number; name: string }) =>
    apiFetch<{ playlistId: string; playlistUrl: string }>('/playlists/top-tracks', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
};
