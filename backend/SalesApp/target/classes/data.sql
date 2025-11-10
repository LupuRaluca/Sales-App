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

-- =========================
-- INVENTORY (upsert per SKU)
-- =========================
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