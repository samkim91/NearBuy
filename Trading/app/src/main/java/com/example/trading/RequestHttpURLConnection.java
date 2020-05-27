package com.example.trading;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestHttpURLConnection {

    public String request(String _url, ContentValues _params){

        // HttpURLConnection 생성
        HttpURLConnection httpURLConnection = null;

        // URL 뒤에 붙여 보낼 파라미터를 넣을 변수
        StringBuffer sbParams = new StringBuffer();

        // Stringbuffer에 파라미터를 연결함

        if(_params == null){
            // 보낼 파라미터가 없으면 아무것도 붙이지 않는다.
            sbParams.append("");
        }else{
            // 보낼 파라미터가 있다면, 파라미터 갯수에 따라서 &을 붙이거나 아니면 마침

            // 파라미터 갯수 카운트 결과에 따라 &을 붙일지, 끝을 낼지 판단하는 불린
            boolean isMore = false;

            // 파라미터 키와 값
            String key;
            String value;

            // 모든 파라미터 키/값을 넣기 위한 반복문
            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                // 파라미터가 더 있다면, 그 사이에 &를 넣어준다.
                if(isMore){
                    sbParams.append("&");
                }

                // 하나의 파라미터를 연결해준다.
                sbParams.append(key).append("=").append(value);

                // 파라미터가 더 있고, 그게 2개 이상이면 파라미터가 더 있다고 알려주므로 다음으로 반복할 때 &이 붙도록 한다.
                if(!isMore){
                    if(_params.size() >= 2){
                        isMore = true;
                    }
                }
            }
        }

        // HttpURLConnection 을 통해 서버에서 데이터를 가져온다.
        try {
            // URL 주소를 담을 객체 생성
            URL url = new URL(_url);

            // http 전송을 위한 객체 생성. 주소는 위의 url
            httpURLConnection = (HttpURLConnection) url.openConnection();

            // 전송방식은 POST이고, UTF-8 문자 방식
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");

            // Context-Type 은 http 머리 부분(header)에 쓰이며, 보내는 데이터(body)의 정보를 표현한다. text 타입으로는 text/css, text/javascript, text/html, text/plain 등이 있고,
            // file을 실어보내기 위한 타입으로는 multiport/formed-data가 있다. 그리고 application 타입으로는 application/json, application/x-www-form-urlencoded 등이 있다.
            // 요청할 때 보내는 데이터 타입에 따라 적절히 선택해서 사용하면 된다. json과 x-www... 의 차이는 {key:value} 과 key=value&key=value..
            // 브라우저에서 알아서 데이터(body)를 인코딩해서 보내주는 것으로 보이지만, 문제는 직접 요청코드를 작성할 때는 명시해 주는게 오류를 방지한다.
            httpURLConnection.setRequestProperty("Context-Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            String strParams = sbParams.toString();
            // outputStream 생성 및 httpURLConnection 에 지원 ..todo OutputStream/InputStream 에 관한 내용은 SignupActivity 참고
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(strParams.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            // 요청한 내용이 제대로 작동하면 200을 리턴 받는다. HTTP_OK = 200.
            // 따라서, 오작동을 했다면, null을 리턴하게 함.
            if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            // todo 이 부분도 SignupActivity 부분 참고
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            String page = "";

            // 결과물을 합쳐서 리턴
            while ((line = bufferedReader.readLine()) != null){
                page += line;
            }

            return page;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
        return null;
    }
}
