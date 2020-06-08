package com.example.trading.Fragments.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.trading.LocalService;
import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    RecyclerView recyclerView;

    String URL = "http://15.165.57.108/Chat/";
    String TAG = "ChatRoomActivity";


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL)
            .build();

    RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    LocalService mService;
    boolean mBound = false;

    // 바인드서비스를 위한 커넥션을 제공함. 만약 바인딩에 성공하면 1번 메소드가, 실패하면 2번 메소드가 자동 호출됨.
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            // 연결에 성공하면, 받아온 바인더를 클라이언트 서비스와 연결해준다.
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            try {
                // 초기 유저에 대한 데이터를 보내자.
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("roomId", roomId);
                jsonObject.put("userId", UserInfo.getPhoneNum());

                mService.sendInitInfo(jsonObject.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        et_chat_text = findViewById(R.id.et_chat_text);
        send_btn = findViewById(R.id.send_btn);

        recyclerView = findViewById(R.id.recyclerView_chatText);
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

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 브로드캐스트 리시브 받았을 때
            String message = intent.getStringExtra("message");
            Log.d(TAG, "onReceive : "+message);

            // 메시지를 해부해서 roomId가 같은지 확인하고 맞으면 뿌리고 아니면 패스

            try {
                JSONObject jsonObject = new JSONObject(message);

                // 채팅 서버로 부터 받은 룸 아이디
                String roomId1 = jsonObject.getString("roomId");

                // 현재 방과 서버에서 온 룸 아이디가 같은지 확인
                if(roomId1.equals(roomId)){
                    Log.i(TAG, "same room");
                    // 같으면 채팅 내용 추가, 아니면 생략
                    String uImage = jsonObject.getString("image");
                    String uId = jsonObject.getString("userId");
                    String uNickname = jsonObject.getString("nickname");
                    String content = jsonObject.getString("content");
                    String date = jsonObject.getString("date");

                    ChatTextRCData chatTextRCData = new ChatTextRCData(uImage, uId, uNickname, content, date);

                    adapter.addItem(chatTextRCData);

                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);  // 마지막 텍스트로 포커스를 이동해주기

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // 지난 대화 불러오기.
        loadLastText();

        Intent intent = new Intent(this, LocalService.class);

        // 서비스 바인딩 하기!

        // 세 번째 매개변수는 바인딩 옵션을 나타내는 플래그. 일반적으로는 BIND_AUTO_CREATE가 되는데,
        // 이는 서비스가 아직 활성화되지 않았을 경우 서비스를 생성하기 위함.
        // 그 외에는 BIND_DEBUG_UNBIND와 BIND_NOT_FOREGROUND를 사용할 수 있고 값이 없으면 0으로 설정.
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // 브로드캐스트 리시버 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Chat Text"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;

        // 브로드캐스트 리시버 등록 해제
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    public void setSend_btn(){
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭되면

                // 서비스로 해당 텍스트를 보내준다. 서비스에서는 TCP 소켓을 통해 채팅 서버로 데이터를 보낼 것이다.
                sendService();

                // 서버에 채팅내용을 저장하는 메소드를 호출한다.
                uploadChat();

                // 에딭텍스트를 지워준다.
                et_chat_text.getText().clear();

            }
        });
    }

    public void sendService(){
        // 서비스의 send 메소드를 실행시키기 위한 메소드이다. 입력란에 있는 내용을 가져와서 가공을 한 다음 서비스의 send 메소드 파라미터로 보내면
        // 채팅 서버에 가서 필요한 채팅방으로 배정될 것이다.
        // 양식은 방Id, 보낸사람Id, 텍스트 내용 등이다.

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("roomId", roomId);
            jsonObject.put("userId", UserInfo.getPhoneNum());
            jsonObject.put("nickname", UserInfo.getNickname());
            jsonObject.put("image", UserInfo.getImage());
            jsonObject.put("content", et_chat_text.getText().toString());

            // 현재시간 불러와서 가공
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            Date date = new Date();
            String time = format.format(date);

            jsonObject.put("date", time);

            mService.send(jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);  // 마지막 텍스트로 포커스를 이동해주기

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
