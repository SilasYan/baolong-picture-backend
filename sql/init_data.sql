
INSERT INTO menu
VALUES (1, '公共图库', 1, '/', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-19 22:35:10');
INSERT INTO menu
VALUES (2, '项目时序', 1, '/timeline', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-22 21:30:02');
INSERT INTO menu
VALUES (3, '关于我', 1, '/about', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-19 22:35:10');
INSERT INTO menu
VALUES (4, '发布列表', 1, '/picture/list', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46',
        '2025-03-27 22:00:23');
INSERT INTO menu
VALUES (5, '空间', 1, '/space', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-27 22:00:26');
INSERT INTO menu
VALUES (6, '个人空间', 3, '/space/person', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46',
        '2025-03-19 22:35:10');
INSERT INTO menu
VALUES (7, '爬取图片', 1, '/picture/grab', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46',
        '2025-03-27 22:00:27');
INSERT INTO menu
VALUES (8, '团队空间', 1, '/groupSpace', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46',
        '2025-03-27 22:00:29');
INSERT INTO menu
VALUES (9, '管理员', 2, 'grp', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46', '2025-03-22 02:24:27');
INSERT INTO menu
VALUES (10, '用户管理', 2, '/admin/userManage', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46',
        '2025-03-22 02:24:27');
INSERT INTO menu
VALUES (11, '图库管理', 2, '/admin/pictureManage', 0, 0, 0, 0, '2025-03-19 20:19:46', '2025-03-19 20:19:46',
        '2025-03-22 02:24:27');
INSERT INTO menu
VALUES (12, '空间管理', 2, '/admin/spaceManage', 0, 0, 0, 0, '2025-03-19 22:07:39', '2025-03-19 22:07:39',
        '2025-03-22 02:24:27');
INSERT INTO menu
VALUES (13, '分类管理', 2, '/admin/categoryManage', 0, 0, 0, 0, '2025-03-19 22:07:56', '2025-03-19 22:07:56',
        '2025-03-22 02:24:27');
INSERT INTO menu
VALUES (14, '发布图片', 3, '/picture/addEdit', 0, 0, 0, 0, '2025-03-19 22:31:37', '2025-03-19 22:31:37',
        '2025-03-22 02:24:27');
INSERT INTO menu
VALUES (15, '个人信息', 3, '/user/profile', 0, 0, 0, 0, '2025-03-22 02:24:16', '2025-03-22 02:24:16',
        '2025-03-22 02:24:27');
INSERT INTO menu
VALUES (16, '定时任务', 2, '/admin/scheduledTask', 0, 0, 0, 0, '2025-03-22 16:19:48', '2025-03-22 16:19:48',
        '2025-03-22 16:19:48');
INSERT INTO menu
VALUES (17, '菜单管理', 2, '/admin/menuManage', 0, 0, 0, 0, '2025-03-22 20:01:35', '2025-03-22 20:01:35',
        '2025-03-23 16:16:18');
INSERT INTO menu
VALUES (18, '以图搜图', 3, '/picture/search', 0, 0, 0, 0, '2025-03-22 21:03:31', '2025-03-22 21:03:31',
        '2025-03-23 12:24:35');
INSERT INTO menu
VALUES (19, 'AI 扩图', 1, '/picture/expand', 0, 0, 0, 0, '2025-03-26 22:38:25', '2025-03-26 22:38:25',
        '2025-03-27 22:03:37');
INSERT INTO menu
VALUES (20, '建议反馈', 1, '/feedback', 0, 0, 0, 0, '2025-03-30 16:47:43', '2025-03-30 16:47:43',
        '2025-03-30 16:47:43');
INSERT INTO menu
VALUES (21, '批量发布图片', 3, '/picture/batch', 0, 0, 0, 0, '2025-04-08 21:24:03', '2025-04-08 21:24:03',
        '2025-04-08 21:24:03');
INSERT INTO menu
VALUES (22, '图片扩展', 1, '/extended', 0, 0, 0, 0, '2025-04-08 21:24:11', '2025-04-08 21:24:11',
        '2025-04-08 21:24:11');
INSERT INTO menu
VALUES (23, '团队空间', 1, '/space/team', 0, 0, 0, 0, '2025-04-08 21:24:21', '2025-04-08 21:24:21',
        '2025-04-08 21:24:21');
INSERT INTO menu
VALUES (24, '文本生图', 1, '/picture/textGenerate', 0, 0, 0, 0, '2025-04-12 00:41:54', '2025-04-12 00:41:54',
        '2025-04-12 00:41:54');


INSERT INTO role_menu
VALUES ('ADMIN', 1);
INSERT INTO role_menu
VALUES ('ADMIN', 2);
INSERT INTO role_menu
VALUES ('ADMIN', 3);
INSERT INTO role_menu
VALUES ('ADMIN', 4);
INSERT INTO role_menu
VALUES ('ADMIN', 5);
INSERT INTO role_menu
VALUES ('ADMIN', 6);
INSERT INTO role_menu
VALUES ('ADMIN', 7);
INSERT INTO role_menu
VALUES ('ADMIN', 8);
INSERT INTO role_menu
VALUES ('ADMIN', 9);
INSERT INTO role_menu
VALUES ('ADMIN', 10);
INSERT INTO role_menu
VALUES ('ADMIN', 11);
INSERT INTO role_menu
VALUES ('ADMIN', 12);
INSERT INTO role_menu
VALUES ('ADMIN', 13);
INSERT INTO role_menu
VALUES ('ADMIN', 14);
INSERT INTO role_menu
VALUES ('ADMIN', 15);
INSERT INTO role_menu
VALUES ('ADMIN', 16);
INSERT INTO role_menu
VALUES ('ADMIN', 17);
INSERT INTO role_menu
VALUES ('ADMIN', 18);
INSERT INTO role_menu
VALUES ('ADMIN', 19);
INSERT INTO role_menu
VALUES ('ADMIN', 20);
INSERT INTO role_menu
VALUES ('ADMIN', 21);
INSERT INTO role_menu
VALUES ('ADMIN', 22);
INSERT INTO role_menu
VALUES ('ADMIN', 23);
INSERT INTO role_menu
VALUES ('ADMIN', 24);
INSERT INTO role_menu
VALUES ('USER', 1);
INSERT INTO role_menu
VALUES ('USER', 2);
INSERT INTO role_menu
VALUES ('USER', 3);
INSERT INTO role_menu
VALUES ('USER', 4);
INSERT INTO role_menu
VALUES ('USER', 5);
INSERT INTO role_menu
VALUES ('USER', 6);
INSERT INTO role_menu
VALUES ('USER', 7);
INSERT INTO role_menu
VALUES ('USER', 14);
INSERT INTO role_menu
VALUES ('USER', 15);
INSERT INTO role_menu
VALUES ('USER', 18);
INSERT INTO role_menu
VALUES ('USER', 19);
INSERT INTO role_menu
VALUES ('USER', 20);
INSERT INTO role_menu
VALUES ('USER', 21);
INSERT INTO role_menu
VALUES ('USER', 22);
INSERT INTO role_menu
VALUES ('USER', 23);
INSERT INTO role_menu
VALUES ('USER', 24);
