package com.example.trading.Fragments.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.trading.R;
import com.example.trading.RealPathFromURI;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

public class EditProfileActivity extends AppCompatActivity {

    ImageView userImg;
    EditText userNickname;
    Button checkDuplicate;

    Boolean available = true;

    String imageURL, nickname, imagePath;

    String URL = "http://15.165.57.108/Profile/";
    String TAG = "EditProfileActivity";

    int REQUEST_CODE_FOR_IMAGE = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userImg = findViewById(R.id.userImg);
        userNickname = findViewById(R.id.userNickname);
        checkDuplicate = findViewById(R.id.checkDuplicate);

        Intent intent = getIntent();
        imageURL = intent.getStringExtra("imageURL");
        nickname = intent.getStringExtra("nickname");
        Log.d(TAG, "getIntent: "+imageURL+"/"+nickname);
        userImg.setImageResource(R.drawable.lion);


        checkDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼을 누르면 서버와 통신해서 중복된 닉네임이 없는지 확인한다.
                setCheckDuplicate();
            }
        });

        // 이미지가 이전에 설정되어 있었다면, 그 이미지를 뷰에 넣어준다.
        if(!imageURL.equals("null")){
            Log.d(TAG, "imageURL is not Null");
            Glide.with(getApplicationContext()).load(imageURL).apply(new RequestOptions().circleCrop()).into(userImg);
        }
        userNickname.setText(nickname);

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "selectImage");
                // 선택을 할 수 있는 인텐트를 만듦
                Intent intent = new Intent(Intent.ACTION_PICK);
                // 이미지 선택을 하겠다는 의미
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                // 선택된 이미지의 uri를 받아오겠다는 의미
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.setType("application/*");

                startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE);
            }
        });

        // 닉네임 중복확인을 하고 다시 수정하는 것을 막기 위해 텍스트체인지 리스너를 달아놓는다.
        userNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!nickname.equals(userNickname.getText().toString())){
                    available = false;
                }else{
                    available = true;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: "+data);
        Log.d(TAG, "requestCode: "+requestCode);
        Log.d(TAG, "resultCode: "+resultCode);

        if(requestCode == REQUEST_CODE_FOR_IMAGE && resultCode == RESULT_OK){

            if(data.getClipData() == null){
                Toast.makeText(getApplicationContext(), "이미지가 선택되지 않았아요.", Toast.LENGTH_SHORT).show();
            }else{
                ClipData clipData = data.getClipData();
                Log.d(TAG, "clipData: "+clipData);

                imagePath = RealPathFromURI.getRealPathFromURI(this, clipData.getItemAt(0).getUri());
                Log.d(TAG, "path: "+imagePath);

                Glide.with(getApplicationContext()).load(imagePath).apply(new RequestOptions().circleCrop()).into(userImg);
            }
        }

    }

    public void updateProfile(){

        // 서버에서 필요할 유저아이디인 휴대폰번호와, 변경된 닉네님을 서버로 보내기 위해 해쉬맵에 담음
        HashMap hashMap = new HashMap();
        hashMap.put("phonenum", UserInfo.phoneNum);
        hashMap.put("nickname", userNickname.getText().toString());
        if(imagePath != null){
            String [] splitLastImg = imagePath.split("/");
//            for(int i = 0 ; i < splitLastImg.length ; i++){
//                Log.d(TAG, "split"+i+" : "+splitLastImg[i]);
//            }
//            Log.d(TAG, "Image Name: "+splitLastImg[3]+splitLastImg[4]);
            hashMap.put("lastImg", splitLastImg[5]);
        }

        // 레트로핏을 사용한다는 말임. URL은 연결할 서버 URL이고, Gson을 이용해 Json 값을 변환한다는 뜻.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 레트로핏 서비스를 레트로핏 객체에 넣어줌.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call;

        // 앨범에서 이미지를 지정했다면 이미지를 서버에 보내는 과정도 같이 해주고, 이미지 변경이 없다면 그냥 닉네임 변경만 진행한다. (예외처리임)
        if(imagePath != null){
            // 앨범에서 가져온 이미지의 주소로 파일을 하나 생성함.
            File file = new File(imagePath);

            // 요청하는 내용에 멀티파트 폼 데이터라는 것을 명시하고, 파일을 같이 넣어줌.
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // 멀티파트로 된 업로드파일을 하나 만들고, 이름을 files (이건 서버에서 불러올 이름이 됨), 이미지 주소와 요청내용을 달아줌.
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file", imagePath, requestBody);

            // 콜 객체를 만들어서 레트로핏 서비스에 만들어 놓은 함수를 넣는다.
            call = retrofitService.updateProfile(uploadFile, hashMap);
        }else{
            // 위의 콜은 사진도 변경되었을 때 실행되는 것이고, 이 콜은 닉네임만 변경되었을 때 실행되는 것.
            call = retrofitService.updateProfileNick(hashMap);
        }

        // 콜 객체를 서버에 요청하는 큐에 넣는다.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    String result = response.body().string();

                    if(response.isSuccessful()){
                        Log.d(TAG, "isSuccessful: "+result);
                        if(result.equals("success")){
                            Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "서버와의 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "response is failed: "+result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }

    public void setCheckDuplicate(){
        // 서버와 통신을 통해 닉네임이 중복되지 않았는지 확인하는 메소드

        // 에딭텍스트에서 입력값을 가져온다.
        String input = userNickname.getText().toString();

        // 레트로핏 객체를 만들고 기본 주소값을 넣어준다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        // 레트로핏 서비스를 선언하고 위에서 만든 레트로핏 객체에 이어준다.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        // 레트로핏 서비스에 선언해둔 함수를 사용하는 콜을 선언한다.
        Call<ResponseBody> call = retrofitService.checkDuplicate(input);

        // 콜을 서버에 보내기 위해 큐에 넣어준다.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    String result = response.body().string();

                    if(response.isSuccessful()){
                        Log.d(TAG, "isSuccessful: "+result);
                        if(result.equals("available")){
                            Toast.makeText(getApplicationContext(), "사용가능한 닉네임이에요.", Toast.LENGTH_SHORT).show();
                            available = true;
                        }else{
                            Toast.makeText(getApplicationContext(), "이미 사용중인 닉네임이에요.", Toast.LENGTH_SHORT).show();
                            available = false;
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "불러오기 실패", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "response is failed: "+result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 액션바에 저장 버튼을 활성화 시킴
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_write, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 저장버튼을 눌렀을 때, 서버에 해당 내용을 저장하는 메소드를 작동시킴
        if(item.getItemId() == R.id.post_btn){
            if( available ){
                updateProfile();
                onBackPressed();
            } else {
                Toast.makeText(getApplicationContext(), "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
