-- CartNova Seed Data
USE cartnova;

-- 1. SEED USERS
-- Admin password: admin123 ($2a$10$rDkPvvAFV8kqwvKzp84f/.6zGZ9Bv4g9C8Q6oQyL.7E4Y9.kL7vQG)
-- Seller password: seller123 ($2a$10$wT0lPZ5F0JjUq6yQ9.b9Ce8e8R4a.9YhWJ3d0v5UvO5s5q.0G1vKO)
-- Buyer password: buyer123 ($2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.aB1N0j1a)

INSERT INTO users (email, password_hash, first_name, last_name, phone, role, address_line1, city, state, zip_code) VALUES
('admin@cartnova.com', '$2a$10$rDkPvvAFV8kqwvKzp84f/.6zGZ9Bv4g9C8Q6oQyL.7E4Y9.kL7vQG', 'System', 'Admin', '9876543210', 'ADMIN', '100 Tech Park, MG Road', 'Bengaluru', 'Karnataka', '560001'),
('techseller@cartnova.com', '$2a$10$wT0lPZ5F0JjUq6yQ9.b9Ce8e8R4a.9YhWJ3d0v5UvO5s5q.0G1vKO', 'Alex', 'Vance', '9812345678', 'SELLER', '45 Electronics Zone', 'Hyderabad', 'Telangana', '500081'),
('fashionhub@cartnova.com', '$2a$10$wT0lPZ5F0JjUq6yQ9.b9Ce8e8R4a.9YhWJ3d0v5UvO5s5q.0G1vKO', 'Elena', 'Russo', '9898765432', 'SELLER', '12 Fashion Avenue', 'Mumbai', 'Maharashtra', '400001'),
('john.doe@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.aB1N0j1a', 'John', 'Doe', '9711223344', 'BUYER', '742 Evergreen Terrace', 'New Delhi', 'Delhi', '110001'),
('sarah.smith@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.aB1N0j1a', 'Sarah', 'Smith', '9755443322', 'BUYER', '88 Park Street', 'Kolkata', 'West Bengal', '700016');

-- 2. SEED CATEGORIES
INSERT INTO categories (name, description, image_url) VALUES
('Electronics', 'Smartphones, Laptops, Audio Gear, and Accessories', 'https://images.unsplash.com/photo-1498049860654-af1a5c566876?auto=format&fit=crop&w=600&q=80'),
('Fashion', 'Trendy Apparel, Footwear, and Designer Wear', 'https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=600&q=80'),
('Home & Kitchen', 'Modern Furniture, Cookware, and Home Decor', 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=600&q=80'),
('Books & Stationery', 'Bestsellers, Fiction, Tech Books, and Office Supplies', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=600&q=80'),
('Fitness & Sports', 'Workout Gear, Gym Equipment, and Outdoor Supplies', 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=600&q=80');

-- 3. SEED PRODUCTS
INSERT INTO products (seller_id, category_id, name, description, price, stock_quantity, image_url, brand) VALUES
-- Tech Seller Products
(2, 1, 'NovaBook Pro 15 Laptop', '15.6-inch Retina display, 16GB RAM, 512GB NVMe SSD, Ultra-thin Aluminum Chassis.', 89999.00, 15, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=600&q=80', 'NovaTech'),
(2, 1, 'SonicPulse ANC Wireless Headphones', 'Active Noise Canceling over-ear bluetooth headphones with 40h battery life and Hi-Fi drivers.', 12499.00, 30, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80', 'SonicPulse'),
(2, 1, 'AuraWatch Horizon Smartwatch', 'AMOLED Curved display, Heart rate monitor, SpO2 sensor, 50m Water Resistance.', 6999.00, 25, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80', 'AuraGear'),
(2, 1, 'PixelTab Ultra 11 Tablet', '11-inch 120Hz display, Stylus support, Quad speakers, Octa-core processor.', 34999.00, 8, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=600&q=80', 'PixelTech'),
-- Fashion Seller Products
(3, 2, 'Classic Tailored Navy Blazer', 'Premium wool-blend tailored slim fit blazer suitable for business and formal occasions.', 4999.00, 20, 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=600&q=80', 'UrbanCraft'),
(3, 2, 'Urban Nomad Leather Sneakers', 'Handcrafted genuine leather casual sneakers with memory foam cushioned insoles.', 3499.00, 45, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=600&q=80', 'NomadFootwear'),
(3, 2, 'Minimalist Leather Crossbody Bag', 'Sleek full-grain leather shoulder handbag with golden hardware accent.', 2999.00, 12, 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=600&q=80', 'VelvetLine'),
-- Home & Kitchen
(2, 3, 'Smart Aroma Essential Oil Diffuser', 'Ultrasonic quiet humidifying diffuser with 7 ambient LED colors and app control.', 1999.00, 50, 'https://images.unsplash.com/photo-1602928321679-560bb453f190?auto=format&fit=crop&w=600&q=80', 'AromaNest'),
(3, 3, 'Ceramic Pour-Over Coffee Maker Set', 'Artisanal ceramic dripper with heat-resistant glass carafe and stainless filter.', 1499.00, 18, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=600&q=80', 'BrewMaster'),
-- Books & Fitness
(2, 4, 'Mastering Java & Enterprise Architecture', 'Comprehensive guide to Core Java, Design Patterns, Multithreading, and Web Applications.', 899.00, 60, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80', 'TechPress'),
(3, 5, 'Pro-Grip Adjustable Dumbbell Set', 'Heavy-duty cast iron adjustable dumbbell pair (2.5kg to 24kg per bell).', 8499.00, 10, 'https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=600&q=80', 'IronFit');

-- 4. SEED WISHLIST ITEMS
INSERT INTO wishlist_items (user_id, product_id) VALUES
(4, 2), -- John likes SonicPulse Headphones
(4, 5), -- John likes Navy Blazer
(5, 1); -- Sarah likes NovaBook Laptop

-- 5. SEED CART ITEMS
INSERT INTO cart_items (user_id, product_id, quantity) VALUES
(4, 3, 1), -- John has AuraWatch in cart
(4, 6, 2); -- John has Sneakers in cart

-- 6. SEED ORDERS & ITEMS
INSERT INTO orders (user_id, total_amount, status, shipping_address) VALUES
(4, 15998.00, 'DELIVERED', '742 Evergreen Terrace, New Delhi, Delhi - 110001'),
(5, 89999.00, 'SHIPPED', '88 Park Street, Kolkata, West Bengal - 700016');

INSERT INTO order_items (order_id, product_id, seller_id, quantity, price_at_purchase) VALUES
(1, 2, 2, 1, 12499.00),
(1, 6, 3, 1, 3499.00),
(2, 1, 2, 1, 89999.00);

INSERT INTO payments (order_id, payment_method, payment_status, amount, transaction_ref) VALUES
(1, 'UPI', 'COMPLETED', 15998.00, 'TXN_UPI_9847291038'),
(2, 'CARD', 'COMPLETED', 89999.00, 'TXN_CARD_7749201934');

-- 7. SEED REVIEWS
INSERT INTO reviews (user_id, product_id, rating, comment) VALUES
(4, 2, 5, 'Exceptional noise cancellation and battery life. Highly recommended!'),
(4, 6, 4, 'Very comfortable for daily wear. True to size.'),
(5, 1, 5, 'Super lightweight and blistering fast performance!');
