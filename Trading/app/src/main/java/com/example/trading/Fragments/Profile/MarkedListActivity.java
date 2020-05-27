package com.example.trading.Fragments.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.trading.EndlessScrollEventListener;
import com.example.trading.Fragments.Home.HomeRCAdapter;
import com.example.trading.Fragments.Home.HomeRCData;
import com.example.trading.Fragments.Home.OnHomeRCClickListener;
import com.example.trading.Fragments.Post.ShowActivity;
import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MarkedListActivity extends AppCompatActivity {

    String URL = "http://15.165.57.108/Profile/";
    String TAG = "MarkedListActivity";
    HomeRCAdapter adapter;

    EndlessScrollEventListener endlessScrollEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_list);


        // 어뎁터를 객체화 하고, 리사이클러뷰를 만들어준 다음, 리사이클러뷰 어뎁터를 객체화한 어뎁터로 넣음.
        adapter = new HomeRCAdapter(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_markedList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        // 무한스크롤리스너를 만들고, 해당 페이지에 따라 서버에서 계속 값을 불러옴.
        endlessScrollEventListener = new EndlessScrollEventListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                Log.d(TAG, "onLoadMore/pageNum: "+pageNum);
                // 서버에 데이터를 더 요청하는 부분
                loadMarkedList(pageNum);
            }
        };

        // 리사이클러뷰에 스크롤리스너를 추가해준다.
        recyclerView.addOnScrollListener(endlessScrollEventListener);

        // 리사이클러뷰의 아이템을 클릭했을 때, 해당 아이템의 상세내용을 볼 수 있도록 넘어가는 기능이다.
        adapter.setOnItemClickListener(new OnHomeRCClickListener() {
            @Override
            public void onItemClick(HomeRCAdapter.ViewHolder viewHolder, View view, int position) {
                HomeRCData item = adapter.getItem(position);
                int no = item.getNo();
                Log.d(TAG, "클릭: "+no);

                Intent intent = new Intent(getApplicationContext(), ShowActivity.class);
                intent.putExtra("no", no);

                startActivity(intent);
            }
        });
    }

    public void loadMarkedList(int pageNum){
        Log.d(TAG, "loadMarkedList");
        // 서버와 통신하는 부분. 내가 올린 판매목록을 모두 가져올 것이다. 여기서는 내 아이디만 서버로 보내주고, 서버에서는 내 아이디로 등록된 게시물만 보내줄 것임.

        // 레트로핏 객체를 만든다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        // 레트로핏 서비스를 객체를 만들 위에서 선언한 레트로핏 객체를 이용해서 레트로핏 서비스 인터페이스를 연결해준다.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        // 레트로핏 서비스에 선언되어 있는 셀링리스트 함수를 콜에 지정하고 유저의 휴대폰 번호를 넣는다.
        Call<ResponseBody> call = retrofitService.markedList(UserInfo.phoneNum, pageNum);

        // 콜을 서버와 통신하는 큐에 넣어준다.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse");

                if(response.isSuccessful()){
                    Log.d(TAG, "isSuccessful");

                    try {
                        // 서버로부터 온 결과를 받아본다. 서버단에서 JSONArray 형태로 보냈기 때문에 변환하는 과정이 필요하다.
                        String result = response.body().string();
                        Log.d(TAG, "isSuccessful: "+result);
                        // 변환을 위한 JSONArray를 만듦
                        JSONArray jsonArray = new JSONArray(result);

                        // JSONArray 에 담긴 JSONObject를 하나씩 꺼내주기 위한 반복문
                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.d(TAG, "JSONObject: "+jsonObject.toString());

                            int no = jsonObject.getInt("no");
                            String phonenum = jsonObject.getString("phonenum");
                            String title = jsonObject.getString("title");
                            String address = jsonObject.getString("address");
                            String time = jsonObject.getString("time");
                            String category = jsonObject.getString("category");
                            int priceInt = jsonObject.getInt("price");
                            int commentInt = jsonObject.getInt("comment");
                            int markedInt = jsonObject.getInt("marked");
                            String status = jsonObject.getString("status");

                            // 이미지가 데이터베이스에 제이슨 객체형태로 저장되어 있기 때문에, 제이슨 객체를 더 선언해준다.
                            String imageInJson = jsonObject.getString("images");

                            JSONObject jsonObject1 = new JSONObject(imageInJson);
                            // 이미지 목록 중에 첫번째 이미지 스트링(주소값)을 가져온다.
                            String image = jsonObject1.getString(String.valueOf(0));

                            // 가격이 int로 왔는데 여기에 컴마를 붙여주기 위함.
                            DecimalFormat decimalFormat = new DecimalFormat("###,###");
                            String price = decimalFormat.format(priceInt);
                            String comment = decimalFormat.format(commentInt);
                            String marked = decimalFormat.format(markedInt);


                            // 리사이클러뷰에 넣을 아이템 객체를 하나 만들어서 값을 넣어줌.
                            HomeRCData item = new HomeRCData(no, image, phonenum, title, address, time, category, price + " 원", comment, marked, status);

                            // 리사이클러뷰 어뎁터를 이용해서 아이템을 넣어줌.
                            adapter.addItem(item);
                        }

                        // 아이템이 추가되었다는 것을 알려줌.
                        adapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "불러오기 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "response is failed: "+response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버와의 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 이 메소드는 서버와 통신을 통해 값을 불러오는 메소드임. 초기값을 0으로 시작.
        adapter.items.clear();
        endlessScrollEventListener.reset();
        loadMarkedList(0);
    }
}
