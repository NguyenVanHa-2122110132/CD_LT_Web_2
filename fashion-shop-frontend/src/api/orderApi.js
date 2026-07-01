import axiosClient from './axiosClient';

const orderApi = {
    create: (data) => axiosClient.post('/orders/create', data),
    complete: (orderId, paymentMethod) =>
        axiosClient.post(`/orders/${orderId}/complete`, null, { params: { paymentMethod } }),
    cancel: (orderId, isAdmin = false) =>
        axiosClient.post(`/orders/${orderId}/cancel`, null, { params: { isAdmin } }),
};

export default orderApi;