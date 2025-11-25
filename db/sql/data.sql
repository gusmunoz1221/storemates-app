-- ============================
-- CATEGORIES
-- ============================
INSERT INTO categories (id, name, description)
VALUES
    (1, 'Mates', 'Mates de calabaza, madera y acero'),
    (2, 'Termos', 'Termos para mantener temperatura');

-- ============================
-- SUBCATEGORIES
-- ============================
INSERT INTO subcategories (id, name, description, category_id)
VALUES
    (1, 'Mate Imperial', 'Mate de cuero tipo imperial', 1),
    (2, 'Mate Camionero', 'Mate de calabaza tipo camionero', 1),
    (3, 'Termo Stanley', 'Termos Stanley originales', 2);

-- ============================
-- PRODUCTS
-- ============================
INSERT INTO products (id, name, price, description, url, stock, subcategory_id)
VALUES
    (1, 'Mate Imperial Azul', 15000, 'Mate imperial forrado en cuero azul', 'https://example.com/mate1.jpg', 20, 1),
    (2, 'Mate Camionero Negro', 9500, 'Camionero clásico de calabaza', 'https://example.com/mate2.jpg', 30, 2),
    (3, 'Termo Stanley Verde 1L', 62000, 'Termo Stanley original de 1 litro', 'https://example.com/termo1.jpg', 15, 3);

-- ============================
-- CART
-- ============================
INSERT INTO carts (id)
VALUES (1);

-- ============================
-- CART ITEMS
-- ============================
INSERT INTO cart_items (id, quantity, unitPrice, subtotal, cart_id, product_id)
VALUES
    (1, 2, 15000, 30000, 1, 1),  -- 2 mates imperiales
    (2, 1, 62000, 62000, 1, 3);  -- 1 termo Stanley

-- ============================
-- ORDERS
-- ============================
INSERT INTO orders
(id, customerName, customerEmail, customerPhone, shippingAddress, shippingCity, shippingZip, total)
VALUES
(1, 'Juan Perez', 'juan@example.com', '1122334455',
 'Calle Falsa 123', 'Buenos Aires', '1000',
 92000);

-- ============================
-- ORDER ITEMS
-- ============================
INSERT INTO order_items (id, quantity, price, subtotal, product_id, order_id)
VALUES
    (1, 1, 15000, 15000, 1, 1),  -- 1 mate imperial
    (2, 1, 62000, 62000, 3, 1);  -- 1 termo