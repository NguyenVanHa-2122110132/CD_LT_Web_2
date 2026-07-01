import axiosClient from './axiosClient';

const brandApi = {
    getAll: () => axiosClient.get('/brands'),
    create: (data) => axiosClient.post('/brands', data),
};

export default brandApi;