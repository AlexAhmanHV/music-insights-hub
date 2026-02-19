import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Chip, CircularProgress, Grid2, Stack, Tab, Tabs, Typography } from '@mui/material';
import { api } from '../api';
import { motion } from 'framer-motion';
const ranges = ['short_term', 'medium_term', 'long_term'];
export function DashboardPage() {
    const [timeRange, setTimeRange] = useState('medium_term');
    const [insights, setInsights] = useState(null);
    const [loading, setLoading] = useState(false);
    const load = async (nextRange) => {
        setLoading(true);
        try {
            setInsights(await api.getTopInsights(nextRange));
        }
        finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        void load(timeRange);
    }, [timeRange]);
    const createSnapshot = async () => {
        await api.createSnapshot(timeRange);
    };
    return (_jsxs(Box, { children: [_jsxs(Stack, { direction: { xs: 'column', md: 'row' }, justifyContent: "space-between", spacing: 2, sx: { mb: 2 }, children: [_jsxs(Box, { children: [_jsx(Typography, { variant: "h4", className: "mih-page-title", gutterBottom: true, children: "Dashboard" }), _jsxs(Stack, { direction: "row", spacing: 1, children: [_jsx(Chip, { label: `Range: ${timeRange}`, color: "primary" }), _jsx(Chip, { label: insights ? `${insights.topTracks.length} tracks loaded` : 'No data' })] })] }), _jsx(Button, { variant: "contained", onClick: createSnapshot, children: "Create Snapshot" })] }), _jsx(Alert, { severity: "info", sx: { mb: 2 }, className: "mih-card", children: "Snapshot tip: create snapshots on different days to get meaningful climbers/droppers in Trends." }), _jsx(Tabs, { value: timeRange, onChange: (_, value) => setTimeRange(value), sx: { mb: 3 }, variant: "scrollable", allowScrollButtonsMobile: true, children: ranges.map((range) => _jsx(Tab, { value: range, label: range.replace('_term', '') }, range)) }), loading || !insights ? _jsx(CircularProgress, {}) : (_jsxs(Grid2, { container: true, spacing: 2.5, children: [_jsx(Grid2, { size: { xs: 12, md: 6 }, children: _jsx(Card, { component: motion.div, initial: { opacity: 0, y: 16 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.28 }, className: "mih-card", children: _jsxs(CardContent, { children: [_jsx(Typography, { variant: "h6", sx: { mb: 1.5 }, children: "Top Artists" }), insights.topArtists.slice(0, 10).map((artist) => (_jsx(Box, { sx: { py: 0.7, borderBottom: '1px dashed rgba(15,23,42,0.08)' }, children: _jsxs(Typography, { fontWeight: 600, children: ["#", artist.rank, " ", artist.name] }) }, artist.id)))] }) }) }), _jsx(Grid2, { size: { xs: 12, md: 6 }, children: _jsx(Card, { component: motion.div, initial: { opacity: 0, y: 16 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.34 }, className: "mih-card", children: _jsxs(CardContent, { children: [_jsx(Typography, { variant: "h6", sx: { mb: 1.5 }, children: "Top Tracks" }), insights.topTracks.slice(0, 10).map((track) => (_jsxs(Box, { sx: { py: 0.7, borderBottom: '1px dashed rgba(15,23,42,0.08)' }, children: [_jsxs(Typography, { fontWeight: 600, children: ["#", track.rank, " ", track.name] }), _jsx(Typography, { color: "text.secondary", children: track.artistName })] }, track.id)))] }) }) })] }))] }));
}
