import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import './Auth.css'; // <--- Același CSS

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        const success = await login(username, password);
        if (success) navigate('/');
        else setError('Username sau parolă incorectă.');
    };

    return (
        <div className="auth-container">
            <h2>Bine ai venit!</h2>
            {error && <div className="error-msg">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Username</label>
                    <input
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        placeholder="Username-ul tău"
                    />
                </div>
                <div className="form-group">
                    <label>Parolă</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        placeholder="••••••••"
                    />
                </div>
                <button type="submit" className="btn-auth">Logare</button>
            </form>

            <p className="auth-link">
                Nu ai cont? <Link to="/register">Creează unul acum</Link>
            </p>
        </div>
    );
}