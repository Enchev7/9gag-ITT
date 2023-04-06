ALTER TABLE 9gag.users CHANGE user_id id int unsigned auto_increment NOT NULL;
ALTER TABLE 9gag.users MODIFY COLUMN last_login_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE 9gag.users CHANGE usr_password password varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL;
ALTER TABLE 9gag.tags CHANGE tag_id id int unsigned auto_increment NOT NULL;
ALTER TABLE 9gag.posts_reactions CHANGE reaction_id id int unsigned auto_increment NOT NULL;
ALTER TABLE 9gag.posts_reactions MODIFY COLUMN is_liked tinyint(1) NOT NULL;
ALTER TABLE 9gag.posts CHANGE post_id id int unsigned auto_increment NOT NULL;
ALTER TABLE 9gag.posts MODIFY COLUMN `date` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE 9gag.comments CHANGE comment_id id int unsigned auto_increment NOT NULL;
ALTER TABLE 9gag.comments MODIFY COLUMN `date` datetime DEFAULT CURRENT_TIMESTAMP NOT NULL;
DROP TABLE 9gag.comments_reactions;

CREATE TABLE 9gag.comments_reactions (
	user_id INT UNSIGNED NOT NULL,
	comment_id INT UNSIGNED NOT NULL,
	is_liked BOOL NOT NULL,
	CONSTRAINT comments_reactions_pk PRIMARY KEY (user_id,comment_id),
	CONSTRAINT comments_reactions_FK FOREIGN KEY (user_id) REFERENCES 9gag.users(id),
	CONSTRAINT comments_reactions_FK_1 FOREIGN KEY (comment_id) REFERENCES 9gag.comments(id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;