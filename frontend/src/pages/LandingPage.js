import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Box, Button, Chip, Container, Paper, Stack, Typography } from '@mui/material';
import { motion } from 'framer-motion';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
export function LandingPage() {
    const { me, loading } = useAuth();
    const navigate = useNavigate();
    useEffect(() => {
        if (!loading && me?.connected) {
            navigate('/dashboard', { replace: true });
        }
    }, [loading, me?.connected, navigate]);
    const connect = () => {
        const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
        window.location.href = `${apiBaseUrl}/auth/spotify/login`;
    };
    return (_jsx(Container, { maxWidth: "md", sx: { py: { xs: 6, md: 10 } }, children: _jsxs(Paper, { component: motion.div, initial: { opacity: 0, y: 24 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.5 }, className: "mih-card mih-glow", sx: { p: { xs: 3, md: 6 }, textAlign: 'center', bgcolor: 'rgba(255,255,255,0.82)' }, children: [_jsxs(Stack, { direction: "row", spacing: 1, justifyContent: "center", sx: { mb: 2 }, children: [_jsx(Chip, { label: "Wrapped Lite", color: "primary" }), _jsx(Chip, { label: "Playlist Builder", color: "secondary" }), _jsx(Chip, { label: "Trend Tracking" })] }), _jsx(Typography, { variant: "h3", className: "mih-page-title mih-hero-gradient", gutterBottom: true, children: "Music Insights Hub" }), _jsx(Typography, { color: "text.secondary", sx: { mb: 4, maxWidth: 540, mx: 'auto' }, children: "Explore your listening identity across time ranges, capture snapshots, compare movement, and build playlists from what you actually play." }), _jsx(Box, { component: motion.div, whileHover: { y: -2, scale: 1.02 }, whileTap: { scale: 0.98 }, children: _jsx(Button, { size: "large", variant: "contained", onClick: connect, children: "Connect with Spotify" }) }), _jsxs(Stack, { direction: { xs: 'column', sm: 'row' }, spacing: 1.5, sx: { mt: 4 }, justifyContent: "center", children: [_jsx(Chip, { label: "Top Artists & Tracks" }), _jsx(Chip, { label: "Snapshot Trends" }), _jsx(Chip, { label: "Playlist Export" })] })] }) }));
}
