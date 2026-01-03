import apiClient from './axiosConfig';

export const UserService = {

    register: (userData) => apiClient.post('/users', userData),
};