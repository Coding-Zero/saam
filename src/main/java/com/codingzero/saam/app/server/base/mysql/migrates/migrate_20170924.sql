--
-- Table structure for table `identifier_policies`
--
ALTER TABLE `saam`.`identifier_policies`
DROP COLUMN `name`,
DROP COLUMN `code`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`application_id`, `type`),
CHANGE COLUMN `is_need_to_verify` `is_verification_required` BIT(1) NOT NULL DEFAULT b'0' ,
ADD COLUMN `update_time` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER `creation_time`,
DROP INDEX `type_INDEX` ;

--
-- Table structure for table `identifiers`
--
ALTER TABLE `saam`.`identifiers`
ADD COLUMN `update_time` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER `creation_time`,
ADD INDEX `update_time_INDEX` (`update_time` ASC);
CHANGE COLUMN `user_id` `user_id` BINARY(16) NOT NULL AFTER `content`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`application_id`, `identifier_policy_code`, `content_hash`),
ADD INDEX `user_id_INDEX` (`application_id` ASC, `user_id` ASC),
DROP INDEX `content_hash_UNIQUE` ;
CHANGE COLUMN `identifier_policy_code` `identifier_type` VARCHAR(25) NOT NULL ;
DROP COLUMN `type`,
DROP INDEX `type_INDEX` ;

--
-- Table structure for table `sso_identifier_policies`
--
ALTER TABLE `saam`.`sso_identifier_policies`
RENAME TO  `saam`.`oauth_identifier_policies` ;
ALTER TABLE `saam`.`oauth_identifier_policies`
ADD COLUMN `update_time` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER `creation_time`;
ALTER TABLE `saam`.`oauth_identifier_policies`
ADD INDEX `update_time_INDEX` (`update_time` ASC);

--
-- Table structure for table `sso_identifiers`
--
ALTER TABLE `saam`.`sso_identifiers`
RENAME TO  `saam`.`oauth_identifiers` ;
CHANGE COLUMN `user_id` `user_id` BINARY(16) NOT NULL AFTER `content`,
ADD COLUMN `update_time` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER `creation_time`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`application_id`, `platform`, `content_hash`),
ADD INDEX `update_time_INDEX` (`update_time` ASC),
ADD INDEX `user_id_INDEX` (`application_id` ASC, `user_id` ASC);

--
-- Table structure for table `user_sessions`
--
ALTER TABLE `saam`.`user_sessions`
CHANGE COLUMN `metadata` `details` JSON NOT NULL ;

--
-- Table structure for table `permissions`
--
ALTER TABLE `saam`.`permissions`
CHANGE COLUMN `resource_key_hash` `resource_key_hash` BINARY(32) NOT NULL AFTER `application_id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`application_id`, `resource_key_hash`, `principal_id`);
DROP COLUMN `type`,
CHANGE COLUMN `resource_key` `resource_key` VARCHAR(1024) NOT NULL AFTER `resource_key_hash`,
ADD COLUMN `actions` JSON NOT NULL AFTER `action_codes`,
DROP INDEX `resource_key_hash_INDEX` ,
ADD INDEX `principal_id_INDEX` (`application_id` ASC, `principal_id` ASC),
DROP INDEX `type_INDEX` ;
CHANGE COLUMN `action_codes` `action_codes` JSON NULL ;

--
-- Table structure for table `email_policies`
--
ALTER TABLE `saam`.`email_policies`
CHANGE COLUMN `code` `type` VARCHAR(45) NOT NULL ;
DROP COLUMN `type`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`application_id`);

--
-- Table structure for table `username_policies`
--
ALTER TABLE `saam`.`username_policies`
CHANGE COLUMN `code` `type` VARCHAR(45) NOT NULL ;
DROP COLUMN `type`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`application_id`);