const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
let errorHandler = null;
export function setGlobalErrorHandler(handler) {
    errorHandler = handler;
}
export async function apiFetch(path, init) {
    const response = await fetch(`${API_BASE_URL}${path}`, {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...(init?.headers ?? {}),
        },
        ...init,
    });
    if (!response.ok) {
        let error = null;
        try {
            error = (await response.json());
        }
        catch {
            error = null;
        }
        const message = error?.message ?? `Request failed: ${response.status}`;
        if (errorHandler) {
            errorHandler(message);
        }
        throw new Error(message);
    }
    if (response.status === 204) {
        return undefined;
    }
    return (await response.json());
}
