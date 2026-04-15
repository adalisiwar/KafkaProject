export default function ProductCard({ product }) {
  return (
    <div className="glass-panel h-full p-6 transition-transform duration-300 hover:-translate-y-1">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="font-display text-xl text-ink">{product.name}</h3>
          <p className="mt-2 text-sm leading-6 text-slate-600">{product.description}</p>
        </div>
        <span className="rounded-full bg-sea/15 px-3 py-1 text-xs font-bold uppercase tracking-[0.2em] text-sea">
          Stock {product.stock}
        </span>
      </div>
      <p className="mt-5 text-2xl font-bold text-accent">${Number(product.price).toFixed(2)}</p>
    </div>
  );
}
