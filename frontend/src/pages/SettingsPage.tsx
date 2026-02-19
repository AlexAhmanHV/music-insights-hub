import { Box, Button, Card, CardContent, Chip, Stack, Switch, Typography } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { api } from '../api';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useThemeMode } from '../context/ThemeModeContext';

export function SettingsPage() {
  const { me, refreshMe } = useAuth();
  const { mode, toggleMode } = useThemeMode();
  const navigate = useNavigate();

  const disconnect = async () => {
    await api.logout();
    await refreshMe();
    navigate('/');
  };

  return (
    <Box>
      <Typography variant="h4" className="mih-page-title" gutterBottom>Settings</Typography>
      <Card
        component={motion.div}
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
        className="mih-card"
      >
        <CardContent>
          <Stack spacing={1.5}>
            <Stack direction="row" spacing={1}>
              <Chip label={`Connected: ${String(me?.connected ?? false)}`} color={me?.connected ? 'primary' : 'default'} />
              <Chip label={`Mock mode: ${String(me?.spotifyMockMode ?? false)}`} />
            </Stack>
            <Typography variant="body1">Spotify user: {me?.spotifyAccount?.displayName ?? '-'}</Typography>
            <Typography variant="body1">Email: {me?.spotifyAccount?.email ?? '-'}</Typography>
            <Typography variant="body1">Country: {me?.spotifyAccount?.country ?? '-'}</Typography>
            <Stack direction="row" spacing={1} alignItems="center">
              <Typography variant="body1">Dark mode</Typography>
              <Switch checked={mode === 'dark'} onChange={toggleMode} />
            </Stack>
            <Button sx={{ mt: 2, width: 'fit-content' }} variant="outlined" onClick={disconnect}>Disconnect / Logout</Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
