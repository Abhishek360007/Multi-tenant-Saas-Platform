/*
 Navicat Premium Data Transfer

 Source Server         : my-mysql
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : tenanthub_master

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 15/12/2022 10:30:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Records of sys_config
-- ----------------------------

INSERT INTO `sys_config` VALUES (10001, 'User management: initial account password', 'default_user_password', '123456', 1, '2021-07-14 11:48:16', 19980817, '2021-07-14 11:48:16', 19980817);
INSERT INTO `sys_config` VALUES (10002, 'Default tenant logo URL', 'default_tenant_logo', 'https://raw.githubusercontent.com/Abhishek360007/Multi-tenant-Saas-Platform/main/docs/brand/tenanthub-logo.png', 1, '2021-07-16 14:28:41', 19980817, '2022-09-19 14:47:26', 19980817);
INSERT INTO `sys_config` VALUES (10003, 'Tenant default role name', 'default_role_name', 'Operator', 1, '2021-07-16 17:26:56', 19980817, '2021-07-16 17:27:06', 19980817);
INSERT INTO `sys_config` VALUES (10004, 'Public holidays API', 'get_holiday_url', 'https://timor.tech/api/holiday/year/%s?type=N&week=Y', 1, '2022-05-16 11:50:20', 19980817, '2022-05-16 11:50:25', 19980817);


SET FOREIGN_KEY_CHECKS = 1;
