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
  ON post (POI_id, postid); #speed up the History procedure
CREATE INDEX idx_user_time
  on post (userid,timestamp); #speed up finding a user's POIs in Recommend procedure
CREATE INDEX idx_poi_time
  ON post (POI_id, timestamp); #speed up the UpdatePOITags precedure

CREATE TABLE POITags (
  POI_id CHAR(30) PRIMARY KEY,
  tags1  CHAR(128) NOT NULL DEFAULT "",
  tags2  CHAR(128) NOT NULL DEFAULT "",
  tags3  CHAR(128) NOT NULL DEFAULT "",
  FOREIGN KEY (POI_id) REFERENCES POI (POI_id)
    ON UPDATE CASCADE
)
  CHARACTER SET = utf8mb4;

DROP TABLE user;
DROP TABLE POI;
DROP TABLE post;
# SELECT COUNT(POI_name) from POI where POI_name like '梯%';
# SELECT * from POI where POI_name like '%银行%';
# DELETE FROM POI where POI_name like '%洗手间%';
# SELECT COUNT(*) from POI where category in ('教育学校','生活服务','餐饮美食');
# SELECT DISTINCT(POI_name) from POI where category in ('生活服务');
/*EXPLAIN SELECT
          category,
          MAX(latitude)
        FROM POI
        WHERE city = '上海市'
        GROUP BY category
        LIMIT 5;*/

SELECT * from POI where category like '%生活%';