import { useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { api } from '../api';
import { TimeRange } from '../types/api';
import { motion } from 'framer-motion';

export function PlaylistBuilderPage() {
  const [timeRange, setTimeRange] = useState<TimeRange>('medium_term');
  const [limit, setLimit] = useState<number>(20);
  const [name, setName] = useState('My Top Tracks Mix');
  const [result, setResult] = useState<{ playlistId: string; playlistUrl: string } | null>(null);

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setResult(await api.createTopTracksPlaylist({ timeRange, limit, name }));
  };

  return (
    <Box>
      <Typography variant="h4" className="mih-page-title" gutterBottom>Playlist Builder</Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>Generate a playlist from your top tracks for a selected listening window.</Typography>
      <Stack direction="row" spacing={1} sx={{ mb: 2 }}>
        <Chip label="Spotify Export" color="primary" />
        <Chip label="Top Tracks Source" />
      </Stack>
      <Alert severity="info" sx={{ mb: 2 }} className="mih-card">
        Helper: choose time range + limit, then we create a new playlist and add your top tracks automatically.
      </Alert>
      <Card
        component={motion.div}
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
        className="mih-card"
      >
        <CardContent component="form" onSubmit={submit} sx={{ display: 'grid', gap: 2 }}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
            <TextField fullWidth select label="Time range" value={timeRange} onChange={(event) => setTimeRange(event.target.value as TimeRange)}>
              <MenuItem value="short_term">short_term</MenuItem>
              <MenuItem value="medium_term">medium_term</MenuItem>
              <MenuItem value="long_term">long_term</MenuItem>
            </TextField>
            <TextField fullWidth type="number" label="Limit" helperText="How many top tracks to include (1-50)." value={limit} inputProps={{ min: 1, max: 50 }} onChange={(event) => setLimit(Number(event.target.value))} />
          </Stack>
          <TextField label="Playlist name" helperText="This will be the new Spotify playlist title." value={name} onChange={(event) => setName(event.target.value)} />
          <Button type="submit" variant="contained">Create Playlist</Button>
          <TextField
            disabled
            label="Preview"
            value={`Top ${limit} tracks from ${timeRange}`}
          />
        </CardContent>
      </Card>
      {result && (
        <Box component={motion.div} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
          <Alert severity="success" sx={{ mt: 2 }} className="mih-card">
            Created playlist {result.playlistId}. <a href={result.playlistUrl} target="_blank" rel="noreferrer">Open in Spotify</a>
          </Alert>
        </Box>
      )}
    </Box>
  );
}
