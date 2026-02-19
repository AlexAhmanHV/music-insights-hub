import { jsx as _jsx } from "react/jsx-runtime";
import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { api } from '../api';
const AuthContext = createContext(undefined);
export function AuthProvider({ children }) {
    const [me, setMe] = useState(null);
    const [loading, setLoading] = useState(true);
    const refreshMe = async () => {
        setLoading(true);
        try {
            setMe(await api.me());
        }
        finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        void refreshMe();
    }, []);
    const value = useMemo(() => ({ me, loading, refreshMe }), [me, loading]);
    return _jsx(AuthContext.Provider, { value: value, children: children });
}
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) {
        throw new Error('useAuth must be used inside AuthProvider');
    }
    return ctx;
}
