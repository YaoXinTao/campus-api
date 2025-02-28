-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_malls DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_malls;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    openid VARCHAR(64) UNIQUE COMMENT '微信openid',
    nickname VARCHAR(50) COMMENT '用户昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    phone VARCHAR(20) COMMENT '手机号',
    student_id VARCHAR(20) COMMENT '学号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    college VARCHAR(50) COMMENT '学院',
    major VARCHAR(50) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_openid (openid),
    INDEX idx_phone (phone),
    INDEX idx_student_id (student_id),
    INDEX idx_phone_status (phone, status),
    INDEX idx_student_status (student_id, status),
    INDEX idx_college_major (college, major),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 管理员表
CREATE TABLE IF NOT EXISTS admin_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'OPERATOR' COMMENT '角色 SUPER_ADMIN-超级管理员 ADMIN-管理员 OPERATOR-运营',
    department VARCHAR(50) COMMENT '部门',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_role_status (role, status),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 初始化超级管理员账号
INSERT INTO admin_users (username, password, real_name, role, status)
VALUES ('admin', '$2a$10$X/uMNuiw3UZKzefO5wZMJeYHWKjlxhzJUEMZGGCxyh0E1KJqFGmyq', '超级管理员', 'SUPER_ADMIN', 1);
-- 注：密码为加密后的 'admin123' ，使用了BCrypt加密算法

-- 商品分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon_url VARCHAR(255) COMMENT '分类图标URL',
    banner_url VARCHAR(255) COMMENT '分类banner图URL',
    description VARCHAR(500) COMMENT '分类描述',
    keywords VARCHAR(255) COMMENT '分类关键词',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    level TINYINT DEFAULT 1 COMMENT '分类层级 1-一级分类 2-二级分类',
    sort_order INT DEFAULT 0 COMMENT '排序号，越小越靠前',
    is_featured TINYINT DEFAULT 0 COMMENT '是否推荐 0-否 1-是',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_sort_status (sort_order, status),
    INDEX idx_featured (is_featured, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 初始化商品分类数据
INSERT INTO categories (name, icon_url, description, parent_id, level, sort_order, is_featured, status) VALUES
-- 一级分类
('数码产品', '/static/images/categories/digital.png', '手机、电脑、配件等数码产品', 0, 1, 1, 1, 1),
('图书教材', '/static/images/categories/books.png', '教材、考试资料、课外读物等', 0, 1, 2, 1, 1),
('美妆个护', '/static/images/categories/beauty.png', '护肤、彩妆、个人护理等', 0, 1, 3, 1, 1),
('服饰鞋包', '/static/images/categories/fashion.png', '衣服、鞋子、箱包等', 0, 1, 4, 1, 1),
('食品饮料', '/static/images/categories/food.png', '零食、饮品、速食等', 0, 1, 5, 1, 1),
('生活用品', '/static/images/categories/life.png', '文具、日用品、床上用品等', 0, 1, 6, 1, 1),

-- 数码产品子分类
('手机', '/static/images/categories/phone.png', '手机、充电器、手机壳等', 1, 2, 1, 0, 1),
('电脑', '/static/images/categories/computer.png', '笔记本、平板、配件等', 1, 2, 2, 0, 1),
('数码配件', '/static/images/categories/accessories.png', '耳机、充电宝、存储卡等', 1, 2, 3, 0, 1),

-- 图书教材子分类
('教材教辅', '/static/images/categories/textbook.png', '各专业教材、辅导资料等', 2, 2, 1, 0, 1),
('考试用书', '/static/images/categories/exam.png', '考研、英语、计算机等', 2, 2, 2, 0, 1),
('文学小说', '/static/images/categories/novel.png', '小说、文学、传记等', 2, 2, 3, 0, 1),

-- 美妆个护子分类
('护肤', '/static/images/categories/skincare.png', '面霜、精华、面膜等', 3, 2, 1, 0, 1),
('彩妆', '/static/images/categories/makeup.png', '口红、粉底、眼影等', 3, 2, 2, 0, 1),
('个人护理', '/static/images/categories/personal.png', '洗护、香水、美容仪等', 3, 2, 3, 0, 1),

-- 服饰鞋包子分类
('男装', '/static/images/categories/men.png', '上衣、裤子、外套等', 4, 2, 1, 0, 1),
('女装', '/static/images/categories/women.png', '裙装、上衣、外套等', 4, 2, 2, 0, 1),
('鞋靴', '/static/images/categories/shoes.png', '运动鞋、皮鞋、靴子等', 4, 2, 3, 0, 1),
('箱包', '/static/images/categories/bags.png', '双肩包、手提包、钱包等', 4, 2, 4, 0, 1),

-- 食品饮料子分类
('零食', '/static/images/categories/snacks.png', '饼干、糖果、坚果等', 5, 2, 1, 0, 1),
('饮品', '/static/images/categories/drinks.png', '奶茶、咖啡、饮料等', 5, 2, 2, 0, 1),
('速食', '/static/images/categories/fastfood.png', '方便面、速食品等', 5, 2, 3, 0, 1),

-- 生活用品子分类
('文具', '/static/images/categories/stationery.png', '笔、本、文具盒等', 6, 2, 1, 0, 1),
('日用品', '/static/images/categories/daily.png', '收纳、清洁、雨伞等', 6, 2, 2, 0, 1),
('床上用品', '/static/images/categories/bedding.png', '被子、枕头、床单等', 6, 2, 3, 0, 1);

-- 商品表（基本信息）
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    brief VARCHAR(255) COMMENT '商品简介',
    keywords VARCHAR(255) COMMENT '商品关键字',
    main_image VARCHAR(255) NOT NULL COMMENT '商品主图',
    album JSON COMMENT '商品相册',
    unit VARCHAR(20) COMMENT '商品单位',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    market_price DECIMAL(10,2) COMMENT '市场价',
    total_stock INT NOT NULL DEFAULT 0 COMMENT '总库存',
    total_sales INT NOT NULL DEFAULT 0 COMMENT '总销量',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    status TINYINT DEFAULT 0 COMMENT '状态：0-下架 1-上架',
    verify_status TINYINT DEFAULT 0 COMMENT '审核状态：0-未审核 1-审核通过 2-审核不通过',
    is_featured TINYINT DEFAULT 0 COMMENT '是否推荐：0-否 1-是',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_featured (is_featured),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品详情表
CREATE TABLE product_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    description TEXT COMMENT '商品描述',
    rich_content TEXT COMMENT '富文本详情',
    spec_desc TEXT COMMENT '规格参数描述',
    packing_list TEXT COMMENT '包装清单',
    service_notes TEXT COMMENT '售后服务',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uniq_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品详情表';

-- 商品规格模板表
CREATE TABLE product_spec_templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    name VARCHAR(50) NOT NULL COMMENT '模板名称',
    spec_items JSON NOT NULL COMMENT '规格项列表，如[{"name":"颜色","values":["红色","蓝色"]},{"name":"尺寸","values":["S","M","L"]}]',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格模板表';

-- 商品SKU表
CREATE TABLE product_skus (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'SKU ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_code VARCHAR(64) NOT NULL COMMENT 'SKU编码',
    spec_data JSON NOT NULL COMMENT '规格数据，如[{"key":"颜色","value":"红色"},{"key":"尺寸","value":"S"}]',
    price DECIMAL(10,2) NOT NULL COMMENT '销售价',
    market_price DECIMAL(10,2) COMMENT '市场价',
    cost_price DECIMAL(10,2) COMMENT '成本价',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    sales INT NOT NULL DEFAULT 0 COMMENT '销量',
    image_url VARCHAR(255) COMMENT 'SKU图片',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product (product_id),
    UNIQUE KEY uniq_sku_code (sku_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- 商品属性表
CREATE TABLE product_attributes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '属性ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    name VARCHAR(50) NOT NULL COMMENT '属性名',
    value VARCHAR(255) NOT NULL COMMENT '属性值',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

-- 商品标签表
CREATE TABLE product_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    icon VARCHAR(255) COMMENT '标签图标',
    color VARCHAR(20) COMMENT '标签颜色',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品标签表';

-- 商品标签关联表
CREATE TABLE product_tag_relations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product (product_id),
    INDEX idx_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品标签关联表';

-- 商品收藏表
CREATE TABLE product_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_product (user_id, product_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

-- 插入商品收藏示例数据
INSERT INTO product_favorites (user_id, product_id, created_at) VALUES 
(1, 1, '2024-02-10 10:00:00'),  -- 用户1收藏了iPhone 15 Pro Max
(1, 2, '2024-02-10 10:30:00'),  -- 用户1收藏了小米14 Pro
(2, 1, '2024-02-10 11:00:00'),  -- 用户2收藏了iPhone 15 Pro Max
(2, 3, '2024-02-10 11:30:00'),  -- 用户2收藏了MacBook Pro M3
(3, 2, '2024-02-10 12:00:00'),  -- 用户3收藏了小米14 Pro
(3, 4, '2024-02-10 12:30:00');  -- 用户3收藏了高等数学教材

-- 商品评价表
CREATE TABLE product_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    rating TINYINT NOT NULL COMMENT '评分(1-5)',
    content TEXT COMMENT '评价内容',
    images JSON COMMENT '评价图片',
    reply TEXT COMMENT '商家回复',
    reply_time DATETIME COMMENT '回复时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-隐藏 1-显示',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product (product_id),
    INDEX idx_user (user_id),
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 插入商品评价示例数据
INSERT INTO product_reviews (user_id, product_id, order_id, sku_id, rating, content, images, reply, reply_time, status, created_at) VALUES 
(1, 1, 1001, 1, 5, 'iPhone 15 Pro Max 非常好用，拍照效果很棒，续航也不错！', '["review/images/iphone15_1.jpg", "review/images/iphone15_2.jpg"]', '感谢您的支持，欢迎下次再来购买！', '2024-02-10 14:00:00', 1, '2024-02-10 13:00:00'),
(2, 1, 1002, 1, 4, '手机整体不错，就是价格稍贵。', '["review/images/iphone15_3.jpg"]', '感谢您的评价，我们会继续提供优质的产品！', '2024-02-10 15:00:00', 1, '2024-02-10 14:00:00'),
(1, 2, 1003, 2, 5, '小米14 Pro性价比很高，系统流畅，拍照也很清晰。', '["review/images/mi14_1.jpg", "review/images/mi14_2.jpg"]', '谢谢您的好评！', '2024-02-10 16:00:00', 1, '2024-02-10 15:00:00'),
(3, 3, 1004, 3, 5, 'MacBook Pro M3性能强大，续航惊人，做开发特别流畅。', '["review/images/macbook_1.jpg"]', '感谢您的认可！', '2024-02-10 17:00:00', 1, '2024-02-10 16:00:00'),
(2, 4, 1005, 4, 5, '教材印刷清晰，讲解详细，很适合自学。', null, '感谢您的评价，祝您学习进步！', '2024-02-10 18:00:00', 1, '2024-02-10 17:00:00');

-- 商品浏览记录表
CREATE TABLE product_view_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_time (user_id, created_at),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品浏览记录表';

-- 插入商品浏览记录示例数据
INSERT INTO product_view_logs (user_id, product_id, created_at) VALUES 
(1, 1, '2024-02-10 09:00:00'),  -- 用户1浏览iPhone
(1, 2, '2024-02-10 09:10:00'),  -- 用户1浏览小米
(1, 1, '2024-02-10 09:20:00'),  -- 用户1再次浏览iPhone
(2, 1, '2024-02-10 10:00:00'),  -- 用户2浏览iPhone
(2, 3, '2024-02-10 10:15:00'),  -- 用户2浏览MacBook
(2, 4, '2024-02-10 10:30:00'),  -- 用户2浏览教材
(3, 2, '2024-02-10 11:00:00'),  -- 用户3浏览小米
(3, 3, '2024-02-10 11:20:00'),  -- 用户3浏览MacBook
(1, 3, '2024-02-10 11:30:00'),  -- 用户1浏览MacBook
(2, 2, '2024-02-10 11:45:00');  -- 用户2浏览小米

-- 插入商品分类示例数据
INSERT INTO categories (name, icon_url, description, parent_id, level, sort_order, is_featured, status) VALUES
('数码产品', '/static/images/categories/digital.png', '手机、电脑、配件等数码产品', 0, 1, 1, 1, 1),
('手机', '/static/images/categories/phone.png', '各类品牌手机及配件', 1, 2, 1, 1, 1),
('电脑', '/static/images/categories/computer.png', '笔记本电脑、台式机等', 1, 2, 2, 1, 1),
('图书教材', '/static/images/categories/book.png', '教材、考试资料、课外读物等', 0, 1, 2, 1, 1),
('教材教辅', '/static/images/categories/textbook.png', '各专业教材、辅导资料', 4, 2, 1, 1, 1);

-- 插入商品示例数据
INSERT INTO products (
    category_id, name, brief, keywords, main_image, album, unit, 
    price, market_price, total_stock, total_sales, view_count, 
    status, verify_status, is_featured, sort_order, created_by
) VALUES 
(2, 'iPhone 15 Pro Max', '苹果最新旗舰手机', 'iPhone,苹果,手机', 
    '/static/images/products/iphone15.jpg',
    '["image1.jpg", "image2.jpg", "image3.jpg"]', '台',
    8999.00, 9999.00, 100, 50, 1000, 1, 1, 1, 1, 1),

(2, '小米14 Pro', '小米年度旗舰', '小米,手机,骁龙8Gen3', 
    '/static/images/products/mi14.jpg',
    '["image1.jpg", "image2.jpg", "image3.jpg"]', '台',
    4999.00, 5999.00, 200, 80, 800, 1, 1, 1, 2, 1),

(3, 'MacBook Pro M3', '搭载M3芯片的MacBook Pro', 'MacBook,苹果,笔记本', 
    '/static/images/products/macbook.jpg',
    '["image1.jpg", "image2.jpg", "image3.jpg"]', '台',
    14999.00, 15999.00, 50, 20, 500, 1, 1, 1, 3, 1),

(5, '高等数学（第七版）', '同济大学数学系列教材', '高数,教材,数学', 
    '/static/images/products/math.jpg',
    '["image1.jpg", "image2.jpg"]', '本',
    49.00, 59.00, 1000, 500, 2000, 1, 1, 1, 4, 1);

-- 插入商品详情示例数据
INSERT INTO product_details (
    product_id, description, rich_content, spec_desc, packing_list, service_notes
) VALUES 
(1, 'iPhone 15 Pro Max 采用钛金属边框，搭载 A17 Pro 芯片',
    '<p>详细的产品介绍HTML内容</p>',
    '{"屏幕尺寸":"6.7英寸","处理器":"A17 Pro","存储":"256GB"}',
    '手机主机 x1、充电器 x1、数据线 x1、说明书 x1',
    '支持14天无理由退换，1年保修'),

(2, '小米14 Pro 搭载骁龙8Gen3，徕卡光学系统',
    '<p>详细的产品介绍HTML内容</p>',
    '{"屏幕尺寸":"6.73英寸","处理器":"骁龙8Gen3","存储":"256GB"}',
    '手机主机 x1、充电器 x1、数据线 x1、保护壳 x1、说明书 x1',
    '支持7天无理由退换，1年保修');

-- 插入规格模板示例数据
INSERT INTO product_spec_templates (name, spec_items, status) VALUES 
('手机规格模板', 
 '[
    {"name":"颜色","values":["暗夜黑","原色白","原野绿"]},
    {"name":"存储容量","values":["128GB","256GB","512GB","1TB"]},
    {"name":"网络类型","values":["全网通","5G"]}
 ]', 1),
('笔记本电脑规格模板', 
 '[
    {"name":"颜色","values":["深空灰","银色"]},
    {"name":"内存","values":["8GB","16GB","32GB"]},
    {"name":"存储容量","values":["256GB","512GB","1TB","2TB"]}
 ]', 1);

-- 插入SKU示例数据
INSERT INTO product_skus (
    product_id, sku_code, spec_data, price, market_price, 
    cost_price, stock, sales, image_url, status
) VALUES 
(1, 'IP15PM-256-BLACK', 
    '[{"key":"颜色","value":"暗夜黑"},{"key":"存储容量","value":"256GB"}]',
    8999.00, 9999.00, 7000.00, 50, 20,
    '/static/images/products/iphone15-black.jpg', 1),
    
(1, 'IP15PM-256-WHITE', 
    '[{"key":"颜色","value":"原色白"},{"key":"存储容量","value":"256GB"}]',
    8999.00, 9999.00, 7000.00, 30, 15,
    '/static/images/products/iphone15-white.jpg', 1);

-- 插入商品标签示例数据
INSERT INTO product_tags (name, icon, color, status, sort_order) VALUES 
('新品', '/static/images/tags/new.png', '#ff4444', 1, 1),
('热销', '/static/images/tags/hot.png', '#ff6b22', 1, 2),
('推荐', '/static/images/tags/recommend.png', '#00aa00', 1, 3),
('限时特惠', '/static/images/tags/sale.png', '#ff0000', 1, 4);

-- 插入商品标签关联示例数据
INSERT INTO product_tag_relations (product_id, tag_id) VALUES 
(1, 1), -- iPhone 15 Pro Max - 新品
(1, 2), -- iPhone 15 Pro Max - 热销
(2, 2), -- 小米14 Pro - 热销
(2, 3); -- 小米14 Pro - 推荐

-- 插入商品属性示例数据
INSERT INTO product_attributes (product_id, name, value, sort_order) VALUES 
(1, '处理器', 'A17 Pro', 1),
(1, '屏幕尺寸', '6.7英寸', 2),
(1, '电池容量', '4422mAh', 3),
(1, '摄像头', '4800万像素主摄', 4),
(2, '处理器', '骁龙8Gen3', 1),
(2, '屏幕尺寸', '6.73英寸', 2),
(2, '电池容量', '4880mAh', 3),
(2, '摄像头', '5000万像素主摄', 4);

-- 收货地址表
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区/县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    postal_code VARCHAR(10) COMMENT '邮政编码',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址 0-否 1-是',
    tag VARCHAR(10) COMMENT '地址标签：家、学校、公司等',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_user_default (user_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 插入地址示例数据
INSERT INTO addresses (
    user_id, receiver, phone, province, city, district, 
    detail_address, postal_code, is_default, tag
) VALUES 
(1, '张三', '13800138001', '浙江省', '杭州市', '西湖区',
    '浙江大学紫金港校区XX号宿舍楼', '310058', 1, '学校'),
(1, '张三', '13800138001', '浙江省', '杭州市', '上城区',
    '某某小区A栋B单元', '310002', 0, '家'),
(2, '李四', '13900139002', '浙江省', '杭州市', '西湖区',
    '浙江大学玉泉校区XX号宿舍楼', '310027', 1, '学校'),
(3, '王五', '13700137003', '浙江省', '杭州市', '滨江区',
    '某某公寓C栋', '310051', 1, '住宅');

-- 优惠券表
CREATE TABLE coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券ID',
    name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    type TINYINT NOT NULL COMMENT '优惠券类型：1-满减券 2-折扣券 3-无门槛券',
    amount DECIMAL(10,2) NOT NULL COMMENT '优惠金额/折扣率',
    min_spend DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
    category_id BIGINT COMMENT '适用分类ID，空表示全场通用',
    start_time DATETIME NOT NULL COMMENT '生效时间',
    end_time DATETIME NOT NULL COMMENT '失效时间',
    total_count INT NOT NULL COMMENT '发行总量',
    remain_count INT NOT NULL COMMENT '剩余数量',
    per_limit INT DEFAULT 1 COMMENT '每人限领数量',
    description TEXT COMMENT '使用说明',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已停用 1-正常',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type_status (type, status),
    INDEX idx_category (category_id),
    INDEX idx_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 用户优惠券表（领取记录）
CREATE TABLE user_coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    order_id BIGINT COMMENT '使用订单ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已作废 1-未使用 2-已使用 3-已过期',
    receive_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    use_time DATETIME COMMENT '使用时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_status (user_id, status),
    INDEX idx_coupon (coupon_id),
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 添加乐观锁和分布式锁相关的修改
-- 在user_coupons表中添加version字段用于乐观锁
ALTER TABLE user_coupons ADD COLUMN version INT DEFAULT 0 COMMENT '版本号，用于乐观锁';

-- 在distributed_locks表上添加唯一索引确保分布式锁的互斥性
CREATE TABLE distributed_locks (
    lock_key VARCHAR(128) PRIMARY KEY COMMENT '锁键',
    lock_value VARCHAR(128) NOT NULL COMMENT '锁值',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_lock_key (lock_key) COMMENT '分布式锁键值唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式锁表';

-- 插入优惠券示例数据
INSERT INTO coupons (
    name, type, amount, min_spend, category_id, 
    start_time, end_time, total_count, remain_count, 
    per_limit, description, status
) VALUES 
-- 满减券
('新人专享券', 1, 50.00, 200.00, NULL,
    '2024-02-01 00:00:00', '2024-03-31 23:59:59', 
    1000, 800, 1, 
    '1. 满200元可用\n2. 全场通用\n3. 不可与其他优惠券叠加使用', 1),

-- 数码类满减券
('数码专享券', 1, 100.00, 1000.00, 1,
    '2024-02-01 00:00:00', '2024-02-29 23:59:59',
    500, 400, 1,
    '1. 满1000元可用\n2. 仅限数码产品分类使用\n3. 不可与其他优惠券叠加使用', 1),

-- 折扣券
('图书95折券', 2, 0.95, 0.00, 2,
    '2024-02-01 00:00:00', '2024-02-29 23:59:59',
    1000, 900, 2,
    '1. 无使用门槛\n2. 仅限图书分类使用\n3. 不可与其他优惠券叠加使用', 1),

-- 无门槛券
('新人无门槛券', 3, 10.00, 0.00, NULL,
    '2024-02-01 00:00:00', '2024-03-31 23:59:59',
    2000, 1500, 1,
    '1. 无使用门槛\n2. 全场通用\n3. 不可与其他优惠券叠加使用', 1);

-- 插入用户优惠券示例数据
INSERT INTO user_coupons (
    user_id, coupon_id, status, receive_time
) VALUES 
(1, 1, 1, '2024-02-10 10:00:00'),  -- 用户1领取新人专享券
(1, 2, 1, '2024-02-10 10:01:00'),  -- 用户1领取数码专享券
(1, 3, 1, '2024-02-10 10:02:00'),  -- 用户1领取图书95折券
(1, 4, 2, '2024-02-10 10:03:00'),  -- 用户1领取并使用了无门槛券

(2, 1, 1, '2024-02-11 14:00:00'),  -- 用户2领取新人专享券
(2, 3, 1, '2024-02-11 14:01:00'),  -- 用户2领取图书95折券
(2, 4, 1, '2024-02-11 14:02:00'),  -- 用户2领取无门槛券

(3, 1, 2, '2024-02-12 09:00:00'),  -- 用户3领取并使用了新人专享券
(3, 2, 1, '2024-02-12 09:01:00'),  -- 用户3领取数码专享券
(3, 4, 1, '2024-02-12 09:02:00');  -- 用户3领取无门槛券

-- 购物车表
CREATE TABLE shopping_cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    selected TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中：0-未选中 1-选中',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_selected (user_id, selected) COMMENT '用户商品选中状态索引',
    INDEX idx_user_product (user_id, product_id) COMMENT '用户商品索引',
    INDEX idx_sku (sku_id) COMMENT 'SKU索引',
    UNIQUE KEY uniq_user_sku (user_id, sku_id) COMMENT '同一用户同一SKU唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 插入购物车示例数据
INSERT INTO shopping_cart (
    user_id, product_id, sku_id, quantity, selected
) VALUES 
-- 用户1的购物车
(1, 1, 1, 2, 1),  -- iPhone 15 Pro Max 黑色版 2件
(1, 2, 3, 1, 1),  -- 小米14 Pro 1件

-- 用户2的购物车
(2, 1, 2, 1, 1),  -- iPhone 15 Pro Max 白色版 1件
(2, 3, 5, 1, 0),  -- MacBook Pro 未选中

-- 用户3的购物车
(3, 2, 4, 1, 1),  -- 小米14 Pro 1件
(3, 4, 6, 2, 1);  -- 高等数学教材 2本

-- 订单主表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    freight_amount DECIMAL(10,2) DEFAULT 0 COMMENT '运费金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    coupon_id BIGINT COMMENT '使用的优惠券ID',
    address_id BIGINT NOT NULL COMMENT '收货地址ID',
    receiver VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区/县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    order_status TINYINT NOT NULL DEFAULT 10 COMMENT '订单状态：10-待付款 20-待发货 30-待收货 40-已完成 50-已取消 60-已退款',
    review_status TINYINT NOT NULL DEFAULT 0 COMMENT '评价状态：0-待评价 1-已评价',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付 1-已支付 2-已退款',
    payment_time DATETIME COMMENT '支付时间',
    delivery_status TINYINT DEFAULT 0 COMMENT '发货状态：0-未发货 1-已发货 2-已收货',
    delivery_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '收货时间',
    finish_time DATETIME COMMENT '完成时间',
    cancel_time DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    remark VARCHAR(500) COMMENT '订单备注',
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '已退款金额',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (order_status),
    INDEX idx_payment (payment_status),
    INDEX idx_delivery (delivery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单详情表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    product_name VARCHAR(255) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(255) NOT NULL COMMENT '商品图片',
    sku_spec_data JSON NOT NULL COMMENT 'SKU规格数据',
    price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    quantity INT NOT NULL COMMENT '购买数量',
    refunded_quantity INT NOT NULL DEFAULT 0 COMMENT '已退款数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '已退款金额',
    refund_status TINYINT NOT NULL DEFAULT 0 COMMENT '退款状态：0-无退款 1-退款中 2-已退款 3-退款失败',
    refund_id BIGINT COMMENT '退款记录ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_product (product_id),
    INDEX idx_sku (sku_id),
    INDEX idx_refund (refund_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- 支付记录表
CREATE TABLE payment_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID',
    payment_no VARCHAR(32) NOT NULL UNIQUE COMMENT '支付单号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    payment_method TINYINT NOT NULL COMMENT '支付方式：1-微信支付 2-支付宝 3-余额支付',
    payment_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    transaction_id VARCHAR(64) COMMENT '第三方支付交易号',
    payment_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付 1-支付成功 2-支付失败',
    payment_time DATETIME COMMENT '支付成功时间',
    callback_time DATETIME COMMENT '回调时间',
    callback_content TEXT COMMENT '回调内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_user (user_id),
    INDEX idx_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 退款记录表
CREATE TABLE refund_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退款记录ID',
    refund_no VARCHAR(32) NOT NULL UNIQUE COMMENT '退款单号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    order_item_id BIGINT NOT NULL COMMENT '订单详情ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    refund_type TINYINT NOT NULL COMMENT '退款类型：1-仅退款 2-退货退款',
    refund_reason_type TINYINT NOT NULL COMMENT '退款原因类型：1-质量问题 2-商品与描述不符 3-商品损坏 4-尺寸不合适 5-其他',
    refund_reason VARCHAR(500) NOT NULL COMMENT '退款原因',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    refund_quantity INT NOT NULL DEFAULT 1 COMMENT '退款数量',
    is_partial TINYINT NOT NULL DEFAULT 0 COMMENT '是否部分退款：0-否 1-是',
    refund_status TINYINT NOT NULL DEFAULT 0 COMMENT '退款状态：0-待处理 1-已同意待退款 2-已拒绝 3-已完成 4-待退货 5-已退货待确认',
    refund_time DATETIME COMMENT '退款完成时间',
    reject_reason VARCHAR(500) COMMENT '拒绝原因',
    reject_time DATETIME COMMENT '拒绝时间',
    evidence_images JSON COMMENT '凭证图片',
    admin_id BIGINT COMMENT '处理人ID',
    admin_note VARCHAR(500) COMMENT '处理备注',
    process_time DATETIME COMMENT '处理时间',
    auto_approved TINYINT DEFAULT 0 COMMENT '是否自动审核：0-否 1-是',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_user (user_id),
    INDEX idx_status (refund_status),
    INDEX idx_admin (admin_id),
    INDEX idx_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 退货物流表
CREATE TABLE refund_delivery (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退货物流ID',
    refund_id BIGINT NOT NULL COMMENT '退款记录ID',
    refund_no VARCHAR(32) NOT NULL COMMENT '退款单号',
    delivery_company VARCHAR(50) NOT NULL COMMENT '物流公司',
    delivery_no VARCHAR(32) NOT NULL COMMENT '物流单号',
    sender_name VARCHAR(50) NOT NULL COMMENT '寄件人姓名',
    sender_phone VARCHAR(20) NOT NULL COMMENT '寄件人电话',
    sender_address TEXT NOT NULL COMMENT '寄件地址',
    delivery_status TINYINT DEFAULT 0 COMMENT '物流状态：0-待发货 1-已发货 2-已签收',
    delivery_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '签收时间',
    tracking_data JSON COMMENT '物流跟踪数据',
    admin_confirm_time DATETIME COMMENT '商家确认时间',
    admin_note VARCHAR(500) COMMENT '商家备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_refund (refund_id),
    INDEX idx_refund_no (refund_no),
    INDEX idx_status (delivery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货物流表';

-- 订单物流表
CREATE TABLE order_delivery (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单物流ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    delivery_company VARCHAR(50) NOT NULL COMMENT '物流公司',
    delivery_no VARCHAR(32) NOT NULL COMMENT '物流单号',
    delivery_status TINYINT DEFAULT 0 COMMENT '物流状态：0-待发货 1-已发货 2-已签收',
    delivery_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '签收时间',
    tracking_data JSON COMMENT '物流跟踪数据',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (delivery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流表';

-- 订单操作日志表
CREATE TABLE order_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_type TINYINT NOT NULL COMMENT '操作人类型：1-用户 2-管理员',
    action_type TINYINT NOT NULL COMMENT '操作类型：1-创建订单 2-支付订单 3-发货 4-确认收货 5-取消订单 6-申请退款 7-同意退款 8-拒绝退款 9-退款完成',
    action_desc VARCHAR(255) NOT NULL COMMENT '操作描述',
    ip VARCHAR(50) COMMENT '操作IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_operator (operator_id, operator_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

-- 插入订单示例数据
INSERT INTO orders (
    order_no, user_id, total_amount, actual_amount, freight_amount, 
    discount_amount, coupon_id, address_id, receiver, phone, 
    province, city, district, detail_address, order_status, 
    payment_status, payment_time, delivery_status, remark
) VALUES 
('202402150001', 1, 8999.00, 8949.00, 0.00, 
 50.00, 1, 1, '张三', '13800138001', 
 '浙江省', '杭州市', '西湖区', '浙江大学紫金港校区XX号宿舍楼', 20,
 1, '2024-02-15 10:30:00', 0, '请尽快发货'),

('202402150002', 2, 4999.00, 4899.00, 0.00,
 100.00, 2, 3, '李四', '13900139002',
 '浙江省', '杭州市', '西湖区', '浙江大学玉泉校区XX号宿舍楼', 30,
 1, '2024-02-15 11:00:00', 1, NULL);

-- 插入订单详情示例数据
INSERT INTO order_items (
    order_id, order_no, product_id, sku_id, product_name,
    product_image, sku_spec_data, price, quantity, refunded_quantity, total_amount
) VALUES 
(1, '202402150001', 1, 1, 'iPhone 15 Pro Max',
 '/static/images/products/iphone15-black.jpg',
 '[{"key":"颜色","value":"暗夜黑"},{"key":"存储容量","value":"256GB"}]',
 8999.00, 1, 0, 8999.00),

(2, '202402150002', 2, 3, '小米14 Pro',
 '/static/images/products/mi14.jpg',
 '[{"key":"颜色","value":"原色白"},{"key":"存储容量","value":"256GB"}]',
 4999.00, 1, 0, 4999.00);

-- 插入支付记录示例数据
INSERT INTO payment_records (
    payment_no, order_id, order_no, user_id, payment_method,
    payment_amount, transaction_id, payment_status, payment_time
) VALUES 
('P202402150001', 1, '202402150001', 1, 1,
 8949.00, 'WX202402150001', 1, '2024-02-15 10:30:00'),

('P202402150002', 2, '202402150002', 2, 1,
 4899.00, 'WX202402150002', 1, '2024-02-15 11:00:00');

-- 插入订单物流示例数据
INSERT INTO order_delivery (
    order_id, order_no, delivery_company, delivery_no,
    delivery_status, delivery_time, tracking_data
) VALUES 
(2, '202402150002', '顺丰速运', 'SF1234567890',
 1, '2024-02-15 14:00:00',
 '[
    {"time": "2024-02-15 14:00:00", "content": "已发货"},
    {"time": "2024-02-15 16:00:00", "content": "快件已到达【杭州转运中心】"},
    {"time": "2024-02-15 18:00:00", "content": "快件已从【杭州转运中心】发出"}
 ]');

-- 插入订单操作日志示例数据
INSERT INTO order_logs (
    order_id, order_no, operator_id, operator_type,
    action_type, action_desc, ip
) VALUES 
(1, '202402150001', 1, 1, 1, '创建订单', '127.0.0.1'),
(1, '202402150001', 1, 1, 2, '支付订单', '127.0.0.1'),
(2, '202402150002', 2, 1, 1, '创建订单', '127.0.0.1'),
(2, '202402150002', 2, 1, 2, '支付订单', '127.0.0.1'),
(2, '202402150002', 1, 2, 3, '订单发货', '127.0.0.1');

-- 换货记录表
CREATE TABLE exchange_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '换货记录ID',
    exchange_no VARCHAR(32) NOT NULL UNIQUE COMMENT '换货单号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    order_item_id BIGINT NOT NULL COMMENT '订单详情ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    old_sku_id BIGINT NOT NULL COMMENT '原SKU ID',
    new_sku_id BIGINT NOT NULL COMMENT '新SKU ID',
    old_sku_spec_data JSON NOT NULL COMMENT '原SKU规格数据',
    new_sku_spec_data JSON NOT NULL COMMENT '新SKU规格数据',
    exchange_reason_type TINYINT NOT NULL COMMENT '换货原因类型：1-尺码不合适 2-颜色与描述不符 3-款式与描述不符 4-其他',
    exchange_reason VARCHAR(500) NOT NULL COMMENT '换货原因',
    exchange_status TINYINT NOT NULL DEFAULT 0 COMMENT '换货状态：0-待处理 1-已同意 2-已拒绝 3-待买家退货 4-已退货待确认 5-待发货 6-已发货 7-已完成',
    reject_reason VARCHAR(500) COMMENT '拒绝原因',
    reject_time DATETIME COMMENT '拒绝时间',
    evidence_images JSON COMMENT '凭证图片',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_user (user_id),
    INDEX idx_status (exchange_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换货记录表';

-- 换货物流表
CREATE TABLE exchange_delivery (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '换货物流ID',
    exchange_id BIGINT NOT NULL COMMENT '换货记录ID',
    exchange_no VARCHAR(32) NOT NULL COMMENT '换货单号',
    delivery_type TINYINT NOT NULL COMMENT '物流类型：1-退货物流 2-换货物流',
    delivery_company VARCHAR(50) NOT NULL COMMENT '物流公司',
    delivery_no VARCHAR(32) NOT NULL COMMENT '物流单号',
    sender_name VARCHAR(50) NOT NULL COMMENT '寄件人姓名',
    sender_phone VARCHAR(20) NOT NULL COMMENT '寄件人电话',
    sender_address TEXT NOT NULL COMMENT '寄件地址',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收件人电话',
    receiver_address TEXT NOT NULL COMMENT '收件地址',
    delivery_status TINYINT DEFAULT 0 COMMENT '物流状态：0-待发货 1-已发货 2-已签收',
    delivery_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '签收时间',
    tracking_data JSON COMMENT '物流跟踪数据',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_exchange (exchange_id),
    INDEX idx_exchange_no (exchange_no),
    INDEX idx_status (delivery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换货物流表';

-- 订单评价表
CREATE TABLE order_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    order_item_id BIGINT NOT NULL COMMENT '订单商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    rating TINYINT NOT NULL COMMENT '评分：1-5星',
    content TEXT COMMENT '评价内容',
    images JSON COMMENT '评价图片',
    is_anonymous TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否 1-是',
    reply_content TEXT COMMENT '商家回复',
    reply_time DATETIME COMMENT '回复时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id)
) COMMENT '订单评价表';

-- 部分退款记录表
CREATE TABLE refund_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退款商品ID',
    refund_id BIGINT NOT NULL COMMENT '退款记录ID',
    order_item_id BIGINT NOT NULL COMMENT '订单商品ID',
    refund_quantity INT NOT NULL COMMENT '退款数量',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_refund_id (refund_id),
    INDEX idx_order_item_id (order_item_id)
) COMMENT '部分退款商品表';

-- 批量发货记录表
CREATE TABLE batch_delivery_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '批量发货ID',
    batch_no VARCHAR(32) NOT NULL UNIQUE COMMENT '批次号',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    total_count INT NOT NULL COMMENT '总订单数',
    success_count INT NOT NULL DEFAULT 0 COMMENT '成功数量',
    fail_count INT NOT NULL DEFAULT 0 COMMENT '失败数量',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-处理中 1-处理完成 2-处理失败',
    error_msg TEXT COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_batch_no (batch_no),
    INDEX idx_operator_id (operator_id)
) COMMENT '批量发货记录表';

-- 批量发货详情表
CREATE TABLE batch_delivery_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    batch_id BIGINT NOT NULL COMMENT '批量发货ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    delivery_company VARCHAR(50) NOT NULL COMMENT '物流公司',
    delivery_no VARCHAR(50) NOT NULL COMMENT '物流单号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理 1-成功 2-失败',
    error_msg VARCHAR(255) COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_batch_id (batch_id),
    INDEX idx_order_id (order_id)
) COMMENT '批量发货详情表';

-- 业务规则配置表
CREATE TABLE business_rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_type VARCHAR(50) NOT NULL COMMENT '规则类型：REFUND-退款 EXCHANGE-换货',
    rule_key VARCHAR(50) NOT NULL COMMENT '规则键',
    rule_value VARCHAR(255) NOT NULL COMMENT '规则值',
    rule_desc VARCHAR(255) COMMENT '规则描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_type_key (rule_type, rule_key)
) COMMENT '业务规则配置表';

-- 分布式锁表
CREATE TABLE distributed_locks (
    lock_key VARCHAR(128) PRIMARY KEY COMMENT '锁键',
    lock_value VARCHAR(128) NOT NULL COMMENT '锁值',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式锁表';

-- 插入默认的业务规则配置
INSERT INTO business_rules (rule_type, rule_key, rule_value, rule_desc) VALUES
-- 退款规则
('REFUND', 'MAX_TIMES', '3', '每个订单最大退款次数'),
('REFUND', 'TIME_LIMIT', '15', '订单完成后可退款的天数'),
('REFUND', 'AMOUNT_LIMIT', '100', '无需审核的最大退款金额'),
('REFUND', 'AUTO_APPROVE', '1', '是否开启小额退款自动审核：0-否 1-是'),
('REFUND', 'RETURN_TIMEOUT', '7', '买家退货期限(天)'),
('REFUND', 'CONFIRM_TIMEOUT', '3', '商家确认退货期限(天)')
ON DUPLICATE KEY UPDATE 
rule_value = VALUES(rule_value),
rule_desc = VALUES(rule_desc);
-- 轮播图表
CREATE TABLE IF NOT EXISTS banners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
    title VARCHAR(100) NOT NULL COMMENT '轮播图标题',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    link_type TINYINT NOT NULL DEFAULT 1 COMMENT '链接类型：1-商品 2-分类 3-外部链接 4-无链接',
    link_value VARCHAR(255) COMMENT '链接值：商品ID/分类ID/外部URL',
    position VARCHAR(50) DEFAULT 'HOME' COMMENT '展示位置：HOME-首页 CATEGORY-分类页',
    sort_order INT DEFAULT 0 COMMENT '排序号，越小越靠前',
    start_time DATETIME COMMENT '开始展示时间',
    end_time DATETIME COMMENT '结束展示时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    remark VARCHAR(255) COMMENT '备注',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_position (position),
    INDEX idx_sort_status (sort_order, status),
    INDEX idx_time (start_time, end_time, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 初始化轮播图示例数据
INSERT INTO banners (title, image_url, link_type, link_value, position, sort_order, status) VALUES
('新品上市', '/static/images/banners/new_products.jpg', 1, '1', 'HOME', 1, 1),
('限时特惠', '/static/images/banners/discount.jpg', 2, '3', 'HOME', 2, 1),
('校园活动', '/static/images/banners/activity.jpg', 3, 'https://example.com/activity', 'HOME', 3, 1);