package model;

/**
 * Created by 11437 on 2017/12/22.
 */

public class Attitude {
    private int postid;
    private int attitude;
    public Attitude(int postid,int attitude){this.postid=postid;this.attitude=attitude;}
    public void setPostid(int postid){this.postid=postid;}
    public void setFeedback(int attitude){this.attitude=attitude;}
    public int getPostid(){return  this.postid;}
    public int getFeedback(){return  this.attitude;}
}
