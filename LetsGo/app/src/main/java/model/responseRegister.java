package model;

/**
 * Created by 11437 on 2017/12/11.
 */

public class responseRegister {
    private  String status;
    private String token;
    private String message;
    private int timestamp;
    private int postid;
    public void setStatus(String status){this.status=status;}
    public void setToken(String token){this.token=token;}
    public void setMessage(String message){this.message=message;}
    public void setTimestamp(int timestamp){this.timestamp=timestamp;}
    public void setPostid(int postid){this.postid=postid;}
    public String getStatus(){return this.status;}
    public String getToken(){return this.token;}
    public String getMessage(){return this.message;}
    public int getTimestamp(){return this.timestamp;}
    public int getPostid(){return this.postid;}
}
