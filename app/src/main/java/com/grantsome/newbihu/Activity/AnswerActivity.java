package com.grantsome.newbihu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.RefrechChecker;
import com.grantsome.newbihu.Util.ToastUtils;

public class AnswerActivity extends BaseActivity {

    private String mToken;

    private int mQuestionID;

    private boolean isCommiting;

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mQuestionID = intent.getIntExtra("qid",-1);
        mEditText = (EditText) findViewById(R.id.answerContent);
        setUpToolBar();
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.question_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (isCommiting){
            return true;
        }
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.commit:
                commit();
                finish();
        }
        return true;
    }

    private void commit(){
        if(isCommiting){
            return;
        }
        String content = mEditText.getText().toString();
        if(content.equals("")){
            ToastUtils.showError(getString(R.string.question_detail_warn));
            return;
        }
        String param = "qid=" + mQuestionID + "&content=" + content + "&token=" + mToken;
        HttpUtils.sendHttpRequest(ApiConstant.POST_ANSWER, param, new HttpUtils.Callback() {
            @Override
            public void onResponse(HttpUtils.Response response) {
                if(response.isSuccess()){
                    isCommiting = true;
                    ToastUtils.showHint(getString(R.string.question_commit_success));
                    RefrechChecker.setAnswerNeedRefresh(true);
                    finish();
                }else {
                    isCommiting = false;
                    ToastUtils.showError(response.message());
                }
            }

            @Override
            public void onFail(Exception e) {
                   isCommiting = false;
                   ToastUtils.showError(e.toString());
            }
        });
    }

}
