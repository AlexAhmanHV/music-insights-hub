import { useEffect, useState } from 'react';
import { Box, Card, CardContent, CircularProgress, Grid2, MenuItem, TextField, Typography } from '@mui/material';
import { api } from '../api';
import { SnapshotSummary, TrendsResponse } from '../types/api';
import { motion } from 'framer-motion';

export function TrendsPage() {
  const [snapshots, setSnapshots] = useState<SnapshotSummary[]>([]);
  const [selected, setSelected] = useState<string>('');
  const [trends, setTrends] = useState<TrendsResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [emptyState, setEmptyState] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      const list = await api.listSnapshots();
      setSnapshots(list);
      if (list.length > 0) {
        setSelected(list[0].id);
        setEmptyState(null);
      } else {
        setEmptyState('No snapshots yet. Create at least two snapshots from Dashboard to view trends.');
      }
    })();
  }, []);

  useEffect(() => {
    if (!selected) return;
    (async () => {
      setLoading(true);
      try {
        setTrends(await api.getTrends(selected));
        setEmptyState(null);
      } catch {
        setTrends(null);
        setEmptyState('Trends require at least two snapshots. Create another snapshot and try again.');
      } finally {
        setLoading(false);
      }
    })();
  }, [selected]);

  return (
    <Box>
      <Typography variant="h4" className="mih-page-title" gutterBottom>Trends</Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>Compare your latest snapshot with the previous one and see movement.</Typography>
      <TextField
        select
        fullWidth
        label="Latest snapshot"
        value={selected}
        onChange={(event) => setSelected(event.target.value)}
        sx={{ mb: 3 }}
      >
        {snapshots.map((snapshot) => (
          <MenuItem value={snapshot.id} key={snapshot.id}>{new Date(snapshot.capturedAt).toLocaleString()} ({snapshot.timeRange})</MenuItem>
        ))}
      </TextField>
      {loading ? <CircularProgress /> : emptyState ? (
        <Typography color="text.secondary">{emptyState}</Typography>
      ) : !trends ? (
        <Typography color="text.secondary">No trend data available.</Typography>
      ) : (
        <Grid2 container spacing={2}>
          <Grid2 size={{ xs: 12, md: 4 }}><TrendCard title="New Entries" items={trends.newEntries} tone="#0284c7" /></Grid2>
          <Grid2 size={{ xs: 12, md: 4 }}><TrendCard title="Climbers" items={trends.climbers} tone="#16a34a" /></Grid2>
          <Grid2 size={{ xs: 12, md: 4 }}><TrendCard title="Droppers" items={trends.droppers} tone="#dc2626" /></Grid2>
        </Grid2>
      )}
    </Box>
  );
}

function TrendCard({ title, items, tone }: { title: string; items: TrendsResponse['newEntries']; tone: string }) {
  return (
    <Card
      component={motion.div}
      initial={{ opacity: 0, y: 18 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.35 }}
      className="mih-card"
      sx={{ borderTop: `4px solid ${tone}` }}
    >
      <CardContent>
        <Typography variant="h6" gutterBottom>{title}</Typography>
        {items.length === 0 ? <Typography color="text.secondary">No entries</Typography> : items.map((item) => (
          <Box key={`${item.itemType}-${item.id}`} sx={{ py: 0.7, borderBottom: '1px dashed rgba(15,23,42,0.08)' }}>
            <Typography fontWeight={600}>{item.name}</Typography>
            <Typography color="text.secondary" variant="body2">
              {item.itemType} | prev: {item.previousRank ?? '-'} | now: {item.currentRank ?? '-'}
            </Typography>
          </Box>
        ))}
      </CardContent>
    </Card>
  );
}
