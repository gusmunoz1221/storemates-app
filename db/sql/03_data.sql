-- ================================
-- CATEGORIES
-- ================================
INSERT INTO categories (name, description) VALUES
('Electrónica', 'Productos electrónicos y tecnológicos'),
('Hogar', 'Artículos para el hogar'),
('Deportes', 'Equipamiento y accesorios deportivos'),
('Audio', 'Equipos y accesorios de audio'),
('Oficina', 'Productos para oficina y estudio');

-- ================================
-- SUBCATEGORIES
-- ================================
INSERT INTO subcategories (name, description, category_id) VALUES
(
  'Celulares',
  'Smartphones y accesorios',
  (SELECT id FROM categories WHERE name = 'Electrónica')
),
(
  'Computación',
  'Notebooks, PCs y periféricos',
  (SELECT id FROM categories WHERE name = 'Electrónica')
),
(
  'Cocina',
  'Electrodomésticos de cocina',
  (SELECT id FROM categories WHERE name = 'Hogar')
),
(
  'Fitness',
  'Equipamiento para entrenamiento',
  (SELECT id FROM categories WHERE name = 'Deportes')
),
(
  'Auriculares',
  'Auriculares y headsets',
  (SELECT id FROM categories WHERE name = 'Audio')
),
(
  'Escritorios',
  'Escritorios y mobiliario de oficina',
  (SELECT id FROM categories WHERE name = 'Oficina')
);


-- ================================
-- PRODUCTS
-- ================================
INSERT INTO products (name, price, description, url, stock, subcategory_id) VALUES
(
  'Samsung Galaxy S23',
  1200000.00,
  'Smartphone Samsung Galaxy S23 128GB',
  'https://example.com/s23',
  10,
  (SELECT id FROM subcategories WHERE name = 'Celulares')
),
(
  'iPhone 14',
  1500000.00,
  'Apple iPhone 14 128GB',
  'https://example.com/iphone14',
  8,
  (SELECT id FROM subcategories WHERE name = 'Celulares')
),
(
  'Notebook Lenovo ThinkPad',
  1800000.00,
  'Notebook Lenovo ThinkPad i5 16GB RAM',
  'https://example.com/thinkpad',
  5,
  (SELECT id FROM subcategories WHERE name = 'Computación')
),
(
  'Licuadora Philips',
  220000.00,
  'Licuadora Philips 600W',
  'https://example.com/licuadora',
  15,
  (SELECT id FROM subcategories WHERE name = 'Cocina')
),
(
  'Mancuernas 10kg',
  85000.00,
  'Par de mancuernas ajustables 10kg',
  'https://example.com/mancuernas',
  20,
  (SELECT id FROM subcategories WHERE name = 'Fitness')
),
(
  'Auriculares Sony WH-1000XM5',
  980000.00,
  'Auriculares inalámbricos con cancelación de ruido',
  'https://example.com/sony-xm5',
  12,
  (SELECT id FROM subcategories WHERE name = 'Auriculares')
),
(
  'Escritorio Oficina Pro',
  450000.00,
  'Escritorio de oficina de madera 140cm',
  'https://example.com/escritorio',
  6,
  (SELECT id FROM subcategories WHERE name = 'Escritorios')
);

-- ================================
-- USERS (ADMINS)
-- ================================
INSERT INTO users (firstname, lastname, email, password, role) VALUES
('Admin', 'Principal', 'admin@store.com', '$2a$12$qrVuwpsC7pTpqfQCiWpY6unpKcdkdvZTW57gJT/EF6dm0EGKtq82y', 'ADMIN'),
('Juan', 'Perez', 'juan.perez@store.com', '$2a$12$qrVuwpsC7pTpqfQCiWpY6unpKcdkdvZTW57gJT/EF6dm0EGKtq82y', 'ADMIN'),
('Lucia', 'Fernandez', 'lucia@store.com', '$2a$12$qrVuwpsC7pTpqfQCiWpY6unpKcdkdvZTW57gJT/EF6dm0EGKtq82y', 'ADMIN'),
('Marcos', 'Diaz', 'marcos@store.com', '$2a$12$qrVuwpsC7pTpqfQCiWpY6unpKcdkdvZTW57gJT/EF6dm0EGKtq82y', 'ADMIN');

-- ================================
-- CARTS
-- ================================
INSERT INTO carts (session_id, total_amount) VALUES
('SESSION_ABC123', 1200000.00),
('SESSION_DEF456', 2025000.00),
('SESSION_GHI789', 980000.00),
('SESSION_JKL321', 450000.00);

-- ================================
-- CART ITEMS
-- ================================
INSERT INTO cart_items (quantity, unit_price, cart_id, product_id) VALUES
(
  1,
  1200000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_ABC123'),
  (SELECT id FROM products WHERE name = 'Samsung Galaxy S23')
),
(
  1,
  1500000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_DEF456'),
  (SELECT id FROM products WHERE name = 'iPhone 14')
),
(
  1,
  525000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_DEF456'),
  (SELECT id FROM products WHERE name = 'Licuadora Philips')
),
(
  1,
  980000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_GHI789'),
  (SELECT id FROM products WHERE name = 'Auriculares Sony WH-1000XM5')
),
(
  1,
  450000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_JKL321'),
  (SELECT id FROM products WHERE name = 'Escritorio Oficina Pro')
),
(
  2,
  85000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_JKL321'),
  (SELECT id FROM products WHERE name = 'Mancuernas 10kg')
);

-- ================================
-- ORDERS
-- ================================
INSERT INTO orders (
  customer_name,
  customer_email,
  customer_phone,
  shipping_address,
  shipping_city,
  shipping_zip,
  total_amount,
  cart_id,
  status
) VALUES
(
  'Carlos Gómez',
  'carlos.gomez@gmail.com',
  '2645123456',
  'Av. Libertador 1234',
  'San Juan',
  '5400',
  1200000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_ABC123'),
  'PAID'
),
(
  'María López',
  'maria.lopez@gmail.com',
  '2645987654',
  'Calle Mendoza 432',
  'San Juan',
  '5400',
  2025000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_DEF456'),
  'PENDING'
),
(
  'Diego Ruiz',
  'diego.ruiz@gmail.com',
  '2645112233',
  'Av. España 222',
  'San Juan',
  '5400',
  980000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_GHI789'),
  'PAID'
),
(
  'Ana Morales',
  'ana.morales@gmail.com',
  '2645332211',
  'Mitre 987',
  'San Juan',
  '5400',
  450000.00,
  (SELECT id FROM carts WHERE session_id = 'SESSION_JKL321'),
  'SHIPPED'
);

-- ================================
-- ORDER ITEMS
-- ================================
INSERT INTO order_items (quantity, price, product_id, order_id) VALUES
(
  1,
  1200000.00,
  (SELECT id FROM products WHERE name = 'Samsung Galaxy S23'),
  (SELECT id FROM orders WHERE customer_email = 'carlos.gomez@gmail.com')
),
(
  1,
  1500000.00,
  (SELECT id FROM products WHERE name = 'iPhone 14'),
  (SELECT id FROM orders WHERE customer_email = 'maria.lopez@gmail.com')
),
(
  1,
  525000.00,
  (SELECT id FROM products WHERE name = 'Licuadora Philips'),
  (SELECT id FROM orders WHERE customer_email = 'maria.lopez@gmail.com')
),
(
  1,
  980000.00,
  (SELECT id FROM products WHERE name = 'Auriculares Sony WH-1000XM5'),
  (SELECT id FROM orders WHERE customer_email = 'diego.ruiz@gmail.com')
),
(
  1,
  450000.00,
  (SELECT id FROM products WHERE name = 'Escritorio Oficina Pro'),
  (SELECT id FROM orders WHERE customer_email = 'ana.morales@gmail.com')
),
(
  2,
  85000.00,
  (SELECT id FROM products WHERE name = 'Mancuernas 10kg'),
  (SELECT id FROM orders WHERE customer_email = 'ana.morales@gmail.com')
);
