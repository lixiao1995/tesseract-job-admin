DROP TABLE IF EXISTS `tesseract_btn_resource`;
CREATE TABLE `tesseract_btn_resource`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `btn_code` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '按钮编码（用于界面配置）',
  `btn_auth_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '按钮权限标志',
  `btn_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '按钮名称',
  `menu_id` int(11) NULL DEFAULT NULL COMMENT '所属菜单ID',
  `create_user_id` int(11) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `update_user_id` int(11) NULL DEFAULT NULL COMMENT '更新人ID',
  `update_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人姓名',
  `is_del` tinyint(4) NULL DEFAULT NULL COMMENT '是否删除，0-未删除，1-删除',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '状态码，保留字段',
  `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `tesseract_btn_resource` VALUES (1, 'add', '/permission/add', '添加', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tesseract_btn_resource` VALUES (2, 'edit', '/permission/edit', '编辑', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


DROP TABLE IF EXISTS `tesseract_menu_resource`;
CREATE TABLE `tesseract_menu_resource`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
  `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT '父级菜单ID',
  `redirect` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认转发路由',
  `path` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单路由地址',
  `url_pattern` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '路径匹配模式,保留字段',
  `icon` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单的图标',
  `level` tinyint(255) NULL DEFAULT NULL COMMENT '菜单级别，1-一级菜单',
  `desc` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '菜单描述',
  `order` mediumint(9) NULL DEFAULT NULL COMMENT '菜单顺序',
  `create_user_id` int(11) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `update_user_id` int(11) NULL DEFAULT NULL COMMENT '更新人ID',
  `update_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人姓名',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '状态码，保留字段',
  `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(4) NULL DEFAULT NULL COMMENT '是否删除，0-未删除，1-删除',
  `always_show_flag` tinyint(4) NULL DEFAULT NULL COMMENT '是否一直显示，即使没有子菜单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


INSERT INTO `tesseract_menu_resource` VALUES (1, 'Permission', 0, '/permission/page', 'permission', '', 'lock', 1, '', 5, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (2, 'PagePermission', 1, NULL, 'page', '', 'example', 2, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (3, 'DirectivePermission', 1, NULL, 'directive', '', 'example', 2, '', 2, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (4, 'RolePermission', 1, NULL, 'role', '', 'example', 2, '', 3, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (5, 'Icon', 0, NULL, 'icons', '', NULL, 1, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (6, 'Icons', 5, NULL, 'index', '', NULL, 2, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (7, 'Example', 0, '/example/list', 'example', '', 'example', 1, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (8, 'CreateArticle', 7, NULL, 'create', '', 'edit', 2, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (9, 'EditArticle', 7, NULL, 'edit', '', 'edit', 2, '', 2, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (10, 'ArticleList', 7, NULL, 'list', '', 'list', 2, '', 3, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (11, '触发器', 0, '/trigger/index', 'trigger', '', 'example', 1, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (12, '触发器列表', 11, '', 'index', '', 'documentation', 2, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (13, '用户', 0, '/user/index', 'user', '', 'example', 1, '', 2, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (14, '用户列表', 13, NULL, 'index', '', 'documentation', 2, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (15, '日志', 0, '/log/index', 'log', '', 'example', 1, '', 4, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (16, '日志列表', 15, NULL, 'index', '', 'documentation', 2, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (17, '执行器', 0, '/executor/index', 'executor', '', 'example', 1, '', 3, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);
INSERT INTO `tesseract_menu_resource` VALUES (18, '执行器列表', 17, NULL, 'index', '', 'documentation', 2, '', 1, 1, 'admin', 1, 'admin', 0, NULL, NULL, 0, 1);


DROP TABLE IF EXISTS `tesseract_role`;
CREATE TABLE `tesseract_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `role_code` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `role_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_desc` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_user_id` int(11) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `update_user_id` int(11) NULL DEFAULT NULL COMMENT '更新人ID',
  `update_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人姓名',
  `is_del` tinyint(4) NULL DEFAULT NULL COMMENT '是否删除，0-未删除，1-删除',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '状态码，保留字段',
  `create_time` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


INSERT INTO `tesseract_role` VALUES (1, 'admin', 'admin123', '超级管理员', 1, 'admin', 1, 'admin', 0, 0, NULL, NULL);
INSERT INTO `tesseract_role` VALUES (2, NULL, 'manager', '经理', 1, 'admin', 1, 'admin', 0, 0, NULL, NULL);


DROP TABLE IF EXISTS `tesseract_role_resources`;
CREATE TABLE `tesseract_role_resources`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  `menu_parent_id` int(11) NOT NULL COMMENT '父级菜单ID',
  `btn_id` int(11) NULL DEFAULT NULL COMMENT '按钮ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


INSERT INTO `tesseract_role_resources` VALUES (1, 1, 2, 1, 1);
INSERT INTO `tesseract_role_resources` VALUES (2, 1, 2, 1, 2);
INSERT INTO `tesseract_role_resources` VALUES (3, 1, 1, 0, NULL);
INSERT INTO `tesseract_role_resources` VALUES (4, 1, 3, 1, 1);
INSERT INTO `tesseract_role_resources` VALUES (5, 1, 4, 1, NULL);
INSERT INTO `tesseract_role_resources` VALUES (6, 1, 5, 0, NULL);
INSERT INTO `tesseract_role_resources` VALUES (7, 1, 6, 5, NULL);
INSERT INTO `tesseract_role_resources` VALUES (8, 1, 7, 0, NULL);
INSERT INTO `tesseract_role_resources` VALUES (9, 1, 8, 7, NULL);
INSERT INTO `tesseract_role_resources` VALUES (10, 1, 9, 7, NULL);
INSERT INTO `tesseract_role_resources` VALUES (11, 1, 10, 7, NULL);
INSERT INTO `tesseract_role_resources` VALUES (12, 1, 11, 0, NULL);
INSERT INTO `tesseract_role_resources` VALUES (13, 1, 12, 11, NULL);
INSERT INTO `tesseract_role_resources` VALUES (14, 1, 13, 0, NULL);
INSERT INTO `tesseract_role_resources` VALUES (15, 1, 14, 13, NULL);
INSERT INTO `tesseract_role_resources` VALUES (16, 1, 15, 0, NULL);
INSERT INTO `tesseract_role_resources` VALUES (17, 1, 16, 15, NULL);
INSERT INTO `tesseract_role_resources` VALUES (18, 1, 17, 0, NULL);
INSERT INTO `tesseract_role_resources` VALUES (19, 1, 18, 17, NULL);

DROP TABLE IF EXISTS `tesseract_user_role`;
CREATE TABLE `tesseract_user_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


INSERT INTO `tesseract_user_role` VALUES (1, 1, 1);

