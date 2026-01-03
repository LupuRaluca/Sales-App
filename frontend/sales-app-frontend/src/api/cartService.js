import apiClient from './axiosConfig';

export const CartService = {
    getCart: (userId) => apiClient.get(`/carts/user/${userId}`),

    addToCart: (userId, productId, quantity) =>
        apiClient.post(`/carts/${userId}/items`, null, {
            params: { productId, quantity }
        }),

    checkout: (userId, shippingDetails) =>
        apiClient.post(`/carts/${userId}/checkout`, null, {
            params: {
                address: shippingDetails.shippingAddress,
                name: shippingDetails.shippingName,
                phone: shippingDetails.shippingPhone
            }
        }),

    removeItem: (userId, productId) =>
        apiClient.delete(`/carts/${userId}/items/${productId}`),

    // Actualizează cantitatea fixă (folosit pt butoanele + și -)
    updateQuantity: (userId, productId, quantity) =>
        apiClient.put(`/carts/${userId}/items/${productId}`, null, {
            params: { quantity }
        })
};