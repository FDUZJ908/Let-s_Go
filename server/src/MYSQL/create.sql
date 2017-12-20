CREATE TABLE user (
  userid   CHAR(32) PRIMARY KEY,
  password CHAR(64) NOT NULL,
  nickname CHAR(32) NOT NULL,
  gender   TINYINT,
  Tel      CHAR(15)
)
  CHARACTER SET = utf8mb4;

CREATE TABLE POI (
  POI_id    CHAR(30) PRIMARY KEY,
  category  CHAR(64) NOT NULL,
  POI_name  CHAR(50) NOT NULL,
  latitude  DOUBLE   NOT NULL,
  longitude DOUBLE   NOT NULL,
  type      TINYINT,
  city      CHAR(20),
  country   CHAR(20) DEFAULT "中国"
)
  CHARACTER SET = utf8mb4;

DROP TABLE user;
DROP TABLE POI;

SELECT * FROM POI where POI_name like '%高科苑%';