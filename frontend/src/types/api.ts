export type TimeRange = 'short_term' | 'medium_term' | 'long_term';

export interface ApiError {
  errorCode: string;
  message: string;
  details: string[];
}

export interface SpotifyArtist {
  id: string;
  name: string;
  uri: string;
  genres: string[];
  rank: number;
}

export interface SpotifyTrack {
  id: string;
  name: string;
  uri: string;
  artistName: string;
  rank: number;
}

export interface GenreCount {
  genre: string;
  count: number;
}

export interface TopInsightsResponse {
  topArtists: SpotifyArtist[];
  topTracks: SpotifyTrack[];
  genreBreakdown: GenreCount[];
}

export interface SnapshotSummary {
  id: string;
  type: string;
  timeRange: TimeRange;
  capturedAt: string;
}

export interface TrendItem {
  id: string;
  name: string;
  itemType: string;
  previousRank: number | null;
  currentRank: number | null;
  rankDelta: number | null;
}

export interface TrendsResponse {
  newEntries: TrendItem[];
  climbers: TrendItem[];
  droppers: TrendItem[];
}

export interface MeResponse {
  connected: boolean;
  appUserId: string | null;
  spotifyAccount: {
    spotifyUserId: string;
    displayName: string;
    email: string;
    country: string;
  } | null;
  spotifyMockMode: boolean;
}
