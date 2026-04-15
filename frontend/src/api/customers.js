import api from "./client";

export const fetchCustomers = async () => (await api.get("/customers")).data;
export const createCustomer = async (payload) => (await api.post("/customers", payload)).data;
