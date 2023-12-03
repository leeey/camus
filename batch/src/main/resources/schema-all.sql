DROP TABLE member IF EXISTS;

CREATE TABLE member
(
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    username  TEXT,
    phone     TEXT
);