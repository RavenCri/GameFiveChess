/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 80013
Source Host           : 127.0.0.1:3306
Source Database       : fivechess

Target Server Type    : MYSQL
Target Server Version : 80013
File Encoding         : 65001

Date: 2019-04-14 19:04:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `passWord` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `winingProbability` double(255,4) DEFAULT NULL,
  `gameBout` int(255) DEFAULT NULL,
  `nickName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '1', '0.9999', '109', 'raven');
INSERT INTO `user` VALUES ('2', '2', '0.6515', '241', 'xiaotuzi');
INSERT INTO `user` VALUES ('123', '123', '0.0000', '0', '魔鬼沙拉酱');
