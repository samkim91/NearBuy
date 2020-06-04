package com.example.trading;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.trading.Fragments.Chat.ChatRoomActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class LocalService extends IntentService {

    // 메인 액티비티에서 서비스를 생성하면, 소켓을 열어주는 역할을 할 것이다.
    // 소켓을 연 다음엔 서버에서 보내는 데이터를 리시브하는 메소드를 쓰레드로 만들 것.

    // 채팅방 액티비티나, 채팅 리스트 프래그먼트와 서비스를 바인드 할 것이다.

    String TAG = "LocalService";

    String CHANNEL_ID = "NearBuy";


    // IntentService를 상속하면서 선언해줘야하는 생성자.. 이로써 클라이언트에서 어떤 인텐트에 대한 요청인지 알 수 있다고 한다.
    public LocalService() {
        super("LocalService");
    }

    Socket socket;

    //     클라이언트에게 주어질 바인더
    IBinder binder = new LocalBinder();

    boolean mBound = false;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    //클라이언트 바인더를 위해서 쓰이는 클래스.
    public class LocalBinder extends Binder {
        public LocalService getService(){
            Log.i(TAG, "LocalBinder class");
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    // IntentService를 상속하면 구현해야하는 메소드.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent");

        requestConnection();

    }

    public void requestConnection(){

        Log.i(TAG, "requestConnection");
        // 서비스 단에서 실행할 코드들을 여기에 적으면 된다.
        // 먼저 TCP 소켓을 열어줄 것이다.

        Thread thread = new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {

                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("10.0.2.2", 5000));

                    Log.i(TAG, "연결완료");
                    Log.i(TAG, "원격소켓 : "+socket.getRemoteSocketAddress());
                    Log.i(TAG, "로컬소켓 : "+socket.getLocalSocketAddress());

                    // 소켓이 연결되면 바로 리시브 메소드를 실행시켜, 채팅서버에서 스트림으로 보내주는 데이터를 받는다.
                    receive();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void sendInitInfo(final String initInfo){
        Log.i(TAG, "send Init Info");

        Thread thread = new Thread(){
            @Override
            public void run() {

                try{
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(initInfo);

                    Log.i(TAG, "init Info sended : "+initInfo);

                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void receive(){

        Log.i(TAG, "receive start");

        Log.i(TAG, "원격소켓 : "+socket.getRemoteSocketAddress());
        Log.i(TAG, "로컬소켓 : "+socket.getLocalSocketAddress());

        // 현재 최상위 액티비티 뭔지 확인하기 위한 매니져
        ActivityManager activityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);

        while (true){

            Log.i(TAG, "리시브 반복문 진입");

            try{
                dataInputStream = new DataInputStream(socket.getInputStream());
                String data = dataInputStream.readUTF();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String data = bufferedReader.readLine();
                Log.d(TAG, "received data : "+data);

                // 데이터를 받으면.. 두가지 상황에 따라 처리하는 방법이 다르다.
                // 1. 화면이 채팅목록이나 채팅방에 있을 때 : 로컬 브로드캐스트를 사용해서 해당 액티비티에 데이터를 보내준다.
                // 2. 위의 경우가 아닐 때 : 알림을 만들어서 보내준다.

                // 최상위 액티비티를 확인! deprecated 됐지만 하위 호환을 위해서다. 사용하는데 문제는 없음.
                List<ActivityManager.RunningTaskInfo> infoList = activityManager.getRunningTasks(1);
                ActivityManager.RunningTaskInfo info = infoList.get(0);

                ComponentName componentName = info.topActivity;

                if(componentName.getClassName().contains(".Fragments.Chat")){
                    // 1번의 경우.. 로컬 브로드캐스트로 데이터 보내기
                    Log.d(TAG, "Local Broadcast");

                    Intent intent = new Intent("Chat Text");
                    intent.putExtra("message", data);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                }else{
                    // 2번의 경우.. 알림 띄우기

                    try{
                        JSONObject jsonObject = new JSONObject(data);

                        // 채팅 서버로 부터 받은 룸 아이디
                        String roomId1 = jsonObject.getString("roomId");

                        // 같으면 채팅 내용 추가, 아니면 생략
                        String uImage = jsonObject.getString("image");
                        String uId = jsonObject.getString("userId");
                        String uNickname = jsonObject.getString("nickname");
                        String content = jsonObject.getString("content");
                        String date = jsonObject.getString("date");

                        // 알림탭을 눌렀을 때 해당 채팅방 액티비티로 이동하게끔 한다.
                        Intent intent = new Intent(this, ChatRoomActivity.class);
                        intent.putExtra("roomId", roomId1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle(uNickname+" 님으로부터 새로운 메시지가 도착했습니다.")
                                .setContentText(content)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        // 노티피케이션 채널을 만드는 메소드
                        createNotificationChannel();

                        // 노티 보내기.
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                        notificationManagerCompat.notify(10001, builder.build());

                    }catch (JSONException e){

                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "can't connect with SERVER");
                stopClient();
                break;
            }

        }
    }

    public void send(final String data){
        Log.i(TAG, "send");
        Log.i(TAG, "원격소켓 : "+socket.getRemoteSocketAddress());
        Log.i(TAG, "로컬소켓 : "+socket.getLocalSocketAddress());

        // 데이터를 보내는 메소드.. 값을 받아와서 쓰레드로 처리해줄 것이다.

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {

                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(data);

//                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                    bufferedWriter.write(data);
//                    bufferedWriter.flush();
                    Log.i(TAG, "sended : "+data);

                } catch (IOException e) {
                    e.printStackTrace();
                    stopClient();
                }

            }
        };
        thread.start();
    }

    public void stopClient(){
        Log.i(TAG, "stopClient");
        // 소켓 연결을 끊는 메소드

        try {
            if(socket!=null && !socket.isClosed()){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 태스크가 종료되었을 때 콜밸되는 메소드.. 여기서 서비스의 종료를 선언해줄 수 있다.
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved");
        stopClient(); // 소켓 끊는 메소드
        stopSelf(); // 서비스 종료하는 메소드
        super.onTaskRemoved(rootIntent);
    }

    // 바인드 되었을 때 호출되는 함수
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");


        requestConnection();


        mBound = true;
        return binder;
    }

    // 언바인드 되었을 때 호출되는 함수
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        mBound = false;
        return super.onUnbind(intent);
    }

    private void createNotificationChannel(){
        Log.d(TAG, "createNotificationChannel");

        // API 26+ 에서는 알림 채널을 설정하는게 추가되어서 해주는 작업
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "알림 이름";
            String description = "채팅이 오면 발생하는 알림.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
