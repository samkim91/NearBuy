package com.example.trading.Fragments.Post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trading.Fragments.Chat.ChatRoomActivity;
import com.example.trading.R;
import com.example.trading.RetrofitService;
import com.example.trading.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShowActivity extends AppCompatActivity {


    String URL = "http://15.165.57.108/Post/";
    String TAG = "ShowActivity";

    // 이건 나중에 페이저로 바꿀 예정...
    ImageView images;

    ImageView uImg, uMarkedIcon;
    TextView uNickname, uLocation, uTitle, uCategory, uTime, uContent, uMarked, uPrice, viewPagerNum, uCommentNum, writeComment;
    Button uSendMSG, editBtn, deleteBtn;

    int no;
    String uPhoneNum;
    String title;
    String category;
    int price;
    String content;

    Boolean isMarked = false;
    int markedNum;

    ArrayList<String> imageUriList;

    Spinner setStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        uImg = findViewById(R.id.userImg);
        uNickname = findViewById(R.id.userNickname);
        uLocation = findViewById(R.id.userLocation);
        uTitle = findViewById(R.id.title);
        uCategory = findViewById(R.id.category);
        uTime = findViewById(R.id.time);
        uContent = findViewById(R.id.content);
        uMarked = findViewById(R.id.marked);
        uPrice = findViewById(R.id.price);
        uMarkedIcon = findViewById(R.id.markedIcon);
        uSendMSG = findViewById(R.id.sendMSG);
        editBtn = findViewById(R.id.edit_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        viewPagerNum = findViewById(R.id.viewPagerNum);
        uCommentNum = findViewById(R.id.num_comment);
        setStatus = findViewById(R.id.setStatus);
        writeComment = findViewById(R.id.write_comment);


        // 이전 액티비티에서 인텐트에 담아서 보낸 게시물 넘버를 뺌.
        Intent intent = getIntent();
        no = intent.getExtras().getInt("no");

        // 버튼들의 클릭 리스너를 선언해놓은 메소드와 스피너 초기설정 및 선택값에 대한 기능을 넣은 메소드
        btnClickListeners();
        statusSpinner();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 게시글 내용을 불러오기 위해 서버에 접근하는 레트로핏을 실행하는 메소드
        getPostInfo();
    }

    public void getPostInfo() {
        Log.d(TAG, "getPostInfo()");
        // 이미지uri 값을 넣을 어레이리스트 생성.
        imageUriList = new ArrayList<>();


        // 서버에 연결할 레트로핏을 만듦. 빌더에 URL을 담아주고, 객체를 Json 변환시켜주는 Gson을 담음. Gson을 이용하지 않고 JsonObject를 풀고 있는데... 어느 방법이 더 좋을까?
        // 현재는 익숙한 JsonObject로 풀고 있는데 Gson도 공부해볼 필요가 있음.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
//                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 인터페이스로 만들어 놓은 레트로핏서비스를 선언. 레트로핏서비스에 만들어 놓은 함수를 이용하기 위함.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        // 레트로핏서비스에 있는 콜 함수를 선언. 내용은 인터페이스에 가면 있다.
        Call<ResponseBody> call = retrofitService.showThisPost(no);
        // 서버에 보낼 콜을 큐에 넣는다.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    // 서버로부터 받은 결과
                    String result = response.body().string();
                    Log.d(TAG, "onResponse: " + result);
                    // 제이슨 형태로 되어 있기에, 풀어준다!
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("postInfo");
                    String nickname = jsonObject.getString("nickname");

                    uPhoneNum = jsonObject1.getString("phonenum");

                    // 이 값들은 수정액티비티로 인텐트에 태워 보낼 예정이므로 전역변수로 선언함.
                    title = jsonObject1.getString("title");
                    category = jsonObject1.getString("category");
                    content = jsonObject1.getString("content");

                    // 이미지를 데이터베이스에 json 형식으로 저장했기 때문에, 다시 한번 제이슨 객체를 뽑아와야한다.
                    String images = jsonObject1.getString("images");

                    // 이미지 string에서 제이슨 객체를 뽑음
                    JSONObject jsonObject2 = new JSONObject(images);

                    // 제이슨 객체 안에 내용이 있는 만큼 반복문을 돌려서 하나하나의 값에 접
                    for (int i = 0; i < jsonObject2.length(); i++) {
                        String image = jsonObject2.getString(String.valueOf(i));
                        imageUriList.add(image);
                        Log.d(TAG, "image: " + image);
                    }

                    // 기타 전역변수일 필요가 없는 것들 지정.
                    uNickname.setText(nickname);
                    uLocation.setText(jsonObject1.getString("address"));
                    uTitle.setText(title);
                    uCategory.setText(category);
                    uTime.setText(jsonObject1.getString("time"));
                    uContent.setText(content);

                    // 좋아요 수는 클릭할 때마다 변경을 보여줄 예정이므로 전역변수로 선언했고 서버에서 가져온 값을 넣어준다.
                    markedNum = jsonObject1.getInt("marked");
                    uMarked.setText(String.valueOf(markedNum));
                    uCommentNum.setText(String.valueOf(jsonObject1.getInt("commentNum")));

                    // 가격이 int로 왔는데 여기에 컴마를 붙여주기 위함.
                    price = jsonObject1.getInt("price");
                    DecimalFormat decimalFormat = new DecimalFormat("###,###");
                    String formattedPrice = decimalFormat.format(price);
                    uPrice.setText(formattedPrice + " 원");

                    // 뷰페이저와 관련된 코드들이 들어간 메소드. 어뎁터를 만들어서 초기화해주고, 페이지 변화에 따라서 값이 변화하는 등의 작업.
                    aboutViewPager(imageUriList);

                    String status = jsonObject1.getString("status");

                    // 게시물을 쓴 유저와 로그인한 유저가 같다면, 수정/삭제 버튼, 판매상태 스피너가 보이게 하고 같지 않다면 메시지보내기 버튼을 보이게 한다.
                    if (uPhoneNum.equals(UserInfo.phoneNum)) {
                        editBtn.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.VISIBLE);
                        setStatus.setVisibility(View.VISIBLE);
                        // 판매 상태에 따라서 스피너 값을 지정해줌.
                        if(status.equals("selling")){
                            setStatus.setSelection(0);
                        }else{
                            setStatus.setSelection(1);
                        }
                    } else {
                        uSendMSG.setVisibility(View.VISIBLE);
                    }

                    JSONObject jsonObject3 = jsonObject.getJSONObject("marked");
                    for (int i = 0; i < jsonObject3.length(); i++) {
                        String markedUser = jsonObject3.getString(String.valueOf(i));

                        // 이 유저가 좋아요를 이전에 했는지 안 했는지에 따라서 좋아요(하트) 색상을 지정해준다.
                        if (markedUser.equals(UserInfo.phoneNum)) {
                            isMarked = true;
                            uMarkedIcon.setImageResource(R.drawable.heart_black);
                        } else {
                            isMarked = false;
                            uMarkedIcon.setImageResource(R.drawable.heart_white);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    public void aboutViewPager(ArrayList<String> imageUriList) {
        // 뷰페이저를 선언하고, 이미지 uri를 저장해놓은 어레이리스트를 넘겨준다.
        ViewPager viewPager = findViewById(R.id.viewPager);
        final ShowVPAdapter showVPAdapter = new ShowVPAdapter(getApplicationContext(), imageUriList);
        viewPager.setAdapter(showVPAdapter);

        if (showVPAdapter.getCount() == 0) {
            // 현재 몇 페이지를 보고 있는지 확인시켜주기 위해서 페이지의 총 숫자와. 초기값을 텍스트로 넣어준다.
            viewPagerNum.setText(0 + "/" + showVPAdapter.getCount());
        } else {
            viewPagerNum.setText(1 + "/" + showVPAdapter.getCount());
        }

        // 이후에 뷰페이저에 페이지 변화를 감지하는 리스너를 달아주고,
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // 페이지 변화가 끝났을 때, 해당 위치 값을 텍스트로 넣어준다.
            @Override
            public void onPageSelected(int position) {
                viewPagerNum.setText((position + 1) + "/" + showVPAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void btnClickListeners() {
        // 수정 버튼을 누르면, 이 게시물 번호(프라이머리키)를 가지고 수정 화면(액티비티)으로 넘어간다.
        editBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("no", no);
                intent.putExtra("title", title);
                intent.putExtra("category", category);
                intent.putExtra("price", price);
                intent.putExtra("content", content);
                intent.putExtra("imageUriList", imageUriList);

                startActivity(intent);
            }
        });

        // 삭제 버튼을 누르면 서버에 이 게시물을 삭제 요청하는 메소드를 실행시킴
        deleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost();
            }
        });

        // 당사자한테 메시지를 보내기 위해 채팅방으로 넘어가는 메소드..
        uSendMSG.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkChatRomm();
            }
        });

        // 좋아요 아이콘
        uMarkedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 좋아요 아이콘을 클릭했는데.. 이 유저가 이전에 좋아요를 한 적이 있던거면, 좋아요를 풀고 서버에서 삭제해준다.
                // 좋아요를 한적이 없던거면, 좋아요를 마킹해주고, 서버에도 업데이트 시켜준다.
                if (isMarked) {
                    Log.d(TAG, "mark is true: " + isMarked);
                    updateMarked(isMarked);
                    // 서버에 업데이트 한 후에 마크아이콘과 마크불린을 반대값으로 바꿔준다. 여기서 마크숫자를 변경해서 넣어주는 것은 서버에 반영 되었지만 액티비티를 갱신하지 않는 이상 마크숫자가 바뀌지 않기 때문에 강제로 변경
                    // 눌릴 때마다 서버에 요청하기엔... 트래픽이 문제될 수도...
                    markedNum--;
                    uMarked.setText(String.valueOf(markedNum));
                    uMarkedIcon.setImageResource(R.drawable.heart_white);
                    isMarked = false;
                } else {
                    Log.d(TAG, "mark is false: " + isMarked);
                    // 위의 반대 과정.
                    updateMarked(isMarked);
                    markedNum++;
                    uMarked.setText(String.valueOf(markedNum));
                    uMarkedIcon.setImageResource(R.drawable.heart_black);
                    isMarked = true;
                }
            }
        });

        // 댓글쓰기 텍스트에 댓글을 달 수 있는 클릭 리스너 달기(인텐트로 댓글 액티비티를 불러오는데 이때 게시물 넘버도 보내서 서버에서 댓글을 가져오게 함)
        writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("no", no);
                startActivity(intent);
            }
        });
    }

    public void checkChatRomm(){

        String URL2 = "http://15.165.57.108/Chat/";

        // 상대방과의 채팅방이 있는지 확인하고, 있으면 해당방을 리턴받아서 채팅룸 액티비티로 보내고, 없으면 하나 생성해서 보낸다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL2)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<ResponseBody> call = retrofitService.checkChatRoom(UserInfo.getPhoneNum(), uPhoneNum);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse : "+response);
                try {
                    String result = response.body().string();
                    Log.d(TAG, result);

                    if( response.isSuccessful() ){
                        Log.d(TAG, "response is successful");

                        if(!result.equals("failed")){
                            // 결과가 failed가 아니라면 룸넘버가 반환됐을 것이다. 이를 chatRoomActivity로 넘겨주자.
                            Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);

                            // 룸 아이디를 인텐트에 포함시켜서 보냄.
                            intent.putExtra("roomId", result);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "returned failed");
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "response is not successful");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    // 좋아요를 활성하거나 해제하는 메소드. 필요한 것은 게시물 번호, 유저 아이디(휴대폰 번호), 마크 불린 값.
    public void updateMarked(Boolean isMarked) {
        // 로그인 유저의 아이디를 가져왔고, 게시물 번호와, 마크 불린값은 전역변수로 지정되어 있음.
        String mPhoneNum = UserInfo.phoneNum;
        // 서버에 연결하기 위한 레트로핏을 빌드함.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();
        // 레트로핏에 레트로핏서비스에 만들어놓은 함수를 부여함.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        // 서버에 보낼 콜을 선언. 레트로핏서비스 인터페이스에 만들어 놓은 함수를 사용.
        Call<ResponseBody> call = retrofitService.updateMark(no, mPhoneNum, isMarked);
        // 보낼 콜을 큐에 넣음.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response);
                // 값을 확인하기 위해 제이슨객체에 넣어준 다음 출력해보자.
                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    // 응답이 성공했는지 확인.
                    if (response.isSuccessful()) {
                        Log.d(TAG, "response is successful: " + jsonObject.getString("boolean") + "/" + jsonObject.getString("response"));
                    } else {
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "response is not successful: " + jsonObject.getString("boolean") + jsonObject.getString("response"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    // 서버에 현재 게시물을 삭제 요청하는 메소드. 파라미터로 프라이머리 키값인 게시물 번호가 간다.
    public void deletePost() {
        // 서버에 연결하기 위한 레트로핏을 빌드함.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        // 레트로핏에 레트로핏서비스에 만들어놓은 함수를 부여함.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        // 서버에 보낼 콜을 선언. 레트로핏서비스 인터페이스에 만들어 놓은 함수를 사용.
        Call<ResponseBody> call = retrofitService.deletePost(no);
        // 보낼 콜을 큐에 넣음.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse");
                if (response.isSuccessful()) {
                    Toast.makeText(ShowActivity.this, "삭제되었어요!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(ShowActivity.this, "다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ShowActivity.this, "서버 연결실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void statusSpinner() {
        // 어레이 어뎁터를 만든다. 카테고리에 들어갈 값들을 선언해 놓은 R.array.category에서 값을 가져온다.
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_item);
        // 어뎁터를 어떤 형식으로 보여줄지 선택한다. layout 옵션이 있는데 크게 다른 점은 없다.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 이전에 선언한 스피너에 이 어뎁터를 달아준다.
        setStatus.setAdapter(adapter);

        // 스피너가 어떠한 값을 선택했는지 리스너를 달아준다.
        setStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 포지션이 0이면 판매중, 1이면 판매완료 이다. 해당 상태를 서버에 갱신시켜준다.
                switch (position) {
                    case 0:
                        changeStatus("selling");
                        break;

                    case 1:
                        changeStatus("soldout");
                        break;

                    default:
                        Log.d(TAG, "nothing changed");
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void changeStatus(String state) {
        Log.d(TAG, "changeStatus");
        // 게시물 상태가 바뀌었다는 것을 서버에 알려주는 부분. 필요한 것은 게시물 번호, 유저 아이디, 상태값이다.

        // 레트로핏 객체를 만든다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        // 레트로핏 객체에서 레트로핏서비스 클래스를 가져와서 레트로핏서비스 객체에 선언해준다.
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        // 콜 객체를 만든다. 이때 레트로핏서비스에 선언해둔 함수를 선언한다.( 변수값으로는 유저의 휴대폰 번호, 게시물 번호, 게시물 판매상태 이다.
        Call<ResponseBody> call = retrofitService.changeStatus(UserInfo.phoneNum, no, state);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse");
                try {
                    String result = response.body().string();

                    if(response.isSuccessful() && result.equals("success")){
                        Log.d(TAG, "isSuccessful: "+result);
                    }else{
                        Toast.makeText(getApplicationContext(), "변경 실패", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "response is failed: "+result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버와의 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }

}
