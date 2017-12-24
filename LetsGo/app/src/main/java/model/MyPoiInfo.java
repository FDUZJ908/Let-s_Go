package model;

/**
 * Created by 11437 on 2017/12/19.
 */

public class MyPoiInfo {
    String city;
    double latitude;
    double longitude;
    String POI_name;
    String category;
    String POI_id;
    int popularity;

    public MyPoiInfo MyPoiInfo(String city,double Lat,double Lng,String name,String category,String uid,int popularity){
        this.city=city;
        this.latitude=Lat;
        this.longitude=Lng;
        this.POI_name=name;
        this.category=category;
        this.POI_id=uid;
        this.popularity=popularity;
        return this;
    }

    public void setCity(String city){this.city=city;}
    public void setLat(double Lat){this.latitude=Lat;}
    public void setLng(double Lng){this.longitude=Lng;}
    public void setName(String name){this.POI_name=name;}
    public void setCategory(String category){this.category=category;}
    public void setUid(String uid){this.POI_id=uid;}
    public void setPopularity(int popularity){this.popularity=popularity;}

    public String getCity(){return this.city;}
    public double getLat(){return this.latitude;}
    public double getLng(){return this.longitude;}
    public String getName(){return this.POI_name;}
    public String getCategory(){return this.category;}
    public String getUid(){return this.POI_id;}
    public int getPopularity(){return this.popularity;}
}
