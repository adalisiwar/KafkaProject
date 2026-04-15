INSERT INTO customers (id, name, email, created_at) VALUES
  (1, 'Sophia Carter', 'sophia.carter@example.com', NOW()),
  (2, 'Liam Bennett', 'liam.bennett@example.com', NOW()),
  (3, 'Ava Richardson', 'ava.richardson@example.com', NOW()),
  (4, 'Noah Foster', 'noah.foster@example.com', NOW()),
  (5, 'Mia Thompson', 'mia.thompson@example.com', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, name, description, price, stock) VALUES
  (1, 'MacBook Air M3', '13-inch laptop with Apple M3 chip and 16GB RAM', 1499.99, 14),
  (2, 'Dell XPS 15', '15-inch OLED productivity laptop with Intel Core Ultra', 1899.00, 10),
  (3, 'Sony WH-1000XM5', 'Noise-cancelling wireless headphones', 399.99, 25),
  (4, 'Logitech MX Master 3S', 'Wireless productivity mouse with quiet clicks', 109.95, 40),
  (5, 'Samsung Odyssey G7', '32-inch curved gaming monitor, 240Hz', 699.00, 8),
  (6, 'Apple iPad Pro 11', '11-inch tablet with Pencil support', 999.00, 12),
  (7, 'NVIDIA Shield TV Pro', '4K streaming and media device', 219.00, 20),
  (8, 'Canon EOS R10', 'Mirrorless camera with 18-45mm kit lens', 1199.00, 6),
  (9, 'Keychron Q1 Max', 'Wireless mechanical keyboard with hot-swappable switches', 239.00, 18),
  (10, 'Anker Prime 250W', 'High-speed desktop charger for laptops and phones', 169.99, 30)
ON CONFLICT (id) DO NOTHING;

SELECT setval('customers_id_seq', COALESCE((SELECT MAX(id) FROM customers), 1), true);
SELECT setval('products_id_seq', COALESCE((SELECT MAX(id) FROM products), 1), true);
