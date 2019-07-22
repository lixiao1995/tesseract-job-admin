DROP TABLE IF EXISTS `tesseract_btn_resource`;
CREATE TABLE `tesseract_btn_resource`
(
    `id`               int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `btn_name`         varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '按钮名称',
    `menu_id`          int(11)                                                 NULL DEFAULT NULL COMMENT '所属菜单ID',
    `menu_name`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '父菜单名',
    `menu_path`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父菜单路径',
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

INSERT INTO `tesseract_btn_resource`( id, btn_name, menu_id, menu_name, menu_path, create_user_id, create_user_name
                                    , update_user_id
                                    , update_user_name, status, create_time, update_time)
VALUES (1, 'add', 1, '添加', '/permission/add', 2, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tesseract_btn_resource`
VALUES (2, 'edit', 1, '编辑', '/permission/edit', 2, NULL, NULL, NULL, NULL, NULL, NULL);


DROP TABLE IF EXISTS `tesseract_menu_resource`;
CREATE TABLE `tesseract_menu_resource`
(
    `id`               int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    `name`             varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT '' COMMENT '菜单名称',
    `parent_id`        int(11)                                                 NOT NULL DEFAULT 0 COMMENT '父级菜单ID',
    `parent_name`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '菜单的名字',
    `redirect`         varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '默认转发路由',
    `path`             varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '菜单路由地址',

    `meta_icon`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '菜单的图标',
    `meta_title`       varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '路由标题',
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


INSERT INTO `tesseract_menu_resource`( id, name, parent_id, parent_name, redirect, path, meta_icon
                                     , meta_title
                                     , meta_cache, menu_desc
                                     , menu_order, create_user_id, create_user_name, update_user_id, update_user_name
                                     , status, create_time, update_time, del_flag)
VALUES (1, 'dashboard', 0, '', '', '/dashboard/index', '', 'documentation', 0, '控制板', 1, 1, 'admin', 1, 'admin',
        0, NULL,
        NULL, 0);



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


INSERT INTO `tesseract_role`(id, role_name, role_desc, create_user_id, create_user_name, update_user_id,
                             update_user_name, del_flag, status, create_time, update_time)
VALUES (1, 'admin', '超级管理员', 1, 'admin', 1, 'admin', 0, 0, NULL, NULL);
INSERT INTO `tesseract_role`(id, role_name, role_desc, create_user_id, create_user_name, update_user_id,
                             update_user_name, del_flag, status, create_time, update_time)
VALUES (2, 'manager', '经理', 1, 'admin', 1, 'admin', 0, 0, NULL, NULL);


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


INSERT INTO `tesseract_user_role`
VALUES (1, 1, 1);

