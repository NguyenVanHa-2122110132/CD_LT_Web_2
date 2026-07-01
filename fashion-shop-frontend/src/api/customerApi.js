import axiosClient from './axiosClient';

const customerApi = {
  quickAdd: (data) => axiosClient.post('/customers/quick-add', data),
  getPurchaseHistory: (id) => axiosClient.get(`/customers/${id}/orders`),
  findByQrCode: (code) => axiosClient.get(`/customers/qr/${code}`),
  redeemPoints: (data) => axiosClient.post('/customers/redeem-points', data),
  getAll: () => axiosClient.get('/customers'),
};


export default customerApi;