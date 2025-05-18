import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

export const getCustomers = () => axios.get(`${API_BASE}/customers`);
export const getCustomer = (id) => axios.get(`${API_BASE}/customers/${id}`);
export const createCustomer = (data) => axios.post(`${API_BASE}/customers`, data);
export const updateCustomer = (id, data) => axios.put(`${API_BASE}/customers/${id}`, data);

export const getCities = () => axios.get(`${API_BASE}/cities`);
export const getCountries = () => axios.get(`${API_BASE}/countries`);

// Upload Excel for bulk customer creation
export const uploadCustomersExcel = (formData) => {
  return axios.post(`${API_BASE}/customers/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
