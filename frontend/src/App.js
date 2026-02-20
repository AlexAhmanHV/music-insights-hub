import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import { useEffect, useMemo, useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ThemeModeProvider } from './context/ThemeModeContext';
import { LandingPage } from './pages/LandingPage';
import { DashboardPage } from './pages/DashboardPage';
import { TrendsPage } from './pages/TrendsPage';
import { PlaylistBuilderPage } from './pages/PlaylistBuilderPage';
import { SettingsPage } from './pages/SettingsPage';
import { AuthCallbackCompletePage } from './pages/AuthCallbackCompletePage';
import { AppShell } from './components/AppShell';
import { ProtectedRoute } from './components/ProtectedRoute';
import { GlobalErrorSnackbar } from './components/GlobalErrorSnackbar';
const MODE_STORAGE_KEY = 'mih-theme-mode';
function RouteTitleManager() {
    const location = useLocation();
    useEffect(() => {
        const titles = {
            '/': 'Music Insights Hub | Spotify Wrapped Lite',
            '/dashboard': 'Dashboard | Music Insights Hub',
            '/trends': 'Trends | Music Insights Hub',
            '/playlist-builder': 'Playlist Builder | Music Insights Hub',
            '/settings': 'Settings | Music Insights Hub',
            '/auth/callback-complete': 'Signing in | Music Insights Hub',
        };
        document.title = titles[location.pathname] ?? 'Music Insights Hub';
    }, [location.pathname]);
    return null;
}
export default function App() {
    const [mode, setMode] = useState(() => {
        const saved = localStorage.getItem(MODE_STORAGE_KEY);
        return saved === 'dark' ? 'dark' : 'light';
    });
    useEffect(() => {
        localStorage.setItem(MODE_STORAGE_KEY, mode);
        document.body.setAttribute('data-theme', mode);
    }, [mode]);
    const theme = useMemo(() => createTheme({
        typography: {
            fontFamily: '"Space Grotesk", "Avenir Next", "Segoe UI", sans-serif',
            h4: { fontWeight: 700, letterSpacing: '-0.02em' },
            h6: { fontWeight: 700, letterSpacing: '-0.01em' },
        },
        palette: mode === 'dark'
            ? {
                mode: 'dark',
                primary: { main: '#2dd4bf' },
                secondary: { main: '#fb923c' },
                background: { default: '#0b1220', paper: '#101a2d' },
            }
            : {
                mode: 'light',
                primary: { main: '#0d9488' },
                secondary: { main: '#fb923c' },
                background: { default: '#eef4fb', paper: '#ffffff' },
            },
        shape: {
            borderRadius: 14,
        },
        components: {
            MuiCard: {
                styleOverrides: {
                    root: {
                        border: mode === 'dark' ? '1px solid rgba(148, 163, 184, 0.2)' : '1px solid rgba(15, 23, 42, 0.08)',
                        boxShadow: mode === 'dark'
                            ? '0 12px 32px rgba(2, 6, 23, 0.45)'
                            : '0 12px 32px rgba(15, 23, 42, 0.08)',
                    },
                },
            },
            MuiButton: {
                styleOverrides: {
                    root: {
                        borderRadius: 999,
                        textTransform: 'none',
                        fontWeight: 600,
                    },
                },
            },
        },
    }), [mode]);
    const toggleMode = () => {
        setMode((prev) => (prev === 'light' ? 'dark' : 'light'));
    };
    return (_jsxs(ThemeProvider, { theme: theme, children: [_jsx(CssBaseline, {}), _jsx(ThemeModeProvider, { value: { mode, toggleMode }, children: _jsx(AuthProvider, { children: _jsxs(BrowserRouter, { children: [_jsx(RouteTitleManager, {}), _jsx(AppShell, { children: _jsxs(Routes, { children: [_jsx(Route, { path: "/", element: _jsx(LandingPage, {}) }), _jsx(Route, { path: "/auth/callback-complete", element: _jsx(AuthCallbackCompletePage, {}) }), _jsx(Route, { path: "/dashboard", element: _jsx(ProtectedRoute, { children: _jsx(DashboardPage, {}) }) }), _jsx(Route, { path: "/trends", element: _jsx(ProtectedRoute, { children: _jsx(TrendsPage, {}) }) }), _jsx(Route, { path: "/playlist-builder", element: _jsx(ProtectedRoute, { children: _jsx(PlaylistBuilderPage, {}) }) }), _jsx(Route, { path: "/settings", element: _jsx(ProtectedRoute, { children: _jsx(SettingsPage, {}) }) }), _jsx(Route, { path: "*", element: _jsx(Navigate, { to: "/", replace: true }) })] }) }), _jsx(GlobalErrorSnackbar, {})] }) }) })] }));
}
