import { Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";
import Notifications from "./pages/Notifications";
import OrderDetail from "./pages/OrderDetail";
import Orders from "./pages/Orders";
import PlaceOrder from "./pages/PlaceOrder";
import Products from "./pages/Products";

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/products" element={<Products />} />
        <Route path="/place-order" element={<PlaceOrder />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/orders/:id" element={<OrderDetail />} />
        <Route path="/notifications" element={<Notifications />} />
      </Routes>
    </Layout>
  );
}
