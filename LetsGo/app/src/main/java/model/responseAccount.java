package model;

/**
 * Created by 11437 on 2017/12/22.
 */

public class responseAccount {
    private String status;
    private String userid;
    private String token;
    private String nickname;
    private String message;
    private int gender;
    private String Tel;
    private long tags;

    public responseAccount(String userid,String token,String nickname,int gender,long tags){
        this.userid=userid;
        this.token=token;
        this.nickname=nickname;
        this.gender=gender;
        this.tags=tags;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public long getTags() {
        return tags;
    }

    public void setTags(long tags) {
        this.tags = tags;
    }
}
