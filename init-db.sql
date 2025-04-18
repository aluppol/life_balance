-- Set default time zone
SET TIME ZONE 'UTC';

-- Create the database (if running against default 'postgres' DB)
CREATE DATABASE life_balance_db
  WITH OWNER = your_user
       ENCODING = 'UTF8'
       LC_COLLATE = 'en_US.utf8'
       LC_CTYPE = 'en_US.utf8'
       TEMPLATE = template0;