CREATE TABLE user (
  userid   CHAR(32) PRIMARY KEY,
  password CHAR(64) NOT NULL,
  nickname CHAR(32)  NOT NULL,
  gender   TINYINT,
  Tel      CHAR(15)
)
  CHARACTER SET = utf8mb4;

drop table user;