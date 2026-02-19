import { useEffect, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, CircularProgress, Grid2, Stack, Tab, Tabs, Typography } from '@mui/material';
import { api } from '../api';
import { TimeRange, TopInsightsResponse } from '../types/api';
import { motion } from 'framer-motion';

const ranges: TimeRange[] = ['short_term', 'medium_term', 'long_term'];

export function DashboardPage() {
  const [timeRange, setTimeRange] = useState<TimeRange>('medium_term');
  const [insights, setInsights] = useState<TopInsightsResponse | null>(null);
  const [loading, setLoading] = useState(false);

  const load = async (nextRange: TimeRange) => {
    setLoading(true);
    try {
      setInsights(await api.getTopInsights(nextRange));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load(timeRange);
  }, [timeRange]);

  const createSnapshot = async () => {
    await api.createSnapshot(timeRange);
  };

  return (
    <Box>
      <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" spacing={2} sx={{ mb: 2 }}>
        <Box>
          <Typography variant="h4" className="mih-page-title" gutterBottom>Dashboard</Typography>
          <Stack direction="row" spacing={1}>
            <Chip label={`Range: ${timeRange}`} color="primary" />
            <Chip label={insights ? `${insights.topTracks.length} tracks loaded` : 'No data'} />
          </Stack>
        </Box>
        <Button variant="contained" onClick={createSnapshot}>Create Snapshot</Button>
      </Stack>
      <Alert severity="info" sx={{ mb: 2 }} className="mih-card">
        Snapshot tip: create snapshots on different days to get meaningful climbers/droppers in Trends.
      </Alert>
      <Tabs value={timeRange} onChange={(_, value) => setTimeRange(value)} sx={{ mb: 3 }} variant="scrollable" allowScrollButtonsMobile>
        {ranges.map((range) => <Tab key={range} value={range} label={range.replace('_term', '')} />)}
      </Tabs>
      {loading || !insights ? <CircularProgress /> : (
        <Grid2 container spacing={2.5}>
          <Grid2 size={{ xs: 12, md: 6 }}>
            <Card component={motion.div} initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.28 }} className="mih-card"><CardContent>
              <Typography variant="h6" sx={{ mb: 1.5 }}>Top Artists</Typography>
              {insights.topArtists.slice(0, 10).map((artist) => (
                <Box key={artist.id} sx={{ py: 0.7, borderBottom: '1px dashed rgba(15,23,42,0.08)' }}>
                  <Typography fontWeight={600}>#{artist.rank} {artist.name}</Typography>
                </Box>
              ))}
            </CardContent></Card>
          </Grid2>
          <Grid2 size={{ xs: 12, md: 6 }}>
            <Card component={motion.div} initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.34 }} className="mih-card"><CardContent>
              <Typography variant="h6" sx={{ mb: 1.5 }}>Top Tracks</Typography>
              {insights.topTracks.slice(0, 10).map((track) => (
                <Box key={track.id} sx={{ py: 0.7, borderBottom: '1px dashed rgba(15,23,42,0.08)' }}>
                  <Typography fontWeight={600}>#{track.rank} {track.name}</Typography>
                  <Typography color="text.secondary">{track.artistName}</Typography>
                </Box>
              ))}
            </CardContent></Card>
          </Grid2>
        </Grid2>
      )}
    </Box>
  );
}
