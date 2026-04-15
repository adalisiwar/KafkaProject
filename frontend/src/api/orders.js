import api from "./client";

export const fetchOrders = async () => (await api.get("/orders")).data;
export const fetchOrder = async (id) => (await api.get(`/orders/${id}`)).data;
export const createOrder = async (payload) => (await api.post("/orders", payload)).data;
export const payOrder = async (id) => (await api.post(`/orders/${id}/pay`)).data;
