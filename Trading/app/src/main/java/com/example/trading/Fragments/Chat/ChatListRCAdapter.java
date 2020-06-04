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

import java.util.ArrayList;

public class ChatListRCAdapter extends RecyclerView.Adapter<ChatListRCAdapter.ViewHolder> implements OnChatListRCClickListener{

    ArrayList<ChatListRCData> items = new ArrayList<>();

    Context context;

    OnChatListRCClickListener listener;


    public ChatListRCAdapter(Context context){
        this.context = context;
    }

    public void setOnItemClickListener(OnChatListRCClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_chat_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatListRCData item = items.get(position);

        // 이미지 url 이 비어있지 않다면.. glide로 이미지를 가져와 뿌려준다.
        if(!item.getuImage().equals("")){
            Glide.with(context).load(item.getuImage()).apply(new RequestOptions().circleCrop()).into(holder.uImage);
        }else{
            // 없으면 기본 이미지 추가.
            holder.uImage.setImageResource(R.drawable.default_img);
        }
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, View view, int position) {
        if(listener!=null){
            listener.onItemClick(viewHolder, view, position);
        }
    }


    // 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView uImage;
        TextView uNickname;
        TextView uText;
        TextView uDate;

        // 생성자
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            uImage = itemView.findViewById(R.id.uImg);
            uNickname = itemView.findViewById(R.id.uNickname);
            uText = itemView.findViewById(R.id.uText);
            uDate = itemView.findViewById(R.id.uDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });

        }

        // 기본 아이템 셋
        public void setItem(ChatListRCData item){

            uNickname.setText(item.getuNickname());
            uText.setText(item.getContent());
            uDate.setText(item.getDate());
        }
    }

    // 이 밑으론 아이템을 추가하거나, 셋터 겟터
    public void addItem(ChatListRCData item){
        items.add(item);
    }

    public ArrayList<ChatListRCData> getItems() {
        return items;
    }

    public void setItems(ArrayList<ChatListRCData> items) {
        this.items = items;
    }

    public ChatListRCData getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, ChatListRCData item){
        this.items.set(position, item);
    }


}
