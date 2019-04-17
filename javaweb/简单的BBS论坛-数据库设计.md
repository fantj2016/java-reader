#####最近帮一个练手的项目组设计了一个bbs论坛的数据库。记录一下，同时也免费分享给大家。
大概包括这么几个表：
* admin用户表
* 文章表
* 文章类型表/标签表'
* 关注表
* 文章收藏表
* 一级评论表
* 多级评论表
* 用户信息表

#####大家不要全部复制sql去跑，我建议大家一个一个表复制去创建。注意外键关联关系的去创建。每个字段几乎都有注释。

![](https://upload-images.jianshu.io/upload_images/5786888-1042e090f8e8d601.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : yunding_bbs

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2018-03-23 12:29:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bbs_admin
-- ----------------------------
DROP TABLE IF EXISTS `bbs_admin`;
CREATE TABLE `bbs_admin` (
  `admin_id` int(11) NOT NULL,
  `admin_login_name` varchar(50) DEFAULT NULL,
  `admin_login_pwd` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='admin用户表';

-- ----------------------------
-- Records of bbs_admin
-- ----------------------------
INSERT INTO `bbs_admin` VALUES ('1', 'jiao', 'jiao');

-- ----------------------------
-- Table structure for bbs_article
-- ----------------------------
DROP TABLE IF EXISTS `bbs_article`;
CREATE TABLE `bbs_article` (
  `art_id` int(11) NOT NULL,
  `art_user_id` int(11) DEFAULT NULL,
  `art_title` varchar(255) DEFAULT NULL COMMENT '标题',
  `art_type_id` int(11) DEFAULT NULL COMMENT '类型id',
  `art_content` text COMMENT '正文',
  `art_comment_id` int(11) DEFAULT NULL COMMENT '评论id',
  `art_cre_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `art_view` int(11) DEFAULT NULL COMMENT '浏览量',
  `art_com_num` int(11) DEFAULT NULL COMMENT '评论数',
  `art_hot_num` int(11) DEFAULT NULL COMMENT '当日浏览量/热度',
  `art_like_num` int(11) DEFAULT NULL COMMENT '点赞数',
  PRIMARY KEY (`art_id`),
  KEY `type_index` (`art_type_id`),
  KEY `com_index` (`art_comment_id`),
  KEY `art_index` (`art_user_id`),
  CONSTRAINT `art_index` FOREIGN KEY (`art_user_id`) REFERENCES `bbs_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `type_index` FOREIGN KEY (`art_type_id`) REFERENCES `bbs_article_type` (`type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文章表';

-- ----------------------------
-- Records of bbs_article
-- ----------------------------
INSERT INTO `bbs_article` VALUES ('1', '1', '第一篇文章', '1', '这里是内容', '1', '2018-03-23 11:26:29', '999', '9', '9', '9');

-- ----------------------------
-- Table structure for bbs_article_type
-- ----------------------------
DROP TABLE IF EXISTS `bbs_article_type`;
CREATE TABLE `bbs_article_type` (
  `type_id` int(11) NOT NULL,
  `type_name` varchar(255) DEFAULT NULL COMMENT '标签/类型',
  `type_create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文章类型表/标签表';

-- ----------------------------
-- Records of bbs_article_type
-- ----------------------------
INSERT INTO `bbs_article_type` VALUES ('1', '标签1', '2018-03-23 11:25:51');

-- ----------------------------
-- Table structure for bbs_attention
-- ----------------------------
DROP TABLE IF EXISTS `bbs_attention`;
CREATE TABLE `bbs_attention` (
  `att_id` int(11) NOT NULL,
  `att_author_id` int(11) DEFAULT NULL COMMENT '关注人id',
  `att_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`att_id`),
  KEY `attention_index` (`att_user_id`) USING BTREE,
  KEY `atten_author_index` (`att_author_id`),
  CONSTRAINT `atten_author_index` FOREIGN KEY (`att_author_id`) REFERENCES `bbs_user` (`user_id`) ON DELETE SET NULL ON UPDATE NO ACTION,
  CONSTRAINT `atten_user_index` FOREIGN KEY (`att_user_id`) REFERENCES `bbs_user` (`user_id`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='关注表';

-- ----------------------------
-- Records of bbs_attention
-- ----------------------------
INSERT INTO `bbs_attention` VALUES ('1', '1', '1');

-- ----------------------------
-- Table structure for bbs_collect
-- ----------------------------
DROP TABLE IF EXISTS `bbs_collect`;
CREATE TABLE `bbs_collect` (
  `col_id` int(11) NOT NULL,
  `col_art_id` int(11) DEFAULT NULL COMMENT '收藏文章id',
  `col_user_id` int(11) DEFAULT NULL COMMENT '收藏用户的id/谁收藏了文章',
  PRIMARY KEY (`col_id`),
  KEY `col_index` (`col_user_id`),
  KEY `col_art_index` (`col_art_id`),
  CONSTRAINT `col_art_index` FOREIGN KEY (`col_art_id`) REFERENCES `bbs_article` (`art_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `col_index` FOREIGN KEY (`col_user_id`) REFERENCES `bbs_user` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文章收藏表';

-- ----------------------------
-- Records of bbs_collect
-- ----------------------------
INSERT INTO `bbs_collect` VALUES ('1', '1', '1');

-- ----------------------------
-- Table structure for bbs_comment
-- ----------------------------
DROP TABLE IF EXISTS `bbs_comment`;
CREATE TABLE `bbs_comment` (
  `com_id` int(11) NOT NULL,
  `com_content` varchar(255) DEFAULT NULL COMMENT '评论正文',
  `com_art_id` int(11) DEFAULT NULL COMMENT '文章id',
  `com_user_id` int(11) DEFAULT NULL COMMENT '评论用户的id',
  `com_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`com_id`),
  KEY `com_user_index` (`com_user_id`),
  KEY `com_art_index` (`com_art_id`),
  CONSTRAINT `com_art_index` FOREIGN KEY (`com_art_id`) REFERENCES `bbs_article` (`art_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `com_user_index` FOREIGN KEY (`com_user_id`) REFERENCES `bbs_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='一级评论表';

-- ----------------------------
-- Records of bbs_comment
-- ----------------------------
INSERT INTO `bbs_comment` VALUES ('1', '评论正文', '1', '1', '2018-03-23 12:24:06');

-- ----------------------------
-- Table structure for bbs_comment_multi
-- ----------------------------
DROP TABLE IF EXISTS `bbs_comment_multi`;
CREATE TABLE `bbs_comment_multi` (
  `com_multi_id` int(11) NOT NULL,
  `com_id` int(11) NOT NULL COMMENT '一级评论id',
  `com_multi_content` varchar(255) DEFAULT NULL,
  `com_multi_user_id` int(11) NOT NULL COMMENT '多级评论用户id',
  `com_multi_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`com_multi_id`),
  KEY `multi_user_index` (`com_multi_user_id`),
  KEY `multi_com_index` (`com_id`),
  CONSTRAINT `multi_com_index` FOREIGN KEY (`com_id`) REFERENCES `bbs_comment` (`com_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `multi_user_index` FOREIGN KEY (`com_multi_user_id`) REFERENCES `bbs_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='多级评论表';

-- ----------------------------
-- Records of bbs_comment_multi
-- ----------------------------
INSERT INTO `bbs_comment_multi` VALUES ('1', '1', '多级评论', '1', '2018-03-23 12:24:21');

-- ----------------------------
-- Table structure for bbs_user
-- ----------------------------
DROP TABLE IF EXISTS `bbs_user`;
CREATE TABLE `bbs_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `user_email` varchar(50) DEFAULT NULL,
  `user_sex` varchar(2) DEFAULT NULL COMMENT '用户性别',
  `user_phone` int(11) DEFAULT NULL COMMENT '电话',
  `user_status` int(1) DEFAULT NULL COMMENT '用户状态   0：未激活   1：激活',
  `user_ex` varchar(255) DEFAULT NULL COMMENT '用户经验',
  `user_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间/更改时间',
  `user_show` varchar(255) DEFAULT NULL COMMENT '用户签名',
  `user_blog` varchar(255) DEFAULT NULL COMMENT '用户主页链接',
  `user_img` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `user_fans` int(11) DEFAULT NULL COMMENT '用户粉丝数',
  `user_concern` int(11) DEFAULT NULL COMMENT '用户关注别人的数量',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `user_admin_index` FOREIGN KEY (`user_id`) REFERENCES `bbs_admin` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of bbs_user
-- ----------------------------
INSERT INTO `bbs_user` VALUES ('1', 'FantJ', 'xxx', '男', '123123123', '1', '1', '2018-03-23 11:20:46', '这是我的个性签名', 'www.baidu.com', 'https://4f95-8639-e69e8c636570?imageMogr2/auto-orient/strip|imageView2/1/w/240/h/240', '9999', '9999');
