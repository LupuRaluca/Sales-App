import { useState } from 'react';
import { UserService } from '../api/userService';
import { useNavigate, Link } from 'react-router-dom';
import './Auth.css'; // <--- Importăm fișierul comun

export default function Register() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '', email: '', password: '', firstName: '', lastName: ''
    });
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await UserService.register(formData);
            navigate('/login');
        } catch (err) {
            setError('Eroare la înregistrare. Verifică datele.');
        }
    };

    return (
        <div className="auth-container">
            <h2>Creează Cont</h2>
            {error && <div className="error-msg">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Username</label>
                    <input name="username" value={formData.username} onChange={handleChange} required placeholder="Ex: ionpopescu" />
                </div>

                <div className="form-group">
                    <label>Email</label>
                    <input type="email" name="email" value={formData.email} onChange={handleChange} required placeholder="Ex: ion@email.com" />
                </div>

                <div className="form-group">
                    <label>Parolă</label>
                    <input type="password" name="password" value={formData.password} onChange={handleChange} required placeholder="••••••••" />
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Prenume</label>
                        <input name="firstName" value={formData.firstName} onChange={handleChange} required placeholder="Ion" />
                    </div>
                    <div className="form-group">
                        <label>Nume</label>
                        <input name="lastName" value={formData.lastName} onChange={handleChange} required placeholder="Popescu" />
                    </div>
                </div>

                <button type="submit" className="btn-auth">Înregistrează-te</button>
            </form>

            <p className="auth-link">
                Ai deja cont? <Link to="/login">Autentifică-te aici</Link>
            </p>
        </div>
    );
}