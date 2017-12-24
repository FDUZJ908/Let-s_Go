package model;

/**
 * Created by 11437 on 2017/12/22.
 */

public class Account {
    private String userid;
    private String token;
    public Account(String userid,String token){
        this.userid=userid;
        this.token=token;
    }
    public void setUserid(String userid){
        this.userid=userid;
    }
    public void setToken(String token){
        this.token=token;
    }
    public String getUserid(){return this.userid;}
    public String getToken(){return this.token;}
}
