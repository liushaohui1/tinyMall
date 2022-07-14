/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.7.38-log : Database - tinymall
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`tinymall` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `tinymall`;

/*Table structure for table `ap_coupon` */

DROP TABLE IF EXISTS `ap_coupon`;

CREATE TABLE `ap_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `status` bit(1) DEFAULT b'1' COMMENT '优惠券状态，0删除 1可用 2下架',
  `name` varchar(63) NOT NULL COMMENT '优惠券名称',
  `desc` varchar(127) DEFAULT '' COMMENT '优惠券介绍，通常是显示优惠券使用限制文字',
  `tag` varchar(63) DEFAULT '' COMMENT '优惠券标签，例如新人专用',
  `total` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券数量，如果是0，则是无限量',
  `discount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额，',
  `min` decimal(10,2) DEFAULT '0.00' COMMENT '最少消费金额才能使用优惠券。',
  `limit` smallint(6) DEFAULT '1' COMMENT '用户领券限制数量，如果是0，则是不限制；默认是1，限领一张.',
  `type` smallint(6) DEFAULT '0' COMMENT '优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换；',
  `goods_type` smallint(6) DEFAULT '0' COMMENT '商品限制类型，如果0则全商品，如果是1则是类目限制，如果是2则是商品限制。',
  `goods_value` varchar(1023) DEFAULT '[]' COMMENT '商品限制值，goods_type如果是0则空集合，如果是1则是类目集合，如果是2则是商品集合。',
  `code` varchar(63) DEFAULT NULL COMMENT '优惠券兑换码',
  `time_type` smallint(6) DEFAULT '0' COMMENT '有效时间限制，如果是0，则基于领取时间的有效天数days；如果是1，则start_time和end_time是优惠券有效期；',
  `days` smallint(6) DEFAULT '0' COMMENT '基于领取时间的有效天数days。',
  `start_time` datetime DEFAULT NULL COMMENT '使用券开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '使用券截至时间',
  PRIMARY KEY (`id`),
  KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='优惠券信息及规则表';

/*Data for the table `ap_coupon` */

insert  into `ap_coupon`(`id`,`create_by`,`create_time`,`update_by`,`update_time`,`status`,`name`,`desc`,`tag`,`total`,`discount`,`min`,`limit`,`type`,`goods_type`,`goods_value`,`code`,`time_type`,`days`,`start_time`,`end_time`) values (1,NULL,NULL,NULL,NULL,'\0','限时满减券','全场通用','无限制',0,'5.00','99.00',1,0,0,'[]',NULL,0,10,'2022-07-14 19:41:14','2060-12-14 19:41:19'),(2,NULL,NULL,NULL,NULL,'\0','限时满减券','全场通用','无限制',0,'10.00','99.00',1,0,0,'[]',NULL,0,10,'2022-07-14 19:41:14','2060-12-14 19:41:19'),(3,NULL,NULL,NULL,NULL,'\0','新用户优惠券','全场通用','无限制',0,'10.00','99.00',1,1,0,'[]',NULL,0,10,'2022-07-14 19:41:14','2060-12-14 19:41:19'),(8,NULL,NULL,NULL,NULL,'\0','可兑换优惠券','全场通用','仅兑换领券',0,'15.00','99.00',1,2,0,'[]','DC6FF8SE',0,7,'2022-07-14 19:41:14','2060-12-14 19:41:19');

/*Table structure for table `ap_user` */

DROP TABLE IF EXISTS `ap_user`;

CREATE TABLE `ap_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `status` bit(1) DEFAULT b'1' COMMENT '帐号启用状态：0->禁用；1->启用',
  `username` varchar(63) NOT NULL COMMENT '用户名称',
  `password` varchar(63) NOT NULL DEFAULT '' COMMENT '用户密码',
  `gender` tinyint(3) NOT NULL DEFAULT '0' COMMENT '性别：0 未知， 1男， 1 女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `last_login_time` datetime DEFAULT NULL COMMENT '最近一次登录时间',
  `last_login_ip` varchar(63) NOT NULL DEFAULT '' COMMENT '最近一次登录IP地址',
  `user_level` tinyint(3) DEFAULT '0' COMMENT '0 普通用户，1 VIP用户，2 高级VIP用户',
  `nickname` varchar(63) NOT NULL DEFAULT '' COMMENT '用户昵称或网络名称',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '用户手机号码',
  `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '用户头像图片',
  `wx_openid` varchar(63) NOT NULL DEFAULT '' COMMENT '微信登录openid',
  `session_key` varchar(100) NOT NULL DEFAULT '' COMMENT '微信登录会话KEY',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='用户表';

/*Data for the table `ap_user` */

insert  into `ap_user`(`id`,`create_by`,`create_time`,`update_by`,`update_time`,`status`,`username`,`password`,`gender`,`birthday`,`last_login_time`,`last_login_ip`,`user_level`,`nickname`,`mobile`,`avatar`,`wx_openid`,`session_key`) values (1,'ADMIN','2022-07-13 17:42:58.000000','ADMIN','2022-07-13 17:43:01.000000','','admin','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG',0,NULL,NULL,'',0,'adm','','http://aprilz-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190129/170157_yIl3_1767531.jpg','',''),(7,'ADMIN','2022-07-14 15:23:59.814000','ADMIN','2022-07-14 15:23:59.814000','','oPr2q1V-icWtUTe8FXCCf6yeryBg','oPr2q1V-icWtUTe8FXCCf6yeryBg',0,NULL,'2022-07-14 17:14:30','10.1.129.68',0,'白','','https://thirdwx.qlogo.cn/mmopen/vi_32/2KOBFlndeR5aIzSMFAzfQewiawkmT6LnZpiaf5DAKWAcTn0qaXCmI6wzP71qXHL55xAwqZLVvvs9j7wUYNlmmpiaw/132','oPr2q1V-icWtUTe8FXCCf6yeryBg','SX67Vcbob8fxHzuoMxTN/Q==');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
