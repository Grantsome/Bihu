package com.grantsome.newbihu.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grantsome.newbihu.Adapter.QuestionListRvAdapter;
import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.BitmapUtils;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.JsonParser;
import com.grantsome.newbihu.Util.RefrechChecker;
import com.grantsome.newbihu.Util.ToastUtils;
import com.grantsome.newbihu.View.CircleImage;
import com.grantsome.newbihu.View.LoginDialog;

public class QuestionListActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;

    private NavigationView mNavigationView;

    private RecyclerView mQuestionRv;

    private SwipeRefreshLayout mRefreshLayout;

    private CircleImage mAvatar;

    private User mUser;

    private QuestionListRvAdapter mQuestionListRvAdapter;

    private boolean mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        mUser = JsonParser.getUser(getIntent().getStringExtra("data"));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mQuestionRv = (RecyclerView) findViewById(R.id.questionRv);
        super.setUpToolbar();
        setUpNavigationView();
        setUpQuestionRv();
        setUpRefreshLayout();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        if(RefrechChecker.isQuestionNeedRefresh()){
            uploadData();
            RefrechChecker.setAnswerNeedRefresh(false);
            mQuestionRv.scrollToPosition(0);
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

    private void uploadData(){
        if(mLoading){
            return;
        }
        mLoading = true;
        HttpUtils.sendHttpRequest(ApiConstant.QUESTION_LIST, "page=0" + "&token=" + mUser.getToken(), new HttpUtils.Callback() {
            @Override
            public void onResponse(HttpUtils.Response response) {
                mLoading = false;
                mRefreshLayout.setRefreshing(false);
                if(response.isSuccess()){
                    mQuestionListRvAdapter.refreshQuestionList(JsonParser.getQuestionList(response.bodyString()));
                }else {
                    ToastUtils.showError(response.message());
                }
            }

            @Override
            public void onFail(Exception e) {
                ToastUtils.showError(e.toString());
                mRefreshLayout.setRefreshing(false);
                mLoading = false;
            }
        });
    }

    private void setUpQuestionRv(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mQuestionRv.setLayoutManager(layoutManager);
        mQuestionListRvAdapter = new QuestionListRvAdapter(mUser);
        mQuestionRv.setAdapter(mQuestionListRvAdapter);
    }

   private void setUpNavigationView(){
       mNavigationView.setCheckedItem(R.id.home);
       mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.home:
                       mQuestionRv.scrollToPosition(0);
                       break;
                   case R.id.question:
                       actionStart(QuestionActivity.class);
                       break;
                   case R.id.favorite:
                       actionStart(FavoriteListActivity.class);
                       break;
                   case R.id.change_avatar:
                       checkAndOpenAlbum();
                       break;
                   case R.id.change_password:
                       changePassword();
                       break;
                   case R.id.quit_login:
                       quitLogin();
                       break;
               }
               mDrawerLayout.closeDrawers();
               return true;
           }
       });
       View view = mNavigationView.inflateHeaderView(R.layout.navigation_header);
       TextView username = (TextView) view.findViewById(R.id.user_name);
       username.setText(mUser.getUsername());
       mAvatar = (CircleImage) view.findViewById(R.id.avatar);
       mAvatar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               checkAndOpenAlbum();
           }
       });

       if(!mUser.getAvatarUrlString().equals("null")){
           HttpUtils.loadImage(mUser.getAvatarUrlString(), new HttpUtils.Callback() {
               @Override
               public void onResponse(HttpUtils.Response response) {
                   if(response.isSuccess()){
                       mAvatar.setImageBitmap(BitmapUtils.toBitmap(response.bodyBytes()));
                   }else {
                       ToastUtils.showError(response.message());
                   }
               }

               @Override
               public void onFail(Exception e) {
                   ToastUtils.showError(e.toString());
               }
           });
       }
   }



    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case OPEN_ALBUM:
                    cropImage(BitmapUtils.parseImageUriString(data), "file://" + getExternalCacheDir() + "/" +System.currentTimeMillis(),mAvatar,mUser);
                    break;

            }
        }
    }

   private void actionStart(Class<? extends Activity> cls){
       Intent intent = new Intent(this,cls);
       intent.putExtra("user",mUser);
       startActivity(intent);
   }

    private void changePassword(){
        final LoginDialog dialog = new LoginDialog(this);
        dialog.show();
        dialog.getMessageTextView().setText(getString(R.string.change_password));
        dialog.addPasswordWrapper("新" +getString(R.string.hint_password));
        dialog.setLoginButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = dialog.getPasswordWrapper().getEditText().getText().toString();
                if(password.length()<6){
                    dialog.getPasswordWrapper().setError(getString(R.string.warn_password));
                    return;
                }
                String param = "token=" + mUser.getToken() + "&password=" + password;
                HttpUtils.sendHttpRequest(ApiConstant.CHANGE_PASSWORD, param, new HttpUtils.Callback() {
                    @Override
                    public void onResponse(HttpUtils.Response response) {
                        if(response.isSuccess()){
                            dialog.dismiss();
                            ToastUtils.showHint(response.getInfo());
                            SharedPreferences sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("password",password);
                            editor.apply();
                            mUser.setToken(JsonParser.getElement(response.bodyString(),"token"));
                        }else {
                            ToastUtils.showError(response.message());
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                           ToastUtils.showError(e.toString());
                    }
                });
            }
        });
    }

    private void quitLogin(){
        SharedPreferences preferences = getSharedPreferences("account",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogin",false);
        editor.putString("password","");
        editor.apply();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return false;
    }

}
