import { useEffect, useState } from "react";
import { fetchProducts } from "../api/products";
import ProductCard from "../components/ProductCard";

export default function Products() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    let ignore = false;
    const load = async () => {
      const data = await fetchProducts();
      if (!ignore) {
        setProducts(data);
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
    <section>
      <div className="mb-6">
        <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">Inventory</p>
        <h2 className="mt-2 font-display text-3xl text-white">Products with live stock visibility</h2>
      </div>
      <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  );
}
