package model;

import java.util.List;

/**
 * Created by 11437 on 2017/12/22.
 */

public class Feedback {
    private String userid;
    private String token;
    private int feedback_num;
    private List<Attitude> feedbacks;

    public Feedback(String userid,String token,int feedback_num,List<Attitude>feedbacks){
        this.userid=userid;
        this.token=token;
        this.feedback_num=feedback_num;
        this.feedbacks=feedbacks;
    }

    public void setUserid(String userid ){this.userid=userid;}
    public void setToken(String token){this.token=token;}
    public void setFeedback_num(int feedback_num){this.feedback_num=feedback_num;}
    public void setFeedbacks(List<Attitude> feedbacks){this.feedbacks=feedbacks;}

    public String getUserid(){return this.userid;}
    public String getToken(){return this.token;}
    public int getFeedback_num(){return this.feedback_num;}
    public List<Attitude> getFeedbacks(){return this.feedbacks;}

}
