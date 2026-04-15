import { useEffect, useState } from "react";
import { fetchOrders } from "../api/orders";
import { fetchNotifications } from "../api/notifications";

export default function Notifications() {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    let ignore = false;
    const load = async () => {
      const orders = await fetchOrders();
      const notifications = await Promise.all(
        orders.map(async (order) => {
          const items = await fetchNotifications(order.id);
          return items.map((item) => ({ ...item, customerName: order.customer.name }));
        })
      );
      if (!ignore) {
        setRows(notifications.flat().sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt)));
      }
    };
    load();
    const interval = setInterval(load, 3000);
    return () => {
      ignore = true;
      clearInterval(interval);
    };
  }, []);

  return (
    <section className="glass-panel p-6">
      <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Mail Log</p>
      <h2 className="mt-2 font-display text-3xl text-ink">Email notifications emitted by Kafka events</h2>

      <div className="mt-6 space-y-3">
        {rows.map((row) => (
          <div key={row.id} className="rounded-2xl border border-slate-100 bg-white px-4 py-4">
            <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
              <div>
                <p className="font-semibold text-ink">Order #{row.orderId} • {row.type}</p>
                <p className="text-sm text-slate-500">{row.customerName}</p>
              </div>
              <p className="text-sm font-semibold uppercase tracking-[0.2em] text-steel">
                {new Date(row.sentAt).toLocaleString()}
              </p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
