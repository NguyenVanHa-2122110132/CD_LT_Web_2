import axiosClient from './axiosClient';

const employeeApi = {
    getAll: (keyword) => axiosClient.get('/employees', { params: { keyword } }),
    getById: (id) => axiosClient.get(`/employees/${id}`),
    generateCode: () => axiosClient.get('/employees/generate-code'),
    create: (data) => axiosClient.post('/employees', data),
    update: (id, data) => axiosClient.put(`/employees/${id}`, data),
    deactivate: (id) => axiosClient.patch(`/employees/${id}/deactivate`),
};

export default employeeApi;