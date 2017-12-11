package model;

/**
 * Created by 11437 on 2017/12/11.
 */

public class User {
    private  String userid;
    private  String password;
    public void setUserid(String userid){this.userid=userid;}
    public void setPassword(String password){this.password=password;}
    public String getUserid(){return this.userid;}
    public String getPassword(){return this.password;}
}
