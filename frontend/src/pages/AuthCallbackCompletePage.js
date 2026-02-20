import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
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
    return (_jsx(Container, { sx: { py: 8 }, children: _jsxs(Stack, { alignItems: "center", spacing: 2, children: [_jsx(CircularProgress, {}), _jsx(Typography, { color: "text.secondary", children: "Finalizing Spotify login..." })] }) }));
}
