package com.example.trading.Fragments.Post;

public class CommentRCData {

    String uImage;
    String uId;
    String uNickname;
    String uTime;
    String uComment;
    String commentNum;
    String parent;
    int sequence;

    public CommentRCData(){

    }

    public CommentRCData(String uImage, String uId, String uNickname, String uTime, String uComment, String commentNum, String parent, int sequence) {
        this.uImage = uImage;
        this.uId = uId;
        this.uNickname = uNickname;
        this.uTime = uTime;
        this.uComment = uComment;
        this.commentNum = commentNum;
        this.parent = parent;
        this.sequence = sequence;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getuImage() {
        return uImage;
    }

    public void setuImage(String uImage) {
        this.uImage = uImage;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuTime() {
        return uTime;
    }

    public void setuTime(String uTime) {
        this.uTime = uTime;
    }

    public String getuComment() {
        return uComment;
    }

    public void setuComment(String uComment) {
        this.uComment = uComment;
    }

    public String getuNickname() {
        return uNickname;
    }

    public void setuNickname(String uNickname) {
        this.uNickname = uNickname;
    }
}
