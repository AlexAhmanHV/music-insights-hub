import { jsx as _jsx } from "react/jsx-runtime";
import { createContext, useContext } from 'react';
const ThemeModeContext = createContext(undefined);
export function ThemeModeProvider({ value, children, }) {
    return _jsx(ThemeModeContext.Provider, { value: value, children: children });
}
export function useThemeMode() {
    const ctx = useContext(ThemeModeContext);
    if (!ctx) {
        throw new Error('useThemeMode must be used inside ThemeModeProvider');
    }
    return ctx;
}
