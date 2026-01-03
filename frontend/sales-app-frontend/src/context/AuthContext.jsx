import { createContext, useState, useContext, useEffect } from 'react';
import apiClient from '../api/axiosConfig';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Verificăm la pornire dacă avem deja un token salvat
    useEffect(() => {
        const savedToken = localStorage.getItem('auth_token');
        const savedUsername = localStorage.getItem('username');
        const savedId = localStorage.getItem('user_id'); // <--- CITIM ID-UL

        if (savedToken && savedUsername && savedId) {
            setUser({ username: savedUsername, id: savedId });
        }
        setLoading(false);
    }, []);

    const login = async (username, password) => {
        const token = 'Basic ' + btoa(username + ':' + password);
        try {
            await apiClient.get('/products', { headers: { 'Authorization': token } });

            const usersResponse = await apiClient.get('/users', { headers: { 'Authorization': token } });
            const currentUser = usersResponse.data.find(u => u.username === username);

            if (currentUser) {
                localStorage.setItem('auth_token', token);
                localStorage.setItem('username', username);
                localStorage.setItem('user_id', currentUser.id);
                setUser({ username, id: currentUser.id });
                return true;
            }
            return false;
        } catch (error) {
            console.error("Login failed", error);
            return false;
        }
    };

    const logout = () => {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('username');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);