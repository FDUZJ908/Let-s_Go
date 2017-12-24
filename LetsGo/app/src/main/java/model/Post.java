package model;

import java.security.Timestamp;

/**
 * Created by 11437 on 2017/12/20.
 */

public class Post {
    private int postid;
    private String text;
    private String nickname;
    private int love;
    private int dislike;
    private int timestamp;
    private int attitude;
    public Post(int postid,String text,int like,int dislike,int timestamp,int attitude,String nickname){
        this.postid=postid;
        this.text=text;
        this.love=like;
        this.dislike=dislike;
        this.timestamp=timestamp;
        this.attitude=attitude;
        this.nickname=nickname;
    }
    public void setPostid(int postid){this.postid=postid;}
    public void setText(String text){this.text=text;}
    public void setLike(int like){this.love=like;}
    public void setDislike(int dislike){this.dislike=dislike;}
    public void setTimestamp(int timestamp){this.timestamp=timestamp;}
    public void setAttitude(int attitude){this.attitude=attitude;}
    public void setNickname(String nickname){this.nickname=nickname;}
    public int getPostid(){return this.postid;}
    public String getText(){return this.text;}
    public int getLike(){return this.love;}
    public int getDislike(){return this.dislike;}
    public int getTimestamp(){return this.timestamp;}
    public int getAttitude(){return this.attitude;}
    public String getNickname(){return this.nickname;}
}
