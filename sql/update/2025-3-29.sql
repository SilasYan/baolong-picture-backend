ALTER TABLE picture
    ADD COLUMN recommend_score DECIMAL(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '推荐综合得分';


-- 推荐查询核心索引
ALTER TABLE picture
    ADD INDEX idx_recommend (review_status, expand_status, recommend_score DESC, create_time DESC);
