ALTER TABLE categories
    ADD CONSTRAINT uq_categories_name UNIQUE (name);


-- === BRANDS ===
INSERT INTO brands (name, description) VALUES
                                           ('NVIDIA', 'GPU vendor'),
                                           ('AMD', 'CPU & GPU'),
                                           ('Samsung', 'Memory & storage')
    ON CONFLICT (name) DO NOTHING;

-- === CATEGORIES ===
INSERT INTO categories (name, description) VALUES
                                               ('Plăci video', 'GPU pentru PC'),
                                               ('Procesoare', 'CPU desktop'),
                                               ('Stocare', 'SSD / HDD')
    ON CONFLICT (name) DO NOTHING;

-- === PRODUCTS ===

-- RTX 4070
INSERT INTO products (sku, name, description, brand_id, category_id, price, currency, vat_rate)
SELECT 'GPU-RTX4070-12G', 'GeForce RTX 4070', 'NVIDIA RTX 4070 12GB GDDR6X',
       b.id, c.id, 2999.99, 'RON', 19.00
FROM brands b, categories c
WHERE b.name='NVIDIA' AND c.name='Plăci video'
    ON CONFLICT (sku) DO NOTHING;

-- Ryzen 7 7800X3D
INSERT INTO products (sku, name, description, brand_id, category_id, price, currency, vat_rate)
SELECT 'CPU-R7-7800X3D', 'Ryzen 7 7800X3D', 'AMD Ryzen 7 7800X3D AM5',
       b.id, c.id, 1899.99, 'RON', 19.00
FROM brands b, categories c
WHERE b.name='AMD' AND c.name='Procesoare'
    ON CONFLICT (sku) DO NOTHING;

-- SSD 1TB
INSERT INTO products (sku, name, description, brand_id, category_id, price, currency, vat_rate)
SELECT 'SSD-970EVO-1TB', '970 EVO Plus 1TB', 'Samsung 970 EVO Plus 1TB NVMe',
       b.id, c.id, 389.99, 'RON', 19.00
FROM brands b, categories c
WHERE b.name='Samsung' AND c.name='Stocare'
    ON CONFLICT (sku) DO NOTHING;

-- === INVENTORY === (1-1 pe product_id)
INSERT INTO inventory (product_id, quantity_available)
SELECT p.id, 10 FROM products p WHERE p.sku='GPU-RTX4070-12G'
    ON CONFLICT (product_id) DO NOTHING;

INSERT INTO inventory (product_id, quantity_available)
SELECT p.id, 15 FROM products p WHERE p.sku='CPU-R7-7800X3D'
    ON CONFLICT (product_id) DO NOTHING;

INSERT INTO inventory (product_id, quantity_available)
SELECT p.id, 30 FROM products p WHERE p.sku='SSD-970EVO-1TB'
    ON CONFLICT (product_id) DO NOTHING;
