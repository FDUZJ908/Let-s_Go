package model;

/**
 * Created by 11437 on 2017/12/5.
 */

public class Register {
    private String userid;
    private String code;
    private String password;
    private String status;
    private String nickname;
    private int gender;
    private String Tel;
    private int type;
    public void setNickname(String nickname){this.nickname=nickname;}
    public void setGender(int gender){this.gender=gender;}
    public void setTel(String Tel){this.Tel=Tel;}
    public void setType(int type){this.type=type;}
    public void setUserid(String userid){
        this.userid=userid;
    }
    public void setCode(String code){
        this.code=code;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public String getUserid(){
        return this.userid;
    }
    public String getCode(){
        return this.code;
    }
    public String getPassword(){
        return this.password;
    }
    public String getStatus(){
        return this.status;
    }
    public int getType(){return this.type;}
    public String getNickname(){return this.nickname;}
    public int getGender(){return this.gender;}
    public String getTel(){return this.Tel;}
}

