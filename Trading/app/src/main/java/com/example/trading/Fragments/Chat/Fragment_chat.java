package com.example.trading.Fragments.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trading.R;
import com.example.trading.RetrofitService;

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
        
    }



    // TODO 여기서도 TCP 소켓을 열어서 데이터를 오가면 좋겠다...하지만 현재의 문제점은 처음 소켓을 연결하고 어느 화면에 있는지에 따라 다르게 처리해주는 부분이 없다는 것.. 나중에 다시 개발한다면 구현할 필요가 있다.

}
