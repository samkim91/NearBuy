package com.example.trading.Fragments.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.trading.R;

public class SetAddressActivity extends AppCompatActivity {

    private WebView browser;

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {

            // 결과를 보낼 인텐트를 생성함.
            Intent intent = new Intent();
            // 인텐트에 데이터를 넣음.
            intent.putExtra("data", data);
            // 결과가 OK라고 지정하고, 인텐트를 태워보냄
            setResult(RESULT_OK, intent);
            // 이 액티비티 끝! 요청 액티비티로 넘어감.
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);

        browser = (WebView) findViewById(R.id.webView);

        // 웹뷰가 자바스크립트를 사용할 수 있도록 함.
        browser.getSettings().setJavaScriptEnabled(true);
        // 웹뷰가 자바스크립트 인터페이스를 사용할 수 있도록 추가함. 위에서 작성한 인터페이스와 Android라는 이름을 같이 넘김.
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        // 웹뷰에 실제 웹페이지를 불러올 클라이언트를 설정함.
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // 아래서 지정한 웹페이지 로딩이 끝났다면, 여기서 지정한 자바스크립트 함수 위치로 이동하게끔 한다.
                browser.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        // '다음'에서 주소를 불러오는 내용을 담고 있는 서버 위치
       browser.loadUrl("http://15.165.57.108/loadAddress.php");

    }

    // 다음에서 주소를 가져오는
//    WebView webView;
//    TextView webView_result;
//    Handler handler;
//
//    String TAG = "SetAddressActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Log.d(TAG, "onCreate");
//
//        setContentView(R.layout.activity_set_address);
//
//        webView = findViewById(R.id.webView);
//
//        webView_result = findViewById(R.id.webView_result);
//
//        // 웹뷰를 초기화 한다.
//        initWebView();
//
//        // 핸들러를 사용해서, 자바스크립트를 가져온다.
//        handler = new Handler();
//
//    }
//
//    public void initWebView(){
//
//        Log.d(TAG, "initWebView");
//
//        // 웹뷰가 자바스크립트를 사용할 수 있도록 허용한다.
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        // 웹뷰에게 자바스크립트의 window.open을 허용한다.
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//
//        // 웹뷰에게 자바스크립트 이벤트에 반응할 수 있는 클래스를 넣어준다.
//        webView.addJavascriptInterface(new AndroidBridge(), "Trading");
//
//        // 웹뷰의 클라이언트를 크롬으로 설정
//        webView.setWebChromeClient(new WebChromeClient());
//
//        // 웹뷰를 로드할 주소
//        webView.loadUrl("http://15.165.57.108/loadAddress.php");
//    }
//
//    private class AndroidBridge{
//
//        @JavascriptInterface
//        public void setAddress(final String arg1, final String arg2, final String arg3){
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    webView_result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
//
//                    initWebView();
//
//                }
//            });
//        }
//    }
}
