const colorMap = {
  PENDING: "bg-slate-200 text-slate-700",
  VALIDATED: "bg-blue-100 text-blue-700",
  PAID: "bg-emerald-100 text-emerald-700",
  SHIPPED: "bg-violet-100 text-violet-700",
  REJECTED: "bg-rose-100 text-rose-700",
};

export default function StatusBadge({ status }) {
  return (
    <span className={`inline-flex rounded-full px-3 py-1 text-xs font-bold uppercase tracking-[0.2em] ${colorMap[status] ?? colorMap.PENDING}`}>
      {status}
    </span>
  );
}
