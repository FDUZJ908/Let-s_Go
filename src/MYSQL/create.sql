CREATE TABLE user (
  userid   CHAR(50) PRIMARY KEY,
  password TEXT     NOT NULL,
  nickname CHAR(30) NOT NULL,
  gender   TINYINT,
  Tel      CHAR(15)
)
  CHARACTER SET = utf8mb4;