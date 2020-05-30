package com.example.trading.Fragments.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatRoomActivity extends AppCompatActivity {

    String roomId;
    EditText et_chat_text;
    ImageButton send_btn;

    ChatTextRCAdapter adapter;

    String URL = "http://15.165.57.108/Chat/";
    String TAG = "ChatRoomActivity";


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL)
            .build();

    RetrofitService retrofitService = retrofit.create(RetrofitService.class);

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
        roomId = intent.getStringExtra("roomId");
        
        // 소켓 연결 요청한다. 방 아이디를 보낸다. 그 뒤론 채팅 계속! 이건 서비스로 만들자!


        setSend_btn(); // 보내기 버튼과 관련된 기능들
        textWatcher(); // 텍스트 와쳐를 통해 보내기 버튼을 활성/비활성하고 이를 색으로 구분되게 함.

    }

    public void setSend_btn(){
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭되면
                // 서버에 채팅내용을 저장하는 메소드를 호출한다.
                uploadChat();

                // 에딭텍스트를 지워준다.
                et_chat_text.getText().clear();
            }
        });
    }

    public void uploadChat(){
        // 채팅 보내기 버튼을 누를 때 서버에 채팅내용을 저장하기 위한 메소드
        Log.d(TAG, "uploadChat : "+roomId);
        Call<ResponseBody> call = retrofitService.uploadChat(roomId, UserInfo.getPhoneNum(), et_chat_text.getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse : "+response);

                if (response.isSuccessful()){
                    try {
                        Log.d(TAG, "response successful : "+response.body().string());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "response is not successful");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 지난 대화 불러오기.
        loadLastText();
    }

    public void loadLastText(){
        Log.d(TAG, "start loadLastText : "+roomId);
        Call<ResponseBody> call = retrofitService.loadLastText(roomId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse : "+response);

                if (response.isSuccessful()){
                    try {
                        String result = response.body().string();
                        Log.d(TAG, "response successful : "+result);

                        // 가져온 대화묶음이 제이슨 형태이기 때문에, 풀어서 리사이클러뷰 아이템으로 만들어준다. 그리고 리사이클러뷰에 뿌려주기까지.
                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0 ; i<jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String uImage = jsonObject.getString("image");
                            String uId = jsonObject.getString("userId");
                            String uNickname = jsonObject.getString("nickname");
                            String content = jsonObject.getString("content");
                            String date = jsonObject.getString("date");

                            ChatTextRCData chatTextRCData = new ChatTextRCData(uImage, uId, uNickname, content, date);

                            adapter.addItem(chatTextRCData);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "response is not successful");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    public void textWatcher(){
        et_chat_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(et_chat_text.getText().toString().equals("")){
                    // 텍스트가 비어있으면.. send 버튼을 회색으로 바꾸고. 비활성화 한다.
                    send_btn.setAlpha(0.2f);
                    send_btn.setClickable(false);
                }else{
                    // 텍스트가 비어있지 않다면.. send 버튼을 검은색으로 바꿔주고 활성화한다.
                    send_btn.setAlpha(1.0f);
                    send_btn.setClickable(true);
                }
            }
        });
    }
}
