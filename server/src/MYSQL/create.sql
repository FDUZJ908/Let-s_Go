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

CREATE INDEX idx_lat_lng
  ON POI (latitude, longitude);

CREATE TABLE post
(
  postid    INT AUTO_INCREMENT
    PRIMARY KEY,
  timestamp INT             NOT NULL,
  userid    CHAR(32)        NOT NULL,
  POI_id    CHAR(30)        NOT NULL,
  latitude  DOUBLE          NOT NULL,
  longitude DOUBLE          NOT NULL,
  `like`    INT DEFAULT '0' NOT NULL,
  dislike   INT DEFAULT '0' NOT NULL,
  text      TEXT            NULL,
  imageUrl  TEXT            NULL,
  FOREIGN KEY (userid) REFERENCES user (userid)
    ON UPDATE CASCADE,
  FOREIGN KEY (POI_id) REFERENCES POI (POI_id)
    ON UPDATE CASCADE
)
  CHARACTER SET = utf8mb4;

CREATE INDEX idx_poi_post
  ON post (POI_id, postid);

DROP TABLE user;
DROP TABLE POI;


SELECT 