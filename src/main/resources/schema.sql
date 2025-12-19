CREATE TABLE IF NOT EXISTS advertisements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    video_url VARCHAR(500) NOT NULL,
    duration INT DEFAULT 15,
    clicks INT DEFAULT 0,
    views INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- 初始测试数据
INSERT INTO advertisements (title, description, video_url, duration) VALUES
 ('夏季促销', '夏季大促销广告', '/uploads/ads/summer_sale.mp4', 15),
 ('新品上市', '最新产品发布', '/uploads/ads/new_product.mp4', 15);

