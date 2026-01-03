import { useEffect, useState } from 'react';
import { CartService } from '../api/cartService';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { useNavigate } from 'react-router-dom';
import './Cart.css';

export default function Cart() {
    const { user } = useAuth();
    const { refreshCart } = useCart();
    const navigate = useNavigate();

    const [cartItems, setCartItems] = useState([]);
    const [cartSummary, setCartSummary] = useState({ subtotal: 0, tax: 0, total: 0 });
    const [loading, setLoading] = useState(true);

    const [shipping, setShipping] = useState({ fullName: '', address: '', phone: '' });

    useEffect(() => {
        loadCart();
    }, [user]);

    const loadCart = async () => {
        if (!user || !user.id) return;
        try {
            const response = await CartService.getCart(user.id);
            const data = response.data;
            setCartItems(data.cartItems || []);
            setCartSummary({
                subtotal: data.subtotal || 0,
                tax: data.tax || 0,
                total: data.total || 0
            });
        } catch (err) {
            console.error("Cart load error:", err);
            setCartItems([]);
            // Resetăm și sumarul dacă e eroare (coș gol)
            setCartSummary({ subtotal: 0, tax: 0, total: 0 });
        } finally {
            setLoading(false);
        }
    };

    // --- LOGICA NOUĂ DE CANTITATE ---
    const handleQuantityChange = async (item, delta) => {
        const newQty = item.quantity + delta;

        // Dacă ajunge la 0, întrebăm dacă vrea să șteargă
        if (newQty < 1) {
            const confirmDelete = window.confirm("Ștergi acest produs din coș?");
            if (confirmDelete) {
                await removeItem(item.productId);
            }
            return;
        }

        try {
            // Apelăm endpoint-ul de update (PUT)
            await CartService.updateQuantity(user.id, item.productId, newQty);
            // Reîncărcăm coșul pentru a primi noile calcule de preț/total de la Backend
            await loadCart();
            await refreshCart(); // Actualizăm bulina din navbar
        } catch (error) {
            console.error("Eroare la modificarea cantității", error);
            alert("Nu s-a putut modifica cantitatea.");
        }
    };

    const removeItem = async (productId) => {
        if (!window.confirm("Ești sigur că vrei să scoți produsul?")) return;

        try {
            await CartService.removeItem(user.id, productId);
            await loadCart();   // Refresh listă și totaluri
            await refreshCart(); // Refresh navbar
        } catch (error) {
            console.error("Eroare la ștergere", error);
            alert("Nu s-a putut șterge produsul.");
        }
    };

    const handleCheckout = async (e) => {
        e.preventDefault();
        if (!shipping.fullName || !shipping.address || !shipping.phone) {
            alert("Completează datele de livrare!"); return;
        }
        try {
            await CartService.checkout(user.id, {
                shippingAddress: shipping.address, shippingName: shipping.fullName, shippingPhone: shipping.phone
            });
            alert("Comandă plasată!");
            await refreshCart(); navigate('/');
        } catch (err) {
            console.error(err); alert("Eroare la plasarea comenzii.");
        }
    };

    if (loading) return <div style={{padding:'20px'}}>Se încarcă coșul...</div>;

    if (cartItems.length === 0) {
        return (
            <div className="cart-page empty-cart">
                <h2>Coșul tău este gol</h2>
                <button className="btn-checkout" style={{width:'200px'}} onClick={() => navigate('/')}>
                    Vezi Produse
                </button>
            </div>
        );
    }

    return (
        <div className="cart-page">
            <h1 className="cart-title">Coșul de Cumpărături ({cartItems.length} produse)</h1>

            <div className="cart-layout">
                <div className="cart-items-container">
                    {cartItems.map((item) => (
                        <div key={item.id} className="cart-item">
                            {/* 1. Imagine Placeholder */}
                            <div className="item-image-placeholder">
                                {item.productName ? item.productName.charAt(0) : '?'}
                            </div>

                            {/* 2. Detalii Produs */}
                            <div className="item-details">
                                <div className="item-name">{item.productName}</div>
                                <div className="item-price-single">
                                    Preț unitar: {item.unitPrice} RON
                                </div>
                            </div>

                            {/* 3. Controale Cantitate (cu validare stoc) */}
                            <div className="item-quantity-wrapper">
                                <div className="quantity-controls">
                                    <button
                                        className="btn-qty"
                                        onClick={() => handleQuantityChange(item, -1)}
                                        disabled={loading}
                                    >
                                        -
                                    </button>

                                    <span className="qty-display">{item.quantity}</span>

                                    <button
                                        className="btn-qty"
                                        onClick={() => handleQuantityChange(item, 1)}
                                        disabled={item.quantity >= item.availableStock}
                                        title={item.quantity >= item.availableStock ? "Stoc maxim atins" : ""}
                                    >
                                        +
                                    </button>
                                </div>

                                {/* Mesaj de avertizare stoc */}
                                {item.quantity >= item.availableStock && (
                                    <span className="stock-warning">Max stoc ({item.availableStock})</span>
                                )}
                            </div>

                            {/* 4. Total per linie */}
                            <div className="item-total">
                                {(item.unitPrice * item.quantity).toFixed(2)} RON
                            </div>

                            {/* 5. Buton Ștergere (RE-ADĂUGAT) */}
                            <button
                                className="btn-remove"
                                onClick={() => removeItem(item.productId)}
                                title="Elimină produsul din coș"
                            >
                                🗑️
                            </button>
                        </div>
                    ))}
                </div>

                <div className="cart-summary">
                    <h3 className="summary-title">Sumar Comandă</h3>
                    <div className="summary-row">
                        <span>Subtotal</span><span>{cartSummary.subtotal.toFixed(2)} RON</span>
                    </div>
                    <div className="summary-row">
                        <span>TVA</span><span>{cartSummary.tax.toFixed(2)} RON</span>
                    </div>
                    <div className="summary-row summary-total">
                        <span>Total</span><span>{cartSummary.total.toFixed(2)} RON</span>
                    </div>

                    <form onSubmit={handleCheckout} className="shipping-form">
                        <h4>Detalii Livrare</h4>
                        <input type="text" placeholder="Nume Complet" className="shipping-input" value={shipping.fullName} onChange={e => setShipping({...shipping, fullName: e.target.value})} required />
                        <input type="text" placeholder="Adresa" className="shipping-input" value={shipping.address} onChange={e => setShipping({...shipping, address: e.target.value})} required />
                        <input type="tel" placeholder="Telefon" className="shipping-input" value={shipping.phone} onChange={e => setShipping({...shipping, phone: e.target.value})} required />
                        <button type="submit" className="btn-checkout">Finalizează Comanda</button>
                    </form>
                </div>
            </div>
        </div>
    );
}