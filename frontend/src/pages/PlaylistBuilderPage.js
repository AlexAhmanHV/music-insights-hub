import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { api } from '../api';
import { motion } from 'framer-motion';
export function PlaylistBuilderPage() {
    const [timeRange, setTimeRange] = useState('medium_term');
    const [limit, setLimit] = useState(20);
    const [name, setName] = useState('My Top Tracks Mix');
    const [result, setResult] = useState(null);
    const submit = async (event) => {
        event.preventDefault();
        setResult(await api.createTopTracksPlaylist({ timeRange, limit, name }));
    };
    return (_jsxs(Box, { children: [_jsx(Typography, { variant: "h4", className: "mih-page-title", gutterBottom: true, children: "Playlist Builder" }), _jsx(Typography, { color: "text.secondary", sx: { mb: 2 }, children: "Generate a playlist from your top tracks for a selected listening window." }), _jsxs(Stack, { direction: "row", spacing: 1, sx: { mb: 2 }, children: [_jsx(Chip, { label: "Spotify Export", color: "primary" }), _jsx(Chip, { label: "Top Tracks Source" })] }), _jsx(Alert, { severity: "info", sx: { mb: 2 }, className: "mih-card", children: "Helper: choose time range + limit, then we create a new playlist and add your top tracks automatically." }), _jsx(Card, { component: motion.div, initial: { opacity: 0, y: 18 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.35 }, className: "mih-card", children: _jsxs(CardContent, { component: "form", onSubmit: submit, sx: { display: 'grid', gap: 2 }, children: [_jsxs(Stack, { direction: { xs: 'column', md: 'row' }, spacing: 2, children: [_jsxs(TextField, { fullWidth: true, select: true, label: "Time range", value: timeRange, onChange: (event) => setTimeRange(event.target.value), children: [_jsx(MenuItem, { value: "short_term", children: "short_term" }), _jsx(MenuItem, { value: "medium_term", children: "medium_term" }), _jsx(MenuItem, { value: "long_term", children: "long_term" })] }), _jsx(TextField, { fullWidth: true, type: "number", label: "Limit", helperText: "How many top tracks to include (1-50).", value: limit, inputProps: { min: 1, max: 50 }, onChange: (event) => setLimit(Number(event.target.value)) })] }), _jsx(TextField, { label: "Playlist name", helperText: "This will be the new Spotify playlist title.", value: name, onChange: (event) => setName(event.target.value) }), _jsx(Button, { type: "submit", variant: "contained", children: "Create Playlist" }), _jsx(TextField, { disabled: true, label: "Preview", value: `Top ${limit} tracks from ${timeRange}` })] }) }), result && (_jsx(Box, { component: motion.div, initial: { opacity: 0, y: 12 }, animate: { opacity: 1, y: 0 }, children: _jsxs(Alert, { severity: "success", sx: { mt: 2 }, className: "mih-card", children: ["Created playlist ", result.playlistId, ". ", _jsx("a", { href: result.playlistUrl, target: "_blank", rel: "noreferrer", children: "Open in Spotify" })] }) }))] }));
}
