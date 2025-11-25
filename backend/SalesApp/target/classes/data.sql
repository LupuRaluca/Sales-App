-- BRANDS
INSERT INTO brands (name, description) VALUES
                                           ('Generic', 'Generic brand for demo'),
                                           ('Intel', 'Intel Corporation'),
                                           ('AMD', 'Advanced Micro Devices'),
                                           ('NVIDIA', 'NVIDIA Corporation'),
                                           ('ASUS', 'ASUSTeK Computer Inc.'),
                                           ('MSI', 'Micro-Star International'),
                                           ('Kingston', 'Kingston Technology'),
                                           ('Corsair', 'Corsair Components'),
                                           ('Samsung', 'Samsung Electronics'),
                                           ('Seagate', 'Seagate Technology'),
                                           ('Cooler Master', 'Cooler Master Co., Ltd.'),
                                           ('Logitech', 'Logitech International'),
                                           ('TP-Link', 'TP-Link Technologies Co., Ltd.')
ON CONFLICT (name) DO NOTHING;

-- CATEGORIES
INSERT INTO categories (name, description) VALUES
                                               ('Default', 'Default category for demo'),
                                               ('CPU', 'Processors'),
                                               ('GPU', 'Graphics cards'),
                                               ('Motherboard', 'Mainboards'),
                                               ('RAM', 'Memory modules'),
                                               ('Storage', 'SSDs and HDDs'),
                                               ('PSU', 'Power supply units'),
                                               ('Case', 'PC cases'),
                                               ('Cooling', 'Air and liquid cooling'),
                                               ('Peripheral', 'Mice, keyboards, headsets'),
                                               ('Networking', 'Routers, switches, adapters')
ON CONFLICT (name) DO NOTHING;

-- PRODUCTS
INSERT INTO products
(sku, name, description, brand_id, category_id, price, currency, vat_rate, created_at, updated_at)
VALUES
-- CPUs
('SKU-CPU-INTEL-12400F', 'Intel Core i5-12400F',
 '6-core Alder Lake processor',
 (SELECT id FROM brands WHERE name='Intel'),
 (SELECT id FROM categories WHERE name='CPU'),
 850.00, 'RON', 19.00, NOW(), NOW()),
('SKU-CPU-AMD-5600', 'AMD Ryzen 5 5600',
 '6-core Zen 3 processor',
 (SELECT id FROM brands WHERE name='AMD'),
 (SELECT id FROM categories WHERE name='CPU'),
 750.00, 'RON', 19.00, NOW(), NOW()),

-- GPUs
('SKU-GPU-NVIDIA-4060', 'NVIDIA GeForce RTX 4060 8GB',
 'Ada Lovelace mid-range GPU',
 (SELECT id FROM brands WHERE name='NVIDIA'),
 (SELECT id FROM categories WHERE name='GPU'),
 2200.00, 'RON', 19.00, NOW(), NOW()),
('SKU-GPU-AMD-6600', 'AMD Radeon RX 6600 8GB',
 'RDNA 2 1080p gaming GPU',
 (SELECT id FROM brands WHERE name='AMD'),
 (SELECT id FROM categories WHERE name='GPU'),
 1300.00, 'RON', 19.00, NOW(), NOW()),

-- Motherboards
('SKU-MB-ASUS-B660', 'ASUS PRIME B660-PLUS D4',
 'LGA1700 ATX motherboard (DDR4)',
 (SELECT id FROM brands WHERE name='ASUS'),
 (SELECT id FROM categories WHERE name='Motherboard'),
 600.00, 'RON', 19.00, NOW(), NOW()),
('SKU-MB-MSI-B550', 'MSI B550-A PRO',
 'AM4 ATX motherboard',
 (SELECT id FROM brands WHERE name='MSI'),
 (SELECT id FROM categories WHERE name='Motherboard'),
 550.00, 'RON', 19.00, NOW(), NOW()),

-- RAM
('SKU-RAM-KING-16G-D4-3200', 'Kingston 16GB (2x8GB) DDR4-3200',
 'CL16 dual-channel kit',
 (SELECT id FROM brands WHERE name='Kingston'),
 (SELECT id FROM categories WHERE name='RAM'),
 200.00, 'RON', 19.00, NOW(), NOW()),
('SKU-RAM-COR-32G-D5-5600', 'Corsair 32GB (2x16GB) DDR5-5600',
 'Vengeance DDR5 kit',
 (SELECT id FROM brands WHERE name='Corsair'),
 (SELECT id FROM categories WHERE name='RAM'),
 650.00, 'RON', 19.00, NOW(), NOW()),

-- Storage
('SKU-SSD-SAMS-970EVO-1TB', 'Samsung 970 EVO Plus 1TB NVMe',
 'PCIe 3.0 x4 M.2 NVMe SSD',
 (SELECT id FROM brands WHERE name='Samsung'),
 (SELECT id FROM categories WHERE name='Storage'),
 350.00, 'RON', 19.00, NOW(), NOW()),
('SKU-HDD-SEAG-2TB', 'Seagate Barracuda 2TB 3.5"',
 '7200 RPM SATA HDD',
 (SELECT id FROM brands WHERE name='Seagate'),
 (SELECT id FROM categories WHERE name='Storage'),
 250.00, 'RON', 19.00, NOW(), NOW()),

-- PSU
('SKU-PSU-COR-RM750X', 'Corsair RM750x 750W 80+ Gold',
 'Fully modular power supply',
 (SELECT id FROM brands WHERE name='Corsair'),
 (SELECT id FROM categories WHERE name='PSU'),
 520.00, 'RON', 19.00, NOW(), NOW()),

-- Case
('SKU-CASE-CM-NR400', 'Cooler Master NR400',
 'mATX compact tower case',
 (SELECT id FROM brands WHERE name='Cooler Master'),
 (SELECT id FROM categories WHERE name='Case'),
 300.00, 'RON', 19.00, NOW(), NOW()),

-- Cooling
('SKU-COOL-CM-212', 'Cooler Master Hyper 212 Black Edition',
 'Tower air cooler with 120mm fan',
 (SELECT id FROM brands WHERE name='Cooler Master'),
 (SELECT id FROM categories WHERE name='Cooling'),
 150.00, 'RON', 19.00, NOW(), NOW()),

-- Peripherals
('SKU-PER-LOGI-G502', 'Logitech G502 HERO',
 'USB gaming mouse 25K sensor',
 (SELECT id FROM brands WHERE name='Logitech'),
 (SELECT id FROM categories WHERE name='Peripheral'),
 280.00, 'RON', 19.00, NOW(), NOW()),

-- Networking
('SKU-NET-TPL-AX55', 'TP-Link Archer AX55 AX3000',
 'Dual-band Wi-Fi 6 router',
 (SELECT id FROM brands WHERE name='TP-Link'),
 (SELECT id FROM categories WHERE name='Networking'),
 350.00, 'RON', 19.00, NOW(), NOW()),

-- Extra SSD
('SKU-SSD-SAMS-980-500', 'Samsung 980 500GB NVMe',
 'PCIe 3.0 x4 M.2 NVMe SSD',
 (SELECT id FROM brands WHERE name='Samsung'),
 (SELECT id FROM categories WHERE name='Storage'),
 220.00, 'RON', 19.00, NOW(), NOW())
ON CONFLICT (sku) DO NOTHING;


-- INVENTORY
-- CPUs
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 40, NOW(), NOW() FROM products WHERE sku = 'SKU-CPU-INTEL-12400F'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 35, NOW(), NOW() FROM products WHERE sku = 'SKU-CPU-AMD-5600'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- GPUs
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 20, NOW(), NOW() FROM products WHERE sku = 'SKU-GPU-NVIDIA-4060'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 25, NOW(), NOW() FROM products WHERE sku = 'SKU-GPU-AMD-6600'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- Motherboards
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 30, NOW(), NOW() FROM products WHERE sku = 'SKU-MB-ASUS-B660'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 28, NOW(), NOW() FROM products WHERE sku = 'SKU-MB-MSI-B550'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- RAM
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 60, NOW(), NOW() FROM products WHERE sku = 'SKU-RAM-KING-16G-D4-3200'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 45, NOW(), NOW() FROM products WHERE sku = 'SKU-RAM-COR-32G-D5-5600'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- Storage
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 80, NOW(), NOW() FROM products WHERE sku = 'SKU-SSD-SAMS-970EVO-1TB'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 70, NOW(), NOW() FROM products WHERE sku = 'SKU-HDD-SEAG-2TB'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- PSU
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 33, NOW(), NOW() FROM products WHERE sku = 'SKU-PSU-COR-RM750X'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- Case
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 40, NOW(), NOW() FROM products WHERE sku = 'SKU-CASE-CM-NR400'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- Cooling
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 50, NOW(), NOW() FROM products WHERE sku = 'SKU-COOL-CM-212'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- Peripherals
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 55, NOW(), NOW() FROM products WHERE sku = 'SKU-PER-LOGI-G502'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- Networking
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 37, NOW(), NOW() FROM products WHERE sku = 'SKU-NET-TPL-AX55'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();

-- Extra SSD
INSERT INTO inventory (product_id, quantity_available, created_at, updated_at)
SELECT id, 50, NOW(), NOW() FROM products WHERE sku = 'SKU-SSD-SAMS-980-500'
ON CONFLICT (product_id) DO UPDATE SET quantity_available = EXCLUDED.quantity_available, updated_at = NOW();


-- USERS
INSERT INTO users (username, first_name, last_name, phone, email, password, role, created_at, updated_at)
VALUES
    ('admin', 'Admin', 'User', '+40123456789', 'admin@shopdemo.com', '$2a$10$xn3LI/AjqicFYZFruO4hqfo4op2.FjeNj85j.uxj0.bK6slCJyWKO', 'ADMIN', NOW(), NOW()),
    ('john_doe', 'John', 'Doe', '+40711111111', 'john.doe@example.com', '$2a$10$xn3LI/AjqicFYZFruO4hqfo4op2.FjeNj85j.uxj0.bK6slCJyWKO', 'USER', NOW(), NOW()),
    ('jane_smith', 'Jane', 'Smith', '+40722222222', 'jane.smith@example.com', '$2a$10$xn3LI/AjqicFYZFruO4hqfo4op2.FjeNj85j.uxj0.bK6slCJyWKO', 'USER', NOW(), NOW()),
    ('alex_popescu', 'Alex', 'Popescu', '+40733333333', 'alex.popescu@example.com', '$2a$10$xn3LI/AjqicFYZFruO4hqfo4op2.FjeNj85j.uxj0.bK6slCJyWKO', 'USER', NOW(), NOW()),
    ('maria_ionescu', 'Maria', 'Ionescu', '+40744444444', 'maria.ionescu@example.com', '$2a$10$xn3LI/AjqicFYZFruO4hqfo4op2.FjeNj85j.uxj0.bK6slCJyWKO', 'USER', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- CARTS (Totaluri recalculate corect: John Doe avea gresit)
INSERT INTO carts (user_id, total_price, created_at, updated_at)
VALUES
    ((SELECT id FROM users WHERE username='john_doe'), 2600.00, NOW(), NOW()),
    ((SELECT id FROM users WHERE username='jane_smith'), 1300.00, NOW(), NOW()),
    ((SELECT id FROM users WHERE username='alex_popescu'), 850.00, NOW(), NOW()),
    ((SELECT id FROM users WHERE username='maria_ionescu'), 600.00, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- CART ITEMS
-- John Doe's cart: GPU (2200) + 2x RAM (2x200=400) = 2600
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT (SELECT id FROM carts WHERE user_id=(SELECT id FROM users WHERE username='john_doe')),
       id, 1, price FROM products WHERE sku='SKU-GPU-NVIDIA-4060';
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT (SELECT id FROM carts WHERE user_id=(SELECT id FROM users WHERE username='john_doe')),
       id, 2, price FROM products WHERE sku='SKU-RAM-KING-16G-D4-3200';

-- Jane Smith's cart: GPU AMD (1300)
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT (SELECT id FROM carts WHERE user_id=(SELECT id FROM users WHERE username='jane_smith')),
       id, 1, price FROM products WHERE sku='SKU-GPU-AMD-6600';

-- Alex Popescu's cart: CPU Intel (850)
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT (SELECT id FROM carts WHERE user_id=(SELECT id FROM users WHERE username='alex_popescu')),
       id, 1, price FROM products WHERE sku='SKU-CPU-INTEL-12400F';

-- Maria Ionescu's cart: Motherboard ASUS (600)
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT (SELECT id FROM carts WHERE user_id=(SELECT id FROM users WHERE username='maria_ionescu')),
       id, 1, price FROM products WHERE sku='SKU-MB-ASUS-B660';


-- ORDERS
-- Statusuri corectate: 'NEW' -> 'PENDING'
-- Payment Status corectat: 'PENDING' -> 'CAPTURED' (pentru ca au plati asociate)
-- Calcule corectate: Subtotal + Shipping = GrandTotal
INSERT INTO orders (user_id, status, subtotal, shipping_fee, tax_total, grand_total, currency, payment_status, transaction_ref,
                    shipping_full_name, shipping_phone, shipping_address, created_at, updated_at)
VALUES
-- John Doe: Subtotal 2600 + Shipping 25 = 2625
((SELECT id FROM users WHERE username='john_doe'), 'PENDING', 2600.00, 25.00, 415.13, 2625.00, 'RON', 'CAPTURED', 'TXN-JOHN-001',
 'John Doe', '+40711111111', 'Str. Libertatii 10, Bucuresti', NOW(), NOW()),

-- Jane Smith: Subtotal 1300 + Shipping 20 = 1320
((SELECT id FROM users WHERE username='jane_smith'), 'PENDING', 1300.00, 20.00, 207.56, 1320.00, 'RON', 'CAPTURED', 'TXN-JANE-001',
 'Jane Smith', '+40722222222', 'Str. Unirii 25, Cluj-Napoca', NOW(), NOW()),

-- Alex Popescu: Subtotal 850 + Shipping 20 = 870
((SELECT id FROM users WHERE username='alex_popescu'), 'PENDING', 850.00, 20.00, 135.71, 870.00, 'RON', 'CAPTURED', 'TXN-ALEX-001',
 'Alex Popescu', '+40733333333', 'Bd. Independentei 5, Iasi', NOW(), NOW()),

-- Maria Ionescu: Subtotal 600 + Shipping 20 = 620
((SELECT id FROM users WHERE username='maria_ionescu'), 'PENDING', 600.00, 20.00, 95.80, 620.00, 'RON', 'CAPTURED', 'TXN-MARIA-001',
 'Maria Ionescu', '+40744444444', 'Str. Mihai Viteazu 12, Timisoara', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ORDER ITEMS
-- John Doe
INSERT INTO order_items (order_id, product_id, quantity, unit_price, vat_rate)
SELECT (SELECT id FROM orders WHERE transaction_ref='TXN-JOHN-001'), id, 1, price, 19.00 FROM products WHERE sku='SKU-GPU-NVIDIA-4060';
INSERT INTO order_items (order_id, product_id, quantity, unit_price, vat_rate)
SELECT (SELECT id FROM orders WHERE transaction_ref='TXN-JOHN-001'), id, 2, price, 19.00 FROM products WHERE sku='SKU-RAM-KING-16G-D4-3200';

-- Jane Smith
INSERT INTO order_items (order_id, product_id, quantity, unit_price, vat_rate)
SELECT (SELECT id FROM orders WHERE transaction_ref='TXN-JANE-001'), id, 1, price, 19.00 FROM products WHERE sku='SKU-GPU-AMD-6600';

-- Alex Popescu
INSERT INTO order_items (order_id, product_id, quantity, unit_price, vat_rate)
SELECT (SELECT id FROM orders WHERE transaction_ref='TXN-ALEX-001'), id, 1, price, 19.00 FROM products WHERE sku='SKU-CPU-INTEL-12400F';

-- Maria Ionescu
INSERT INTO order_items (order_id, product_id, quantity, unit_price, vat_rate)
SELECT (SELECT id FROM orders WHERE transaction_ref='TXN-MARIA-001'), id, 1, price, 19.00 FROM products WHERE sku='SKU-MB-ASUS-B660';


-- PAYMENTS
-- Provider corectat: 'CARD' -> 'NETOPIA' (sau 'STRIPE')
-- Status corectat: 'PAID' -> 'CAPTURED'
-- Sume corectate (Match Grand Total)
INSERT INTO payments (order_id, provider, status, amount, currency, transaction_ref, created_at)
VALUES
    ((SELECT id FROM orders WHERE transaction_ref='TXN-JOHN-001'), 'NETOPIA', 'CAPTURED', 2625.00, 'RON', 'PAY-JOHN-001', NOW()),
    ((SELECT id FROM orders WHERE transaction_ref='TXN-JANE-001'), 'STRIPE', 'CAPTURED', 1320.00, 'RON', 'PAY-JANE-001', NOW()),
    ((SELECT id FROM orders WHERE transaction_ref='TXN-ALEX-001'), 'NETOPIA', 'CAPTURED', 870.00, 'RON', 'PAY-ALEX-001', NOW()),
    ((SELECT id FROM orders WHERE transaction_ref='TXN-MARIA-001'), 'NETOPIA', 'CAPTURED', 620.00, 'RON', 'PAY-MARIA-001', NOW())
ON CONFLICT DO NOTHING;

-- INVOICES
INSERT INTO invoices (invoice_number, order_id, created_at, updated_at)
VALUES
    ('INV-2025-001', (SELECT id FROM orders WHERE transaction_ref='TXN-JOHN-001'), NOW(), NOW()),
    ('INV-2025-002', (SELECT id FROM orders WHERE transaction_ref='TXN-JANE-001'), NOW(), NOW()),
    ('INV-2025-003', (SELECT id FROM orders WHERE transaction_ref='TXN-ALEX-001'), NOW(), NOW()),
    ('INV-2025-004', (SELECT id FROM orders WHERE transaction_ref='TXN-MARIA-001'), NOW(), NOW())
ON CONFLICT DO NOTHING;