package model;

/**
 * Created by 11437 on 2017/12/20.
 */

public class Post {
    private int postid;
    private String text;
    private int like;
    private int dislike;
    public Post(int postid,String text,int like,int dislike){
        this.postid=postid;
        this.text=text;
        this.like=like;
        this.dislike=dislike;
    }
    public void setPostid(int postid){this.postid=postid;}
    public void setText(String text){this.text=text;}
    public void setLike(int like){this.like=like;}
    public void setDislike(int dislike){this.dislike=dislike;}
    public int getPostid(){return this.postid;}
    public String getText(){return this.text;}
    public int getLike(){return this.like;}
    public int getDislike(){return this.dislike;}
}
