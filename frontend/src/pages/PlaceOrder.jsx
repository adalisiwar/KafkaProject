import { useEffect, useMemo, useState } from "react";
import { fetchCustomers } from "../api/customers";
import { createOrder } from "../api/orders";
import { fetchProducts } from "../api/products";

export default function PlaceOrder() {
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [customerId, setCustomerId] = useState("");
  const [lines, setLines] = useState([{ productId: "", quantity: 1 }]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    Promise.all([fetchCustomers(), fetchProducts()]).then(([customersData, productsData]) => {
      setCustomers(customersData);
      setProducts(productsData);
      if (customersData[0]) {
        setCustomerId(String(customersData[0].id));
      }
    });
  }, []);

  const total = useMemo(
    () =>
      lines.reduce((sum, line) => {
        const product = products.find((item) => String(item.id) === String(line.productId));
        return sum + (product ? Number(product.price) * Number(line.quantity) : 0);
      }, 0),
    [lines, products]
  );

  const updateLine = (index, key, value) => {
    setLines((current) => current.map((line, idx) => (idx === index ? { ...line, [key]: value } : line)));
  };

  const addLine = () => setLines((current) => [...current, { productId: "", quantity: 1 }]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const payload = {
        customerId: Number(customerId),
        items: lines.filter((line) => line.productId).map((line) => ({
          productId: Number(line.productId),
          quantity: Number(line.quantity),
        })),
      };
      const order = await createOrder(payload);
      setMessage(`Order #${order.id} created and published to Kafka.`);
      setLines([{ productId: "", quantity: 1 }]);
    } catch (error) {
      setMessage(error.response?.data?.error ?? "Unable to create order");
    }
  };

  return (
    <section className="grid gap-8 lg:grid-cols-[1.3fr,0.7fr]">
      <form onSubmit={handleSubmit} className="glass-panel p-6">
        <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Checkout Simulation</p>
        <h2 className="mt-2 font-display text-3xl text-ink">Place a new order</h2>

        <label className="mt-6 block text-sm font-semibold text-ink">
          Customer
          <select
            value={customerId}
            onChange={(event) => setCustomerId(event.target.value)}
            className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3"
          >
            {customers.map((customer) => (
              <option key={customer.id} value={customer.id}>
                {customer.name} ({customer.email})
              </option>
            ))}
          </select>
        </label>

        <div className="mt-6 space-y-4">
          {lines.map((line, index) => (
            <div key={index} className="grid gap-3 rounded-2xl border border-slate-100 bg-slate-50 p-4 md:grid-cols-[2fr,1fr]">
              <select
                value={line.productId}
                onChange={(event) => updateLine(index, "productId", event.target.value)}
                className="rounded-2xl border border-slate-200 px-4 py-3"
              >
                <option value="">Select product</option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} (${Number(product.price).toFixed(2)})
                  </option>
                ))}
              </select>
              <input
                min="1"
                type="number"
                value={line.quantity}
                onChange={(event) => updateLine(index, "quantity", event.target.value)}
                className="rounded-2xl border border-slate-200 px-4 py-3"
              />
            </div>
          ))}
        </div>

        <div className="mt-6 flex flex-wrap gap-3">
          <button type="button" onClick={addLine} className="rounded-full bg-white px-5 py-3 font-semibold text-ink">
            Add product
          </button>
          <button type="submit" className="rounded-full bg-accent px-5 py-3 font-semibold text-white">
            Submit order
          </button>
        </div>
      </form>

      <aside className="glass-panel p-6">
        <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Summary</p>
        <h3 className="mt-2 font-display text-2xl text-ink">Cart total</h3>
        <p className="mt-6 font-display text-5xl text-accent">${total.toFixed(2)}</p>
        <p className="mt-3 text-sm leading-6 text-slate-600">
          Creating the order writes the order to PostgreSQL and publishes an <code>order.created</code> event.
        </p>
        {message && <p className="mt-6 rounded-2xl bg-slate-100 px-4 py-3 text-sm text-slate-700">{message}</p>}
      </aside>
    </section>
  );
}
