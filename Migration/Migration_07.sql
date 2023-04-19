ALTER TABLE users ADD COLUMN is_admin TINYINT NOT NULL;
ALTER TABLE users ADD COLUMN is_banned TINYINT NOT NULL;

ALTER TABLE posts ADD COLUMN reports INT NOT NULL;

CREATE TABLE post_reports (
    reported_id INT UNSIGNED NOT NULL,
    reporter_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (reported_id, reporter_id),
    FOREIGN KEY (reported_id) REFERENCES posts(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id)
);