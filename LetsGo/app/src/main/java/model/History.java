package model;

/**
 * Created by 11437 on 2017/12/20.
 */

public class History {
    private int postid;
    private String POI_id;
    private String token;
    private String userid;
    public History(String POI_id,String token,String userid,int postid){
        this.POI_id=POI_id;
        this.token=token;
        this.userid=userid;
        this.postid=postid;
    }
    public void setPOI_id(String POI_id){
        this.POI_id=POI_id;
    }
    public void setToken(String token){
        this.token=token;
    }
    public void setUserid(String userid){
        this.userid=userid;
    }
    public void setPostid(int postid){this.postid=postid;}
    public String getPOI_id(){return this.POI_id;}
    public String getToken(){return this.token;}
    public String getUserid(){return this.userid;}
    public int getPostid(){return this.postid;}
}
