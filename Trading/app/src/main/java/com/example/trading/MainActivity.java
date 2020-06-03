package com.example.trading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trading.Fragments.Fragment_category;
import com.example.trading.Fragments.Chat.Fragment_chat;
import com.example.trading.Fragments.Home.Fragment_home;
import com.example.trading.Fragments.Profile.Fragment_profile;
import com.example.trading.Fragments.Post.WriteActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "MainAcitivy";

    // FragmentManager 는 액티비티 내에 선언된 프래그먼트들을 관리하는 역할임. 이것이 하는 일은
    // 1. 액티비티 내에 존재하는 프래그먼트를 findFragmentById()나 findFragmentByTag()로 가져옴. 둘의 차이는 전자는 화면에 UI를 뿌리는 프래그먼트이고, 후자는 UI를 뿌리지 않음
    // 2. popBackStack() 을 사용해서 프래그먼트를 백스택에서 빼온다.
    // 3. 백 스택에 변경 내용이 있는지 알아보기 위해 addOnBackStackChangedListener()로 리스너를 등록할 수 있음.
    // 그리고 여기서 가장 중요한 역할인 FragmentTransaction을 만들 수 있다. (이로써, 프래그먼트 추가, 제거 등의 트랜잭션을 할 수 있음)
    // 기타 내용은 안드로이드 공식문서를 참고하자.
    FragmentManager fragmentManager;

    // FragmentTransaction 을 알기 위해 Transaction에 대해 먼저 알아보면, 트랜잭션은 어떤 것을 추가, 제거, 변경 등의 작업들이 발생하는 것을 의미
    // 그래서 프래그먼트를 추가, 제거, 변경하는 작업들이 발생하는 것을 프래그먼트트랜잭션이라 볼 수 있음
    // 프래그먼트 트랜잭션은 위에서 정의한 것 뿐만 아니라 프래그먼트 백스택 관리, 프래그먼트 전환 애니메이션 설정 등의 역할도 할 수 있음
    FragmentTransaction transaction;

    // 근데 백 스택이란 뭐지?
    // 프래그먼트 백스택은 현재 실행하려는 트랜잭션의 상태를 기억해두기 위해 만들어진 개념이다. 이 개념을 이용하면 스마트폰에서 뒤로가기 버튼을 통해 프래그먼트를 이전 상태로 되돌릴 수 있음
    // 마치 액티비티 사이를 뒤로가기 버튼으로 돌아가는 것 처럼... 이 기능을 구현하려면 transaction block 안에 addToBackStack() 메소드를 호출해주면 된다. transaction.addToBackStack(null); 이런식으로

    // 프래그먼트들의 클래스들을 선언
    Fragment_home fragment_home;
    Fragment_category fragment_category;
    Fragment_chat fragment_chat;
    Fragment_profile fragment_profile;

    // 버튼들 선언
    Button home_btn, category_btn, write_btn, chat_btn, profile_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버튼들 연결
        home_btn = findViewById(R.id.home_btn);
        category_btn = findViewById(R.id.category_btn);
        write_btn = findViewById(R.id.write_btn);
        chat_btn = findViewById(R.id.chat_btn);
        profile_btn = findViewById(R.id.profile_btn);

        // 버튼들에 클릭 이벤트 설정
        home_btn.setOnClickListener(this);
        category_btn.setOnClickListener(this);
        write_btn.setOnClickListener(this);
        chat_btn.setOnClickListener(this);
        profile_btn.setOnClickListener(this);

        // 선언된 클래스들의 새로운 객체를 만듦
        fragment_home = new Fragment_home();
        fragment_category = new Fragment_category();
        fragment_chat = new Fragment_chat();
        fragment_profile = new Fragment_profile();

        // 기본화면은 0번인 홈화면
        setFrag(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 서비스 시작
//        Intent intent = new Intent(this, LocalService.class);
//        startService(intent);

    }


    @Override
    public void onClick(View v) {
        // 버튼들의 클릭 이벤트에 따라 프래그먼트(화면)들을 바꿔주는 역할
        switch (v.getId()){
            case R.id.home_btn:
                setFrag(0);
                break;
            case R.id.category_btn:
                setFrag(1);
                break;
            case R.id.write_btn:
                setFrag(2);
                break;
            case R.id.chat_btn:
                setFrag(3);
                break;
            case R.id.profile_btn:
                setFrag(4);
                break;
        }
    }

    public void setFrag(int n){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        // 입력된 값에 따라서 프레임레이아웃에 어떠한 프래그먼트(화면)을 넣을지 정해주는 조건문
        // 트랜잭션을 이용해서 프레임레이아웃에 지정된 프래그먼트를 넣고(대체) 저장한다.
        switch (n){
            case 0:
                transaction.replace(R.id.frameLayout, fragment_home);
                transaction.commit();
                break;
            case 1:
                transaction.replace(R.id.frameLayout, fragment_category);
                transaction.commit();
                break;
            case 2:
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                startActivity(intent);
                break;
            case 3:
                transaction.replace(R.id.frameLayout, fragment_chat);
                transaction.commit();
                break;
            case 4:
                transaction.replace(R.id.frameLayout, fragment_profile);
                transaction.commit();
                break;
        }
    }

}
