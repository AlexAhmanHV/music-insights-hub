import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { Box, Card, CardContent, CircularProgress, Grid2, MenuItem, TextField, Typography } from '@mui/material';
import { api } from '../api';
import { motion } from 'framer-motion';
export function TrendsPage() {
    const [snapshots, setSnapshots] = useState([]);
    const [selected, setSelected] = useState('');
    const [trends, setTrends] = useState(null);
    const [loading, setLoading] = useState(false);
    const [emptyState, setEmptyState] = useState(null);
    useEffect(() => {
        (async () => {
            const list = await api.listSnapshots();
            setSnapshots(list);
            if (list.length > 0) {
                setSelected(list[0].id);
                setEmptyState(null);
            }
            else {
                setEmptyState('No snapshots yet. Create at least two snapshots from Dashboard to view trends.');
            }
        })();
    }, []);
    useEffect(() => {
        if (!selected)
            return;
        (async () => {
            setLoading(true);
            try {
                setTrends(await api.getTrends(selected));
                setEmptyState(null);
            }
            catch {
                setTrends(null);
                setEmptyState('Trends require at least two snapshots. Create another snapshot and try again.');
            }
            finally {
                setLoading(false);
            }
        })();
    }, [selected]);
    return (_jsxs(Box, { children: [_jsx(Typography, { variant: "h4", className: "mih-page-title", gutterBottom: true, children: "Trends" }), _jsx(Typography, { color: "text.secondary", sx: { mb: 2 }, children: "Compare your latest snapshot with the previous one and see movement." }), _jsx(TextField, { select: true, fullWidth: true, label: "Latest snapshot", value: selected, onChange: (event) => setSelected(event.target.value), sx: { mb: 3 }, children: snapshots.map((snapshot) => (_jsxs(MenuItem, { value: snapshot.id, children: [new Date(snapshot.capturedAt).toLocaleString(), " (", snapshot.timeRange, ")"] }, snapshot.id))) }), loading ? _jsx(CircularProgress, {}) : emptyState ? (_jsx(Typography, { color: "text.secondary", children: emptyState })) : !trends ? (_jsx(Typography, { color: "text.secondary", children: "No trend data available." })) : (_jsxs(Grid2, { container: true, spacing: 2, children: [_jsx(Grid2, { size: { xs: 12, md: 4 }, children: _jsx(TrendCard, { title: "New Entries", items: trends.newEntries, tone: "#0284c7" }) }), _jsx(Grid2, { size: { xs: 12, md: 4 }, children: _jsx(TrendCard, { title: "Climbers", items: trends.climbers, tone: "#16a34a" }) }), _jsx(Grid2, { size: { xs: 12, md: 4 }, children: _jsx(TrendCard, { title: "Droppers", items: trends.droppers, tone: "#dc2626" }) })] }))] }));
}
function TrendCard({ title, items, tone }) {
    return (_jsx(Card, { component: motion.div, initial: { opacity: 0, y: 18 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.35 }, className: "mih-card", sx: { borderTop: `4px solid ${tone}` }, children: _jsxs(CardContent, { children: [_jsx(Typography, { variant: "h6", gutterBottom: true, children: title }), items.length === 0 ? _jsx(Typography, { color: "text.secondary", children: "No entries" }) : items.map((item) => (_jsxs(Box, { sx: { py: 0.7, borderBottom: '1px dashed rgba(15,23,42,0.08)' }, children: [_jsx(Typography, { fontWeight: 600, children: item.name }), _jsxs(Typography, { color: "text.secondary", variant: "body2", children: [item.itemType, " | prev: ", item.previousRank ?? '-', " | now: ", item.currentRank ?? '-'] })] }, `${item.itemType}-${item.id}`)))] }) }));
}
