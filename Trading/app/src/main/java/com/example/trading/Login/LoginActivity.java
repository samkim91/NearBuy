package com.example.trading.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.trading.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";
    // xml에 만들어놓은 개체들을 선언
    EditText mPhoneNum, mPassword;
    Button loginBtn, findPasswordBtn, signUpBtn;

    String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //xml 에 만들어 놓은 개체들을 연결
        mPhoneNum = findViewById(R.id.phoneNum);
        mPassword = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        findPasswordBtn = findViewById(R.id.findPasswordBtn);
        signUpBtn = findViewById(R.id.signUpBtn);



        //버튼 클릭 이벤트들을 모아놓은 메소드
        clickListeners();

    }

    // AsyncTask <params, progress, result>..
    // 1. params 는 이 객체를 execution(집행)할 때 태스크에 보내질 파라미터 타입
    // 2. progress 는 백그라운드 연산 동안 나올 프로그레스 유닛의 타입
    // 3. result 는 백그라운드 연산 결과의 타입
    public class RequestLogin extends AsyncTask<String, Void, String>{

        // AlertDialog dialog;
        String url;
        ContentValues values;

        public RequestLogin(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "onPreExecute");

            // 진행 중이라는 다이얼로그를 띄워준다.
            // AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            // builder.setView(R.layout.progressbar);
            // dialog = builder.create();
            // dialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, "onPostExecute");
            // 작업이 끝나면 다이얼로그를 지워준다.
            //dialog.dismiss();

            if(!s.equals("wrong")||!s.equals("null")){

                Log.i(TAG, "user info : "+s);
                // 로그인 성공! 메인 화면으로 간다.
//                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                // 로그인한 유저를 유지시키기 위해 휴대폰 번호를 메모리에 올려놓음. 쉐어드프리퍼런스를 사용하는 방법도 있으나, 필요할 때마다 쉐어드프리퍼런스를 열어야하는 문제가 조금 있음.
                // 보안적인 이슈는 없는지 확인해볼 필요 있음.
                UserInfo.phoneNum = phoneNum;

                try {
                    JSONObject jsonObject = new JSONObject(s);

                    UserInfo.setImage(jsonObject.getString("image"));
                    UserInfo.setNickname(jsonObject.getString("nickname"));

                    // 자동로그인에 휴대폰 번호를 넘겨서 저장.
                    saveLogin(phoneNum, jsonObject.getString("image"), jsonObject.getString("nickname"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }




                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // 액티비티 스택에서 지난 액티비티를 모두 삭제하고, 새로운 스택으로 시작하겠다는 플래그를 넣엉줌.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                startActivity(intent);
            }else if(s.equals("wrong")){
                Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }else if(s.equals("null")){
                Toast.makeText(LoginActivity.this, "존재하지 않는 휴대폰 번호입니다.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;

            //httpURLconn 을 위한 객체를 선언하고, 위에서 받은 url과 값을 넣어준다. 이는 post 방식으로 서버에 요청됨
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);

            return result;
        }
    }

    public void clickListeners(){
        //로그인 버튼을 누르면, 입력값을 http 통신으로 서버에서 검증을 함
        loginBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 접근할 서버주소와 가져갈 값들을 담은 contentvalue를 선언
                String url = "http://15.165.57.108/Login/login.php";
                ContentValues contentValues = new ContentValues();

                // 에딭텍스트에 입력된 값을 받아옴
                String phoneNumFromUI = mPhoneNum.getText().toString();
                // 데이터베이스에서 +를 저장이 안되는 오류가 있었기에 제거
                int idx = phoneNumFromUI.indexOf("+");

                phoneNum = phoneNumFromUI.substring(idx+1);
                String password = mPassword.getText().toString();

                // 받아온 값들을 contentValue에 넣어준다.
                contentValues.put("phoneNum", phoneNum);
                contentValues.put("password", password);

                // 로그인을 요청하는 객체를 만들고 값을 넣어서 집행한다.
                RequestLogin requestLogin = new RequestLogin(url, contentValues);
                requestLogin.execute();
            }
        });

        // 회원가입 버튼을 누르면, 인증 창으로 이동함.
        signUpBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 찾기 버튼을 누르면, 해당 액티비티로 이동함.
        findPasswordBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindPwdActivity.class);
                startActivity(intent);
            }
        });
    }

    // 자동 로그인을 위해 로그인 시 정보를 저장하는 기능
    public void saveLogin(String phoneNum, String image, String nickname){
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("auto", true);
        editor.putString("phoneNum", phoneNum);
        editor.putString("image", image);
        editor.putString("nickname", nickname);

        editor.commit();
    }

}
