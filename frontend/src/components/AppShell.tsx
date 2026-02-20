import { AppBar, Box, Button, Chip, Dialog, DialogContent, DialogTitle, IconButton, List, ListItem, ListItemText, Stack, Toolbar, Tooltip, Typography } from '@mui/material';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { motion } from 'framer-motion';
import DarkModeRoundedIcon from '@mui/icons-material/DarkModeRounded';
import LightModeRoundedIcon from '@mui/icons-material/LightModeRounded';
import HelpOutlineRoundedIcon from '@mui/icons-material/HelpOutlineRounded';
import { useThemeMode } from '../context/ThemeModeContext';
import { useState } from 'react';

export function AppShell({ children }: { children: React.ReactNode }) {
  const { me, refreshMe } = useAuth();
  const { mode, toggleMode } = useThemeMode();
  const navigate = useNavigate();
  const location = useLocation();
  const [howToOpen, setHowToOpen] = useState(false);

  const logout = async () => {
    await api.logout();
    await refreshMe();
    navigate('/');
  };

  const nav = [
    { label: 'Dashboard', to: '/dashboard' },
    { label: 'Trends', to: '/trends' },
    { label: 'Playlist Builder', to: '/playlist-builder' },
    { label: 'Settings', to: '/settings' },
  ];

  return (
    <Box sx={{ minHeight: '100vh', position: 'relative', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ position: 'absolute', inset: 0, pointerEvents: 'none' }}>
        <Box sx={{ position: 'absolute', top: -120, left: -80, width: 340, height: 340, borderRadius: '50%', bgcolor: 'rgba(14, 116, 144, 0.12)', filter: 'blur(12px)' }} />
        <Box sx={{ position: 'absolute', right: -90, top: 40, width: 280, height: 280, borderRadius: '50%', bgcolor: 'rgba(251, 146, 60, 0.16)', filter: 'blur(12px)' }} />
      </Box>
      <AppBar position="sticky" elevation={0} sx={{ bgcolor: 'rgba(255,255,255,0.7)', color: 'text.primary', backdropFilter: 'blur(10px)', borderBottom: '1px solid rgba(15,23,42,0.06)' }}>
        <Toolbar sx={{ gap: 2, flexWrap: 'wrap', py: 1 }}>
          <Stack direction="row" spacing={1} alignItems="center" sx={{ flexGrow: 1 }}>
            <Typography variant="h6">Music Insights Hub</Typography>
            <Chip size="small" color={me?.connected ? 'primary' : 'default'} label={me?.connected ? 'Connected' : 'Guest'} />
          </Stack>
          {nav.map((item) => (
            <Button
              key={item.to}
              component={RouterLink}
              to={item.to}
              color={location.pathname === item.to ? 'primary' : 'inherit'}
              variant={location.pathname === item.to ? 'contained' : 'text'}
              sx={{ position: 'relative', overflow: 'hidden' }}
            >
              {item.label}
              {location.pathname === item.to && (
                <Box
                  component={motion.span}
                  layoutId="mih-nav-indicator"
                  sx={{
                    position: 'absolute',
                    left: 10,
                    right: 10,
                    bottom: 4,
                    height: 3,
                    borderRadius: 99,
                    bgcolor: 'rgba(13,148,136,0.7)',
                  }}
                />
              )}
            </Button>
          ))}
          <Tooltip title="Quick how-to">
            <IconButton onClick={() => setHowToOpen(true)} color="inherit" aria-label="open how-to">
              <HelpOutlineRoundedIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title={mode === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}>
            <IconButton onClick={toggleMode} color="inherit" aria-label="toggle theme">
              {mode === 'dark' ? <LightModeRoundedIcon /> : <DarkModeRoundedIcon />}
            </IconButton>
          </Tooltip>
          {me?.connected && <Button color="inherit" onClick={logout} variant="outlined">Logout</Button>}
        </Toolbar>
      </AppBar>
      <Box
        component={motion.div}
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
        sx={{ p: { xs: 2, md: 3 }, position: 'relative', flex: 1 }}
      >
        {children}
      </Box>
      <Dialog open={howToOpen} onClose={() => setHowToOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>How-to</DialogTitle>
        <DialogContent>
          <List dense>
            <ListItem>
              <ListItemText primary="1. Connect with Spotify" secondary="Click Connect on landing page and complete login flow." />
            </ListItem>
            <ListItem>
              <ListItemText primary="2. Check Dashboard" secondary="Switch short/medium/long term and review top artists/tracks." />
            </ListItem>
            <ListItem>
              <ListItemText primary="3. Create snapshots" secondary="Create snapshots over time to enable meaningful trends." />
            </ListItem>
            <ListItem>
              <ListItemText primary="4. View Trends" secondary="Open Trends page and compare latest snapshot to previous one." />
            </ListItem>
            <ListItem>
              <ListItemText primary="5. Build playlist" secondary="Choose range + limit in Playlist Builder and create a playlist." />
            </ListItem>
            <ListItem>
              <ListItemText primary="Tip" secondary="If login fails, verify redirect URI and keep host consistent (localhost or 127.0.0.1)." />
            </ListItem>
          </List>
          <Stack direction="row" justifyContent="flex-end">
            <Button onClick={() => setHowToOpen(false)} variant="contained">Close</Button>
          </Stack>
        </DialogContent>
      </Dialog>
      <Box
        component="footer"
        sx={{
          borderTop: '1px solid rgba(15,23,42,0.08)',
          bgcolor: 'rgba(255,255,255,0.56)',
          backdropFilter: 'blur(8px)',
          px: 2,
          py: 1.5,
        }}
      >
        <Stack direction="row" spacing={1} alignItems="center" justifyContent="center">
          <Box
            component="img"
            src="/alexahman-icon.png"
            alt="AlexAhman icon"
            sx={{ width: 20, height: 20, borderRadius: 1 }}
            onError={(event) => {
              (event.currentTarget as HTMLImageElement).style.display = 'none';
            }}
          />
          <Typography
            component="a"
            href="https://alexahman.se"
            target="_blank"
            rel="noreferrer noopener"
            sx={{ color: 'text.secondary', textDecoration: 'none', '&:hover': { color: 'primary.main' } }}
          >
            Skapad av AlexAhman.se
          </Typography>
        </Stack>
      </Box>
    </Box>
  );
}
