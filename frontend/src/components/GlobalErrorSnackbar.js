import { jsx as _jsx } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { Alert, Snackbar } from '@mui/material';
import { setGlobalErrorHandler } from '../api/client';
export function GlobalErrorSnackbar() {
    const [message, setMessage] = useState(null);
    useEffect(() => {
        setGlobalErrorHandler((nextMessage) => setMessage(nextMessage));
    }, []);
    return (_jsx(Snackbar, { open: Boolean(message), autoHideDuration: 4000, onClose: () => setMessage(null), children: _jsx(Alert, { severity: "error", onClose: () => setMessage(null), children: message }) }));
}
