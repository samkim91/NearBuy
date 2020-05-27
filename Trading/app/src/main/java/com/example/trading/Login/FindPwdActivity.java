package com.example.trading.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trading.MainActivity;
import com.example.trading.R;
import com.example.trading.RequestHttpURLConnection;

public class FindPwdActivity extends AppCompatActivity {

    EditText mPhoneNum, mEmail;
    Button findPasswordBtn;

    String TAG = "FindPwdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);

        mPhoneNum = findViewById(R.id.phoneNum);
        mEmail = findViewById(R.id.email);
        findPasswordBtn = findViewById(R.id.findPasswordBtn);

        findPasswordBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPwdEvent();
            }
        });


    }

    public void findPwdEvent(){
        Log.d(TAG, "findPwdEvent");

        // 접근할 서버주소와 가져갈 값들을 담은 contentvalue를 선언
        String url = "http://15.165.57.108/Login/findpwd.php";
        ContentValues contentValues = new ContentValues();

        // 데이터베이스에서 +를 저장이 안되는 오류가 있었기에 제거
        String phoneNumFromUI = mPhoneNum.getText().toString();
        int idx = phoneNumFromUI.indexOf("+");
        String phoneNum = phoneNumFromUI.substring(idx+1);
        // 입력된 이메일 주소 가져옴.
        String email = mEmail.getText().toString();

        // 받아온 값들을 contentValue에 넣어준다.
        contentValues.put("phoneNum", phoneNum);
        contentValues.put("email", email);

        // 비밀번호 찾기를 요청하는 객체를 만들고 값을 넣어서 집행한다.
        RequestFindPwd requestFindPwd = new RequestFindPwd(url, contentValues);
        requestFindPwd.execute();

    }

    // AsyncTask <params, progress, result>..
    // 1. params 는 이 객체를 execution(집행)할 때 태스크에 보내질 파라미터 타입
    // 2. progress 는 백그라운드 연산 동안 나올 프로그레스 유닛의 타입
    // 3. result 는 백그라운드 연산 결과의 타입
    public class RequestFindPwd extends AsyncTask<String, Void, String>{

        String url;
        ContentValues values;

        public RequestFindPwd(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground");

            String result;

            //httpURLconn 을 위한 객체를 선언하고, 위에서 받은 url과 값을 넣어준다. 이는 post 방식으로 서버에 요청됨
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, "onPostExecute"+s);

            if(s.contains("ok")){
                // 로그인 성공! 메인 화면으로 간다.
                Toast.makeText(FindPwdActivity.this, "임시비밀번호가 발송되었어요!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }else if(s.equals("wrong")){
                Toast.makeText(FindPwdActivity.this, "이메일 주소가 일치하지 않아요!", Toast.LENGTH_SHORT).show();
            }else if(s.equals("null")) {
                Toast.makeText(FindPwdActivity.this, "존재하지 않는 휴대폰 번호네요.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(FindPwdActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
