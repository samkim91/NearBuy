package com.example.trading;

import java.net.Socket;

public class UserInfo {

    public static String phoneNum;

    public static Socket socket;

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
