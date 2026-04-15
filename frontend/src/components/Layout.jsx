import { NavLink } from "react-router-dom";
import { BarChart3, Package, ShoppingCart, ClipboardList, Bell } from 'lucide-react';

const links = [
  { to: "/", label: "Dashboard", icon: <BarChart3 size={16} /> },
  { to: "/products", label: "Products", icon: <Package size={16} /> },
  { to: "/place-order", label: "Place Order", icon: <ShoppingCart size={16} /> },
  { to: "/orders", label: "Orders", icon: <ClipboardList size={16} /> },
  { to: "/notifications", label: "Notifications", icon: <Bell size={16} /> },
];

export default function Layout({ children }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-800">
      {/* Sticky Header */}
      <header className="sticky top-0 z-50 border-b border-slate-700/30 backdrop-blur-md bg-slate-950/40">
        {/* Top Bar */}
        <div className="px-6 py-4 md:px-8">
          <div className="mx-auto max-w-7xl flex items-center justify-between">
            {/* Logo & Branding */}
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-teal-400 to-cyan-500 flex items-center justify-center">
                <span className="text-white font-bold text-lg">K</span>
              </div>
              <div>
                <p className="text-xs font-semibold text-slate-400 uppercase tracking-widest">Kafka</p>
                <h1 className="text-lg font-bold text-white">Ecommerce Lab</h1>
              </div>
            </div>

            {/* Right Side Info */}
            <div className="hidden md:flex items-center gap-4">
              <div className="text-right">
                <p className="text-xs text-slate-400 uppercase tracking-wider">Real-time Orchestration</p>
                <p className="text-sm font-semibold text-teal-300">Active</p>
              </div>
              <div className="w-2 h-2 rounded-full bg-teal-400 animate-pulse"></div>
            </div>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="border-t border-slate-700/20 px-6 md:px-8">
          <div className="mx-auto max-w-7xl flex gap-1 overflow-x-auto pb-0">
            {links.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  `group relative whitespace-nowrap px-4 py-3 text-sm font-semibold transition-all duration-200 ${
                    isActive
                      ? "text-white"
                      : "text-slate-400 hover:text-slate-200"
                  }`
                }
              >
                {({ isActive }) => (
                  <>
                    <span className="flex items-center gap-2">
                      <span>{link.icon}</span>
                      {link.label}
                    </span>
                    {isActive && (
                      <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-gradient-to-r from-teal-400 to-cyan-400 rounded-full"></div>
                    )}
                    {!isActive && (
                      <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-gradient-to-r from-slate-600 to-slate-700 rounded-full scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left"></div>
                    )}
                  </>
                )}
              </NavLink>
            ))}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="relative">
        <div className="px-4 py-6 md:px-8 md:py-8">
          <div className="mx-auto max-w-7xl">
            {/* Decorative Grid Background */}
            <div className="absolute inset-0 -z-10 opacity-10">
              <div className="absolute inset-0 bg-[linear-gradient(0deg,transparent_24%,rgba(148,163,184,0.05)_25%,rgba(148,163,184,0.05)_26%,transparent_27%,transparent_74%,rgba(148,163,184,0.05)_75%,rgba(148,163,184,0.05)_76%,transparent_77%,transparent)] bg-[length:50px_50px]"></div>
            </div>
            
            {/* Content Container */}
            <div className="relative">
              {children}
            </div>
          </div>
        </div>
      </main>

      {/* Subtle Glow Effect */}
      <div className="fixed inset-0 -z-20 pointer-events-none">
        <div className="absolute top-1/4 right-0 w-96 h-96 bg-teal-500/5 rounded-full blur-3xl"></div>
        <div className="absolute bottom-1/4 left-0 w-96 h-96 bg-cyan-500/5 rounded-full blur-3xl"></div>
      </div>
    </div>
  );
}
