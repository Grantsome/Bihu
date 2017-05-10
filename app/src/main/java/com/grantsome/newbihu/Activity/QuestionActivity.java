package com.grantsome.newbihu.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.RefrechChecker;
import com.grantsome.newbihu.Util.ToastUtils;


public class QuestionActivity extends BaseActivity {

    private User mUser;

    private boolean isCommiting;

    private EditText mQuestionTitle;

    private EditText mQuestionDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        mUser = getIntent().getParcelableExtra("user");
        mQuestionDetail = (EditText) findViewById(R.id.questionDetail);
        mQuestionTitle = (EditText) findViewById(R.id.questionTitle);
        super.setUpToolbar();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.question_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(isCommiting){
            return true;
        }
        switch (item.getItemId()){
            case R.id.commit:
                commit();
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void commit(){
        if(isCommiting){
            return;
        }
        final String title = mQuestionTitle.getText().toString();
        String content = mQuestionDetail.getText().toString();
        if(title.equals("")){
            ToastUtils.showHint(getString(R.string.question_title_warn));
            return;
        }
        if(content.equals("")){
            ToastUtils.showHint(getString(R.string.question_detail_warn));
            return;
        }
        String param = "title=" + title + "&content=" + content + "&token=" + mUser.getToken();
        HttpUtils.sendHttpRequest(ApiConstant.POST_QUESTION, param, new HttpUtils.Callback() {
            @Override
            public void onResponse(HttpUtils.Response response) {
                if(response.isSuccess()){
                    isCommiting = true;
                    ToastUtils.showHint(getString(R.string.question_commit_success));
                    RefrechChecker.setQuestionNeedRefresh(true);
                }else {
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
