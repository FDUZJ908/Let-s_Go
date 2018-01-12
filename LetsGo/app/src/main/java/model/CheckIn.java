package model;

/**
 * Created by 11437 on 2017/12/21.
 */

public class CheckIn {
    private String POI_id;
    private String userid;
    private String token;
    private double latitude;
    private double longitude;
    private String text;
    private String image;
    private String format;
    private long tags;
    public CheckIn(String POI_id,String userid, String token,double latitude,double longitude,String text,String image,String format,long tags){
        this.POI_id=POI_id;
        this.userid=userid;
        this.token=token;
        this.latitude=latitude;
        this.longitude=longitude;
        this.text=text;
        this.tags=tags;
        this.image=image;
        this.format=format;
    }
    public CheckIn(String POI_id,String userid, String token,double latitude,double longitude,String text,long tags){
        this.POI_id=POI_id;
        this.userid=userid;
        this.token=token;
        this.latitude=latitude;
        this.longitude=longitude;
        this.text=text;
        this.tags=tags;
    }

    public void setPOI_id(String POI_id){this.POI_id=POI_id;}
    public void setUserid(String userid){this.userid=userid;}
    public void setToken(String token){this.token=token;}
    public void setLatitude(double latitude){this.latitude=latitude;}
    public void setLongitude(double longitude){this.longitude=longitude;}
    public void setText(String text){this.text=text;}
    public void setImage(String image){this.image=image;}
    public void setTags(long tags){this.tags=tags;}

    public String getPOI_id(){return this.POI_id;}
    public String getUserid(){return this.userid;}
    public String getToken(){return this.token;}
    public double getLatitude(){return  this.latitude;}
    public double getLongitude(){return  this.longitude;}
    public String getText(){return this.text;}
    public String getImage(){return this.image;}
    public long getTags(){return this.tags;}
}
