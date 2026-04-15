import api from "./client";

export const fetchNotifications = async (orderId) => (await api.get(`/notifications/${orderId}`)).data;
