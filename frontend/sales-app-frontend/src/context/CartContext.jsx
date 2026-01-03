import { createContext, useState, useContext, useEffect } from 'react';
import { CartService } from '../api/cartService';
import { useAuth } from './AuthContext';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [cartCount, setCartCount] = useState(0);
    const { user } = useAuth();

    useEffect(() => {
        if (user && user.id) {
            refreshCart();
        } else {
            setCartCount(0);
        }
    }, [user]);

    const refreshCart = async () => {
        try {
            const response = await CartService.getCart(user.id);
            const items = response.data.cartItems || [];
            setCartCount(items.length);

        } catch (error) {
            console.error("Nu am putut încărca coșul", error);
        }
    };

    const addToCart = async (productId, quantity = 1) => {
        if (!user || !user.id) {
            alert("Trebuie să fii logat!");
            return false;
        }

        try {
            await CartService.addToCart(user.id, productId, quantity);
            await refreshCart(); // Actualizăm numărul după adăugare
            return true;
        } catch (error) {
            console.error("Eroare la adăugare în coș", error);
            alert("Eroare! Poate stoc insuficient?");
            return false;
        }
    };

    return (
        <CartContext.Provider value={{ cartCount, addToCart, refreshCart }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => useContext(CartContext);