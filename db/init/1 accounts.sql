CREATE USER 'passwordmanager'@'localhost' IDENTIFIED BY 'qZJYkVtu2NfgldrR';
GRANT ALL PRIVILEGES ON *.* TO 'passwordmanager'@'localhost' WITH GRANT OPTION;
CREATE USER 'passwordmanager'@'%' IDENTIFIED BY 'qZJYkVtu2NfgldrR';
GRANT ALL PRIVILEGES ON *.* TO 'passwordmanager'@'%' WITH GRANT OPTION;
