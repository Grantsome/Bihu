package com.grantsome.newbihu.Activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.grantsome.newbihu.Adapter.FavoriteListRvAdapter;
import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.JsonParser;
import com.grantsome.newbihu.Util.ToastUtils;

public class FavoriteListActivity extends BaseActivity {

    private User mUser;

    private RecyclerView mFavoriteRv;

    private SwipeRefreshLayout mRefreshLayout;

    private FavoriteListRvAdapter mFavoriteListRvAdapter;

    private boolean mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        mUser = getIntent().getParcelableExtra("user");
        mFavoriteRv = (RecyclerView) findViewById(R.id.favoriteRv);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        super.setUpToolbar();
        setUpQuestionRv();
        setUpRefreshLayout();
    }



    private void setUpQuestionRv(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mFavoriteRv.setLayoutManager(layoutManager);
        mFavoriteListRvAdapter = new FavoriteListRvAdapter(mUser);
        mFavoriteRv.setAdapter(mFavoriteListRvAdapter);
    }

    private void setUpRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mLoading){
                    return;
                }
                HttpUtils.sendHttpRequest(ApiConstant.FAVORITE_LIST, "token=" + mUser.getToken() + "&page=0", new HttpUtils.Callback() {
                    @Override
                    public void onResponse(HttpUtils.Response response) {
                        mLoading = false;
                        mRefreshLayout.setRefreshing(false);
                        if(response.isSuccess()){
                            mFavoriteListRvAdapter.refreshFavoriteList(JsonParser.getQuestionList(response.bodyString()));
                        }else {
                            ToastUtils.showError(response.message());
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLoading = false;
                        ToastUtils.showError(e.toString());
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }


}
