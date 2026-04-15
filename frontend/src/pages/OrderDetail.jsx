import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchOrder } from "../api/orders";
import { fetchNotifications } from "../api/notifications";
import OrderTimeline from "../components/OrderTimeline";
import StatusBadge from "../components/StatusBadge";

export default function OrderDetail() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    let ignore = false;
    const load = async () => {
      const [orderData, notificationData] = await Promise.all([fetchOrder(id), fetchNotifications(id)]);
      if (!ignore) {
        setOrder(orderData);
        setNotifications(notificationData);
      }
    };
    load();
    const interval = setInterval(load, 3000);
    return () => {
      ignore = true;
      clearInterval(interval);
    };
  }, [id]);

  if (!order) {
    return <div className="glass-panel p-6">Loading order...</div>;
  }

  return (
    <div className="grid gap-8 lg:grid-cols-[1fr,0.9fr]">
      <section className="glass-panel p-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Order Detail</p>
            <h2 className="mt-2 font-display text-3xl text-ink">Order #{order.id}</h2>
            <p className="mt-2 text-slate-600">{order.customer.name} • {order.customer.email}</p>
          </div>
          <StatusBadge status={order.status} />
        </div>

        <div className="mt-6 space-y-3">
          {order.items.map((item) => (
            <div key={item.id} className="rounded-2xl border border-slate-100 bg-slate-50 px-4 py-4">
              <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                <div>
                  <p className="font-semibold text-ink">{item.productName}</p>
                  <p className="text-sm text-slate-500">Qty {item.quantity} • ${Number(item.unitPrice).toFixed(2)} each</p>
                </div>
                <p className="font-bold text-accent">${Number(item.lineTotal).toFixed(2)}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="space-y-6">
        <div className="glass-panel p-6">
          <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Timeline</p>
          <h3 className="mt-2 font-display text-2xl text-ink">Status and notification history</h3>
          <div className="mt-6">
            <OrderTimeline items={order.timeline} />
          </div>
        </div>

        <div className="glass-panel p-6">
          <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Emails</p>
          <div className="mt-4 space-y-3">
            {notifications.map((notification) => (
              <div key={notification.id} className="rounded-2xl bg-slate-50 px-4 py-3">
                <p className="font-semibold text-ink">{notification.type}</p>
                <p className="text-sm text-slate-500">{new Date(notification.sentAt).toLocaleString()}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
