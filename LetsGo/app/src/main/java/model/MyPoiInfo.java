package model;

/**
 * Created by 11437 on 2017/12/19.
 */

public class MyPoiInfo {
    String city;
    double latitude;
    double longitude;
    String POI_name;
    int type;
    String POI_id;

    public MyPoiInfo MyPoiInfo(String city,double Lat,double Lng,String name,int type,String uid){
        this.city=city;
        this.latitude=Lat;
        this.longitude=Lng;
        this.POI_name=name;
        this.type=type;
        this.POI_id=uid;
        return this;
    }

    public void setCity(String city){this.city=city;}
    public void setLat(double Lat){this.latitude=Lat;}
    public void setLng(double Lng){this.longitude=Lng;}
    public void setName(String name){this.POI_name=name;}
    public void setType(int type){this.type=type;}
    public void setUid(String uid){this.POI_id=uid;}

    public String getCity(){return this.city;}
    public double getLat(){return this.latitude;}
    public double getLng(){return this.longitude;}
    public String getName(){return this.POI_name;}
    public int getType(){return this.type;}
    public String getUid(){return this.POI_id;}
}
