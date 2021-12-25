CREATE DATABASE db;
USE db;

-- TODO: adjust string lengths, especially for encrypted/hashed values

CREATE TABLE users (
    id                BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username          VARCHAR(32)  NOT NULL UNIQUE,
    -- PBKDF2 (TODO: use bcrypt instead?)
    -- NIST recommendation: https://security.stackexchange.com/questions/4781/do-any-security-experts-recommend-bcrypt-for-password-storage/6415#6415, nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-132.pdf
    account_password  VARCHAR(32)  NOT NULL,
    master_password   VARCHAR(32)  NOT NULL,
    roles             VARCHAR(64)  NOT NULL DEFAULT "user",
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