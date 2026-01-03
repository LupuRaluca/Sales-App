import apiClient from './axiosConfig';

export const ProductService = {

    getAllProducts: () => apiClient.get('/products'),

    getProductById: (id) => apiClient.get(`/products/${id}`),
};
