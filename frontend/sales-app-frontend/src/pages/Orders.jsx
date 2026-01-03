import React, { useEffect, useState } from 'react';
import { OrderService } from '../api/orderService';
import { useAuth } from '../context/AuthContext';
import './Orders.css'; // Vom crea stilurile imediat

export default function Orders() {
    const { user } = useAuth();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (user && user.id) {
            fetchOrders();
        }
    }, [user]);

    const fetchOrders = async () => {
        try {
            const response = await OrderService.getUserOrders(user.id);
            setOrders(response.data);
        } catch (error) {
            console.error("Eroare la preluarea comenzilor:", error);
        } finally {
            setLoading(false);
        }
    };

    // Funcție helper pentru formatarea datei
    const formatDate = (isoDate) => {
        if (!isoDate) return '-';
        return new Date(isoDate).toLocaleString('ro-RO', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // Funcție pentru culoarea statusului
    const getStatusClass = (status) => {
        switch(status) {
            case 'NEW': return 'status-new';
            case 'PROCESSED': return 'status-processed';
            case 'SHIPPED': return 'status-shipped';
            case 'DELIVERED': return 'status-delivered';
            case 'CANCELED': return 'status-canceled';
            default: return '';
        }
    };

    if (loading) return <div className="orders-container loading">Se încarcă istoricul...</div>;

    if (orders.length === 0) {
        return (
            <div className="orders-container empty">
                <h2>Nu ai plasat nicio comandă încă.</h2>
            </div>
        );
    }

    return (
        <div className="orders-container">
            <h1 className="page-title">Istoric Comenzi</h1>

            <div className="orders-list">
                {orders.map((order) => (
                    <div key={order.id} className="order-card">

                        {/* Header-ul Cardului: Detalii generale */}
                        <div className="order-header">
                            <div className="order-info-group">
                                <span className="label">Comanda #</span>
                                <span className="value">{order.id}</span>
                            </div>
                            <div className="order-info-group">
                                <span className="label">Data</span>
                                <span className="value">{formatDate(order.date)}</span>
                            </div>
                            <div className="order-info-group">
                                <span className="label">Total</span>
                                <span className="value total-price">{order.total} RON</span>
                            </div>
                            <div className={`order-status ${getStatusClass(order.status)}`}>
                                {order.status}
                            </div>
                        </div>

                        {/* Body-ul Cardului: Lista de produse */}
                        <div className="order-body">
                            <h4>Produse comandate:</h4>
                            <ul className="order-items-list">
                                {order.items.map((item, idx) => (
                                    <li key={idx} className="order-item">
                                        <span className="item-qty">{item.quantity} x</span>
                                        <span className="item-name">{item.productName}</span>
                                        <span className="item-price">
                                            {(item.unitPrice * item.quantity).toFixed(2)} RON
                                        </span>
                                    </li>
                                ))}
                            </ul>
                        </div>

                        {/* Footer-ul Cardului: Adresa */}
                        <div className="order-footer">
                            <small>Livrat la: {order.shippingAddress}</small>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}