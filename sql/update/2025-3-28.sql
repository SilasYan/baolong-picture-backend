-- 用户反馈表
CREATE TABLE feedback
(
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    feedback_type   TINYINT      NOT NULL COMMENT '反馈类型（0-使用体验, 1-功能建议, 2-BUG错误, 3-其他）',
    content         TEXT         NOT NULL COMMENT '反馈内容',
    contact_type    TINYINT      NOT NULL COMMENT '联系方式（0-QQ, 1-微信, 2-邮箱）',
    contact_info    VARCHAR(100) NOT NULL COMMENT '联系方式内容',
    process_status  TINYINT      NOT NULL DEFAULT 0 COMMENT '处理状态（0-待处理, 1-处理中, 2-完成, 3-拒绝）',
    process_content TEXT                  DEFAULT NULL COMMENT '处理内容',
    is_delete       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除（0-正常, 1-删除）',
    edit_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_feedback_type (feedback_type),
    INDEX idx_process_status (process_status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户反馈表';
