import apiClient from './axiosConfig';

export const OrderService = {

    getUserOrders: (userId) => apiClient.get(`/orders/user/${userId}`),
};