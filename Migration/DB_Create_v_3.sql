CREATE DATABASE `9gag` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `comments` (
  `comment_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int unsigned NOT NULL,
  `post_id` int unsigned NOT NULL,
  `parent_comment_id` int unsigned DEFAULT NULL,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `comments_FK` (`user_id`),
  KEY `comments_FK_1` (`post_id`),
  KEY `comments_FK_2` (`parent_comment_id`),
  CONSTRAINT `comments_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `comments_FK_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`),
  CONSTRAINT `comments_FK_2` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `comments_reactions` (
  `comment_reaction_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int unsigned NOT NULL,
  `is_liked` tinyint(1) DEFAULT NULL,
  `comment_id` int unsigned NOT NULL,
  PRIMARY KEY (`comment_reaction_id`),
  KEY `comments_reactions_FK` (`comment_id`),
  KEY `comments_reactions_FK_1` (`user_id`),
  CONSTRAINT `comments_reactions_FK` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`),
  CONSTRAINT `comments_reactions_FK_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `post_tags` (
  `post_id` int unsigned DEFAULT NULL,
  `tag_id` int unsigned DEFAULT NULL,
  KEY `post_tags_FK` (`tag_id`),
  KEY `post_tags_FK_1` (`post_id`),
  CONSTRAINT `post_tags_FK` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`),
  CONSTRAINT `post_tags_FK_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE 9gag.post_tags ADD CONSTRAINT post_tags_pk PRIMARY KEY (post_id,tag_id);


CREATE TABLE `posts` (
  `post_id` int unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(280) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `user_id` int unsigned NOT NULL,
  `date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `posts_FK` (`user_id`),
  CONSTRAINT `posts_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `posts_reactions` (
  `reaction_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int unsigned NOT NULL,
  `is_liked` tinyint(1) DEFAULT NULL,
  `post_id` int unsigned NOT NULL,
  PRIMARY KEY (`reaction_id`),
  KEY `reactions_FK` (`user_id`),
  KEY `reactions_FK_1` (`post_id`),
  CONSTRAINT `reactions_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `reactions_FK_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tags` (
  `tag_id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tags_un` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
  `user_id` int unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `last_login_time` timestamp NULL DEFAULT NULL,
  `usr_password` varchar(100) NOT NULL,
  `age` int NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
