import axiosClient from './axiosClient';

const categoryApi = {
    getAll: () => axiosClient.get('/categories'),
    create: (data) => axiosClient.post('/categories', data),
};

export default categoryApi;