package com.example.trading.Fragments.Home;

public class HomeRCData {

    //리사이클러뷰의 한 아이템을 담을 클래스

    int no;
    String phoneNum;
    String uri;
    String title;
    String location;
    String time;
    String category;
    String price;
    String comment;
    String marked;
    String status;

    public HomeRCData(){

    }

    public HomeRCData(int no, String uri, String phoneNum, String title, String location, String time, String category, String price, String comment, String marked, String status){
        this.no = no;
        this.uri = uri;
        this.phoneNum = phoneNum;
        this.title = title;
        this.location = location;
        this.time = time;
        this.category = category;
        this.price = price;
        this.comment = comment;
        this.marked = marked;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMarked() {
        return marked;
    }

    public void setMarked(String marked) {
        this.marked = marked;
    }
}
