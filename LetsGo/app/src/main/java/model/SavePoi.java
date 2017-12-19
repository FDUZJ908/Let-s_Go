package model;

import java.util.ArrayList;

/**
 * Created by 11437 on 2017/12/19.
 */

public class SavePoi {
    private int POI_num;
    private String category;
    private ArrayList<MyPoiInfo> POIs;

    public int getPOI_num(){return this.POI_num;}
    public String getCategory(){return this.category;}
    public ArrayList<MyPoiInfo> getPOIs(){return this.POIs;}

    public void setPOI_num(int POI_num){this.POI_num=POI_num;}
    public void setCategory(String category){this.category=category;}
    public void setPOIs(ArrayList<MyPoiInfo> POIs){this.POIs=POIs;}
}
