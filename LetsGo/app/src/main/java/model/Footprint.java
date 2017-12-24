package model;


/**
 * Created by 11437 on 2017/12/20.
 */

public class Footprint {
    private String content;
    private String like;
    private String dislike;
    private String nickname;
    private int timestamp;
    private int postid;
    private int attitude;
    public Footprint(String content,String like,String dislike,int timestamp,int postid,int attitude,String nickname){
        this.content=content;
        this.like=like;
        this.dislike=dislike;
        this.timestamp=timestamp;
        this.postid=postid;
        this.attitude=attitude;
        this.nickname=nickname;
    }
    public void setContent(String content){this.content=content;}
    public void setLike(String like){this.like=like;}
    public void setDislike(String dislike){this.dislike=dislike;}
    public void setTimestamp(int timestamp){this.timestamp=timestamp;}
    public void setPostid(int postid){this.postid=postid;}
    public void setAttitude(int attitude){this.attitude=attitude;}
    public void setNickname(String nickname){this.nickname=nickname;}
    public String getContent(){return this.content;}
    public String getLike(){return this.like;}
    public String getDislike(){return this.dislike;}
    public int getTimestamp(){return this.timestamp;}
    public int getPostid(){return this.postid;}
    public int getAttitude(){return this.attitude;}
    public String getNickname(){return this.nickname;}
}
