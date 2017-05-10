package com.grantsome.newbihu.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grantsome.newbihu.Model.Answer;
import com.grantsome.newbihu.Model.Question;
import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.BitmapUtils;
import com.grantsome.newbihu.Util.DateUtils;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.ToastUtils;
import com.grantsome.newbihu.View.CircleImage;

import java.util.ArrayList;

/**
 * Created by Grantsome on 2017/5/5.
 */

public class AnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private CircleImage mAvatar;

    private TextView mAuthorName;

    private TextView mDate;

    private TextView mAnswerContent;

    private TextView mExcitingCount;

    private TextView mNaiveCount;

    private ImageButton mExcitingButton;

    private ImageButton mNaiveButton;

    private ImageButton mAcceptButton;

    private ArrayList<Answer> mAnswerList;

    private Question mQuestion;

    private User mUser;

    public AnswerViewHolder(View itemView,User user,ArrayList<Answer> answerList){
        super(itemView);
        mAvatar = (CircleImage) itemView.findViewById(R.id.avatar);
        mAuthorName = (TextView) itemView.findViewById(R.id.authorName);
        mDate = (TextView) itemView.findViewById(R.id.date);
        mAnswerContent = (TextView) itemView.findViewById(R.id.answerContent);
        mExcitingCount = (TextView) itemView.findViewById(R.id.excitingCount);
        mNaiveCount = (TextView) itemView.findViewById(R.id.naiveCount);
        mExcitingButton = (ImageButton) itemView.findViewById(R.id.excitingButton);
        mNaiveButton = (ImageButton) itemView.findViewById(R.id.naiveButton);
        mAcceptButton =(ImageButton) itemView.findViewById(R.id.acceptButton);
        mUser = user;
        mAnswerList = answerList;
    }

    public void setAcceptButtonVisible(boolean visible){
        mAcceptButton.setVisibility(visible?View.VISIBLE:View.GONE);
    }

    public void update(Answer answer,Question question){
        mQuestion = question;
        updateAllImage(answer);
        updateAllTextView(answer);
    }

    private void updateAllTextView(Answer answer){
        mAuthorName.setText(answer.getAuthorName());
        mDate.setText(DateUtils.getDataDescription(answer.getDate()));
        mAnswerContent.setText(answer.getContent());
        mNaiveCount.setText("(" + answer.getNaiveCount() + ")");
        mExcitingCount.setText("(" + answer.getExcitingCount() + ")");
    }

    private void updateAllImage(Answer answer){
        mNaiveButton.setBackgroundResource(answer.isNaive()? R.drawable.ic_thumb_down_blue_24dp:R.drawable.ic_thumb_down_black_24dp);
        mExcitingButton.setBackgroundResource(answer.isExciting()?R.drawable.ic_thumb_up_blue_24dp:R.drawable.ic_thumb_up_black_24dp);
        mAcceptButton.setBackgroundResource(answer.isBest()?R.drawable.ic_accept_pink_24dp:R.drawable.ic_accept_gray_24dp);
        if(answer.getAuthorAvatarUrlString().equals("null")){
            mAvatar.setImageResource(R.mipmap.default_avatar);
        }else {
            HttpUtils.loadImage(answer.getAuthorAvatarUrlString(), new HttpUtils.Callback() {
                @Override
                public void onResponse(HttpUtils.Response response) {
                    if(response.isSuccess()){
                        mAvatar.setImageBitmap(BitmapUtils.toBitmap(response.bodyBytes()));
                    }
                }

                @Override
                public void onFail(Exception e) {
                    ToastUtils.showError(e.toString());
                }
            });
        }
    }

    public void addOnClickListeners(){
        mExcitingButton.setOnClickListener(this);
        mNaiveButton.setOnClickListener(this);
        mAcceptButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Answer answer = mAnswerList.get(getLayoutPosition()-1);
        String param = "id=" + answer.getId() + "&type=2&token=" + mUser.getToken();
        switch (v.getId()){
            case R.id.naiveButton:
               HttpUtils.sendHttpRequest(answer.isNaive()?ApiConstant.CANCEL_NAIVE:ApiConstant.NAIVE,param);
               mNaiveButton.setBackgroundResource(answer.isNaive()?R.drawable.ic_accept_gray_24dp:R.drawable.ic_accept_pink_24dp);
               answer.setNaiveCount(answer.isNaive()?answer.getNaiveCount()-1:answer.getNaiveCount()+1);
               answer.setNaive(!answer.isNaive());
               mNaiveCount.setText("(" + answer.getNaiveCount() + ")");
               break;
            case R.id.excitingButton:
                HttpUtils.sendHttpRequest(answer.isExciting()?ApiConstant.CANCEL_EXCITING:ApiConstant.EXCITING,param);
                mExcitingButton.setBackgroundResource(answer.isExciting()?R.drawable.ic_thumb_up_black_24dp:R.drawable.ic_thumb_up_blue_24dp);
                answer.setExcitingCount(answer.isExciting()?answer.getNaiveCount()-1:answer.getNaiveCount()+1);
                answer.setExciting(!answer.isExciting());
                mExcitingCount.setText("(" + answer.getExcitingCount() + ")");
                break;
            case R.id.acceptButton:
                param = "qid=" + mQuestion.getId() + "&aid=" + answer.getId() + "&token=" + mUser.getToken();
                HttpUtils.sendHttpRequest(ApiConstant.ACCEPT,param);
                mAcceptButton.setBackgroundResource(R.drawable.ic_accept_pink_24dp);
                answer.setBest(true);
                break;
        }
    }
}

