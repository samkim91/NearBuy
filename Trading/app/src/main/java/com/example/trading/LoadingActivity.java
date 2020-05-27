package com.example.trading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.trading.Login.LoginActivity;

public class LoadingActivity extends AppCompatActivity {

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // 이전에 로그인한 이력이 있다고 하면, 자동으로 로그인 되도록 함.
                loadLogin();
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 핸들러에 지정 시간의 딜레이를 두고 위에서 선언한 러너블을 실행하게 한다.
        handler.postDelayed(runnable, 1500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 핸들러에 들어가 있는 로딩 러너블을 삭제한다.
        handler.removeCallbacks(runnable);

    }

    // 이전에 로그인했는지 검사하는 기능(자동로그인을 위함)
    public void loadLogin(){
        // 쉐어드프리퍼런스에 로그인 정보가 남아 있는지 접근하기 위해 선언.
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        Boolean loginBefore = sharedPreferences.getBoolean("auto", false);

        // 로그인 정보가 남아있다면 바로 메인페이지로 넘어가고, 아니라면 로그인 페이지로 이동.
        if(loginBefore == true){
            String phoneNumFromShared = sharedPreferences.getString("phoneNum", "");
            UserInfo.phoneNum = phoneNumFromShared;

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            // 액티비티 스택에서 지난 액티비티를 모두 삭제하고, 새로운 스택으로 시작하겠다는 플래그를 넣엉줌.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(intent);

        }else{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            // 액티비티 스택에서 지난 액티비티를 모두 삭제하고, 새로운 스택으로 시작하겠다는 플래그를 넣엉줌.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(intent);

        }
    }
}
