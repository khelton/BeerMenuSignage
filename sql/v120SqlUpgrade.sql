# Tables to modify and create for upgrading to version 1.2

# Add price_type_id and rank columns to price table
ALTER TABLE `churchill`.`price` 
ADD COLUMN `price_type_id` INT(10) UNSIGNED NOT NULL DEFAULT '1' AFTER `beer_id`,
ADD COLUMN `rank` INT(10) UNSIGNED NOT NULL DEFAULT '0' AFTER `active`,
ADD COLUMN `enabled` TINYINT(1) NULL DEFAULT '1' AFTER `rank`;


# Add srm, beer_name_color, and beer_pour_color
ALTER TABLE `churchill`.`beer` 
ADD COLUMN `srm` DECIMAL(10,1) NULL DEFAULT '0.0' AFTER `ibu`,
ADD COLUMN `beer_name_color` VARCHAR(9) NOT NULL DEFAULT '#FFFFFF' AFTER `date_modified`,
ADD COLUMN `beer_pour_color` VARCHAR(9) NOT NULL DEFAULT '#FFDD00' AFTER `beer_name_color`;


# Create price_type table
CREATE TABLE `churchill`.`price_type` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` TINYTEXT CHARACTER SET 'utf8' COLLATE 'utf8_general_ci' NOT NULL,
  `schedule_id` INT(10) UNSIGNED NOT NULL DEFAULT '1',
  `active` INT(10) UNSIGNED NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = 'Price Type for happy hours or specials';

# Insert a price_type
INSERT INTO `churchill`.`price_type` (`name`, `schedule_id`)
VALUES ("Normal", 1), ("Special", 1), ("Happy Hour 1", 2);


# Create schedule table
CREATE TABLE `churchill`.`schedule` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` TINYTEXT NOT NULL,
  `time_start` TIME NOT NULL DEFAULT '00:00:00',
  `time_end` TIME NOT NULL DEFAULT '24:00:00',
  `days_string` VARCHAR(7) CHARACTER SET 'utf8' NOT NULL DEFAULT '1111111',
  `active` INT UNSIGNED NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = 'Time Schedules for Price types like specials or happy hour\n\ndays_string is a string of 0s and 1s for effective days in format SMTWTFS';

# Insert a schedule
INSERT INTO `churchill`.`schedule` (`name`, `time_start`, `time_end`)
VALUES ('24/7', '00:00:00', '24:00:00'), ('Happy Hour 1', '16:00:00', '21:00:00');


# Create Image Type Table
CREATE TABLE `churchill`.`image_type` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_general_ci' NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

# Insert image types
INSERT INTO `churchill`.`image_type` (`name`)
VALUES ('Beer Logo'), ('Cigar Logo'), ('Beer Closeup'), ('Cigar Closeup'), 
('Featured Screen'), ('Specials Screen');


# Create Image Table
CREATE TABLE `churchill`.`image` (
  `id` INT(10) NOT NULL,
  `beer_id` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `image_type_id` INT(10) UNSIGNED NOT NULL DEFAULT '1',
  `name` VARCHAR(45) NOT NULL,
  `location` TEXT NOT NULL,
  PRIMARY KEY (`id`));
