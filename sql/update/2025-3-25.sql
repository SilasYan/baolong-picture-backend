ALTER TABLE baolong_picture.picture
    ADD COLUMN expand_status TINYINT NOT NULL DEFAULT 0 COMMENT '扩图状态（0-普通图片, 1-扩图图片, 2-扩图成功后的图片）' AFTER is_share;
