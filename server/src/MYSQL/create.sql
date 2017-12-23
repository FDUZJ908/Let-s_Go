DROP TABLE feedback;
DROP TABLE post;
DROP TABLE POITags;
#DROP TABLE POI;*******
DROP TABLE user;

CREATE TABLE user (
  userid   CHAR(32) PRIMARY KEY,
  password CHAR(64)        NOT NULL,
  nickname CHAR(32)        NOT NULL,
  gender   TINYINT,
  Tel      CHAR(15),
  tags     BIGINT UNSIGNED NOT NULL DEFAULT 0
)
  CHARACTER SET = utf8mb4;

CREATE TABLE POI (
  POI_id     CHAR(30) PRIMARY KEY,
  category   CHAR(64) NOT NULL,
  POI_name   CHAR(50) NOT NULL,
  latitude   DOUBLE   NOT NULL,
  longitude  DOUBLE   NOT NULL,
  popularity INT      NOT NULL DEFAULT 0,
  city       CHAR(20),
  country    CHAR(20)          DEFAULT "中国"
)
  CHARACTER SET = utf8mb4;

CREATE INDEX idx_lat_lng
  ON POI (latitude, longitude);

CREATE TABLE POITags (
  POI_id CHAR(30) PRIMARY KEY,
  tags1  CHAR(128) NOT NULL DEFAULT "",
  tags2  CHAR(128) NOT NULL DEFAULT "",
  tags3  CHAR(128) NOT NULL DEFAULT "",
  FOREIGN KEY (POI_id) REFERENCES POI (POI_id)
    ON UPDATE CASCADE
)
  CHARACTER SET = utf8mb4;

CREATE TABLE post
(
  postid    INT                      AUTO_INCREMENT PRIMARY KEY,
  timestamp INT             NOT NULL,
  userid    CHAR(32)        NOT NULL,
  POI_id    CHAR(30)        NOT NULL,
  latitude  DOUBLE          NOT NULL,
  longitude DOUBLE          NOT NULL,
  `like`    INT DEFAULT '0' NOT NULL,
  dislike   INT DEFAULT '0' NOT NULL,
  text      TEXT            NULL,
  imageUrl  TEXT            NULL,
  tags      BIGINT UNSIGNED NOT NULL DEFAULT 0,
  FOREIGN KEY (userid) REFERENCES user (userid)
    ON UPDATE CASCADE,
  FOREIGN KEY (POI_id) REFERENCES POI (POI_id)
    ON UPDATE CASCADE
)
  CHARACTER SET = utf8mb4;

CREATE INDEX idx_poi_post
  ON post (POI_id, postid); #speed up the History procedure
CREATE INDEX idx_user_time
  ON post (userid, timestamp); #speed up finding a user's POIs in Recommend procedure
CREATE INDEX idx_time
  ON post (timestamp); #speed up the UpdatePOI precedure

CREATE TABLE feedback (
  userid   CHAR(32) NOT NULL,
  postid   INT      NOT NULL,
  attitude TINYINT  NOT NULL DEFAULT 0,
  FOREIGN KEY (userid) REFERENCES user (userid)
    ON UPDATE CASCADE,
  FOREIGN KEY (postid) REFERENCES post(postid)
)
  CHARACTER SET = utf8mb4;

CREATE TABLE sysvar (
  name   CHAR(20) PRIMARY KEY,
  value1 INT
)
  CHARACTER SET = utf8mb4;

/*ALTER TABLE POI
  CHANGE type popularity INT NOT NULL DEFAULT 0;*/

/*SELECT count(*) from POI;
*/