-- ============================
-- CATEGORIES
-- ============================
INSERT INTO categories (id, name, description) VALUES
    (1, 'Mates', 'Mates de calabaza, madera y acero'),
    (2, 'Termos', 'Termos para mantener temperatura');


-- ============================
-- SUBCATEGORIES
-- ============================
INSERT INTO subcategories (id, name, description, category_id) VALUES
    (1, 'Mate Imperial', 'Mate de cuero tipo imperial', 1),
    (2, 'Mate Camionero', 'Mate de calabaza tipo camionero', 1),
    (3, 'Termo Stanley', 'Termos Stanley originales', 2),
    (4, 'Mate Torpedo', 'Mate de madera tipo torpedo', 1),
    (5, 'Termo Genérico', 'Termos de distintas marcas', 2),
    (6, 'Mate Acero', 'Mate de acero inoxidable', 1),
    (7, 'Termo Adventure', 'Termos Adventure resistentes', 2);


-- ============================
-- PRODUCTS
-- ============================
INSERT INTO products (id, name, price, description, url, stock, subcategory_id) VALUES
    (1, 'Mate Imperial Azul', 15000, 'Mate imperial forrado en cuero azul', 'https://example.com/mate1.jpg', 20, 1),
    (2, 'Mate Camionero Negro', 9500, 'Camionero clásico de calabaza', 'https://example.com/mate2.jpg', 30, 2),
    (3, 'Termo Stanley Verde 1L', 62000, 'Termo Stanley original de 1 litro', 'https://example.com/termo1.jpg', 15, 3),
    (4, 'Mate Torpedo Algarrobo', 8000, 'Mate torpedo de algarrobo premium', 'https://example.com/mate4.jpg', 25, 4),
    (5, 'Mate Torpedo Lustrado', 10000, 'Torpedo lustrado artesanal', 'https://example.com/mate5.jpg', 10, 4),
    (6, 'Termo Genérico Acero 1L', 25000, 'Termo de acero inoxidable 1L', 'https://example.com/termo2.jpg', 40, 5),
    (7, 'Termo Genérico 750ml', 18000, 'Termo acero 750ml genérico', 'https://example.com/termo3.jpg', 35, 5),
    (8, 'Mate Acero Negro', 6000, 'Mate acero inoxidable negro', 'https://example.com/mate6.jpg', 50, 6),
    (9, 'Mate Acero Blanco', 6000, 'Mate acero inoxidable blanco', 'https://example.com/mate7.jpg', 45, 6),
    (10, 'Termo Adventure Verde', 45000, 'Termo Adventure verde 1L', 'https://example.com/termo4.jpg', 20, 7),
    (11, 'Termo Adventure Negro', 46000, 'Adventure negro 1L', 'https://example.com/termo5.jpg', 22, 7),
    (12, 'Mate Camionero Marrón', 11000, 'Camionero cuero marrón', 'https://example.com/mate8.jpg', 28, 2),
    (13, 'Mate Imperial Rojo', 16000, 'Imperial rojo premium', 'https://example.com/mate9.jpg', 18, 1);


-- ============================
-- CART
-- ============================
INSERT INTO carts (id, session_id, total_amount) VALUES
    (1, 'session-1', 0),
    (2, 'session-2', 0),
    (3, 'session-3', 0),
    (4, 'session-4', 0);


-- ============================
-- CART ITEMS
-- ============================
INSERT INTO cart_items (id, quantity, unit_price, cart_id, product_id) VALUES
    (1, 2, 15000, 1, 1),
    (2, 1, 62000, 1, 3),
    (3, 1, 8000, 2, 4),
    (4, 2, 6000, 2, 8),
    (5, 1, 45000, 3, 10),
    (6, 3, 6000, 3, 9),
    (7, 1, 18000, 4, 7);


-- ============================
-- ORDERS
-- ============================
INSERT INTO orders
(id, customer_name, customer_email, customer_phone, shipping_address, shipping_city, shipping_zip, total)VALUES
    (1, 'Juan Perez', 'juan@example.com', '1122334455',
     'Calle Falsa 123', 'Buenos Aires', '1000', 92000),
    (2, 'Carla Gomez', 'carla@example.com', '1166778899',
     'Av. Libertador 2000', 'CABA', '1425', 32000),
    (3, 'Roberto Díaz', 'rober@example.com', '1144556677',
     'Calle 9 555', 'La Plata', '1900', 60000),
    (4, 'Lucía Fernández', 'lucia@example.com', '1177889900',
     'Av. Belgrano 800', 'CABA', '1070', 45000),
    (5, 'Pedro Sosa', 'pedro@example.com', '1155667788',
     'Calle Luna 44', 'Córdoba', '5000', 18000),
    (6, 'Sofía Cabrera', 'sofia@example.com', '1133445566',
     'Av. Rivadavia 900', 'CABA', '1406', 26000);


-- ============================
-- ORDER ITEMS
-- ============================
INSERT INTO order_items (id, quantity, price, subtotal, product_id, order_id) VALUES
    -- Orden 1
    (1, 1, 15000, 15000, 1, 1),
    (2, 1, 62000, 62000, 3, 1),
    -- Orden 2
    (3, 1, 8000, 8000, 4, 2),
    (4, 1, 24000, 24000, 3, 2),
    -- Orden 3
    (5, 1, 45000, 45000, 10, 3),
    (6, 1, 15000, 15000, 1, 3),
    -- Orden 4
    (7, 1, 46000, 46000, 11, 4),
    -- Orden 5
    (8, 3, 6000, 18000, 8, 5),
    -- Orden 6
    (9, 1, 25000, 25000, 6, 6),
    (10, 1, 1000, 1000, 9, 6);

