import { CircularProgress, Container, Stack, Typography } from '@mui/material';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function AuthCallbackCompletePage() {
  const { me, loading, refreshMe } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const timeout = setTimeout(() => {
      void refreshMe();
    }, 250);
    return () => clearTimeout(timeout);
  }, [refreshMe]);

  useEffect(() => {
    if (!loading) {
      navigate(me?.connected ? '/dashboard' : '/', { replace: true });
    }
  }, [loading, me?.connected, navigate]);

  return (
    <Container sx={{ py: 8 }}>
      <Stack alignItems="center" spacing={2}>
        <CircularProgress />
        <Typography color="text.secondary">Finalizing Spotify login...</Typography>
      </Stack>
    </Container>
  );
}
