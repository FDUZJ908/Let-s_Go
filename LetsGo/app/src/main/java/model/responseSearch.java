package model;

import java.util.List;

/**
 * Created by 11437 on 2017/12/20.
 */

public class responseSearch {
    private String status;
    private int POI_num;
    private List<MyPoiInfo> POIs;
    public responseSearch(String status,int POI_num,List<MyPoiInfo> POIs){
        this.status=status;
        this.POI_num=POI_num;
        this.POIs=POIs;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public void setPOI_num(int POI_num){
        this.POI_num=POI_num;
    }
    public void setPOIs(List<MyPoiInfo> POIs){
        this.POIs=POIs;
    }
    public String getStatus(){return this.status;}
    public int getPOI_num(){return this.POI_num;}
    public List<MyPoiInfo> getPOIs(){return this.POIs;}
}
