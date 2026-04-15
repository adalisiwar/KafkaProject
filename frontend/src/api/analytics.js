import api from "./client";

export const fetchDailyAnalytics = async () => (await api.get("/analytics/daily")).data;
