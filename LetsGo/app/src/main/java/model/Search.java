package model;

/**
 * Created by 11437 on 2017/12/20.
 */

public class Search {
    private String userid;
    private double latitude;
    private double longitude;
    private String token;
    public Search(String userid,double latitude,double longitude,String token){
        this.userid=userid;
        this.latitude=latitude;
        this.longitude=longitude;
        this.token=token;
    }
    public void setUserid(String  userid){
        this.userid=userid;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude){
        this.latitude=latitude;
    }
    public void setToken(String token){
        this.token=token;
    }
    public String getUserid(){return this.userid;}
    public double getLatitude(){return this.latitude;}
    public double getLongitude(){return this.longitude;}
    public String getToken(){return this.token;}
}
