import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { fetchOrders, payOrder } from "../api/orders";
import StatusBadge from "../components/StatusBadge";

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    let ignore = false;
    const load = async () => {
      const data = await fetchOrders();
      if (!ignore) {
        setOrders(data);
      }
    };
    load();
    const interval = setInterval(load, 3000);
    return () => {
      ignore = true;
      clearInterval(interval);
    };
  }, []);

  const handlePay = async (id) => {
    try {
      const response = await payOrder(id);
      setMessage(response.message);
    } catch (error) {
      setMessage(error.response?.data?.error ?? "Unable to process payment");
    }
  };

  return (
    <section className="glass-panel p-6">
      <div className="mb-6 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Orders</p>
          <h2 className="mt-2 font-display text-3xl text-ink">Track state transitions in real time</h2>
        </div>
        {message && <p className="rounded-full bg-slate-100 px-4 py-2 text-sm text-slate-600">{message}</p>}
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full text-left">
          <thead>
            <tr className="text-sm uppercase tracking-[0.2em] text-steel">
              <th className="pb-4">Order</th>
              <th className="pb-4">Customer</th>
              <th className="pb-4">Created</th>
              <th className="pb-4">Total</th>
              <th className="pb-4">Status</th>
              <th className="pb-4">Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id} className="border-t border-slate-100">
                <td className="py-4 font-semibold text-ink">#{order.id}</td>
                <td className="py-4">{order.customer.name}</td>
                <td className="py-4 text-sm text-slate-500">{new Date(order.createdAt).toLocaleString()}</td>
                <td className="py-4 font-semibold">${Number(order.totalAmount).toFixed(2)}</td>
                <td className="py-4"><StatusBadge status={order.status} /></td>
                <td className="py-4">
                  <div className="flex flex-wrap gap-2">
                    <Link to={`/orders/${order.id}`} className="rounded-full bg-ink px-4 py-2 text-sm font-semibold text-white">
                      View
                    </Link>
                    {order.status === "VALIDATED" && (
                      <button onClick={() => handlePay(order.id)} className="rounded-full bg-accent px-4 py-2 text-sm font-semibold text-white">
                        Pay
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
