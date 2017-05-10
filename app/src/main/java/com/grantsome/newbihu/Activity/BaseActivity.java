package com.grantsome.newbihu.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.BitmapUtils;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.ToastUtils;
import com.grantsome.newbihu.View.CircleImage;

/**
 * Created by tom on 2017/4/13.
 */

public class BaseActivity extends AppCompatActivity {

    protected static final int OPEN_ALBUM = 0;

    protected Handler mHandler;

    public static Context sContext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        sContext = this;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    //用户同意/拒绝,activity的onRequestPermissionsResult会被回调来通知结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions,@Nullable int[] grantResults){
       super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 & grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openAlbum();
        }
    }

    public void checkAndOpenAlbum(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},OPEN_ALBUM);
        }else {
            openAlbum();
        }
    }

    public void openAlbum() {
        //选择数据
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        //指定数据类型为图片类型: String IMAGE_UNSPECIFIED = "image/*";
        intent.setType("image/*");
        startActivityForResult(intent,OPEN_ALBUM);
    }

    public void cropImage(String imageUriString,String outputUriString,CircleImage mAvatar, User mUser){
        Intent intent = new Intent("com.android.camera.action.CROP");
        //aspectX aspectY 是长宽比
        intent.setDataAndType(Uri.parse(imageUriString),"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data",false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.parse(outputUriString));
        //Bitmap.CompressFormat.JPEG:返回string类型的枚举常量 JPEG
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        upLoadAvatar(intent.getData(),mAvatar,mUser);
    }

    private void upLoadAvatar(Uri uri,CircleImage mAvatar, User mUser){
        if(uri!=null) {
            Bitmap avatar = BitmapUtils.toBitmap(uri);
            mAvatar.setImageBitmap(avatar);
            String name = System.currentTimeMillis() + "";
            String param = "token=" + mUser.getToken() + "&avatar=" + ApiConstant.QINIU_URL + name;
            HttpUtils.uploadImage(BitmapUtils.toBytes(avatar),name,param,ApiConstant.MODIFY_AVATAR);
        }else {
            ToastUtils.showError("上传URI为空");
            return;
        }

    }

    public void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
    }

}
