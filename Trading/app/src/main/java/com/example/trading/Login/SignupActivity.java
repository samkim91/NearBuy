package com.example.trading.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trading.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SignupActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "15.165.57.108";
    String TAG = "SingupActivity";

    //xml 개체들 선언
    TextView phoneNum;
    TextView textWatch1, textWatch2;
    EditText mPassword, mPasswordConfirm;
    EditText mNickname;
    EditText mEmail;
    Button signUpBtn;
    boolean passwordsconfirmed;

    String phoneNumFromIntent;
    String phoneNumForSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // xml 개체들 연결
        phoneNum = findViewById(R.id.phoneNum);
        textWatch1 = findViewById(R.id.textwatch1);
        textWatch2 = findViewById(R.id.textwatch2);
        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.passwordconfirm);
        mNickname = findViewById(R.id.nickname);
        mEmail = findViewById(R.id.email);
        signUpBtn = findViewById(R.id.signUpBtn);

        // 휴대폰 번호인증 액티비티에서 받아온 휴대폰 번호를 띄워주기 위해 인텐트를 선언
        Intent intent = getIntent();
        phoneNumFromIntent = intent.getExtras().getString("phoneNum");
        int idx = phoneNumFromIntent.indexOf("+");
        phoneNumForSave = phoneNumFromIntent.substring(idx+1);
        // 텍스트뷰를 인텐트에서 꺼낸 휴대폰 번호로 지정
        phoneNum.setText("휴대폰 번호 : +" + phoneNumForSave);

        passwordsConfirm();

        // 가입하기 버튼에 클릭 리스너를 선언. 클릭 리스너 안에는 어싱크태스크로 http를 통해서 서버와 통신하는 작업을 함.
        signUpBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = phoneNumForSave;
                String password = mPassword.getText().toString();
                String nickname = mNickname.getText().toString();
                String email = mEmail.getText().toString();

                //비밀번호가 일치하는지 결과에 의해 작동
                if(passwordsconfirmed){
                    if(nickname.equals("")){
                        Toast.makeText(SignupActivity.this, "닉네임을 채워주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        if(email.equals("")){
                            Toast.makeText(SignupActivity.this, "이메일을 채워주세요.", Toast.LENGTH_SHORT).show();
                        }else{
                            //어싱크태스크 클래스 객체화 및 실행
                            InsertData task = new InsertData();
                            task.execute("http://"+IP_ADDRESS+"/Login/signup.php", phoneNum, password, nickname, email);

                            // 어싱크태스크의 결과를 받아서 토스트로 띄워주자.
                            try {
                                // 여기서 겟은 두인백그라운드 리턴 값을 가져온다는 것.
                                String result = task.get();
                                Toast.makeText(SignupActivity.this, result, Toast.LENGTH_SHORT).show();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    Toast.makeText(SignupActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // AsyncTask <params, progress, result>..
    // 1. params 는 이 객체를 execution(집행)할 때 태스크에 보내질 파라미터 타입
    // 2. progress 는 백그라운드 연산 동안 나올 프로그레스 유닛의 타입
    // 3. result 는 백그라운드 연산 결과의 타입
    class InsertData extends AsyncTask<String, Void, String>{


       // AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "onPreExecute");

            // 진행 중이라는 다이얼로그를 띄워준다.
           // AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
//            builder.setCancelable(false);   // 화면을 눌러서 다이얼로그를 취소하게 하는 기능을 없앰.
          //  builder.setView(R.layout.progressbar);
           // dialog = builder.create();
          //  dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute");
            // 작업이 끝나면 다이얼로그를 지워준다.
            // dialog.dismiss();

            // 로그인 화면으로 돌아간다.
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground");
            // 어싱크태스크 실행하면서 받아온 파라미터 값들을 가시화
            String serverURL = strings[0];
            String phoneNum = strings[1];
            String password = strings[2];
            String nickname = strings[3];
            String email = strings[4];

            // POST로 서버에 보낼 값
            String postParameters = "phoneNum=" + phoneNum + "&password=" + password + "&nickname=" + nickname + "&email=" + email;

            try {
                // 위에서 가져온 서버 주소를 선언
                URL url = new URL(serverURL);

                // httpURL 전송을 위한 객체를 선언. 위에서 선언한 주소를 연걸점으로 지정
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(8000);     // 응답에 대한 타임아웃을 걸어놓는다. 현재는 5초.. todo 몇 초로 하는게 맞는지 고민 필요
                httpURLConnection.setConnectTimeout(8000);  // 연결에 대한 타임아웃을 걸어놓는다. 현재는 5초.. todo 몇 초로 하는게 맞는지 고민 필요
                httpURLConnection.setRequestMethod("POST"); // 요청방식을 post로 한다. get으로 하면 주소에 노출되므로..
                httpURLConnection.connect();                // 연결 실행

                // 출력 스트림 객체를 만들고, httpURLConnection에 출력 스트림을 줌.
                OutputStream outputStream = httpURLConnection.getOutputStream();
                // 값을 바이트 단위로 보내는데 이때, 형식을 utf-8로 사용. 유니코드가 깨지는 것을 방지
                // UTF-8 은 유니코드를 위한 가변 길이 문자 인코딩 방식 중 하나. 이전에 ASCII는 알파벳 외에 다른 언어를 표현하는데 제한되어 유니코드가 나옴.(한 문자당 1~4바이트 크기를 가짐)
                outputStream.write(postParameters.getBytes("UTF-8"));

                // 출력 스트림 내부에 작은 버퍼가 있는데 이를 비워주는 것. 버퍼에 데이터가 남지 않게 하기 위함.
                outputStream.flush();
                // 출력 스트림을 닫음. 리소스 낭비를 방지하기 위함.
                outputStream.close();

                // 응답을 읽기 위해 응답코드를 선언하고 httpURLConnection 객체로부터 받아온다.
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code : "+ responseStatusCode);

                // 읽기 위한 inputStream 을 선언
                InputStream inputStream;

                // 응답코드가 ok 인지 확인(code : 200)
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    // ok 이면 httpURLConnection 객체에 인풋스트림을 준다.
                    inputStream = httpURLConnection.getInputStream();
                    Log.d(TAG, inputStream.toString());
                }else{
                    // not ok 이면, 에러 출력을 하게 한다.
                    inputStream = httpURLConnection.getErrorStream();
                    Log.d(TAG, inputStream.toString());
                }

                // 인풋스트림 리더를 선언, 인풋스트림 리더는 바이트스트림을 문자스트림으로 변환하는 역할을 한다. 여기선 바이트를 utf-8 형식으로 변환한다는 얘기.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                Log.d(TAG, inputStreamReader.toString());
                // 데이터를 입력받는 역할을 함. 버퍼드는 큰 데이터 받기 위해 버퍼에 저장해서 받는다는 의미.
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Log.d(TAG, bufferedReader.toString());

                // StringBuilder 는 문자열을 합쳐서 새로운 문자열을 만드는(concat) 기능을 가짐. StringBuffer도 같은 기능을 가지는데, 다만 stringbuffer는 동기화 기능을 지원함.(todo.. 멀티 쓰레드에서 쓰인다? 이건 더 공부..)
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;

                // 버퍼드리더를 이용해서 위에서 선언한 스트링빌더에 값을 모두 붙여준다.
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }

                // 버퍼드리더를 닫음. 리소스 낭비 방지
                bufferedReader.close();
                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "InsertData Error : ", e);
                return "Error: "+ e.getMessage();
            }


        }
    }

    public void passwordsConfirm(){
        // 비밀번호란과 비밀번호 확인란이 같은지 텍스트 와쳐를 통해 검사해서 결과를 알려줌.
        mPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 비밀번호와 비밀번호 확인이 일치하는지 검사하고, 여부에 따라 참/거짓을 만듦
                if(mPassword.getText().toString().equals(mPasswordConfirm.getText().toString())){
                    passwordsconfirmed = true;
                    textWatch2.setVisibility(View.INVISIBLE);
                }else{
                    passwordsconfirmed = false;
                    textWatch2.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
