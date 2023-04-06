ALTER TABLE posts CHANGE date created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE comments CHANGE date created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE comments ADD COLUMN content varchar(500);
ALTER TABLE post_tags ADD PRIMARY KEY (post_id, tag_id);
DROP TABLE posts_reactions;
CREATE TABLE post_reactions (
  user_id INT UNSIGNED NOT NULL,
  post_id INT UNSIGNED NOT NULL,
  is_liked TINYINT(1) NOT NULL,
  PRIMARY KEY (user_id, post_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (post_id) REFERENCES posts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;







