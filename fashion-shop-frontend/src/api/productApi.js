import axiosClient from './axiosClient';

const productApi = {
    getAll: () => axiosClient.get('/products'),
    getVariants: (productId) => axiosClient.get(`/products/${productId}/variants`),
    create: (data) => axiosClient.post('/products', data),
    search: (params) => axiosClient.get('/products/search', { params }),
    findBySku: (sku) => axiosClient.get(`/products/sku/${sku}`),
};

export default productApi;