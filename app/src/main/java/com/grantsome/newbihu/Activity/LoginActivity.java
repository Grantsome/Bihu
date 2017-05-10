package com.grantsome.newbihu.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.ToastUtils;
import com.grantsome.newbihu.View.LoginDialog;

public class LoginActivity extends BaseActivity {

    private LoginDialog mDialog;

    private SharedPreferences mPreferences;

    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mDialog = new LoginDialog(this);
        initDialog();
    }

    private void initDialog(){
        mDialog.show();
        mDialog.getMessageTextView().setText(R.string.welcome);
        mDialog.setRegisterButton(R.string.register, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = getUserInfoFromInput();
                String password = getPasswordInfoFromInput();
                register(username,password);
            }
        });
        mDialog.setLoginButton(R.string.login, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getUserInfoFromInput();
                String password = getPasswordInfoFromInput();
                login(username,password);
            }
        });
        mDialog.addUsernameWrapper(getString(R.string.hint_user_name));
        mDialog.addPasswordWrapper(getString(R.string.hint_password));
        String username = mPreferences.getString("username","");
        String password = mPreferences.getString("password","");
        mDialog.getUsernameWrapper().getEditText().setTag(username);
        mDialog.getPasswordWrapper().getEditText().setText(password);
        boolean isLogin = mPreferences.getBoolean("isLogin",false);
        if(isLogin){
            login(username,password);
        }
    }

    private String getUserInfoFromInput(){
        String username = mDialog.getUsernameWrapper().getEditText().getText().toString();
        return username;
    }

    private String getPasswordInfoFromInput(){
        String password = mDialog.getPasswordWrapper().getEditText().getText().toString();
        return password;
    }


    private void login(String username,String password){
        loginOrRegister(ApiConstant.LOGIN,username,password);
    }

    private void register(String username,String password){
        if (username.length()<2){
            mDialog.getUsernameWrapper().setError(getString(R.string.warn_user_name));
            return;
        }
        if (password.length()<6){
            mDialog.getPasswordWrapper().setError(getString(R.string.warn_password));
            return;
        }
        loginOrRegister(ApiConstant.REGISTER,username,password);

    }

    private void loginOrRegister(String address,String username,String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.logining));
        progressDialog.show();

        HttpUtils.sendHttpRequest(address, "username=" + username + "&password=" + password, new HttpUtils.Callback() {
            @Override
            public void onResponse(HttpUtils.Response response) {
                checkResponseStatusCode(response.getStatusCode(),response);
                progressDialog.dismiss();
            }

            @Override
            public void onFail(Exception e) {
                e.printStackTrace();
                ToastUtils.showError(e.toString());
                progressDialog.dismiss();
            }
        });

    }

    private void checkResponseStatusCode(int statusCode,final HttpUtils.Response response){
        switch (statusCode){
            case 200:
                mDialog.dismiss();
                ToastUtils.showHint(getString(R.string.welcome_community));
                saveInfo();
                goToStartActivity(response.bodyString().toString());
                break;
            case 400:
                mDialog.getPasswordWrapper().setError(response.getInfo());
                break;
            default:
                ToastUtils.showError(response.message());
        }
    }

    private void saveInfo(){
        String username = getUserInfoFromInput();
        String password = getPasswordInfoFromInput();
        mEditor.putString("username",username);
        mEditor.putString("password",password);
        mEditor.apply();
    }

    private void goToStartActivity(String data){
        Intent intent = new Intent(this,QuestionListActivity.class);
        intent.putExtra("data",data);
        startActivity(intent);
        finish();
    }

}
