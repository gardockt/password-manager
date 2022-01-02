CREATE DATABASE db;
USE db;

-- TODO: adjust string lengths, especially for encrypted/hashed values

CREATE TABLE users (
    id                BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username          VARCHAR(32)   NOT NULL UNIQUE,
    account_password  CHAR(60)      NOT NULL,   -- bcrypt
    unlock_password   CHAR(60)      NOT NULL,   -- bcrypt
    roles             VARCHAR(64)   NOT NULL DEFAULT 'user',
    active            BIT           NOT NULL DEFAULT TRUE,
    unlock_datetime   DATETIME      NULL
);

CREATE TABLE passwords (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    password     VARCHAR(200)  NOT NULL,   -- AES-256-GCM
    description  VARCHAR(64)  NOT NULL,
    CONSTRAINT FK_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);