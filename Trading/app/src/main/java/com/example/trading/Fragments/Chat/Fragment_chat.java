package com.example.trading.Fragments.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Fragment_chat extends Fragment {

    String URL = "http://15.165.57.108/";
    String TAG = "Fragment_chat";

    ChatListRCAdapter adapter;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL)
            .build();

    RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView_chatList);

        // 어뎁터 선언
        adapter = new ChatListRCAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // 어뎁터의 온클릭리스너 추가
        adapter.setOnItemClickListener(new OnChatListRCClickListener() {
            @Override
            public void onItemClick(ChatListRCAdapter.ViewHolder viewHolder, View view, int position) {
                // 클릭된 아이템의 룸아이디를 가져와서.. 인텐트로 채팅방 액티비티를 실행시켜준다.

                ChatListRCData item = adapter.getItem(position);

                String roomId = item.getRoomId();
                Log.d(TAG, "onItemClicked : "+roomId);

                Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                intent.putExtra("roomId", roomId);

                startActivity(intent);
            }
        });

        // 리사이클러뷰 선언.. 리니어 레이아웃 매니저 선언..
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        adapter.items.clear();
        // 온 스타트에서 서버 통신메소드를 작동
        loadChatList();
    }

    public void loadChatList(){
        // 서버에는 사용자의 아이디를 가지고 chatList 테이블에서 갖고 있는 방과, chatText 테이블에서 해당 방의 가장 마지막 데이터를 가져와서 뿌려줘야한다.

        Call<ResponseBody> call = retrofitService.loadChatList(UserInfo.getPhoneNum());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse : "+response);
                try {
                    if(response.isSuccessful()){
                        String result = response.body().string();
                        Log.d(TAG, "result : "+result);

                        // 결과를 정상적으로 받았다. 제이슨으로 보냈을테니 제이슨으로 해부해준다.
                        JSONArray jsonArray = new JSONArray(result);

                        for(int i = 0 ; i<jsonArray.length() ; i++){

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String uImage = jsonObject.getString("image");
                            String uNickname = jsonObject.getString("uNickname");
                            String uId = jsonObject.getString("uId");
                            String roomId = jsonObject.getString("roomId");
                            String content = jsonObject.getString("content");
                            String date = jsonObject.getString("uDate");

                            ChatListRCData item = new ChatListRCData(roomId, uImage, uId, uNickname, content, date);

                            adapter.addItem(item);

                        }

                        adapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(getContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "response is not successful");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "서버와 통신이 좋지 않아요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t);
            }
        });

    }

    // TODO 여기서도 TCP 소켓을 열어서 데이터를 오가면 좋겠다...하지만 현재의 문제점은 처음 소켓을 연결하고 어느 화면에 있는지에 따라 다르게 처리해주는 부분이 없다는 것.. 나중에 다시 개발한다면 구현할 필요가 있다.

}
