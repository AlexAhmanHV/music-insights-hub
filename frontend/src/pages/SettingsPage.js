import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Box, Button, Card, CardContent, Chip, Stack, Switch, Typography } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { api } from '../api';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useThemeMode } from '../context/ThemeModeContext';
export function SettingsPage() {
    const { me, refreshMe } = useAuth();
    const { mode, toggleMode } = useThemeMode();
    const navigate = useNavigate();
    const disconnect = async () => {
        await api.logout();
        await refreshMe();
        navigate('/');
    };
    return (_jsxs(Box, { children: [_jsx(Typography, { variant: "h4", className: "mih-page-title", gutterBottom: true, children: "Settings" }), _jsx(Card, { component: motion.div, initial: { opacity: 0, y: 16 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.35 }, className: "mih-card", children: _jsx(CardContent, { children: _jsxs(Stack, { spacing: 1.5, children: [_jsxs(Stack, { direction: "row", spacing: 1, children: [_jsx(Chip, { label: `Connected: ${String(me?.connected ?? false)}`, color: me?.connected ? 'primary' : 'default' }), _jsx(Chip, { label: `Mock mode: ${String(me?.spotifyMockMode ?? false)}` })] }), _jsxs(Typography, { variant: "body1", children: ["Spotify user: ", me?.spotifyAccount?.displayName ?? '-'] }), _jsxs(Typography, { variant: "body1", children: ["Email: ", me?.spotifyAccount?.email ?? '-'] }), _jsxs(Typography, { variant: "body1", children: ["Country: ", me?.spotifyAccount?.country ?? '-'] }), _jsxs(Stack, { direction: "row", spacing: 1, alignItems: "center", children: [_jsx(Typography, { variant: "body1", children: "Dark mode" }), _jsx(Switch, { checked: mode === 'dark', onChange: toggleMode })] }), _jsx(Button, { sx: { mt: 2, width: 'fit-content' }, variant: "outlined", onClick: disconnect, children: "Disconnect / Logout" })] }) }) })] }));
}
