package com.grantsome.newbihu.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.grantsome.newbihu.R;

/**
 * Created by tom on 2017/4/13.
 */

public class LoginDialog extends Dialog {

    private TextView mMessageTextView;

    private Button mLoginButton;

    private Button mRegisterButton;

    private TextInputLayout mUsernameWrapper;

    private TextInputLayout mPasswordWrapper;

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.dialog_login);
        mMessageTextView = (TextView) findViewById(R.id.message);
        mLoginButton = (Button) findViewById(R.id.login);
        mRegisterButton = (Button) findViewById(R.id.register);
        mUsernameWrapper = (TextInputLayout) findViewById(R.id.user_name_wrapper);
        mPasswordWrapper = (TextInputLayout) findViewById(R.id.password_wrapper);
    }

    public TextView getMessageTextView(){
        return mMessageTextView;
    }

    public Button getLoginButton(){
       return mLoginButton;
    }

    public Button getRegisterButton(){
        return mRegisterButton;
    }

    public TextInputLayout getUsernameWrapper(){
        return mUsernameWrapper;
    }

    public TextInputLayout getPasswordWrapper(){
        return mPasswordWrapper;
    }

    public void setLoginButton(String message, View.OnClickListener listener){
        mLoginButton.setVisibility(View.VISIBLE);
        mLoginButton.setText(message);
        if(listener!=null){
            mLoginButton.setOnClickListener(listener);
        }
    }

    public void setLoginButton(int id,View.OnClickListener listener){
        setLoginButton(getContext().getString(id),listener);
    }

    public void setRegisterButton(String message,View.OnClickListener listener){
        mRegisterButton.setVisibility(View.VISIBLE);
        mRegisterButton.setText(message);
        if(listener!=null){
            mRegisterButton.setOnClickListener(listener);
        }
    }

    public void setRegisterButton(int id,View.OnClickListener listener){
        setRegisterButton(getContext().getString(id),listener);
    }

    public void addUsernameWrapper(String hint){
        mUsernameWrapper.setVisibility(View.VISIBLE);
        mUsernameWrapper.setHint(hint);
    }

    public void addUsernameWrapprer(int id){
        addUsernameWrapper(getContext().getString(id));
    }

    public void addPasswordWrapper(String hint){
        mPasswordWrapper.setVisibility(View.VISIBLE);
        mPasswordWrapper.setHint(hint);
    }

    public void addPasswordWrapper(int id){
        addPasswordWrapper(getContext().getString(id));
    }


}
