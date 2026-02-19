import { jsx as _jsx } from "react/jsx-runtime";
import { CircularProgress, Container } from '@mui/material';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
export function ProtectedRoute({ children }) {
    const { me, loading } = useAuth();
    if (loading) {
        return (_jsx(Container, { sx: { py: 8, textAlign: 'center' }, children: _jsx(CircularProgress, {}) }));
    }
    if (!me?.connected) {
        return _jsx(Navigate, { to: "/", replace: true });
    }
    return children;
}
