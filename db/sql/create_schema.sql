-- ================================
-- CATEGORIES
-- ================================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT
);

-- ================================
-- SUBCATEGORIES
-- ================================
CREATE TABLE subcategories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    category_id BIGINT,
    CONSTRAINT fk_subcategory_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- ================================
-- PRODUCTS
-- ================================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    price NUMERIC(15,2),
    description TEXT,
    url TEXT,
    stock INTEGER NOT NULL,
    subcategory_id BIGINT,
    CONSTRAINT fk_product_subcategory
        FOREIGN KEY (subcategory_id)
        REFERENCES subcategories(id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- ================================
-- CARTS
-- ================================
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY
);

-- ================================
-- CART ITEMS
-- ================================
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(15,2) NOT NULL,
    subtotal NUMERIC(15,2) NOT NULL,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    CONSTRAINT fk_cartitem_cart
        FOREIGN KEY (cart_id)
        REFERENCES carts(id)
        ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT fk_cartitem_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ================================
-- ORDERS
-- ================================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(100),

    shipping_address TEXT,
    shipping_city VARCHAR(255),
    shipping_zip VARCHAR(50),

    total NUMERIC(15,2),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

-- ================================
-- ORDER ITEMS
-- ================================
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,

    quantity INTEGER NOT NULL,
    price NUMERIC(15,2) NOT NULL,
    subtotal NUMERIC(15,2) NOT NULL,

    product_id BIGINT,
    order_id BIGINT,

    CONSTRAINT fk_orderitem_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE SET NULL,

    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON UPDATE CASCADE ON DELETE CASCADE
);