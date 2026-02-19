import { Box, Button, Chip, Container, Paper, Stack, Typography } from '@mui/material';
import { motion } from 'framer-motion';

export function LandingPage() {
  const connect = () => {
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
    window.location.href = `${apiBaseUrl}/auth/spotify/login`;
  };

  return (
    <Container maxWidth="md" sx={{ py: { xs: 6, md: 10 } }}>
      <Paper
        component={motion.div}
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="mih-card mih-glow"
        sx={{ p: { xs: 3, md: 6 }, textAlign: 'center', bgcolor: 'rgba(255,255,255,0.82)' }}
      >
        <Stack direction="row" spacing={1} justifyContent="center" sx={{ mb: 2 }}>
          <Chip label="Wrapped Lite" color="primary" />
          <Chip label="Playlist Builder" color="secondary" />
          <Chip label="Trend Tracking" />
        </Stack>
        <Typography variant="h3" className="mih-page-title mih-hero-gradient" gutterBottom>Music Insights Hub</Typography>
        <Typography color="text.secondary" sx={{ mb: 4, maxWidth: 540, mx: 'auto' }}>
          Explore your listening identity across time ranges, capture snapshots, compare movement, and build playlists from what you actually play.
        </Typography>
        <Box component={motion.div} whileHover={{ y: -2, scale: 1.02 }} whileTap={{ scale: 0.98 }}>
          <Button size="large" variant="contained" onClick={connect}>Connect with Spotify</Button>
        </Box>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} sx={{ mt: 4 }} justifyContent="center">
          <Chip label="Top Artists & Tracks" />
          <Chip label="Snapshot Trends" />
          <Chip label="Playlist Export" />
        </Stack>
      </Paper>
    </Container>
  );
}
