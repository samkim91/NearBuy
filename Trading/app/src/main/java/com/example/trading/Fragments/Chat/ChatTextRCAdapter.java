package com.example.trading.Fragments.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.trading.R;
import com.example.trading.UserInfo;

import java.util.ArrayList;

public class ChatTextRCAdapter extends RecyclerView.Adapter{

    ArrayList<ChatTextRCData> items = new ArrayList<>();
    Context context;

    public ChatTextRCAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더를 생성해주는 메소드

        // 뷰홀더로 쓸 아이템뷰를 선언해주고, 레이아웃을 덧씌워줄 레이아웃 인플레이터도 선언해준다.
        View itemView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // getItemViewType 에서 리턴해준 값에 따라서 다른 뷰홀더를 씌워준다. 뷰타입이 0은 나, 1은 상대 뷰홀더이다.
        if( viewType == 0 ){
            // 나라면..
            itemView = inflater.inflate(R.layout.item_chat_mtext, parent, false);
            return new mViewHolder(itemView);
        } else {
            // 상대라면..
            itemView = inflater.inflate(R.layout.item_chat_utext, parent, false);
            return  new uViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 아이템에 값을 넣어주는 (바인드 하는) 메소드. 아이템을 가져와서 내 데이터인지 상대 데이터인지 확인하고
        // 맞는 셋터를 선언해줌. 상대 데이터일 경우 이미지 glide도 병행

        ChatTextRCData item = items.get(position);

        if(item.getuId().equals(UserInfo.getPhoneNum())){
            // 나라면...
            mViewHolder viewHolder = (mViewHolder) holder;
            viewHolder.setItem(item);
        }else{
            // 상대라면...
            uViewHolder viewHolder = (uViewHolder) holder;

            // 상대 이미지 url 이 비어있지 않다면.. glide로 이미지를 가져와 뿌려준다.
            if(!item.getuImage().equals("")){
                Glide.with(context).load(item.getuImage()).apply(new RequestOptions().circleCrop()).into(viewHolder.uImg);
            }else{
                // 없으면 기본 이미지 추가.
                viewHolder.uImg.setImageResource(R.drawable.default_img);
            }
            viewHolder.setItem(item);

        }

    }

    @Override
    public int getItemViewType(int position) {
        // 뷰홀더의 뷰타입을 지정해주기 위한 메소드이다.
        // 해당 아이템에서 아이디를 가져와서 아이디가 나라면 내가 보낸 뷰홀더를, 내가 아니라면 상대가 보낸 뷰홀더를 지정해줄 것이다.
        String uId = items.get(position).getuId();
        if ( uId.equals(UserInfo.getPhoneNum()) ){
            // 나와 아이디가 일치한다면..
            return 0;
        } else {
            // 내가 아니라면..
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 아이템들의 겟터 / 셋터
    public void addItem(ChatTextRCData item){
        items.add(item);
    }

    public void setItems(ArrayList<ChatTextRCData> items){
        this.items = items;
    }

    public ChatTextRCData getItem(int position){
        return items.get(position);
    }

    public void setItem(ChatTextRCData item, int position){
        this.items.set(position, item);
    }


    // 내 채팅메시지를 보여주는 뷰홀더 클래스
    public class mViewHolder extends RecyclerView.ViewHolder{

        TextView mText;
        TextView mDate;

        // 생성자
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mText = itemView.findViewById(R.id.mSendText);
            this.mDate = itemView.findViewById(R.id.mSendTime);
        }

        // 셋터
        public void setItem(ChatTextRCData item){
            mText.setText(item.getContent());
            mDate.setText(item.getDate());
        }
    }


    // 상대 채팅메시지를 보여주는 뷰홀더 클래스
    public class uViewHolder extends RecyclerView.ViewHolder{

        ImageView uImg;
        TextView uNickname;
        TextView uText;
        TextView uDate;

        // 생성자
        public uViewHolder(@NonNull View itemView) {
            super(itemView);
            this.uImg = itemView.findViewById(R.id.uImg);
            this.uNickname = itemView.findViewById(R.id.uNickname);
            this.uText = itemView.findViewById(R.id.uText);
            this.uDate = itemView.findViewById(R.id.uDate);
        }

        // 셋터
        public void setItem(ChatTextRCData item){
            uNickname.setText(item.getuNickname());
            uText.setText(item.getContent());
            uDate.setText(item.getDate());
        }
    }
}
