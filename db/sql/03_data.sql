-- ================================
-- CATEGORIES
-- ================================
INSERT INTO categories (name, description)
VALUES
    ('Mates',        'Mates de calabaza, madera, cerámica y acero'),
    ('Bombillas',    'Bombillas de alpaca, acero y bronce'),
    ('Yerbas',       'Yerbas orgánicas, tradicionales y compuestas'),
    ('Termos',       'Termos de acero inoxidable y repuestos'),
    ('Accesorios',   'Materas, yerberas y complementos')
ON CONFLICT DO NOTHING;

-- ================================
-- SUBCATEGORIES
-- ================================
INSERT INTO subcategories (name, description, category_id)
VALUES
    ('Imperiales',      'Mates de calabaza forrados con virola de alpaca', (SELECT id FROM categories WHERE name = 'Mates')),
    ('Pico de Loro',    'Bombillas curvas ideales para montañita',         (SELECT id FROM categories WHERE name = 'Bombillas')),
    ('Sin Palo',        'Yerba despalada estilo uruguayo',                 (SELECT id FROM categories WHERE name = 'Yerbas')),
    ('Media Manija',    'Termos con manija de transporte',                 (SELECT id FROM categories WHERE name = 'Termos')),
    ('Materas de Cuero','Bolsos materos de cuero vacuno legítimo',         (SELECT id FROM categories WHERE name = 'Accesorios'))
ON CONFLICT DO NOTHING;

-- ================================
-- PRODUCTS
-- ================================
INSERT INTO products (name, price, stock, description, url, subcategory_id)
VALUES
    ('Mate Imperial Cincelado', 45000.00, 20,  'Calabaza seleccionada, virola alpaca', 'https://via.placeholder.com/300', (SELECT id FROM subcategories WHERE name = 'Imperiales')),
    ('Bombilla Alpaca Premium', 12500.00, 50,  'Caño grueso, filtro ranurado',         'https://via.placeholder.com/300', (SELECT id FROM subcategories WHERE name = 'Pico de Loro')),
    ('Yerba Canarias 1kg',      8500.00,  100, 'Yerba importada, molienda fina',       'https://via.placeholder.com/300', (SELECT id FROM subcategories WHERE name = 'Sin Palo')),
    ('Termo Stanley 1.4L',      110000.00,15,  'Verde clásico, tapón cebador',         'https://via.placeholder.com/300', (SELECT id FROM subcategories WHERE name = 'Media Manija')),
    ('Matera Rígida Negra',     35000.00, 10,  'Cuero ecológico, entra termo grande',  'https://via.placeholder.com/300', (SELECT id FROM subcategories WHERE name = 'Materas de Cuero'))
ON CONFLICT (name) DO NOTHING;

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
INSERT INTO carts (session_id, total_amount)
VALUES
    ('sess_mate_001', 0),
    ('sess_mate_002', 0),
    ('sess_mate_003', 0),
    ('sess_mate_004', 0),
    ('sess_mate_005', 0)
ON CONFLICT (session_id) DO NOTHING;;

-- ================================
-- CART ITEMS
-- ================================


-- ================================
-- ORDERS
-- ================================
INSERT INTO orders (customer_name, customer_email, total_amount, status, shipping_address, shipping_city, shipping_zip, cart_id)
SELECT 'Facundo Arana', 'facu@mate.com', 45000.00, 'PAID', 'Av. Corrientes 1234', 'CABA', '1043', (SELECT id FROM carts WHERE session_id = 'sess_mate_001')
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE customer_email = 'facu@mate.com');

INSERT INTO orders (customer_name, customer_email, total_amount, status, shipping_address, shipping_city, shipping_zip, cart_id)
SELECT 'Lionel Messi', 'lio@mate.com', 118500.00, 'PENDING', 'Av. Corrientes 1234', 'CABA', '1043', (SELECT id FROM carts WHERE session_id = 'sess_mate_002')
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE customer_email = 'lio@mate.com');

INSERT INTO orders (customer_name, customer_email, total_amount, status, shipping_address, shipping_city, shipping_zip, cart_id)
SELECT 'Marta Minujín', 'marta@mate.com', 12500.00, 'SHIPPED', 'Av. Corrientes 1234', 'CABA', '1043', (SELECT id FROM carts WHERE session_id = 'sess_mate_003')
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE customer_email = 'marta@mate.com');

INSERT INTO orders (customer_name, customer_email, total_amount, status, shipping_address, shipping_city, shipping_zip, cart_id)
SELECT 'Ricardo Darín', 'richard@mate.com', 35000.00, 'PAID', 'Av. Corrientes 1234', 'CABA', '1043', (SELECT id FROM carts WHERE session_id = 'sess_mate_004')
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE customer_email = 'richard@mate.com');

INSERT INTO orders (customer_name, customer_email, total_amount, status, shipping_address, shipping_city, shipping_zip, cart_id)
SELECT 'Duki', 'duki@mate.com', 8500.00, 'CANCELLED', 'Av. Corrientes 1234', 'CABA', '1043', (SELECT id FROM carts WHERE session_id = 'sess_mate_005')
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE customer_email = 'duki@mate.com');

-- ================================
-- ORDER ITEMS
-- ================================
INSERT INTO order_items (quantity, price, order_id, product_id)
VALUES
    (1, 45000.00,  (SELECT id FROM orders WHERE customer_email = 'facu@mate.com'),    (SELECT id FROM products WHERE name = 'Mate Imperial Cincelado')),
    (1, 110000.00, (SELECT id FROM orders WHERE customer_email = 'lio@mate.com'),     (SELECT id FROM products WHERE name = 'Termo Stanley 1.4L')),
    (1, 8500.00,   (SELECT id FROM orders WHERE customer_email = 'lio@mate.com'),     (SELECT id FROM products WHERE name = 'Yerba Canarias 1kg')),
    (1, 12500.00,  (SELECT id FROM orders WHERE customer_email = 'marta@mate.com'),   (SELECT id FROM products WHERE name = 'Bombilla Alpaca Premium')),
    (1, 35000.00,  (SELECT id FROM orders WHERE customer_email = 'richard@mate.com'), (SELECT id FROM products WHERE name = 'Matera Rígida Negra'));

-- ============================
-- SINCRONIZACIÓN DE SECUENCIAS
-- ============================
SELECT setval(pg_get_serial_sequence('categories', 'id'), COALESCE(MAX(id), 1)) FROM categories;
SELECT setval(pg_get_serial_sequence('subcategories', 'id'), COALESCE(MAX(id), 1)) FROM subcategories;
SELECT setval(pg_get_serial_sequence('products', 'id'), COALESCE(MAX(id), 1)) FROM products;
SELECT setval(pg_get_serial_sequence('carts', 'id'), COALESCE(MAX(id), 1)) FROM carts;
SELECT setval(pg_get_serial_sequence('orders', 'id'), COALESCE(MAX(id), 1)) FROM orders;
SELECT setval(pg_get_serial_sequence('order_items', 'id'), COALESCE(MAX(id), 1)) FROM order_items;