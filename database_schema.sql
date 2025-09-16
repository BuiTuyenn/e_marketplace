-- =================================================================
-- E-MARKETPLACE DATABASE SCHEMA
-- Thiết kế cơ sở dữ liệu cho hệ thống thương mại điện tử
-- Dựa trên requirements từ file de.txt
-- 
-- Thông tin kết nối:
-- Database: e_marketplace
-- Username: root
-- Password: 1234
-- Host: localhost
-- Port: 3306
-- =================================================================

-- Tạo database
DROP DATABASE IF EXISTS e_marketplace;
CREATE DATABASE e_marketplace 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE e_marketplace;

-- =================================================================
-- 1. QUẢN LÝ NGƯỜI DÙNG (User Management)
-- =================================================================

-- Bảng vai trò người dùng
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'admin, customer, vendor',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng người dùng chính
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url VARCHAR(500),
    email_verified_at TIMESTAMP NULL,
    phone_verified_at TIMESTAMP NULL,
    status ENUM('active', 'inactive', 'banned') DEFAULT 'active',
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
);

-- Bảng phân quyền người dùng
CREATE TABLE user_roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_role (user_id, role_id)
);

-- Bảng địa chỉ giao hàng
CREATE TABLE user_addresses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    label VARCHAR(50) COMMENT 'Nhà riêng, Văn phòng, etc.',
    recipient_name VARCHAR(100) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    ward VARCHAR(100),
    district VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_default (user_id, is_default)
);

-- Bảng reset password tokens
CREATE TABLE password_reset_tokens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_expires (user_id, expires_at)
);

-- =================================================================
-- 2. QUẢN LÝ SẢN PHẨM (Product Management)
-- =================================================================

-- Bảng danh mục sản phẩm (đa cấp)
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    parent_id INT NULL,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    seo_title VARCHAR(255),
    seo_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent (parent_id),
    INDEX idx_slug (slug),
    INDEX idx_active (is_active),
    INDEX idx_sort (sort_order)
);

-- Bảng thương hiệu
CREATE TABLE brands (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    website_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_slug (slug),
    INDEX idx_active (is_active)
);

-- Bảng sản phẩm chính
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    short_description TEXT,
    sku VARCHAR(100) UNIQUE,
    brand_id INT,
    weight DECIMAL(8,2) COMMENT 'Trọng lượng (gram)',
    dimensions VARCHAR(100) COMMENT 'Kích thước (DxRxC cm)',
    status ENUM('draft', 'active', 'inactive', 'out_of_stock') DEFAULT 'draft',
    is_featured BOOLEAN DEFAULT FALSE,
    seo_title VARCHAR(255),
    seo_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL,
    INDEX idx_slug (slug),
    INDEX idx_sku (sku),
    INDEX idx_status (status),
    INDEX idx_featured (is_featured),
    FULLTEXT idx_search (name, description, short_description)
);

-- Bảng quan hệ sản phẩm - danh mục
CREATE TABLE product_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    category_id INT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE COMMENT 'Danh mục chính',
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY unique_product_category (product_id, category_id),
    INDEX idx_category (category_id),
    INDEX idx_primary (product_id, is_primary)
);

-- Bảng biến thể sản phẩm (variants)
CREATE TABLE product_variants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    name VARCHAR(255) NOT NULL COMMENT 'Tên biến thể: Áo thun đỏ size M',
    sku VARCHAR(100) UNIQUE,
    price DECIMAL(12,2) NOT NULL,
    compare_price DECIMAL(12,2) COMMENT 'Giá gốc (để hiển thị % giảm)',
    cost_price DECIMAL(12,2) COMMENT 'Giá nhập',
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 10,
    weight DECIMAL(8,2),
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product (product_id),
    INDEX idx_sku (sku),
    INDEX idx_price (price),
    INDEX idx_stock (stock_quantity),
    INDEX idx_active (is_active)
);

-- Bảng thuộc tính sản phẩm (attributes)
CREATE TABLE product_attributes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT 'Màu sắc, Kích thước, Chất liệu',
    type ENUM('text', 'number', 'select', 'multiselect', 'boolean') DEFAULT 'text',
    is_required BOOLEAN DEFAULT FALSE,
    is_filterable BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng giá trị thuộc tính
CREATE TABLE attribute_values (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attribute_id INT NOT NULL,
    value VARCHAR(255) NOT NULL,
    color_code VARCHAR(7) COMMENT 'Mã màu hex cho thuộc tính màu sắc',
    sort_order INT DEFAULT 0,
    
    FOREIGN KEY (attribute_id) REFERENCES product_attributes(id) ON DELETE CASCADE,
    INDEX idx_attribute (attribute_id)
);

-- Bảng thuộc tính của biến thể sản phẩm
CREATE TABLE variant_attributes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    variant_id INT NOT NULL,
    attribute_id INT NOT NULL,
    attribute_value_id INT NOT NULL,
    
    FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    FOREIGN KEY (attribute_id) REFERENCES product_attributes(id) ON DELETE CASCADE,
    FOREIGN KEY (attribute_value_id) REFERENCES attribute_values(id) ON DELETE CASCADE,
    UNIQUE KEY unique_variant_attribute (variant_id, attribute_id)
);

-- Bảng hình ảnh sản phẩm
CREATE TABLE product_images (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    variant_id INT NULL COMMENT 'NULL = ảnh chung, có giá trị = ảnh của variant',
    image_url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(255),
    sort_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    INDEX idx_product (product_id),
    INDEX idx_variant (variant_id),
    INDEX idx_sort (sort_order)
);

-- =================================================================
-- 3. QUẢN LÝ GIỎ HÀNG (Shopping Cart)
-- =================================================================

CREATE TABLE shopping_carts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL COMMENT 'NULL cho guest user',
    session_id VARCHAR(255) NULL COMMENT 'Cho guest user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_session (session_id)
);

CREATE TABLE cart_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT NOT NULL,
    variant_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(12,2) NOT NULL COMMENT 'Giá tại thời điểm thêm vào giỏ',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_variant (cart_id, variant_id)
);

-- =================================================================
-- 4. QUẢN LÝ ĐƠN HÀNG (Order Management)
-- =================================================================

CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT 'Mã đơn hàng hiển thị cho khách',
    user_id INT NULL,
    
    -- Thông tin khách hàng (lưu snapshot)
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    customer_name VARCHAR(200) NOT NULL,
    
    -- Địa chỉ giao hàng
    shipping_address JSON NOT NULL COMMENT 'Lưu toàn bộ thông tin địa chỉ',
    
    -- Thông tin đơn hàng
    status ENUM('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled', 'refunded') DEFAULT 'pending',
    payment_status ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending',
    payment_method ENUM('cod', 'bank_transfer', 'credit_card', 'e_wallet') NOT NULL,
    
    -- Giá trị đơn hàng
    subtotal DECIMAL(12,2) NOT NULL COMMENT 'Tổng tiền sản phẩm',
    shipping_fee DECIMAL(12,2) DEFAULT 0,
    tax_amount DECIMAL(12,2) DEFAULT 0,
    discount_amount DECIMAL(12,2) DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    
    -- Ghi chú và theo dõi
    notes TEXT COMMENT 'Ghi chú của khách hàng',
    admin_notes TEXT COMMENT 'Ghi chú nội bộ',
    tracking_number VARCHAR(100) COMMENT 'Mã vận đơn',
    shipping_provider VARCHAR(50) COMMENT 'GHTK, GHN, ViettelPost',
    
    -- Timestamps
    confirmed_at TIMESTAMP NULL,
    shipped_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_order_number (order_number),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created (created_at)
);

CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    variant_id INT NOT NULL,
    
    -- Snapshot thông tin sản phẩm tại thời điểm đặt hàng
    product_name VARCHAR(255) NOT NULL,
    variant_name VARCHAR(255),
    sku VARCHAR(100),
    price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE RESTRICT,
    INDEX idx_order (order_id),
    INDEX idx_variant (variant_id)
);

-- Bảng lịch sử trạng thái đơn hàng
CREATE TABLE order_status_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    created_by INT COMMENT 'User ID của người thay đổi trạng thái',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_order (order_id)
);

-- =================================================================
-- 5. QUẢN LÝ THANH TOÁN (Payment Management)
-- =================================================================

CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    payment_method ENUM('cod', 'bank_transfer', 'credit_card', 'momo', 'zalopay', 'shopeepay') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status ENUM('pending', 'processing', 'completed', 'failed', 'cancelled', 'refunded') DEFAULT 'pending',
    
    -- Thông tin giao dịch
    transaction_id VARCHAR(255) COMMENT 'ID từ cổng thanh toán',
    gateway_response JSON COMMENT 'Response từ payment gateway',
    
    -- Timestamps
    paid_at TIMESTAMP NULL,
    failed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order (order_id),
    INDEX idx_transaction (transaction_id),
    INDEX idx_status (status)
);

-- =================================================================
-- 6. QUẢN LÝ KHUYẾN MÃI (Promotion Management)
-- =================================================================

CREATE TABLE coupons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    
    -- Loại giảm giá
    type ENUM('percentage', 'fixed_amount', 'free_shipping') NOT NULL,
    value DECIMAL(12,2) NOT NULL COMMENT 'Giá trị giảm giá',
    
    -- Điều kiện áp dụng
    minimum_amount DECIMAL(12,2) DEFAULT 0 COMMENT 'Giá trị đơn hàng tối thiểu',
    maximum_discount DECIMAL(12,2) COMMENT 'Giảm tối đa (cho % discount)',
    
    -- Giới hạn sử dụng
    usage_limit INT COMMENT 'Số lần sử dụng tối đa',
    usage_limit_per_user INT DEFAULT 1,
    used_count INT DEFAULT 0,
    
    -- Thời gian hiệu lực
    starts_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_code (code),
    INDEX idx_active_dates (is_active, starts_at, expires_at)
);

CREATE TABLE coupon_usage (
    id INT PRIMARY KEY AUTO_INCREMENT,
    coupon_id INT NOT NULL,
    user_id INT,
    order_id INT NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_coupon (coupon_id),
    INDEX idx_user (user_id)
);

-- =================================================================
-- 7. QUẢN LÝ ĐÁNH GIÁ (Review Management)
-- =================================================================

CREATE TABLE product_reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    order_item_id INT COMMENT 'Chỉ cho phép review sản phẩm đã mua',
    
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    content TEXT,
    
    -- Hình ảnh đánh giá
    images JSON COMMENT 'Mảng URLs hình ảnh',
    
    -- Trạng thái
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    
    -- Phản hồi từ shop
    admin_reply TEXT,
    admin_replied_at TIMESTAMP NULL,
    admin_replied_by INT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE SET NULL,
    FOREIGN KEY (admin_replied_by) REFERENCES users(id) ON DELETE SET NULL,
    
    UNIQUE KEY unique_user_order_item (user_id, order_item_id),
    INDEX idx_product (product_id),
    INDEX idx_user (user_id),
    INDEX idx_rating (rating),
    INDEX idx_status (status)
);

-- =================================================================
-- 8. QUẢN LÝ NỘI DUNG (CMS)
-- =================================================================

CREATE TABLE pages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    content LONGTEXT,
    excerpt TEXT,
    status ENUM('draft', 'published') DEFAULT 'draft',
    
    -- SEO
    seo_title VARCHAR(255),
    seo_description TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_slug (slug),
    INDEX idx_status (status)
);

CREATE TABLE blog_posts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    content LONGTEXT,
    excerpt TEXT,
    featured_image VARCHAR(500),
    status ENUM('draft', 'published') DEFAULT 'draft',
    
    -- SEO
    seo_title VARCHAR(255),
    seo_description TEXT,
    
    published_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    INDEX idx_published (published_at),
    FULLTEXT idx_search (title, content, excerpt)
);

-- =================================================================
-- 9. QUẢN LÝ WISHLIST & SO SÁNH
-- =================================================================

CREATE TABLE wishlists (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product (user_id, product_id)
);

CREATE TABLE product_comparisons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product (user_id, product_id)
);

-- =================================================================
-- 10. BẢNG HỖ TRỢ KHÁC
-- =================================================================

-- Bảng cấu hình hệ thống
CREATE TABLE settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    key_name VARCHAR(100) NOT NULL UNIQUE,
    value TEXT,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng log hoạt động
CREATE TABLE activity_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    model_type VARCHAR(100) COMMENT 'Product, Order, User, etc.',
    model_id INT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_model (model_type, model_id),
    INDEX idx_created (created_at)
);

-- Bảng thông báo
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50) DEFAULT 'info' COMMENT 'info, success, warning, error',
    is_read BOOLEAN DEFAULT FALSE,
    data JSON COMMENT 'Dữ liệu bổ sung',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created (created_at)
);

-- =================================================================
-- DỮ LIỆU MẪU (SAMPLE DATA)
-- =================================================================

-- Thêm roles mặc định
INSERT INTO roles (name, description) VALUES 
('admin', 'Quản trị viên hệ thống'),
('customer', 'Khách hàng'),
('vendor', 'Người bán hàng');

-- Thêm user admin mặc định (password: admin123)
INSERT INTO users (email, password_hash, first_name, last_name, status) VALUES 
('admin@e_marketplace.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'System', 'active');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- Thêm thuộc tính sản phẩm mặc định
INSERT INTO product_attributes (name, type, is_filterable) VALUES 
('Màu sắc', 'select', TRUE),
('Kích thước', 'select', TRUE),
('Chất liệu', 'select', TRUE),
('Thương hiệu', 'select', TRUE);

-- Thêm một số giá trị thuộc tính
INSERT INTO attribute_values (attribute_id, value, color_code) VALUES 
(1, 'Đỏ', '#FF0000'),
(1, 'Xanh', '#0000FF'),
(1, 'Vàng', '#FFFF00'),
(1, 'Đen', '#000000'),
(1, 'Trắng', '#FFFFFF');

INSERT INTO attribute_values (attribute_id, value) VALUES 
(2, 'S'),
(2, 'M'),
(2, 'L'),
(2, 'XL'),
(2, 'XXL');

-- Thêm cấu hình hệ thống mặc định
INSERT INTO settings (key_name, value, description) VALUES 
('site_name', 'E-Marketplace', 'Tên website'),
('site_description', 'Hệ thống thương mại điện tử', 'Mô tả website'),
('currency', 'VND', 'Đơn vị tiền tệ'),
('tax_rate', '10', 'Thuế VAT (%)'),
('free_shipping_threshold', '500000', 'Miễn phí ship từ (VND)'),
('low_stock_threshold', '10', 'Cảnh báo hết hàng khi còn'),
('order_number_prefix', 'EM', 'Tiền tố mã đơn hàng');

-- =================================================================
-- INDEXES TỐI ƯU PERFORMANCE
-- =================================================================

-- Tạo thêm các index cho performance
CREATE INDEX idx_products_status_featured ON products(status, is_featured);
CREATE INDEX idx_product_variants_stock ON product_variants(product_id, stock_quantity);
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_date_status ON orders(created_at, status);

-- Composite index cho tìm kiếm sản phẩm
CREATE INDEX idx_products_search ON products(status, is_featured, created_at);

COMMIT;

-- =================================================================
-- END OF SCHEMA
-- =================================================================
