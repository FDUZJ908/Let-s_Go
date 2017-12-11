package model;

/**
 * Created by 11437 on 2017/12/11.
 */

public class responseRegister {
    private  String status;
    private String token;
    private String message;
    public void setStatus(String status){this.status=status;}
    public void setToken(String token){this.token=token;}
    public void setMessage(String message){this.message=message;}
    public String getStatus(){return this.status;}
    public String getToken(){return this.token;}
    public String getMessage(){return this.message;}
}
