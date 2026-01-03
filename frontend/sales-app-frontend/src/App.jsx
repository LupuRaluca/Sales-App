import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Navbar from './components/Navbar';
import ProductList from './pages/ProductList';
import Login from './pages/Login';
import Register from "./pages/Register";
import Cart from "./pages/Cart";
import Orders from "./pages/Orders";

// Componentă pentru rute protejate
const PrivateRoute = ({ children }) => {
    const { user, loading } = useAuth();

    if (loading) return <div style={{padding: '20px'}}>Se verifică autentificarea...</div>;

    return user ? children : <Navigate to="/login" />;
};

function App() {
    return (
        <AuthProvider>
            <CartProvider>
                <Router>
                    <Navbar />
                    <div className="container">
                        <Routes>
                            {/* Rute Publice */}
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />

                            {/* Rute Protejate */}
                            <Route path="/" element={
                                <PrivateRoute>
                                    <ProductList />
                                </PrivateRoute>
                            } />

                            {/* 2. Conectăm pagina de Cart aici */}
                            <Route path="/cart" element={
                                <PrivateRoute>
                                    <Cart />
                                </PrivateRoute>
                            } />
                            <Route path="/orders" element={<Orders />} />
                        </Routes>
                    </div>
                </Router>
            </CartProvider>
        </AuthProvider>
    );
}

export default App;