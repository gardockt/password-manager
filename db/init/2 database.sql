CREATE DATABASE db;
USE db;

-- TODO: adjust string lengths, especially for encrypted/hashed values

CREATE TABLE users (
    id                BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username          VARCHAR(32)  NOT NULL UNIQUE,
    -- bcrypt
    account_password  CHAR(60)     NOT NULL,
    unlock_password   CHAR(60)     NOT NULL,
    roles             VARCHAR(64)  NOT NULL DEFAULT 'user',
    active            BIT          NOT NULL DEFAULT TRUE,
    unlock_datetime   DATETIME     NULL
);

CREATE TABLE passwords (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    -- AES-256-CBC (TODO: use GCM instead?)
    -- IV may be concatenated as prefix: https://stackoverflow.com/questions/44694994/storing-iv-when-using-aes-asymmetric-encryption-and-decryption
    password     VARCHAR(64)  NOT NULL,
    description  VARCHAR(64)  NOT NULL,
    CONSTRAINT FK_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);