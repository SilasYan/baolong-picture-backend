CREATE DATABASE IF NOT EXISTS baolong_picture DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE baolong_picture;

-- 用户表
CREATE TABLE IF NOT EXISTS user
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_account    VARCHAR(50)     NOT NULL COMMENT '账号',
    user_password   VARCHAR(512)    NOT NULL COMMENT '密码',
    user_email      VARCHAR(50)     NOT NULL COMMENT '用户邮箱',
    user_phone      VARCHAR(50)              DEFAULT NULL COMMENT '用户手机号',
    user_name       VARCHAR(256)             DEFAULT NULL COMMENT '用户昵称',
    user_avatar     VARCHAR(512)             DEFAULT NULL COMMENT '用户头像',
    user_profile    VARCHAR(512)             DEFAULT NULL COMMENT '用户简介',
    user_role       VARCHAR(20)              DEFAULT 'USER' COMMENT '用户角色（USER-普通用户, ADMIN-管理员）',
    birthday        DATE                     DEFAULT NULL COMMENT '出生日期',
    vip_number      BIGINT                   DEFAULT NULL COMMENT '会员编号',
    vip_expire_time DATETIME                 DEFAULT NULL COMMENT '会员过期时间',
    vip_code        VARCHAR(20)              DEFAULT NULL COMMENT '会员兑换码',
    vip_sign        VARCHAR(20)              DEFAULT NULL COMMENT '会员标识（vip 表的类型字段）',
    share_code      VARCHAR(20)              DEFAULT NULL COMMENT '分享码',
    invite_user     BIGINT                   DEFAULT NULL COMMENT '邀请用户',
    is_delete       TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_account (user_account),
    UNIQUE KEY uk_user_email (user_email),
    UNIQUE KEY uk_user_phone (user_phone),
    INDEX idx_user_name (user_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';
ALTER TABLE baolong_picture.user
    ADD COLUMN is_disabled TINYINT NOT NULL DEFAULT 0 COMMENT '是否禁用（0-正常, 1-禁用）' AFTER share_code;


-- 会员表
CREATE TABLE IF NOT EXISTS vip
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    code        VARCHAR(20)     NOT NULL COMMENT '会员码',
    type        VARCHAR(20)     NOT NULL DEFAULT 'VIP' COMMENT '会员类型（VIP-普通会员, SVIP-超级会员）',
    duration    INT             NOT NULL COMMENT '时长',
    unit        VARCHAR(20)     NOT NULL COMMENT '时长单位（DAY-天, MONTH-月, YEAR-年, PERM-永久）',
    status      TINYINT         NOT NULL DEFAULT 0 COMMENT '状态（0-未使用, 1-已使用, 2-已过期）',
    used_user   BIGINT                   DEFAULT NULL COMMENT '使用用户 ID',
    used_time   DATETIME                 DEFAULT NULL COMMENT '使用时间',
    user_id     BIGINT          NOT NULL COMMENT '创建用户 ID',
    is_delete   TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '会员表';

-- 图片表
CREATE TABLE IF NOT EXISTS picture
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    origin_name     VARCHAR(256)    NOT NULL COMMENT '原图名称',
    origin_url      VARCHAR(512)    NOT NULL COMMENT '原图地址',
    origin_size     BIGINT          NOT NULL COMMENT '原图大小（单位: B）',
    origin_format   VARCHAR(20)              DEFAULT NULL COMMENT '原图格式',
    origin_width    VARCHAR(20)              DEFAULT NULL COMMENT '原图宽度',
    origin_height   VARCHAR(20)              DEFAULT NULL COMMENT '原图高度',
    origin_scale    VARCHAR(20)              DEFAULT NULL COMMENT '原图比例（宽高比）',
    origin_color    VARCHAR(20)              DEFAULT NULL COMMENT '原图主色调',
    resource_path   VARCHAR(512)             DEFAULT NULL COMMENT '资源路径',
    resource_status TINYINT                  DEFAULT 0 NOT NULL COMMENT '资源状态（0-存在 COS, 1-不存在 COS）',
    thumbnail_url   VARCHAR(512)    NOT NULL COMMENT '缩略图地址',
    pic_name        VARCHAR(256)    NOT NULL COMMENT '图片名称（展示）',
    pic_url         VARCHAR(512)    NOT NULL COMMENT '图片地址（展示, 压缩后）',
    pic_desc        VARCHAR(256)             DEFAULT NULL COMMENT '图片描述（展示）',
    category        BIGINT                   DEFAULT NULL COMMENT '分类 ID',
    tags            VARCHAR(256)    NULL COMMENT '标签（逗号分隔的标签 ID 列表）',
    user_id         BIGINT          NOT NULL COMMENT '创建用户 ID',
    space_id        BIGINT                   DEFAULT 0 NOT NULL COMMENT '所属空间 ID（0-表示公共空间）',
    review_status   TINYINT                  DEFAULT 0 NOT NULL COMMENT '审核状态（0-待审核, 1-通过, 2-拒绝）',
    review_message  VARCHAR(256)             DEFAULT NULL COMMENT '审核信息',
    reviewer_user   BIGINT                   DEFAULT NULL COMMENT '审核人 ID',
    review_time     DATETIME                 DEFAULT NULL COMMENT '审核时间',
    is_delete       TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_pic_name (pic_name),
    INDEX idx_pic_desc (pic_desc),
    INDEX idx_category (category),
    INDEX idx_tags (tags),
    INDEX idx_user_id (user_id),
    INDEX idx_space_id (space_id),
    INDEX idx_review_status (review_status),
    INDEX idx_reviewer_user (reviewer_user),
    INDEX idx_resource_status (resource_status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '图片表';

ALTER TABLE baolong_picture.picture
    ADD COLUMN view_quantity     INT     NOT NULL DEFAULT 0 COMMENT '查看数量' AFTER review_time,
    ADD COLUMN like_quantity     INT     NOT NULL DEFAULT 0 COMMENT '点赞数量' AFTER view_quantity,
    ADD COLUMN collect_quantity  INT     NOT NULL DEFAULT 0 COMMENT '收藏数量' AFTER like_quantity,
    ADD COLUMN download_quantity INT     NOT NULL DEFAULT 0 COMMENT '下载数量' AFTER collect_quantity,
    ADD COLUMN share_quantity    INT     NOT NULL DEFAULT 0 COMMENT '分享数量' AFTER download_quantity,
    ADD COLUMN is_share          TINYINT NOT NULL DEFAULT 0 COMMENT '是否分享（0-分享, 1-不分享）' AFTER share_quantity;

-- 图片交互表
CREATE TABLE IF NOT EXISTS picture_interaction
(
    user_id            BIGINT   NOT NULL COMMENT '用户 ID',
    picture_id         BIGINT   NOT NULL COMMENT '图片 ID',
    interaction_type   TINYINT  NOT NULL COMMENT '交互类型（0-点赞, 1-收藏）',
    interaction_status TINYINT  NOT NULL COMMENT '交互状态（0-存在, 1-取消）',
    is_delete          TINYINT  NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_id, picture_id, interaction_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '图片交互表';

-- 分类表
CREATE TABLE IF NOT EXISTS category
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(128)    NOT NULL COMMENT '名称',
    parent_id   BIGINT UNSIGNED          DEFAULT 0 COMMENT '父分类 ID（0-表示顶层分类）',
    use_num     INT                      DEFAULT 0 NOT NULL COMMENT '使用数量',
    user_id     BIGINT          NOT NULL COMMENT '创建用户 ID',
    is_delete   TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '分类表';

-- 空间表
CREATE TABLE IF NOT EXISTS space
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    space_name  VARCHAR(128)    NOT NULL COMMENT '空间名称',
    space_type  TINYINT         NOT NULL DEFAULT 0 COMMENT '空间类型（0-私有空间, 1-团队空间）',
    space_level TINYINT         NOT NULL DEFAULT 0 COMMENT '空间级别（0-普通版, 1-专业版, 2-旗舰版）',
    max_size    BIGINT          NOT NULL DEFAULT 0 COMMENT '空间图片最大大小（单位: KB）',
    max_count   BIGINT          NOT NULL DEFAULT 0 COMMENT '空间图片最大数量（单位: 张）',
    used_size   BIGINT          NOT NULL DEFAULT 0 COMMENT '空间使用大小（单位: KB）',
    used_count  BIGINT          NOT NULL DEFAULT 0 COMMENT '空间使用数量（单位: 张）',
    user_id     BIGINT          NOT NULL COMMENT '创建用户 ID',
    is_delete   TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_space_name (space_name),
    INDEX idx_user_id (user_id),
    INDEX idx_space_type (space_type),
    INDEX idx_space_level (space_level)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '空间表';

-- 空间用户表
CREATE TABLE IF NOT EXISTS space_user
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    space_id    BIGINT          NOT NULL COMMENT '空间 ID',
    user_id     BIGINT          NOT NULL COMMENT '用户 ID',
    space_role  VARCHAR(20)              DEFAULT 'viewer' NULL COMMENT '空间角色（admin-管理员, editor-编辑者, viewer-访问）',
    is_delete   TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_space_id_user_id (space_id, user_id),
    INDEX idx_spaceId (space_id),
    INDEX idx_userId (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '空间用户表';

-- 请求日志表
CREATE TABLE request_log
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    op_name         VARCHAR(100)             DEFAULT NULL COMMENT '操作名称',
    op_desc         VARCHAR(255)             DEFAULT NULL COMMENT '操作描述',
    req_path        VARCHAR(255)             DEFAULT NULL COMMENT '请求路径',
    req_method      VARCHAR(10)              DEFAULT NULL COMMENT '请求方式（GET, POST, PUT, DELETE, PATCH）',
    qualified_name  VARCHAR(255)             DEFAULT NULL COMMENT '全限定类名称',
    input_param     VARCHAR(1024)            DEFAULT NULL COMMENT '入参',
    output_param    TEXT                     DEFAULT NULL COMMENT '出参',
    error_msg       TEXT                     DEFAULT NULL COMMENT '异常信息',
    req_time        DATETIME                 DEFAULT NULL COMMENT '请求开始时间',
    resp_time       DATETIME                 DEFAULT NULL COMMENT '请求响应时间',
    cost_time       BIGINT                   DEFAULT NULL COMMENT '接口耗时（单位：ms）',
    req_status      INT                      DEFAULT NULL COMMENT '请求状态',
    log_level       VARCHAR(10)              DEFAULT NULL COMMENT '日志级别（INFO, ERROR）',
    user_id         BIGINT                   DEFAULT NULL COMMENT '发起请求的用户 ID（0-表示无需登录）',
    source          VARCHAR(50)              DEFAULT NULL COMMENT '日志来源',
    req_ip          VARCHAR(50)              DEFAULT NULL COMMENT '请求 IP 地址',
    device_type     VARCHAR(20)              DEFAULT NULL COMMENT '设备类型（PC、MOBILE）',
    os_type         VARCHAR(20)              DEFAULT NULL COMMENT '操作系统类型（WINDOWS, IOS, ANDROID）',
    os_version      VARCHAR(50)              DEFAULT NULL COMMENT '操作系统版本',
    browser_name    VARCHAR(50)              DEFAULT NULL COMMENT '浏览器名称',
    browser_version VARCHAR(50)              DEFAULT NULL COMMENT '浏览器版本',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_op_name (op_name),
    KEY idx_req_method (req_method),
    KEY idx_log_level (log_level),
    KEY idx_req_ip (req_ip),
    KEY idx_user_id (user_id),
    KEY idx_device_type (device_type),
    KEY idx_os_type (os_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '请求日志表';


-- 菜单表
CREATE TABLE menu
(
    id            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    menu_name     VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_position TINYINT     NOT NULL COMMENT '菜单位置（1-顶部, 2-左侧, 3-其他）',
    menu_path     VARCHAR(255)         DEFAULT NULL COMMENT '路由路径（vue-router path）',
    menu_type     TINYINT     NOT NULL COMMENT '类型（1-菜单, 2-按钮）',
    parent_id     BIGINT      NOT NULL DEFAULT 0 COMMENT '父菜单 ID',
    is_disabled   TINYINT     NOT NULL DEFAULT 0 COMMENT '是否禁用（0-正常, 1-禁用）',
    is_delete     TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '菜单表';
INSERT INTO `menu` VALUES (1, '公共图库', 1, '/', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-19 22:35:10');
INSERT INTO `menu` VALUES (2, '项目时序', 1, '/timeline', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-22 21:30:02');
INSERT INTO `menu` VALUES (3, '关于我', 1, '/about', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-19 22:35:10');
INSERT INTO `menu` VALUES (4, '发布列表', 1, '/picture/list', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-27 22:00:23');
INSERT INTO `menu` VALUES (5, '空间', 1, '/space', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-27 22:00:26');
INSERT INTO `menu` VALUES (6, '个人空间', 3, '/space/person', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-19 22:35:10');
INSERT INTO `menu` VALUES (7, '爬取图片', 1, '/picture/grab', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-27 22:00:27');
INSERT INTO `menu` VALUES (8, '团队空间', 1, '/groupSpace', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-27 22:00:29');
INSERT INTO `menu` VALUES (9, '管理员', 2, 'grp', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-22 02:24:27');
INSERT INTO `menu` VALUES (10, '用户管理', 2, '/admin/userManage', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-22 02:24:27');
INSERT INTO `menu` VALUES (11, '图库管理', 2, '/admin/pictureManage', 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-22 02:24:27');
INSERT INTO `menu` VALUES (12, '空间管理', 2, '/admin/spaceManage', 0, 0, 0, '2025-03-19 22:07:39', '2025-03-19 22:07:39', '2025-03-22 02:24:27');
INSERT INTO `menu` VALUES (13, '分类管理', 2, '/admin/categoryManage', 0, 0, 0, '2025-03-19 22:07:56', '2025-03-19 22:07:56', '2025-03-22 02:24:27');
INSERT INTO `menu` VALUES (14, '发布图片', 3, '/picture/addEdit', 0, 0, 0, '2025-03-19 22:31:37', '2025-03-19 22:31:37', '2025-03-22 02:24:27');
INSERT INTO `menu` VALUES (15, '个人信息', 3, '/user/profile', 0, 0, 0, '2025-03-22 02:24:16', '2025-03-22 02:24:16', '2025-03-22 02:24:27');
INSERT INTO `menu` VALUES (16, '定时任务', 2, '/admin/scheduledTask', 0, 0, 0, '2025-03-22 16:19:48', '2025-03-22 16:19:48', '2025-03-22 16:19:48');
INSERT INTO `menu` VALUES (17, '菜单管理', 2, '/admin/menuManage', 0, 0, 0, '2025-03-22 20:01:35', '2025-03-22 20:01:35', '2025-03-23 16:16:18');
INSERT INTO `menu` VALUES (18, '以图搜图', 3, '/picture/search', 0, 0, 0, '2025-03-22 21:03:31', '2025-03-22 21:03:31', '2025-03-23 12:24:35');
INSERT INTO `menu` VALUES (19, 'AI 扩图', 1, '/picture/expand', 0, 0, 0, '2025-03-26 22:38:25', '2025-03-26 22:38:25', '2025-03-27 22:03:37');
INSERT INTO `menu` VALUES (20, '建议反馈', 1, '/feedback', 0, 0, 0, '2025-03-30 16:47:43', '2025-03-30 16:47:43', '2025-03-30 16:47:43');
INSERT INTO `menu` VALUES (21, '批量发布图片', 3, '/picture/batch', 0, 0, 0, '2025-04-08 21:24:03', '2025-04-08 21:24:03', '2025-04-08 21:24:03');
INSERT INTO `menu` VALUES (22, '图片扩展', 1, '/extended', 0, 0, 0, '2025-04-08 21:24:11', '2025-04-08 21:24:11', '2025-04-08 21:24:11');
INSERT INTO `menu` VALUES (23, '团队空间', 1, '/space/team', 0, 0, 0, '2025-04-08 21:24:21', '2025-04-08 21:24:21', '2025-04-08 21:24:21');
INSERT INTO `menu` VALUES (24, '文本生图', 1, '/picture/textGenerate', 0, 0, 0, '2025-04-12 00:41:54', '2025-04-12 00:41:54', '2025-04-12 00:41:54');

-- 角色-菜单关联表
CREATE TABLE role_menu
(
    role_key VARCHAR(20) NOT NULL,
    menu_id  BIGINT      NOT NULL,
    PRIMARY KEY (role_key, menu_id)
) COMMENT = '角色菜单关联表';
INSERT INTO `role_menu` VALUES ('ADMIN', 1);
INSERT INTO `role_menu` VALUES ('ADMIN', 2);
INSERT INTO `role_menu` VALUES ('ADMIN', 3);
INSERT INTO `role_menu` VALUES ('ADMIN', 4);
INSERT INTO `role_menu` VALUES ('ADMIN', 5);
INSERT INTO `role_menu` VALUES ('ADMIN', 6);
INSERT INTO `role_menu` VALUES ('ADMIN', 7);
INSERT INTO `role_menu` VALUES ('ADMIN', 8);
INSERT INTO `role_menu` VALUES ('ADMIN', 9);
INSERT INTO `role_menu` VALUES ('ADMIN', 10);
INSERT INTO `role_menu` VALUES ('ADMIN', 11);
INSERT INTO `role_menu` VALUES ('ADMIN', 12);
INSERT INTO `role_menu` VALUES ('ADMIN', 13);
INSERT INTO `role_menu` VALUES ('ADMIN', 14);
INSERT INTO `role_menu` VALUES ('ADMIN', 15);
INSERT INTO `role_menu` VALUES ('ADMIN', 16);
INSERT INTO `role_menu` VALUES ('ADMIN', 17);
INSERT INTO `role_menu` VALUES ('ADMIN', 18);
INSERT INTO `role_menu` VALUES ('ADMIN', 19);
INSERT INTO `role_menu` VALUES ('ADMIN', 20);
INSERT INTO `role_menu` VALUES ('ADMIN', 21);
INSERT INTO `role_menu` VALUES ('ADMIN', 22);
INSERT INTO `role_menu` VALUES ('ADMIN', 23);
INSERT INTO `role_menu` VALUES ('ADMIN', 24);
INSERT INTO `role_menu` VALUES ('USER', 1);
INSERT INTO `role_menu` VALUES ('USER', 2);
INSERT INTO `role_menu` VALUES ('USER', 3);
INSERT INTO `role_menu` VALUES ('USER', 4);
INSERT INTO `role_menu` VALUES ('USER', 5);
INSERT INTO `role_menu` VALUES ('USER', 6);
INSERT INTO `role_menu` VALUES ('USER', 7);
INSERT INTO `role_menu` VALUES ('USER', 14);
INSERT INTO `role_menu` VALUES ('USER', 15);
INSERT INTO `role_menu` VALUES ('USER', 18);
INSERT INTO `role_menu` VALUES ('USER', 19);
INSERT INTO `role_menu` VALUES ('USER', 20);
INSERT INTO `role_menu` VALUES ('USER', 21);
INSERT INTO `role_menu` VALUES ('USER', 22);
INSERT INTO `role_menu` VALUES ('USER', 23);
INSERT INTO `role_menu` VALUES ('USER', 24);


-- 定时任务表
CREATE TABLE IF NOT EXISTS scheduled_task
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    job_key     VARCHAR(255)    NOT NULL COMMENT '任务 KEY（存在内存中）',
    job_name    VARCHAR(255)    NOT NULL COMMENT '任务名称',
    job_cron    VARCHAR(255)    NOT NULL COMMENT '任务 corn 表达式',
    job_desc    VARCHAR(255)    NULL     DEFAULT NULL COMMENT '任务描述',
    job_bean    VARCHAR(255)    NOT NULL COMMENT '任务 Bean 名称（执行任务的 bean）',
    job_status  TINYINT(4)      NOT NULL DEFAULT 0 COMMENT '任务状态（0-关闭, 1-开启）',
    is_delete   TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务表';
