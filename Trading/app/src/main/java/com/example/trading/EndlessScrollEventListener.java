package com.example.trading;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessScrollEventListener extends RecyclerView.OnScrollListener {

    String TAG = "EndlessScrollEventListener";

    private LinearLayoutManager linearLayoutManager;

    // 임계점. 아이템 목록의 끝으로 다가가는 것을 확인하기 위함. 임계점과 화면 마지막위치의 합이 총 아이템갯수보다 커지면, 로딩이 필요하다는 것을 인지시킴.
    // 임계점이 크다면 사용자가 화면을 내리다가 걸리는 느낌이 적겠지만, 그만큼 서버와의 통신이 잦아질테고 리소스를 낭비하게 될 것이다.
    private int visibleThreshold = 5;

    // 스크롤 될 때마다 새 페이지를 불러오기 위해 클라이언트단에서 페이지를 기억해 놓음. 이 정수값이 서버로 다음 페이지를 불러올 때 사용된다.
    private int currentPage = 0;

    // 지난 로딩까지의 총 아이템 개수.. 이 변수는 총 아이템 개수와 비교하면서 페이지를 리셋할지를 정한다.
    private int previousTotalItemCount = 0;

    // 새로운 데이터를 서버에서 받아야하는지 아닌지를 정하는 불린
    private boolean loading = true;

    // 페이지를 초기화할 때 사용할 인덱스
    private int startingPageIndex = 0;

    // 현재 리사이클러뷰에 뿌려져있는 아이템의 총 개수.
    // 레이아웃에서 가져온다.
    private int totolItemCount;

    // 휴대폰 화면에서 마지막에 보이는 아이템의 위치
    // 레이아웃에서 가져온다.
    private int lastVisibleItemPosition;

    public EndlessScrollEventListener(LinearLayoutManager layoutManager) {
        linearLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.d(TAG, "onScrolled");

        // 총 아이템의 개수와 화면의 마지막 아이템의 위치를 구함.
        totolItemCount = linearLayoutManager.getItemCount();
        lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

        // 스크롤 될 때 세가지의 조건을 주고, 그에 따른 작동을 시킬 것이다.

        // 첫째, 리사이클러뷰에 있는 총 아이템 개수가 지난 로딩의 총 아이템 개수보다 작을 때(totalItemCount < previousTotalItemCount)
        // 이때는 리사이클러뷰를 지우고 최초 상태로 만들어야한다.
        // 예를 들어, 리사이클러뷰를 스크롤 해서 아이템을 한참 불러온 뒤에 다른 화면에 갔다가 돌아왔다? 등의 총 아이템 개수가 줄어들었다면 실행
        if(totolItemCount < previousTotalItemCount){
            Log.d(TAG, "1st Case");
            Log.d(TAG, "totalItemCount: "+totolItemCount+"/previousTotalItemCount: "+previousTotalItemCount);
            Log.d(TAG, "currentPage: "+currentPage);
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totolItemCount;
            if(totolItemCount == 0){
                this.loading = true;
            }
        }

        // 둘째, loading 불린이 참이라면 리사이클러뷰의 총 아이템 개수가 지난 로딩의 총 아이템 개수보다 큰지 확인하고,
        // 만약 크다면 로딩을 더할 필요가 없다는 것이다. 그러면 loading 불린을 거짓으로 바꾸고, 지난 로딩 총 아이템 개수를 새로운 총 아이템 개수로 변경(previousTotalItemCount = totalItemCount)
        // 예를 들어, 스크롤을 했는데 아직 보여줄 아이템들이 충분해서 로딩할 필요가 없다 하면 실행
        if(loading && (totolItemCount > previousTotalItemCount)){
            Log.d(TAG, "2nd Case");
            Log.d(TAG, "totalItemCount: "+totolItemCount+"/previousTotalItemCount: "+previousTotalItemCount);
            loading = false;
            previousTotalItemCount = totolItemCount;
        }

        // 셋째, 로딩이 끝났고 마지막으로 보이는 아이템의 위치와 임계점의 합이 총 아이템 개수보다 큰지 확인한다.
        // 크다면 리사이클러뷰의 끝에 다다르고 있다는 것이기 때문에 새로운 아이템을 가져와야한다는 것을 의미
        if(!loading && (lastVisibleItemPosition + visibleThreshold) > totolItemCount){
            Log.d(TAG, "3rd Case");
            Log.d(TAG, "lastVisibleItemPosition: "+lastVisibleItemPosition);
            Log.d(TAG, "currentPage: "+currentPage);
            currentPage++;
            onLoadMore(currentPage, recyclerView);
            loading = true;
        }
    }

    // 페이지 로딩했던 것들을 리셋할 때 사용하는 메소드. 예를 들어 리사이클러뷰의 아이템을 보다가 검색 기능을 쓴다고 할 때, 한번 변수들을 리셋해줄 필요가 있음.
    public void reset(){
        Log.d(TAG, "reset");
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    // 서버로부터 데이터를 받아오는 기능을 넣을 부분.. EndlessScrollEventListener를 객체화한 부분에서 내용을 채움.
    public abstract void onLoadMore(int pageNum, RecyclerView recyclerView);
}
