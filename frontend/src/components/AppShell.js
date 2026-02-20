import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { AppBar, Box, Button, Chip, Dialog, DialogContent, DialogTitle, IconButton, List, ListItem, ListItemText, Stack, Toolbar, Tooltip, Typography } from '@mui/material';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { motion } from 'framer-motion';
import DarkModeRoundedIcon from '@mui/icons-material/DarkModeRounded';
import LightModeRoundedIcon from '@mui/icons-material/LightModeRounded';
import HelpOutlineRoundedIcon from '@mui/icons-material/HelpOutlineRounded';
import { useThemeMode } from '../context/ThemeModeContext';
import { useState } from 'react';
export function AppShell({ children }) {
    const { me, refreshMe } = useAuth();
    const { mode, toggleMode } = useThemeMode();
    const navigate = useNavigate();
    const location = useLocation();
    const [howToOpen, setHowToOpen] = useState(false);
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
    return (_jsxs(Box, { sx: { minHeight: '100vh', position: 'relative', display: 'flex', flexDirection: 'column' }, children: [_jsxs(Box, { sx: { position: 'absolute', inset: 0, pointerEvents: 'none' }, children: [_jsx(Box, { sx: { position: 'absolute', top: -120, left: -80, width: 340, height: 340, borderRadius: '50%', bgcolor: 'rgba(14, 116, 144, 0.12)', filter: 'blur(12px)' } }), _jsx(Box, { sx: { position: 'absolute', right: -90, top: 40, width: 280, height: 280, borderRadius: '50%', bgcolor: 'rgba(251, 146, 60, 0.16)', filter: 'blur(12px)' } })] }), _jsx(AppBar, { position: "sticky", elevation: 0, sx: { bgcolor: 'rgba(255,255,255,0.7)', color: 'text.primary', backdropFilter: 'blur(10px)', borderBottom: '1px solid rgba(15,23,42,0.06)' }, children: _jsxs(Toolbar, { sx: { gap: 2, flexWrap: 'wrap', py: 1 }, children: [_jsxs(Stack, { direction: "row", spacing: 1, alignItems: "center", sx: { flexGrow: 1 }, children: [_jsx(Typography, { variant: "h6", children: "Music Insights Hub" }), _jsx(Chip, { size: "small", color: me?.connected ? 'primary' : 'default', label: me?.connected ? 'Connected' : 'Guest' })] }), nav.map((item) => (_jsxs(Button, { component: RouterLink, to: item.to, color: location.pathname === item.to ? 'primary' : 'inherit', variant: location.pathname === item.to ? 'contained' : 'text', sx: { position: 'relative', overflow: 'hidden' }, children: [item.label, location.pathname === item.to && (_jsx(Box, { component: motion.span, layoutId: "mih-nav-indicator", sx: {
                                        position: 'absolute',
                                        left: 10,
                                        right: 10,
                                        bottom: 4,
                                        height: 3,
                                        borderRadius: 99,
                                        bgcolor: 'rgba(13,148,136,0.7)',
                                    } }))] }, item.to))), _jsx(Tooltip, { title: "Quick how-to", children: _jsx(IconButton, { onClick: () => setHowToOpen(true), color: "inherit", "aria-label": "open how-to", children: _jsx(HelpOutlineRoundedIcon, {}) }) }), _jsx(Tooltip, { title: mode === 'dark' ? 'Switch to light mode' : 'Switch to dark mode', children: _jsx(IconButton, { onClick: toggleMode, color: "inherit", "aria-label": "toggle theme", children: mode === 'dark' ? _jsx(LightModeRoundedIcon, {}) : _jsx(DarkModeRoundedIcon, {}) }) }), me?.connected && _jsx(Button, { color: "inherit", onClick: logout, variant: "outlined", children: "Logout" })] }) }), _jsx(Box, { component: motion.div, initial: { opacity: 0, y: 10 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.35 }, sx: { p: { xs: 2, md: 3 }, position: 'relative', flex: 1 }, children: children }), _jsxs(Dialog, { open: howToOpen, onClose: () => setHowToOpen(false), fullWidth: true, maxWidth: "sm", children: [_jsx(DialogTitle, { children: "How-to" }), _jsxs(DialogContent, { children: [_jsxs(List, { dense: true, children: [_jsx(ListItem, { children: _jsx(ListItemText, { primary: "1. Connect with Spotify", secondary: "Click Connect on landing page and complete login flow." }) }), _jsx(ListItem, { children: _jsx(ListItemText, { primary: "2. Check Dashboard", secondary: "Switch short/medium/long term and review top artists/tracks." }) }), _jsx(ListItem, { children: _jsx(ListItemText, { primary: "3. Create snapshots", secondary: "Create snapshots over time to enable meaningful trends." }) }), _jsx(ListItem, { children: _jsx(ListItemText, { primary: "4. View Trends", secondary: "Open Trends page and compare latest snapshot to previous one." }) }), _jsx(ListItem, { children: _jsx(ListItemText, { primary: "5. Build playlist", secondary: "Choose range + limit in Playlist Builder and create a playlist." }) }), _jsx(ListItem, { children: _jsx(ListItemText, { primary: "Tip", secondary: "If login fails, verify redirect URI and keep host consistent (localhost or 127.0.0.1)." }) })] }), _jsx(Stack, { direction: "row", justifyContent: "flex-end", children: _jsx(Button, { onClick: () => setHowToOpen(false), variant: "contained", children: "Close" }) })] })] }), _jsx(Box, { component: "footer", sx: {
                    borderTop: '1px solid rgba(15,23,42,0.08)',
                    bgcolor: 'rgba(255,255,255,0.56)',
                    backdropFilter: 'blur(8px)',
                    px: 2,
                    py: 1.5,
                }, children: _jsxs(Stack, { direction: "row", spacing: 1, alignItems: "center", justifyContent: "center", children: [_jsx(Box, { component: "img", src: "/alexahman-icon.png", alt: "AlexAhman icon", sx: { width: 20, height: 20, borderRadius: 1 }, onError: (event) => {
                                event.currentTarget.style.display = 'none';
                            } }), _jsx(Typography, { component: "a", href: "https://alexahman.se", target: "_blank", rel: "noreferrer noopener", sx: { color: 'text.secondary', textDecoration: 'none', '&:hover': { color: 'primary.main' } }, children: "Skapad av AlexAhman.se" })] }) })] }));
}
