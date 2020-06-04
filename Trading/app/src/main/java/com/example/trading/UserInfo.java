package com.example.trading;

import java.net.Socket;

public class UserInfo {

    public static String phoneNum;

    public static Socket socket;

    public static String nickname;

    public static String image;

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String nickname) {
        UserInfo.nickname = nickname;
    }

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        UserInfo.image = image;
    }

    public static String getPhoneNum() {
        return phoneNum;
    }

    public static void setPhoneNum(String phoneNum) {
        UserInfo.phoneNum = phoneNum;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        UserInfo.socket = socket;
    }
}
