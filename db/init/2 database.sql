CREATE DATABASE db;
USE db;

-- TODO: adjust string lengths, especially for encrypted/hashed values
-- TODO: disallow unicode characters for ALL fields?

CREATE TABLE users (
    id                             BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username                       VARCHAR(32)   NOT NULL UNIQUE,
    account_password               CHAR(60)      NOT NULL,   -- bcrypt
    unlock_password                CHAR(60)      NOT NULL,   -- bcrypt
    roles                          VARCHAR(64)   NOT NULL DEFAULT 'user',
    active                         BIT           NOT NULL DEFAULT TRUE,
    failed_attempts_since_unlock   TINYINT       NOT NULL DEFAULT 0,
    failed_attempts_since_login    INT           NOT NULL DEFAULT 0,
    unlock_datetime                DATETIME      NULL
);

CREATE TABLE passwords (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    password     VARCHAR(500) NOT NULL,   -- AES-256-GCM
    description  VARCHAR(64)  NOT NULL UNIQUE,
    last_access  DATETIME,
    CONSTRAINT FK_passwords_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE user_agents (
    id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_agent  TEXT   NOT NULL
);

CREATE TABLE login_history (
    id             BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    user_agent_id  BIGINT      NOT NULL,
    ip             VARCHAR(45) NOT NULL,
    count          INT         NOT NULL DEFAULT 0,
    last_access    DATETIME    NOT NULL,
    CONSTRAINT FK_login_history_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_login_history_user_agent_id FOREIGN KEY (user_agent_id) REFERENCES user_agents(id)
);
