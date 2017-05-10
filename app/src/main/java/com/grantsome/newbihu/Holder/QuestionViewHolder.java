package com.grantsome.newbihu.Holder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grantsome.newbihu.Activity.AnswerActivity;
import com.grantsome.newbihu.Activity.AnswerListActivity;
import com.grantsome.newbihu.Model.Question;
import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;
import com.grantsome.newbihu.Util.BitmapUtils;
import com.grantsome.newbihu.Util.DateUtils;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.View.CircleImage;

import java.util.ArrayList;

/**
 * Created by tom on 2017/4/28.
 */

public class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private CircleImage mAvater;

    private TextView mAuthorName;

    private TextView mDate;

    private TextView mRecentDate;

    private TextView mQuestionTitle;

    private TextView mQuestionDetail;

    private TextView mExcitingCount;

    private TextView mNaiveCount;

    private TextView mAnswerCount;

    private ImageButton mExcitingButton;

    private ImageButton mNaiveButton;

    private ImageButton mAnswerButton;

    private ImageButton mFavoriteButton;

    private User mUser;

    private ArrayList<Question> mQuestionList;

    public QuestionViewHolder(View itemView,User user,ArrayList<Question> questionList){
        super(itemView);
        setItemViewOnClickListener(itemView);
        mAvater = (CircleImage) itemView.findViewById(R.id.avatar);
        mAuthorName = (TextView) itemView.findViewById(R.id.authorName);
        mDate = (TextView) itemView.findViewById(R.id.date);
        mRecentDate = (TextView) itemView.findViewById(R.id.recentDate);
        mQuestionTitle = (TextView) itemView.findViewById(R.id.questionTitle);
        mQuestionDetail = (TextView) itemView.findViewById(R.id.questionDetail);
        mExcitingCount = (TextView) itemView.findViewById(R.id.excitingCount);
        mNaiveCount = (TextView) itemView.findViewById(R.id.naiveCount);
        mAnswerCount = (TextView) itemView.findViewById(R.id.answerCount);
        mNaiveButton = (ImageButton) itemView.findViewById(R.id.naiveButton);
        mExcitingButton = (ImageButton) itemView.findViewById(R.id.excitingButton);
        mAnswerButton = (ImageButton) itemView.findViewById(R.id.answerButton);
        mFavoriteButton = (ImageButton) itemView.findViewById(R.id.favoriteButton);
        mUser = user;
        mQuestionList = questionList;
    }

    private void setItemViewOnClickListener(View itemView){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AnswerListActivity.class);
                Bundle data = new Bundle();
                data.putParcelable("user",mUser);
                data.putParcelable("question",mQuestionList.get(getLayoutPosition()));
                intent.putExtra("data",data);
                v.getContext().startActivity(intent);
            }
        });
    }

    public void update(Question question){
        updateAllTextView(question);
        updataAllImageView(question);
    }

    private void updateAllTextView(Question question){
        mAuthorName.setText(question.getAuthorName());
        mDate.setText(DateUtils.getDataDescription(question.getDate()));
        mRecentDate.setText(DateUtils.getDataDescription(question.getRecent()));
        mQuestionTitle.setText(question.getTitle());
        mQuestionDetail.setText(question.getContent());
        mExcitingCount.setText("("+question.getExcitingCount()+")");
        mNaiveCount.setText("("+question.getNaiveCount()+")");
        mAnswerCount.setText("("+question.getAnswerCount()+")");
    }

    private void updataAllImageView(final Question question){
        mNaiveButton.setBackgroundResource(question.isNaive()?R.drawable.ic_thumb_down_blue_24dp:R.drawable.ic_thumb_down_black_24dp);
        mExcitingButton.setBackgroundResource(question.isExciting()?R.drawable.ic_thumb_up_blue_24dp:R.drawable.ic_thumb_up_black_24dp);
        mFavoriteButton.setBackgroundResource(question.isFavorite()?R.drawable.ic_star_blue_24dp:R.drawable.ic_star_black_24dp);
        if(question.getAuthorAvatarUrlString().equals("null")){
            mAvater.setImageResource(R.mipmap.default_avatar);
        }else {
            HttpUtils.loadImage(question.getAuthorAvatarUrlString(), new HttpUtils.Callback() {
                @Override
                public void onResponse(HttpUtils.Response response) {
                    if(response.isSuccess()){
                        mAvater.setImageBitmap(BitmapUtils.toBitmap(response.bodyBytes()));
                    }
                }

                @Override
                public void onFail(Exception e) {

                }
            });
        }
    }

    public void addOnClickListener(){
        mExcitingButton.setOnClickListener(this);
        mNaiveButton.setOnClickListener(this);
        mFavoriteButton.setOnClickListener(this);
        mAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),AnswerActivity.class);
                intent.putExtra("qid",mQuestionList.get(getLayoutPosition()).getId());
                intent.putExtra("token",mUser.getToken());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Question question = mQuestionList.get(getLayoutPosition());
        String param = "id=" + question.getId() + "&type=1&token=" + mUser.getToken();
        switch (v.getId()){
            case R.id.naiveButton:
                if(question.isNaive()){
                    HttpUtils.sendHttpRequest(ApiConstant.CANCEL_NAIVE,param);
                    mNaiveButton.setBackgroundResource(R.drawable.ic_thumb_down_black_24dp);
                    question.setNaiveCount(question.getNaiveCount()-1);
                    question.setNaive(false);
                }else {
                    HttpUtils.sendHttpRequest(ApiConstant.NAIVE,param);
                    mNaiveButton.setBackgroundResource(R.drawable.ic_thumb_down_blue_24dp);
                    question.setNaiveCount(question.getNaiveCount()+1);
                    question.setNaive(true);
                }
                mNaiveCount.setText("("+question.getNaiveCount()+")");
                break;
            case R.id.excitingButton:
                if(question.isExciting()){
                    HttpUtils.sendHttpRequest(ApiConstant.CANCEL_EXCITING,param);
                    mExcitingButton.setBackgroundResource(R.drawable.ic_thumb_up_black_24dp);
                    question.setExcitingCount(question.getNaiveCount()-1);
                    question.setExciting(false);
                }else {
                    HttpUtils.sendHttpRequest(ApiConstant.EXCITING,param);
                    mExcitingButton.setBackgroundResource(R.drawable.ic_thumb_up_blue_24dp);
                    question.setExcitingCount(question.getNaiveCount()+1);
                    question.setExciting(true);
                }
                mExcitingCount.setText("(" +question.getExcitingCount() + ")");
                break;
            case R.id.favoriteButton:
                param = "qid=" + question.getId() + "&token=" + mUser.getToken();
                if(question.isFavorite()){
                    HttpUtils.sendHttpRequest(ApiConstant.CANCEL_FAVORITE,param);
                    question.setFavorite(false);
                    mFavoriteButton.setBackgroundResource(R.drawable.ic_star_black_24dp);
                }else {
                    HttpUtils.sendHttpRequest(ApiConstant.FAVORITE,param);
                    question.setFavorite(true);
                    mFavoriteButton.setBackgroundResource(R.drawable.ic_star_blue_24dp);
                }
                break;
        }
    }

}