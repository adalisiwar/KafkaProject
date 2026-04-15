import api from "./client";

export const fetchProducts = async () => (await api.get("/products")).data;
export const createProduct = async (payload) => (await api.post("/products", payload)).data;
