import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';
import {useCart} from "../context/CartContext.jsx";

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const { cartCount } = useCart();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="logo-container">
                {/* Logo-ul rămâne calea principală către Produse */}
                <Link to="/" className="nav-logo">SalesApp</Link>
            </div>

            <div className="nav-links">
                {user ? (
                    <>
                        <div className="user-info">
                            <span className="welcome-text">Salut,</span>
                            <span className="username">{user.username}</span>
                        </div>
                        <Link to="/orders" className="nav-item">Comenzi</Link>

                        <Link to="/cart" className="nav-link cart-link">
                            Coșul meu
                            {cartCount > 0 && (
                                <span className="cart-badge">{cartCount}</span>
                            )}
                        </Link>

                        <button onClick={handleLogout} className="btn-logout">
                            Logout
                        </button>
                    </>
                ) : (
                    <Link to="/login" className="nav-link login-link">Contul Meu</Link>
                )}
            </div>
        </nav>
    );
}