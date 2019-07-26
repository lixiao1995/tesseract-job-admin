DROP TABLE IF EXISTS `tesseract_btn_resource`;
CREATE TABLE `tesseract_btn_resource`
(
    `id`               int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `btn_code`         varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '按钮code',
    `btn_name`         varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '按钮名称',
    `create_user_id`   int(11)                                                 NULL DEFAULT NULL COMMENT '创建人ID',
    `create_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '创建人姓名',
    `update_user_id`   int(11)                                                 NULL DEFAULT NULL COMMENT '更新人ID',
    `update_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '更新人姓名',
    `status`           tinyint(4)                                              NULL DEFAULT NULL COMMENT '状态码，保留字段',
    `create_time`      bigint(20)                                              NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`      bigint(20)                                              NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `tesseract_menu_resource`;
CREATE TABLE `tesseract_menu_resource`
(
    `id`               int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `code`             varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL   COMMENT '菜单唯一code',
    `name`             varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '命名路由',
    `parent_id`        int(11)                                                 NOT NULL DEFAULT 0 COMMENT '父级菜单ID',
    `parent_name`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '菜单的名字',
    `redirect`         varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '默认转发路由',
    `path`             varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '路由路径',
    `full_path`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '资源路径',
    `meta_icon`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '菜单的图标',
    `meta_title`       varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '展示名称',
    `meta_cache`       tinyint                                                 NOT NULL COMMENT '是否缓存',

    `menu_desc`        varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '菜单描述',
    `menu_order`       mediumint(9)                                            NULL     DEFAULT NULL COMMENT '菜单顺序',
    `create_user_id`   int(11)                                                 NULL     DEFAULT NULL COMMENT '创建人ID',
    `create_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '创建人姓名',
    `update_user_id`   int(11)                                                 NULL     DEFAULT NULL COMMENT '更新人ID',
    `update_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '更新人姓名',
    `status`           tinyint(4)                                              NULL     DEFAULT NULL COMMENT '状态码，保留字段',
    `create_time`      bigint(20)                                              NULL     DEFAULT NULL COMMENT '创建时间',
    `update_time`      bigint(20)                                              NULL     DEFAULT NULL COMMENT '更新时间',
    `del_flag`         tinyint(4)                                              NULL     DEFAULT NULL COMMENT '是否删除，0-未删除，1-删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `tesseract_role`;
CREATE TABLE `tesseract_role`
(
    `id`               int(11)                                                NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `role_name`        varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
    `role_desc`        varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色描述',

    `create_user_id`   int(11)                                                NULL DEFAULT NULL COMMENT '创建人ID',
    `create_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
    `update_user_id`   int(11)                                                NULL DEFAULT NULL COMMENT '更新人ID',
    `update_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人姓名',
    `del_flag`         tinyint(4)                                             NULL DEFAULT NULL COMMENT '是否删除，0-未删除，1-删除',
    `status`           tinyint(4)                                             NULL DEFAULT NULL COMMENT '状态码，保留字段',
    `create_time`      bigint(20)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`      bigint(20)                                             NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;



DROP TABLE IF EXISTS `tesseract_role_resources`;
CREATE TABLE `tesseract_role_resources`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `role_id` int(11) NOT NULL COMMENT '角色ID',
    `menu_id` int(11) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `tesseract_role_btn`;
CREATE TABLE `tesseract_role_btn`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `role_id` int(11) NOT NULL COMMENT '角色ID',
    `btn_id`  int(11) NOT NULL COMMENT '按钮ID',
    `menu_id` int(11) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;



DROP TABLE IF EXISTS `tesseract_user_role`;
CREATE TABLE `tesseract_user_role`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `user_id` int(11) NOT NULL COMMENT '用户ID',
    `role_id` int(11) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

# 用户角色关联表
INSERT INTO `tesseract_user_role`
VALUES (1, 1, 1);

# 角色
INSERT INTO `tesseract_role`(id, role_name, role_desc, create_user_id, create_user_name, update_user_id,
                             update_user_name, del_flag, status, create_time, update_time)
VALUES (1, 'admin', '超级管理员', 1, 'admin', 1, 'admin', 0, 0, NULL, NULL);

# 菜单
INSERT INTO `tesseract_menu_resource`
VALUES (1, 'dashboard', 0, '', '', '/dashboard/index', '/dashboard/index', 'documentation', '控制板', 0, '', 1, 1, 'admin',
        1, 'admin', 0, NULL, 1563606315098, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (2, 'executor', 0, '', '', '/executor/index', '/executor/index', 'documentation', '执行器列表', 0, '', 2, 1, 'admin',
        1, 'admin', 0, NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (3, 'log', 0, '', '', '/log/index', '/log/index', 'documentation', '日志列表', 0, '', 3, 1, 'admin', 1, 'admin', 0,
        NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (4, 'group', 6, '权限管理', '', '/permission/group/index', '/permission/group/index', 'documentation',
        '用户组管理', 0, '', 6, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (5, 'trigger', 0, '', '', '/trigger/index', '/trigger/index', 'documentation', '触发器列表', 0, '', 4, 1, 'admin', 1,
        'admin', 0, NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (6, 'permission', 0, '', '', '/permission', '/permission', 'lock', '权限管理', 0, '', 5, 1, 'admin', 1,
        'admin', 0, NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (7, 'menu', 6, '权限管理', '', '/permission/menuResource/index', '/permission/menuResource/index', 'lock',
        '菜单管理', 0, '', 7, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (8, 'role', 6, '权限管理', '', '/permission/role/index', '/permission/role/index', 'lock', '角色管理', 0, '', 8,
        1, 'admin', 1, 'admin', 0, NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (9, 'btn', 6, '权限管理', '', '/permission/btn/index', '/permission/btn/index', 'lock', '按钮管理', 0, '', 9, 1,
        'admin', 1, 'admin', 0, NULL, NULL, 0);
INSERT INTO `tesseract_menu_resource`
VALUES (10, 'user', 6, '权限管理', '', '/permission/user/index', '/permission/user/index', 'documentation', '用户管理',
        0, '', 10, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0);

# 角色菜单
INSERT INTO `tesseract_role_resources`
VALUES (1, 1, 1);
INSERT INTO `tesseract_role_resources`
VALUES (2, 1, 2);
INSERT INTO `tesseract_role_resources`
VALUES (3, 1, 3);
INSERT INTO `tesseract_role_resources`
VALUES (4, 1, 4);
INSERT INTO `tesseract_role_resources`
VALUES (5, 1, 5);
INSERT INTO `tesseract_role_resources`
VALUES (6, 1, 6);
INSERT INTO `tesseract_role_resources`
VALUES (7, 1, 7);
INSERT INTO `tesseract_role_resources`
VALUES (8, 1, 8);
INSERT INTO `tesseract_role_resources`
VALUES (9, 1, 9);
INSERT INTO `tesseract_role_resources`
VALUES (10, 1, 10);

# 按钮
INSERT INTO `tesseract_btn_resource`
VALUES (1, 'add', 2, '执行器列表', '/executor/index', 1, 'admin', 1, 'admin', NULL, 1563608954787, 1563608954787);
INSERT INTO `tesseract_btn_resource`
VALUES (2, 'edit', 2, '执行器列表', '/executor/index', 1, 'admin', 1, 'admin', NULL, 1563609045332, 1563609045332);
INSERT INTO `tesseract_btn_resource`
VALUES (3, 'delete', 2, '执行器列表', '/executor/index', 1, 'admin', 1, 'admin', NULL, 1563609177143, 1563609177143);
INSERT INTO `tesseract_btn_resource`
VALUES (4, 'select', 6, '触发器列表', '/trigger/index', 1, 'admin', 1, 'admin', NULL, 1563609314272, 1563609314272);

# 角色按钮

INSERT INTO `tesseract_role_btn`
VALUES (1, 1, 1, 1);
INSERT INTO `tesseract_role_btn`
VALUES (2, 1, 2, 1);
INSERT INTO `tesseract_role_btn`
VALUES (3, 1, 3, 1);
INSERT INTO `tesseract_role_btn`
VALUES (4, 1, 4, 1);

INSERT INTO `tesseract_role_btn`
VALUES (5, 1, 1, 2);
INSERT INTO `tesseract_role_btn`
VALUES (6, 1, 2, 2);
INSERT INTO `tesseract_role_btn`
VALUES (7, 1, 3, 2);
INSERT INTO `tesseract_role_btn`
VALUES (8, 1, 4, 2);

INSERT INTO `tesseract_role_btn`
VALUES (9, 1, 1, 3);
INSERT INTO `tesseract_role_btn`
VALUES (10, 1, 2, 3);
INSERT INTO `tesseract_role_btn`
VALUES (11, 1, 3, 3);
INSERT INTO `tesseract_role_btn`
VALUES (12, 1, 4, 3);

INSERT INTO `tesseract_role_btn`
VALUES (13, 1, 1, 4);
INSERT INTO `tesseract_role_btn`
VALUES (14, 1, 2, 4);
INSERT INTO `tesseract_role_btn`
VALUES (15, 1, 3, 4);
INSERT INTO `tesseract_role_btn`
VALUES (16, 1, 4, 4);


INSERT INTO `tesseract_role_btn`
VALUES (17, 1, 1, 5);
INSERT INTO `tesseract_role_btn`
VALUES (18, 1, 2, 5);
INSERT INTO `tesseract_role_btn`
VALUES (19, 1, 3, 5);
INSERT INTO `tesseract_role_btn`
VALUES (20, 1, 4, 5);

INSERT INTO `tesseract_role_btn`
VALUES (21, 1, 1, 6);
INSERT INTO `tesseract_role_btn`
VALUES (22, 1, 2, 6);
INSERT INTO `tesseract_role_btn`
VALUES (23, 1, 3, 6);
INSERT INTO `tesseract_role_btn`
VALUES (24, 1, 4, 6);

INSERT INTO `tesseract_role_btn`
VALUES (25, 1, 1, 7);
INSERT INTO `tesseract_role_btn`
VALUES (26, 1, 2, 7);
INSERT INTO `tesseract_role_btn`
VALUES (27, 1, 3, 7);
INSERT INTO `tesseract_role_btn`
VALUES (28, 1, 4, 7);


INSERT INTO `tesseract_role_btn`
VALUES (29, 1, 1, 8);
INSERT INTO `tesseract_role_btn`
VALUES (30, 1, 2, 8);
INSERT INTO `tesseract_role_btn`
VALUES (31, 1, 3, 8);
INSERT INTO `tesseract_role_btn`
VALUES (32, 1, 4, 8);

INSERT INTO `tesseract_role_btn`
VALUES (33, 1, 1, 9);
INSERT INTO `tesseract_role_btn`
VALUES (34, 1, 2, 9);
INSERT INTO `tesseract_role_btn`
VALUES (35, 1, 3, 9);
INSERT INTO `tesseract_role_btn`
VALUES (36, 1, 4, 9);


INSERT INTO `tesseract_role_btn`
VALUES (37, 1, 1, 10);
INSERT INTO `tesseract_role_btn`
VALUES (38, 1, 2, 10);
INSERT INTO `tesseract_role_btn`
VALUES (39, 1, 3, 10);
INSERT INTO `tesseract_role_btn`
VALUES (40, 1, 4, 10);

