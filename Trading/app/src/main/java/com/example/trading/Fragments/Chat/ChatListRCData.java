package com.example.trading.Fragments.Chat;

public class ChatListRCData {

    String roomId;
    String uImage;
    String uId;
    String uNickname;
    String content;
    String date;

    public ChatListRCData(String roomId, String uImage, String uId, String uNickname, String content, String date) {
        this.roomId = roomId;
        this.uImage = uImage;
        this.uId = uId;
        this.uNickname = uNickname;
        this.content = content;
        this.date = date;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getuNickname() {
        return uNickname;
    }

    public void setuNickname(String uNickname) {
        this.uNickname = uNickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
