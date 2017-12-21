package model;

import java.util.List;

/**
 * Created by 11437 on 2017/12/20.
 */

public class responseHistory {
    private String status;
    private int post_num;
    private List<Post> posts;
    public responseHistory(String status,int post_num,List<Post>posts){
        this.status=status;
        this.post_num=post_num;
        this.posts=posts;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public void setPost_num(int post_num){this.post_num=post_num;}
    public void setPosts(List<Post> posts){this.posts=posts;}
    public String getStatus(){return this.status;}
    public int getPost_num(){return this.post_num;}
    public List<Post> getPosts(){return this.posts;}
}
