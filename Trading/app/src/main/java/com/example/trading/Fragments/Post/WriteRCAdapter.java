package com.example.trading.Fragments.Post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trading.R;

import java.util.ArrayList;

public class WriteRCAdapter extends RecyclerView.Adapter<WriteRCAdapter.ViewHolder> implements OnWriteRCClickListener {

    String TAG = "WriteRCAdapter";

    // 이미지뷰들의 주소값을 담을 어레이 선언
    ArrayList<WriteRCData> items = new ArrayList<>();
    // Glide 사용을 위한 context
    Context context;

    OnWriteRCClickListener listener;

    // 어뎁터를 선언할 때 context를 포함해준다.
    public WriteRCAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        // 뷰홀더를 만드는 메소드, 뷰를 하나 만들고, 레이아웃 인플레이터를 통해 리사이클러뷰 아이템을 씌운다.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recyclerview_write_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        // 리사이클러뷰 아이템을 하나 가져와서 이미지를 씌우기 위한 작업을 한다.
        WriteRCData item = items.get(position);

        if(!item.imageUri.equals("")){
            Glide.with(context).load(item.getImageUri()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 아이템 클릭 리스너를 달기 위한 메소드
    public void setOnItemClickListener(OnWriteRCClickListener listener){
        this.listener = listener;
    }

    // 리사이클러뷰의 아이템 클릭 리스너
    @Override
    public void onItemClick(ViewHolder viewHolder, View view, int position) {
        if(listener != null){
            listener.onItemClick(viewHolder, view, position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // 뷰 홀더에 들어가는 이미지 뷰 선언
        ImageView imageView, imageView1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder");
            // 뷰홀더의 이미지와 xml 파일을 연결
            imageView = itemView.findViewById(R.id.imageCell);
            imageView1 = itemView.findViewById(R.id.close_btn);

            // x 이미지에 아이템 클릭 리스너를 달아준다.
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

//        public void setItem(WriteRCData item){
//
//        }
    }

    // 이 밑으론 아이템을 추가하거나, 셋터 겟터
    public void addItem(WriteRCData item){
        items.add(item);
    }

    public ArrayList<WriteRCData> getItems() {
        return items;
    }

    public void setItems(ArrayList<WriteRCData> items) {
        this.items = items;
    }

    public WriteRCData getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, WriteRCData item){
        this.items.set(position, item);
    }

}
