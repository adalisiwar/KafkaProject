export default function OrderTimeline({ items = [] }) {
  return (
    <div className="space-y-4">
      {items.map((item, index) => (
        <div key={`${item.label}-${index}`} className="flex gap-4">
          <div className="mt-1 flex h-10 w-10 items-center justify-center rounded-full bg-ink text-xs font-bold text-white">
            {index + 1}
          </div>
          <div className="glass-panel flex-1 p-4">
            <p className="font-display text-lg text-ink">{item.label}</p>
            <p className="mt-1 text-sm text-slate-600">{item.description}</p>
            <p className="mt-2 text-xs font-semibold uppercase tracking-[0.25em] text-steel">
              {new Date(item.at).toLocaleString()}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}
