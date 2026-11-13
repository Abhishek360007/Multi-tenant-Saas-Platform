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
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Records of cms_project
-- ----------------------------
INSERT INTO `cms_project` (`project_id`, `project_name`, `linkman`, `contact_number`, `area_code`, `address`,
                           `sort`, `password`, `status`, `dept_id`, `create_time`, `create_user`,
                           `last_update_time`, `last_update_user`)
VALUES
(10001, 'Default project', 'Anil Rao', '080-41234567', 29052505542, '12 MG Road, Bengaluru', 1, '$2a$10$omnFqGPZ3/pMfctkVkxiQOYMUC8pp1iNsaJDI8vQmLdtIdAXvlS8K', 1, 10002, '2023-08-11 11:17:33', 19980817, '2023-08-11 10:12:24', 19980817),
(10002, 'Metro Expansion', 'Sunita Nair', '080-41234568', 29052505542, 'Hebbal, Bengaluru', 1, '$2a$10$omnFqGPZ3/pMfctkVkxiQOYMUC8pp1iNsaJDI8vQmLdtIdAXvlS8K', 1, 10003, '2023-08-11 11:17:33', 19980817, '2023-08-11 10:12:24', 19980817),
(10003, 'Solar Grid', 'Vikram Singh', '080-41234569', 29052505542, 'Whitefield, Bengaluru', 1, '$2a$10$omnFqGPZ3/pMfctkVkxiQOYMUC8pp1iNsaJDI8vQmLdtIdAXvlS8K', 1, 10004, '2023-08-11 11:17:33', 19980817, '2023-08-11 10:12:24', 19980817);


-- ----------------------------
-- Records of cms_device
-- ----------------------------
INSERT INTO `cms_device` (`device_id`, `device_name`, `status`, `project_id`, `create_time`, `create_user`, `last_update_time`, `last_update_user`)
VALUES
(6569649088961, 'Jet aircraft', 1, 10001, '2023-11-08 18:07:23', 19980817, '2023-11-08 18:07:23', 19980817),
(6569649088962, 'Heavy transport aircraft', 1, 10001, '2023-11-08 18:07:23', 19980817, '2023-11-08 18:07:23', 19980817),
(6569649151233, 'Excavator', 1, 10002, '2023-11-08 18:07:27', 19980817, '2023-11-08 18:07:27', 19980817),
(6569649151234, 'Water pump', 1, 10002, '2023-11-08 18:07:27', 19980817, '2023-11-08 18:07:27', 19980817),
(6569649151235, 'Gas pipeline', 1, 10003, '2023-11-08 18:07:27', 19980817, '2023-11-08 18:07:27', 19980817),
(6569649151236, 'Tower crane', 1, 10003, '2023-11-08 18:07:27', 19980817, '2023-11-08 18:07:27', 19980817),
(6569649223207, 'Bulldozer', 1, 10003, '2023-11-08 18:07:31', 19980817, '2023-11-08 18:07:31', 19980817);


SET
FOREIGN_KEY_CHECKS = 1;
