import axios from 'axios'

// Use relative URL so nginx can proxy to API Gateway
// When running in Docker, nginx proxies /api to api-gateway:8080
// When running locally with npm run dev, vite.config.ts proxies /api to localhost:8080
const API_BASE_URL = import.meta.env.VITE_API_URL || '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api

// Product API
export const productApi = {
  getAll: () => api.get('/products'),
  getById: (id: number) => api.get(`/products/${id}`),
  search: (query: string) => api.get(`/products/search?q=${query}`),
  getByCategory: (category: string) => api.get(`/products/category/${category}`),
  create: (data: {
    name: string
    description?: string
    category: string
    price: number
    stockQuantity: number
    imageUrl?: string
  }) => api.post('/products', data),
  update: (id: number, data: {
    name: string
    description?: string
    category: string
    price: number
    stockQuantity: number
    imageUrl?: string
  }) => api.put(`/products/${id}`, data),
  delete: (id: number) => api.delete(`/products/${id}`),
}

// Cart API
export const cartApi = {
  get: (sessionId: string) => api.get(`/cart/${sessionId}`),
  addItem: (sessionId: string, item: any) => api.post(`/cart/${sessionId}/items`, item),
  removeItem: (sessionId: string, productId: number) => 
    api.delete(`/cart/${sessionId}/items/${productId}`),
  updateQuantity: (sessionId: string, productId: number, quantity: number) =>
    api.put(`/cart/${sessionId}/items/${productId}?quantity=${quantity}`),
  clear: (sessionId: string) => api.delete(`/cart/${sessionId}`),
}

// Auth API
export const authApi = {
  register: (data: { email: string; password: string; firstName?: string; lastName?: string }) =>
    api.post('/auth/register', data),
  login: (data: { email: string; password: string }) =>
    api.post('/auth/login', data),
}

// Order API
export const orderApi = {
  create: (data: any) => api.post('/orders', data),
  getById: (id: number) => api.get(`/orders/${id}`),
  getByCustomer: (customerId: number) => api.get(`/orders/customer/${customerId}`),
}

