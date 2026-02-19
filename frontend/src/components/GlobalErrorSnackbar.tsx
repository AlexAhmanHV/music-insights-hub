import { useEffect, useState } from 'react';
import { Alert, Snackbar } from '@mui/material';
import { setGlobalErrorHandler } from '../api/client';

export function GlobalErrorSnackbar() {
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    setGlobalErrorHandler((nextMessage) => setMessage(nextMessage));
  }, []);

  return (
    <Snackbar open={Boolean(message)} autoHideDuration={4000} onClose={() => setMessage(null)}>
      <Alert severity="error" onClose={() => setMessage(null)}>
        {message}
      </Alert>
    </Snackbar>
  );
}
