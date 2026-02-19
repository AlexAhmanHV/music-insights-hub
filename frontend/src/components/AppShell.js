import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { AppBar, Box, Button, Chip, IconButton, Stack, Toolbar, Tooltip, Typography } from '@mui/material';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { motion } from 'framer-motion';
import DarkModeRoundedIcon from '@mui/icons-material/DarkModeRounded';
import LightModeRoundedIcon from '@mui/icons-material/LightModeRounded';
import { useThemeMode } from '../context/ThemeModeContext';
export function AppShell({ children }) {
    const { me, refreshMe } = useAuth();
    const { mode, toggleMode } = useThemeMode();
    const navigate = useNavigate();
    const location = useLocation();
    const logout = async () => {
        await api.logout();
        await refreshMe();
        navigate('/');
    };
    const nav = [
        { label: 'Dashboard', to: '/dashboard' },
        { label: 'Trends', to: '/trends' },
        { label: 'Playlist Builder', to: '/playlist-builder' },
        { label: 'Settings', to: '/settings' },
    ];
    return (_jsxs(Box, { sx: { minHeight: '100vh', position: 'relative' }, children: [_jsxs(Box, { sx: { position: 'absolute', inset: 0, pointerEvents: 'none' }, children: [_jsx(Box, { sx: { position: 'absolute', top: -120, left: -80, width: 340, height: 340, borderRadius: '50%', bgcolor: 'rgba(14, 116, 144, 0.12)', filter: 'blur(12px)' } }), _jsx(Box, { sx: { position: 'absolute', right: -90, top: 40, width: 280, height: 280, borderRadius: '50%', bgcolor: 'rgba(251, 146, 60, 0.16)', filter: 'blur(12px)' } })] }), _jsx(AppBar, { position: "sticky", elevation: 0, sx: { bgcolor: 'rgba(255,255,255,0.7)', color: 'text.primary', backdropFilter: 'blur(10px)', borderBottom: '1px solid rgba(15,23,42,0.06)' }, children: _jsxs(Toolbar, { sx: { gap: 2, flexWrap: 'wrap', py: 1 }, children: [_jsxs(Stack, { direction: "row", spacing: 1, alignItems: "center", sx: { flexGrow: 1 }, children: [_jsx(Typography, { variant: "h6", children: "Music Insights Hub" }), _jsx(Chip, { size: "small", color: me?.connected ? 'primary' : 'default', label: me?.connected ? 'Connected' : 'Guest' })] }), nav.map((item) => (_jsxs(Button, { component: RouterLink, to: item.to, color: location.pathname === item.to ? 'primary' : 'inherit', variant: location.pathname === item.to ? 'contained' : 'text', sx: { position: 'relative', overflow: 'hidden' }, children: [item.label, location.pathname === item.to && (_jsx(Box, { component: motion.span, layoutId: "mih-nav-indicator", sx: {
                                        position: 'absolute',
                                        left: 10,
                                        right: 10,
                                        bottom: 4,
                                        height: 3,
                                        borderRadius: 99,
                                        bgcolor: 'rgba(13,148,136,0.7)',
                                    } }))] }, item.to))), _jsx(Tooltip, { title: mode === 'dark' ? 'Switch to light mode' : 'Switch to dark mode', children: _jsx(IconButton, { onClick: toggleMode, color: "inherit", "aria-label": "toggle theme", children: mode === 'dark' ? _jsx(LightModeRoundedIcon, {}) : _jsx(DarkModeRoundedIcon, {}) }) }), me?.connected && _jsx(Button, { color: "inherit", onClick: logout, variant: "outlined", children: "Logout" })] }) }), _jsx(Box, { component: motion.div, initial: { opacity: 0, y: 10 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.35 }, sx: { p: { xs: 2, md: 3 }, position: 'relative' }, children: children })] }));
}
