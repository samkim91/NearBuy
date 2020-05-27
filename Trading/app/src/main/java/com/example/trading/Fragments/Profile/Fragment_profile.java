package com.example.trading.Fragments.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.trading.Login.LoginActivity;
import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_profile extends Fragment {

    ImageView userImg;
    TextView userNickname, userTown;

    CardView selling_list, marked_list, set_town;
    Button edit_profile;

    String TAG = "Fragment_profile";
    String URL = "http://15.165.57.108/Profile/";

    String nickname, imageURL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        selling_list = v.findViewById(R.id.selling_list);
        marked_list = v.findViewById(R.id.marked_list);
        set_town = v.findViewById(R.id.set_town);
        edit_profile = v.findViewById(R.id.edit_profile_btn);

        userImg = v.findViewById(R.id.userImg);
        userNickname = v.findViewById(R.id.userNickname);
        userTown = v.findViewById(R.id.userTown);

        // 이 프래그먼트가 메뉴옵션을 가졌다고 알려주는 구문. 이 문장을 선언해야, 프래그먼트 마다 액션바를 달리보이게 할 수 있다.
        setHasOptionsMenu(true);

        clickListeners();
        getUserInfoFromServer();
        getAddressFromSharedPreferences();

        return v;
    }

    public void clickListeners(){
        selling_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SellingListActivity.class);
                startActivity(intent);
            }
        });

        marked_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MarkedListActivity.class);
                startActivity(intent);
            }
        });

        set_town.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SetTownActivity.class);
                startActivity(intent);
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EditProfileActivity.class);
                intent.putExtra("imageURL", imageURL);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });
    }

    public void getUserInfoFromServer(){
        Log.d(TAG, "getUserInfoFromServer, ID: "+UserInfo.phoneNum);

        String userId = UserInfo.phoneNum;
        // 서버에서 유저의 정보를 가져온다.
        // 레트로핏 객체를 선언 및 기본 주소를 정해준다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        // 레트로핏 서비스를 위에서 선언한 레트로핏 객체가 사용 가능하도록 넣어준다.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        // 레티로핏 서비스에서 선언된 함수를 사용하기 위해 콜을 선언하고 필요한 정보를 가지고, 큐에 넣어준다.
        Call<ResponseBody> call = retrofitService.userInfo(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if( response.isSuccessful() ){
                    try {
                        String result = response.body().string();
                        Log.d(TAG, "isSuccessful: "+result);

                        JSONObject jsonObject = new JSONObject(result);

                        nickname = jsonObject.getString("nickname");
                        userNickname.setText(nickname);

                        imageURL = jsonObject.getString("image");
                        if(!imageURL.equals("")){
                            Glide.with(getContext()).load(imageURL).apply(new RequestOptions().circleCrop()).into(userImg);
                        }else{
                            userImg.setImageResource(R.drawable.lion);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getContext(), "불러오기 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "response is failed: "+response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "서버와의 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }

    public void getAddressFromSharedPreferences(){
        Log.d(TAG, "getAddressFromSharedPreferences");
        // 쉐어드프리퍼런스에서 값을 불러온다.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String addressesString = sharedPreferences.getString("addresses", "");
        int checkedId = sharedPreferences.getInt("isChecked", 0);
        ArrayList<String> addressList = new ArrayList<>();

        Log.d(TAG, "onStart: "+addressesString+"/checkedId: "+checkedId);
        try {
            // 제이슨 어레이에서 저장된 값을 빼오고, 그 값의 양에 따라 주소어레이리스트에 넣어준다. 그러면, 이 어레이리스트를 가지고 칩을 만들어줄 것임.
            JSONArray jsonArray = new JSONArray(addressesString);
            for(int i = 0 ; i<jsonArray.length() ; i++){
                String address = jsonArray.getString(i);
                addressList.add(address);
            }
            Log.d(TAG, "addressList From JSONArray: "+addressList.toString());

            // arrayAddress가 담고 있는 갯수에 따라서 칩을 배치해준다. 사이즈가 0일 때는 (+)칩이 나오게 하고, 1일 때는 정보를 담고 있는 칩 1개와 (+)칩이 나오게 한다.
            // 2일 때는 (+)을 없애고, 정보를 담고 있는 칩 2개가 나오게 한다.
            if (0 == addressList.size()) {
                userTown.setText("동네 설정이 필요해요!");
            } else if (1 == addressList.size()) {
                // 어레이리스트 안에 있는 값이 행정동과 전체주소로 이루어져 있기 때문에 /을 기준으로 잘라준다. 그 다음에
                String rawAddress = addressList.get(0);
                String[] arrayAddress = rawAddress.split("/");
                userTown.setText(arrayAddress[0]);
            } else if (2 == addressList.size()) {
                // 어떤 칩이 체크되어 있었는지 확인하고 해당값에 따라서 동네를 명시해준다.
                if(1 == checkedId){
                    // 어레이리스트 안에 있는 값이 행정동과 전체주소로 이루어져 있기 때문에 /을 기준으로 잘라준다. 그 다음에
                    String rawAddress = addressList.get(0);
                    String[] arrayAddress = rawAddress.split("/");
                    userTown.setText(arrayAddress[0]);
                }else if(2 == checkedId){
                    // 어레이리스트 안에 있는 값이 행정동과 전체주소로 이루어져 있기 때문에 /을 기준으로 잘라준다. 그 다음에
                    String rawAddress = addressList.get(1);
                    String[] arrayAddress = rawAddress.split("/");
                    userTown.setText(arrayAddress[0]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:
                // 쉐어드 프리프런스에 저장된 값을 삭제함.
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //TODO.. 쉐어드프리퍼런스를 모두 삭제하는 게 맞음... 서버단에서 게시물을 불러오는 예외처리를 해준 다음에 모두 삭제로 바꿀 예정..
                editor.remove("auto");
                editor.remove("phonenum");
                //editor.clear();
                editor.commit();

                // 인텐트를 만들어서 넘김
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
