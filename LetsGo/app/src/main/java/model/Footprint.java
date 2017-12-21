package model;

/**
 * Created by 11437 on 2017/12/20.
 */

public class Footprint {
    private String content;
    private String comment;
    public Footprint(String content,String comment){
        this.content=content;
        this.comment=comment;
    }
    public void setContent(String content){this.content=content;}
    public void setComment(String comment){this.comment=comment;}
    public String getContent(){return this.content;}
    public String getComment(){return this.comment;}
}
