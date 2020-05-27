package com.example.trading.Fragments.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.trading.EndlessScrollEventListener;
import com.example.trading.Fragments.Post.ShowActivity;
import com.example.trading.Fragments.Profile.SetTownActivity;
import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_home  extends Fragment {

    String URL = "http://15.165.57.108/Post/";
    String TAG = "Fragment_Home";
    HomeRCAdapter adapter;

    EndlessScrollEventListener endlessScrollEventListener;

    Float mLat, mLng;
    int mRadius;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // 프래그먼트에는 액티비티 같이 view 가 없기에, 액티비티의 뷰를 인플레이트(덮어) 해준다.
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // 이 프래그먼트가 메뉴옵션을 가졌다고 알려주는 구문. 이 문장을 선언해야, 프래그먼트 마다 액션바를 달리보이게 할 수 있다.
        setHasOptionsMenu(true);

        adapter = new HomeRCAdapter(getContext());
        // 리사이클러뷰 선언
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView_home);
        // 레이아웃을 관리하는 매니저를 만들어주고, 이를 리사이클러뷰에 적용해준다.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 위에서 선언한 어뎁터를 리사이클러뷰에 적용해준다.
        recyclerView.setAdapter(adapter);

        endlessScrollEventListener = new EndlessScrollEventListener(linearLayoutManager){
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                Log.d(TAG, "onLoadMore/pageNum: "+pageNum);
                // 서버에 데이터를 더 요청하는 부분
                showPosts(pageNum);
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

                Intent intent = new Intent(getContext(), ShowActivity.class);
                intent.putExtra("no", no);

                startActivity(intent);
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh");

                // 어뎁터를 클리어해줌.. 이 과정을 생략하면 리사이클러뷰가 불러와질 때마다 이전 아이템들도 계속 쌓이게 됨.
                adapter.items.clear();
                endlessScrollEventListener.reset();
                showPosts(0);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // 쉐어드프리퍼런스에서 주소에 대한 정보를 불러오는 부분. 불러온 다음, 주소를 경위도로 바꿔서 서버로 보내고, 서버에서 경위도를 이용해서 가까운 거리에 있는 게시물을 받아올 것이다.
        getAddressFromSharedPreferences();

        // 어뎁터를 클리어해줌.. 이 과정을 생략하면 리사이클러뷰가 불러와질 때마다 이전 아이템들도 계속 쌓이게 됨.
        adapter.items.clear();
        endlessScrollEventListener.reset();
        showPosts(0);
    }

    public void showPosts(int pageNum){
        Log.d(TAG, "showPosts");
        // 레트로핏을 사용한다는 말임. URL은 연결할 서버 URL이고, Gson을 이용해 Json 값을 변환한다는 뜻.
        // TODO... Gson 을 이용하려면 Json을 담을 클래스를 작성해야하는데... 서버와 클라이언트에서 같은 키를 가져야한다.. 해보니까 그냥 Json으로 받아서 Json 객체를 해체하는게 아직은 더 쉬움.. 나중에 Gson도 해보자.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HashMap hashMap = new HashMap();
        // TODO... 검색 사용시에 바꿀 예정...
        hashMap.put("request", "all");
        hashMap.put("page", pageNum);
        hashMap.put("radius", mRadius);

        HashMap hashMap1 = new HashMap();
        hashMap1.put("lat", mLat);
        hashMap1.put("lng", mLng);

        // 레트로서비스 인터페이스를 객체화하고 위에서 선언한 레트로핏에 연결. 인터페이스에 선언해둔 함수를 사용하기 위함.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        // 서버에 보낼 콜을 만듦. 포스트 방식으로 보낼 것이고, 포스트 안에 들어가는 파라미터가 서버로 전송되는 값임.
        // TODO.. request 란에 필요한 값들을 넣자. 현재는 모든 값을 불러올 예정이라 all이라 했지만.. 필요에 따라 카테고리로 검색한다던가, 찾기 기능을 통해 키워드를 넣을 수 있을 듯. 이후에 서버단에서 처리해주면 좋을 것 같다.
        Call<ResponseBody> call = retrofitService.showPost(hashMap, hashMap1, mRadius);
        call.enqueue(new Callback<ResponseBody>() {
            // 전송이 성공했고 응답을 받았을 때
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+mLat+"/"+mLng+"/"+mRadius);

                if(response.isSuccessful()){

                    try {
                        // 서버로부터 온 결과를 받아본다. 서버단에서 JSONArray 형태로 보냈기 때문에 변환하는 과정이 필요하다.
                        String result = response.body().string();
                        Log.d(TAG, "isSuccessful: "+result);
                        // 변환을 위한 JSONArray를 만듦
                        JSONArray jsonArray = new JSONArray(result);
                        int lengthOfJA = jsonArray.length();
                        Log.d(TAG, "어레이 길이 : " + lengthOfJA);

                        // JSONArray 에 담긴 JSONObject를 하나씩 꺼내주기 위한 반복문
                        for(int i = 0 ; i<jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Log.d(TAG, "JSONObject: "+jsonObject.toString());
                            // JSONObject에서 값을 가져옴. 바로 HomeRcData에 지정해도 되지만.. 디버깅에 변수를 보려고 다 풀어봄...
                            int no = jsonObject.getInt("no");
                            String phonenum = jsonObject.getString("phonenum");
                            String title = jsonObject.getString("title");
                            String address = jsonObject.getString("address");
                            String time = jsonObject.getString("time");
                            String category = jsonObject.getString("category");
                            int priceInt = jsonObject.getInt("price");
                            int commentInt = jsonObject.getInt("commentNum");
                            int markedInt = jsonObject.getInt("marked");
                            String status = jsonObject.getString("status");


                            // 이미지가 데이터베이스에 제이슨 객체형태로 저장되어 있기 때문에, 제이슨 객체를 더 선언해준다.
                            String imageInJson = jsonObject.getString("images");

                            JSONObject jsonObject1 = new JSONObject(imageInJson);
                            // 이미지 목록 중에 첫번째 이미지 스트링(주소값)을 가져온다.
                            String image = jsonObject1.getString(String.valueOf(0));
                            Log.d(TAG, "이미지 주소 : " + image);

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
                    Toast.makeText(getContext(), "불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            // 응답에 뭔가 오류가 났을 때.
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure : "+t);
                Toast.makeText(getContext(), "불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.setPosition:
                Intent intent = new Intent(getActivity().getApplicationContext(), SetTownActivity.class);
                startActivity(intent);
                break;

            case R.id.search_btn:

                break;

            case R.id.setCategory:

                break;



        }

        return super.onOptionsItemSelected(item);
    }

    public void getAddressFromSharedPreferences(){
        // 쉐어드프리퍼런스에서 이 유저가 설정해놓은 메인 주소와, 물품 탐색반경을 가져오고, 이 정보를 토대로 서버에 요청을 한다! 여기서는 서버 요청 전에 정보(주소, 반경) 불러오는 것만 함
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String addressesString = sharedPreferences.getString("addresses", "");
        mRadius = sharedPreferences.getInt("radius", 0);

        try {
            String mAddress = null;
            // 제이슨 어레이에서 저장된 값을 빼오고, 그 값의 양에 따라 주소어레이리스트에 넣어준다.
            JSONArray jsonArray = new JSONArray(addressesString);
            // 제이슨어레이가 하나의 값만 갖고 있다면 바로 내 주소로 지정해줌
            if (1 == jsonArray.length()) {
                mAddress = jsonArray.getString(0);
            } else if (2 == jsonArray.length()) {
                // 제이슨어레이가 두개의 주소를 갖고 있다면, 쉐어드프리퍼런스에 선택되어 있는 주소 아이디를 확인하고, 그 아이디에 따라서 내 주소를 지정해줌.
                int checkedID = sharedPreferences.getInt("isChecked", 0);
                if (1 == checkedID) {
                    mAddress = jsonArray.getString(0);
                } else if (2 == checkedID) {
                    mAddress = jsonArray.getString(1);
                }
            }

            if(mAddress != null){
                // 위에서 불러온 내 주소를 가지고 경위도로 바꿔줌. 서버에 보낼 경위도임.
                changeLocationToLatlng(mAddress);
            }else{
                mLat = 0.0f;
                mLng = 0.0f;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeLocationToLatlng(String address) {
        // 어레이리스트에서 받아온 스트링이 /을 기준으로 행정동과 전체주소로 이루어져 있기 때문에 이를 사용하기 위해서 나눠준다.
        String[] splitedAddress = address.split("/");
        String fullAddress = splitedAddress[1];

        Log.d(TAG, "CheckedAddress is: " + fullAddress);

        // 주소를 경위도로 바꿔주기 위해 사용할 지오코더를 선언한다.
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        // 지오코더로 불러오는 주소를 담아줄 리스트
        List<Address> addresses = null;

        try {
            // 지오코더를 사용해 주소이름을 통해 전체 주소정보를 가져온다.

            addresses = geocoder.getFromLocationName(fullAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException");
        }

        // 주소가 잘 가져와졋는지 검사하고,
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "주소가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 주소정보리스트에서 첫번째 값을 가져온다.
            Log.d(TAG, "Addresses is: " + addresses);
            Address address1 = addresses.get(0);
            // 이 주소에서 경위도를 가져와서 맵에 넣어주고 포커스를 이동시킨다.
            Double dLat = address1.getLatitude();
            mLat = dLat.floatValue();
            Double dLng = address1.getLongitude();
            mLng = dLng.floatValue();
        }
        Log.d(TAG, "mLatlng: " + mLat + "/" + mLng);
    }

}
