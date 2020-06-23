--# connect to mysql and run as root user
--#Create Databases
CREATE DATABASE sfg_dev;
CREATE DATABASE sfg_prod;

--# Create database service accounts
CREATE USER 'sfg_dev_user'@'localhost' IDENTIFIED BY 'guru';
CREATE USER 'sfg_prod_user'@'localhost' IDENTIFIED BY 'guru';

--# Database grants
GRANT SELECT ON sfg_dev.* to 'sfg_dev_user'@'localhost';
GRANT INSERT ON sfg_dev.* to 'sfg_dev_user'@'localhost';
GRANT DELETE ON sfg_dev.* to 'sfg_dev_user'@'localhost';
GRANT UPDATE ON sfg_dev.* to 'sfg_dev_user'@'localhost';
GRANT SELECT ON sfg_prod.* to 'sfg_prod_user'@'localhost';
GRANT INSERT ON sfg_prod.* to 'sfg_prod_user'@'localhost';
GRANT DELETE ON sfg_prod.* to 'sfg_prod_user'@'localhost';
GRANT UPDATE ON sfg_prod.* to 'sfg_prod_user'@'localhost';