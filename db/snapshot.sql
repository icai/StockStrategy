/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : stockquery

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-04-11 00:31:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for snapshot
-- ----------------------------
DROP TABLE IF EXISTS `snapshot`;
CREATE TABLE `snapshot` (
  `sid` char(8) NOT NULL COMMENT '股票代码',
  `date` int(8) NOT NULL COMMENT '日期',
  `time` int(9) NOT NULL COMMENT '时间',
  `preClose` float(12,12) NOT NULL COMMENT '昨收',
  `high` float(12,12) NOT NULL COMMENT '最高',
  `open` float(12,12) NOT NULL COMMENT '今开',
  `low` float(12,12) NOT NULL COMMENT '最低',
  `close` float(12,12) NOT NULL COMMENT '今收',
  `volume` int(11) NOT NULL COMMENT '成交量',
  `amount` int(11) NOT NULL COMMENT '成交额',
  `netChange` float(12,12) NOT NULL COMMENT '涨跌价',
  `netChangeRatio` float(12,12) NOT NULL COMMENT '涨跌幅',
  `amplitudeRatio` float(12,12) NOT NULL COMMENT '振幅',
  `turnoverRatio` float(12,12) NOT NULL COMMENT '换手率',
  `inside` int(11) NOT NULL COMMENT '内盘',
  `outside` int(11) NOT NULL COMMENT '外盘',
  `currencyValue` int(11) NOT NULL COMMENT '流通市值',
  `capitalization` int(11) NOT NULL COMMENT '总市值',
  `peratio` float(12,12) NOT NULL COMMENT '市盈率MRQ',
  `volumeRatio` float(12,12) NOT NULL COMMENT '量比',
  `weibiRatio` float(12,12) NOT NULL COMMENT '委比',
  `bvRatio` float(12,12) NOT NULL COMMENT '市净率',
  `psRatio` float(12,12) NOT NULL COMMENT '市销率',
  `dealCount` int(11) NOT NULL COMMENT '成交笔数',
  `perShareEarn` float(12,12) NOT NULL COMMENT '每股收益',
  `LYRPeratio` float(12,12) NOT NULL COMMENT 'LYR市盈率',
  `TTMPeratio` float(12,12) NOT NULL COMMENT 'TTM市盈率',
  `netFundsFlow` double(10,5) NOT NULL COMMENT '现金流量净额',
  `limitUp` double(11,3) NOT NULL COMMENT '涨停',
  `limitDown` double(11,3) NOT NULL COMMENT '跌停',
  `netAssetsPerShare` double(11,5) NOT NULL COMMENT '每股净资产',
  `totalShareCapital` int(11) NOT NULL COMMENT '总股本',
  `circulatingCapital` int(11) NOT NULL COMMENT '流通股',
  `stockStatus` int(1) NOT NULL COMMENT '*0:停盘 1:退市 2:正常 3:未上市',
  `stockCode` varchar(10) NOT NULL COMMENT '代码',
  `stockName` varchar(255) NOT NULL COMMENT '名字',
  `stockAsset` int(2) NOT NULL COMMENT '0:股票 1:期货 2:期权 3:外汇 4指数 5:场内基金 6:债券、7:认购权证 8:认沽权证 9: 牛证 10:熊证  ',
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
