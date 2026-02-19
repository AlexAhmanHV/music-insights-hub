import { AppBar, Box, Button, Chip, IconButton, Stack, Toolbar, Tooltip, Typography } from '@mui/material';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { motion } from 'framer-motion';
import DarkModeRoundedIcon from '@mui/icons-material/DarkModeRounded';
import LightModeRoundedIcon from '@mui/icons-material/LightModeRounded';
import { useThemeMode } from '../context/ThemeModeContext';

export function AppShell({ children }: { children: React.ReactNode }) {
  const { me, refreshMe } = useAuth();
  const { mode, toggleMode } = useThemeMode();
  const navigate = useNavigate();
  const location = useLocation();

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
    <Box sx={{ minHeight: '100vh', position: 'relative' }}>
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
        sx={{ p: { xs: 2, md: 3 }, position: 'relative' }}
      >
        {children}
      </Box>
    </Box>
  );
}
