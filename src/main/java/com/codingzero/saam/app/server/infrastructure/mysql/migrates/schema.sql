-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: saam
-- ------------------------------------------------------
-- Server version	5.7.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actions`
--

DROP TABLE IF EXISTS `actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actions` (
  `application_id` binary(16) NOT NULL,
  `code` varchar(25) NOT NULL,
  `name` varchar(45) NOT NULL,
  `attached_resource_key` varchar(1024) DEFAULT NULL,
  `attached_resource_key_hash` binary(32) DEFAULT NULL,
  `creation_time` datetime(6) NOT NULL,
  PRIMARY KEY (`application_id`,`code`),
  KEY `attached_resource_key_hash_INDEX` (`attached_resource_key_hash`),
  KEY `creation_time_INDEX` (`creation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `apikeys`
--

DROP TABLE IF EXISTS `apikeys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apikeys` (
  `application_id` binary(16) NOT NULL,
  `id` binary(16) NOT NULL,
  `key` varchar(64) NOT NULL,
  `name` varchar(45) NOT NULL,
  `user_id` binary(16) NOT NULL,
  `is_active` bit(1) NOT NULL,
  PRIMARY KEY (`application_id`,`id`),
  UNIQUE KEY `key_UNIQUE` (`application_id`,`key`),
  KEY `is_active_INDEX` (`is_active`),
  KEY `user_id_INDEX` (`application_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `applications` (
  `id` binary(16) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL,
  `creation_time` datetime(6) NOT NULL,
  `password_policy` json NOT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `creation_time_INDEX` (`creation_time`),
  KEY `status_INDEX` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_policies`
--

DROP TABLE IF EXISTS `email_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `email_policies` (
  `application_id` binary(16) NOT NULL,
  `code` varchar(25) NOT NULL,
  `domains` json NOT NULL,
  PRIMARY KEY (`application_id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identifier_policies`
--

DROP TABLE IF EXISTS `identifier_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identifier_policies` (
  `application_id` binary(16) NOT NULL,
  `code` varchar(25) NOT NULL,
  `name` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `is_need_to_verify` bit(1) NOT NULL DEFAULT b'0',
  `min_length` smallint(2) NOT NULL,
  `max_length` smallint(2) NOT NULL,
  `is_active` bit(1) NOT NULL DEFAULT b'0',
  `creation_time` datetime(6) NOT NULL,
  PRIMARY KEY (`application_id`,`code`),
  KEY `type_INDEX` (`type`),
  KEY `is_active_INDEX` (`is_active`),
  KEY `creation_time_INDEX` (`creation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identifiers`
--

DROP TABLE IF EXISTS `identifiers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identifiers` (
  `application_id` binary(16) NOT NULL,
  `user_id` binary(16) NOT NULL,
  `identifier_policy_code` varchar(25) NOT NULL,
  `content_hash` binary(32) NOT NULL,
  `content` varchar(125) NOT NULL,
  `type` varchar(45) NOT NULL,
  `is_verified` bit(1) NOT NULL DEFAULT b'0',
  `verification_code` json DEFAULT NULL,
  `creation_time` datetime(6) NOT NULL,
  PRIMARY KEY (`application_id`,`user_id`,`identifier_policy_code`),
  UNIQUE KEY `content_hash_UNIQUE` (`application_id`,`content_hash`),
  KEY `type_INDEX` (`type`),
  KEY `is_verified_INDEX` (`is_verified`),
  KEY `creation_time_INDEX` (`creation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permissions` (
  `application_id` binary(16) NOT NULL,
  `principal_id` binary(16) NOT NULL,
  `resource_key_hash` binary(32) NOT NULL,
  `resource_key` varchar(1024) NOT NULL,
  `type` varchar(45) NOT NULL,
  `creation_time` datetime(6) NOT NULL,
  `action_codes` json NOT NULL,
  PRIMARY KEY (`application_id`,`principal_id`,`resource_key_hash`),
  KEY `type_INDEX` (`type`),
  KEY `creation_time_INDEX` (`creation_time`),
  KEY `resource_key_hash_INDEX` (`application_id`,`resource_key_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `principals`
--

DROP TABLE IF EXISTS `principals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `principals` (
  `application_id` binary(16) NOT NULL,
  `id` binary(16) NOT NULL,
  `type` varchar(45) NOT NULL,
  `creation_time` datetime(6) NOT NULL,
  PRIMARY KEY (`application_id`,`id`),
  KEY `type_INDEX` (`type`),
  KEY `creation_time_INDEX` (`creation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resources`
--

DROP TABLE IF EXISTS `resources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resources` (
  `application_id` binary(16) NOT NULL,
  `key_hash` binary(32) NOT NULL,
  `key` varchar(1024) NOT NULL,
  `principal_id` binary(16) NOT NULL,
  `creation_time` datetime(6) NOT NULL,
  `parent_key_hash` binary(32) DEFAULT NULL,
  PRIMARY KEY (`application_id`,`key_hash`),
  KEY `principal_id_INDEX` (`principal_id`),
  KEY `creation_time_INDEX` (`creation_time`),
  KEY `parent_key_hash_INDEX` (`parent_key_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `application_id` binary(16) NOT NULL,
  `id` binary(16) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`application_id`,`id`),
  UNIQUE KEY `name_UNIQUE` (`application_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_identifier_policies`
--

DROP TABLE IF EXISTS `sso_identifier_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_identifier_policies` (
  `application_id` binary(16) NOT NULL,
  `platform` varchar(45) NOT NULL,
  `configurations` json NOT NULL,
  `is_active` bit(1) NOT NULL DEFAULT b'1',
  `creation_time` datetime(6) NOT NULL,
  PRIMARY KEY (`application_id`,`platform`),
  KEY `is_active_INDEX` (`is_active`),
  KEY `creation_time_INDEX` (`creation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sso_identifiers`
--

DROP TABLE IF EXISTS `sso_identifiers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sso_identifiers` (
  `application_id` binary(16) NOT NULL,
  `user_id` binary(16) NOT NULL,
  `platform` varchar(25) NOT NULL,
  `content_hash` binary(32) NOT NULL,
  `content` varchar(125) NOT NULL,
  `properties` json NOT NULL,
  `creation_time` datetime(6) NOT NULL,
  PRIMARY KEY (`application_id`,`user_id`,`platform`),
  UNIQUE KEY `content_UNIQUE` (`application_id`,`platform`,`content_hash`),
  KEY `creation_time_INDEX` (`creation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_sessions`
--

DROP TABLE IF EXISTS `user_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_sessions` (
  `application_id` binary(16) NOT NULL,
  `key` varchar(65) NOT NULL,
  `expiration_time` datetime(6) NOT NULL,
  `creation_time` datetime(6) NOT NULL,
  `user_id` binary(16) NOT NULL,
  `metadata` json NOT NULL,
  PRIMARY KEY (`application_id`,`key`),
  KEY `expiration_time_INDEX` (`expiration_time`),
  KEY `creation_time_INDEX` (`creation_time`),
  KEY `user_id_INDEX` (`application_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `username_policies`
--

DROP TABLE IF EXISTS `username_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `username_policies` (
  `application_id` binary(16) NOT NULL,
  `code` varchar(25) NOT NULL,
  `format` varchar(45) NOT NULL,
  PRIMARY KEY (`application_id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `application_id` binary(16) NOT NULL,
  `id` binary(16) NOT NULL,
  `password` varchar(125) DEFAULT NULL,
  `password_reset_code` json NOT NULL,
  `role_ids` json NOT NULL,
  PRIMARY KEY (`application_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-08 14:33:21
