package com.example.trading;

public class ResponseOnly {

    // 이 변수명은 서버단에서 json 코드로 보내줄 키값과 같아야 그 밸류를 얻을 수 있다.
    String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
