import { useEffect, useState } from "react";
import { fetchDailyAnalytics } from "../api/analytics";
import { fetchOrders } from "../api/orders";
import StatsCard from "../components/StatsCard";
import StatusBadge from "../components/StatusBadge";

export default function Dashboard() {
  const [orders, setOrders] = useState([]);
  const [analytics, setAnalytics] = useState({ revenue: 0, paidOrders: 0 });

  useEffect(() => {
    let ignore = false;
    const load = async () => {
      const [ordersData, analyticsData] = await Promise.all([fetchOrders(), fetchDailyAnalytics()]);
      if (!ignore) {
        setOrders(ordersData);
        setAnalytics(analyticsData);
      }
    };

    load();
    const interval = setInterval(load, 3000);
    return () => {
      ignore = true;
      clearInterval(interval);
    };
  }, []);

  const pendingCount = orders.filter((order) => order.status === "PENDING" || order.status === "VALIDATED").length;

  return (
    <div className="space-y-8">
      <section className="grid gap-4 md:grid-cols-3">
        <StatsCard title="Total Orders" value={orders.length} hint="Polled every 3 seconds" />
        <StatsCard title="Revenue Today" value={`$${Number(analytics.revenue ?? 0).toFixed(2)}`} hint={`${analytics.paidOrders ?? 0} paid orders`} />
        <StatsCard title="Pending Queue" value={pendingCount} hint="Awaiting payment or shipment" />
      </section>

      <section className="glass-panel p-6">
        <div className="mb-5 flex items-center justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Live Pipeline</p>
            <h2 className="mt-2 font-display text-2xl text-ink">Latest orders</h2>
          </div>
        </div>
        <div className="space-y-3">
          {orders.slice(0, 5).map((order) => (
            <div key={order.id} className="rounded-2xl border border-slate-100 bg-white px-4 py-3">
              <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                <div>
                  <p className="font-display text-lg text-ink">Order #{order.id}</p>
                  <p className="text-sm text-slate-500">{order.customer.name} • {new Date(order.createdAt).toLocaleString()}</p>
                </div>
                <div className="flex items-center gap-3">
                  <p className="font-semibold text-ink">${Number(order.totalAmount).toFixed(2)}</p>
                  <StatusBadge status={order.status} />
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
