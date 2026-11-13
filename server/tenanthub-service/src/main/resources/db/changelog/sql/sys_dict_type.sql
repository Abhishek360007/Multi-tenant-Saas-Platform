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

 Date: 15/12/2022 10:30:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` (`type_id`, `type_name`, `type_key`, `description`, `create_time`, `create_user`, `last_update_time`, `last_update_user`) 
VALUES 
(10001, 'User gender', 'sys_user_sex', 'User gender list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10002, 'Menu status', 'sys_show_hide', 'Menu status list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10003, 'System switch', 'sys_normal_disable', 'System switch list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10004, 'Job status', 'sys_job_status', 'Job status list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10005, 'Job group', 'sys_job_group', 'Job group list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10006, 'Yes/No', 'sys_yes_no', 'Yes/No list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10007, 'Notice type', 'sys_notice_type', 'Notice type list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10008, 'Notice status', 'sys_notice_status', 'Notice status list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10009, 'Operation type', 'sys_oper_type', 'Operation type list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10010, 'System status', 'sys_common_status', 'Login status list', '2018-03-16 11:33:00', 19980817, '2018-03-16 11:33:00', 19980817),
(10011, 'Alarm type', 'sys_fault_type', 'Alarm type list', '2020-07-01 11:29:23', 19980817, '2020-07-01 11:30:02', 19980817),
(10012, 'Menu type', 'sys_menu_type', 'Menu type', '2022-06-25 10:02:55', 19980817, '2022-06-25 10:02:59', 19980817),
(10013, 'Menu audience', 'sys_menu_target', 'Menu audience list', '2022-06-25 10:03:27', 19980817, '2022-06-25 10:03:30', 19980817),
(10014, 'Statistics query type', 'statistics_query_type', 'Statistics query type', '2022-07-09 14:05:30', 19980817, '2022-07-09 14:05:30', 19980817),
(10015, 'Data scope type', 'sys_data_scope', 'Data scope type', '2024-08-13 10:18:49', 19980817, '2024-08-13 10:18:59', 19980817);

SET FOREIGN_KEY_CHECKS = 1;
