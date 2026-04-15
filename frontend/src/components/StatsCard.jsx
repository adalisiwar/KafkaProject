export default function StatsCard({ title, value, hint }) {
  return (
    <div className="glass-panel p-6">
      <p className="text-sm font-semibold uppercase tracking-[0.25em] text-steel">{title}</p>
      <p className="mt-3 font-display text-4xl text-ink">{value}</p>
      <p className="mt-2 text-sm text-slate-500">{hint}</p>
    </div>
  );
}
