CREATE USER 'user'@'localhost' IDENTIFIED BY 'qZJYkVtu2NfgldrR'; -- TODO: change user to random value?
GRANT ALL PRIVILEGES ON *.* TO 'user'@'localhost' WITH GRANT OPTION; -- TODO: ?
CREATE USER 'user'@'%' IDENTIFIED BY 'qZJYkVtu2NfgldrR'; -- TODO: usunąć?
GRANT ALL PRIVILEGES ON *.* TO 'user'@'%' WITH GRANT OPTION; -- TODO: usunąć?