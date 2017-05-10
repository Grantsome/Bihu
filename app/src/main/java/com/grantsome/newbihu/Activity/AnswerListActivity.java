package com.grantsome.newbihu.Activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.grantsome.newbihu.Adapter.AnswerListRvAdapter;
import com.grantsome.newbihu.Model.Question;
import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.JsonParser;
import com.grantsome.newbihu.Util.RefrechChecker;
import com.grantsome.newbihu.Util.ToastUtils;

public class AnswerListActivity extends BaseActivity {

    private Question mQuestion;

    private User mUser;

    private SwipeRefreshLayout mRefreshLayout;

    private RecyclerView mAnswerRv;

    private AnswerListRvAdapter mAnswerListRvAdapter;

    private boolean mLoading;

    private String TAG = "AnswerListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        Bundle data = getIntent().getBundleExtra("data");
        mUser = data.getParcelable("user");
        Log.d(TAG,"user.getName() " +mUser.getUsername());
        mQuestion = data.getParcelable("question");
        Log.d(TAG,"question.getContent() " +mQuestion.getContent());
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mAnswerRv = (RecyclerView) findViewById(R.id.answerRv);
        super.setUpToolbar();
        setUpAnswerRv();
        setUpRefreshLayout();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        if(RefrechChecker.isAnswerNeedRefresh()){
            uploadData();
            RefrechChecker.setAnswerNeedRefresh(false);
            mAnswerRv.scrollToPosition(0);
        }
    }


    private void setUpRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                uploadData();
            }
        });
    }

    private void uploadData() {
        if (mLoading)
            return;
        mLoading = true;
        HttpUtils.sendHttpRequest(ApiConstant.ANSWER_LIST, "qid=" + mQuestion.getId() + "&page=0&token=" + mUser.getToken(), new HttpUtils.Callback() {
            @Override
            public void onResponse(HttpUtils.Response response) {
                mLoading = false;
                mRefreshLayout.setRefreshing(false);
                if (response.isSuccess())
                    mAnswerListRvAdapter.refreshAnswerList(JsonParser.getAnswerList(response.bodyString()));
                else ToastUtils.showError(response.message());
            }

            @Override
            public void onFail(Exception e) {
                mLoading = false;
                ToastUtils.showError(e.toString());
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setUpAnswerRv(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAnswerListRvAdapter = new AnswerListRvAdapter(mUser,mQuestion);
        mAnswerRv.setAdapter(mAnswerListRvAdapter);
        mAnswerRv.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

}
