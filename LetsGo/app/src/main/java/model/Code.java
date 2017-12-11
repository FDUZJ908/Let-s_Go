package model;

/**
 * Created by 11437 on 2017/12/11.
 */

public class Code {
    private String status;
    private String code;
    private String message;
    public void setStatus(String status){
        this.status=status;
    }
    public void setCode(String code){
        this.code=code;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public String getStatus(){
        return this.status;
    }
    public String getCode(){return this.code;}
    public String getMessage(){return this.message;}
}
