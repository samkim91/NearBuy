package com.example.trading.Fragments.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.trading.R;

public class ChatRoomActivity extends AppCompatActivity {

    String writerId;
    EditText et_chat_text;
    ImageButton send_btn;

    ChatTextRCAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        et_chat_text = findViewById(R.id.et_chat_text);
        send_btn = findViewById(R.id.send_btn);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_chatText);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 리사이클러뷰의 Adapter를 선언하고 지정!!
        adapter = new ChatTextRCAdapter(this);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        writerId = intent.getStringExtra("writerId");


        // 작성자 아이디 받고, 내 아이디랑 같이 서버에 보내서 해당 채팅이 있는지 확인한다.

        // 소켓 연결 요청한다. 방 아이디를 보낸다. 그 뒤론 채팅 계속!

    }
}
