CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email CITEXT NOT NULL UNIQUE,
                       password_hash TEXT NOT NULL,
                       first_name TEXT NOT NULL,
                       last_name TEXT NOT NULL,
                       phone TEXT,
                       role TEXT NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name TEXT NOT NULL,
                            description TEXT NOT NULL
);

CREATE TABLE brands (
                        id BIGSERIAL PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT NOT NULL
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          sku TEXT NOT NULL UNIQUE,
                          name TEXT NOT NULL,
                          description TEXT NOT NULL,
                          brand_id BIGINT REFERENCES brands(id) ON DELETE SET NULL,
                          category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
                          price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
                          currency CHAR(3) NOT NULL DEFAULT 'RON',
                          vat_rate NUMERIC(4,2) NOT NULL DEFAULT 19,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_category ON products(category_id);

CREATE TABLE inventory (
                           id BIGSERIAL PRIMARY KEY,
                           product_id BIGINT NOT NULL UNIQUE REFERENCES products(id) ON DELETE CASCADE,
                           quantity_available INT NOT NULL DEFAULT 0 CHECK (quantity_available >= 0),
                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE carts (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       total_price NUMERIC(12,2) NOT NULL DEFAULT 0
);
CREATE INDEX idx_carts_user ON carts(user_id);

CREATE TABLE cart_items (
                            id BIGSERIAL PRIMARY KEY,
                            cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
                            product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                            quantity INT NOT NULL CHECK (quantity > 0),
                            unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
                            CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id)
);

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                        status TEXT NOT NULL,
                        subtotal NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
                        shipping_fee NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (shipping_fee >= 0),
                        tax_total NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (tax_total >= 0),
                        grand_total NUMERIC(12,2) NOT NULL CHECK (grand_total >= 0),
                        currency CHAR(3) NOT NULL DEFAULT 'RON',
                        payment_status TEXT NOT NULL,
                        transaction_ref TEXT,
                        shipping_full_name TEXT NOT NULL,
                        shipping_phone TEXT,
                        shipping_address TEXT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_orders_user_created ON orders(user_id, created_at);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id BIGINT REFERENCES products(id) ON DELETE SET NULL,
                             quantity INT NOT NULL CHECK (quantity > 0),
                             unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
                             vat_rate NUMERIC(4,2) NOT NULL DEFAULT 19 CHECK (vat_rate >= 0),
                             CONSTRAINT uq_order_product UNIQUE (order_id, product_id)
);

CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                          provider TEXT NOT NULL,
                          status TEXT NOT NULL,
                          amount NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
                          currency CHAR(3) NOT NULL DEFAULT 'RON',
                          transaction_ref TEXT,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_payments_order_created ON payments(order_id, created_at);

CREATE TABLE invoices (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                          invoice_number TEXT NOT NULL UNIQUE,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- convertim din CHAR(3) in VARCHAR(3), evitam spatiile pad cu TRIM
ALTER TABLE products
ALTER COLUMN currency TYPE varchar(3) USING trim(currency);

-- (optional) ne asiguram de not null + default
ALTER TABLE products
    ALTER COLUMN currency SET NOT NULL;

ALTER TABLE products
    ALTER COLUMN currency SET DEFAULT 'RON';
