package model;

/**
 * Created by 11437 on 2017/12/24.
 */

public class Recommend {
    private String userid;
    private double latitude;
    private double longitude;
    private String token;
    private long tags;

    public Recommend(String userid, double latitude, double longitude, String token, long tags) {
        this.userid = userid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.token = token;
        this.tags = tags;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTags() {
        return tags;
    }

    public void setTags(long tags) {
        this.tags = tags;
    }

}
