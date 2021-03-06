package com.grantsome.newbihu.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grantsome.newbihu.Holder.QuestionViewHolder;
import com.grantsome.newbihu.Holder.TailViewHolder;
import com.grantsome.newbihu.Model.Question;
import com.grantsome.newbihu.Model.User;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.ApiConstant;

import java.util.ArrayList;

/**
 * Created by tom on 2017/4/28.
 */

public class QuestionListRvAdapter extends RecyclerView.Adapter {

    private static final int TYPE_QUESTION = 0;

    private static final int TYPE_TAIL = 1;

    private ArrayList<Question> mQuestionList;

    private User mUser;

    public QuestionListRvAdapter(User user){
        mUser = user;
        mQuestionList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position){
        return position == getItemCount()-1?TYPE_TAIL:TYPE_QUESTION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case TYPE_QUESTION:
                QuestionViewHolder holder = new QuestionViewHolder(inflater.inflate(R.layout.item_question,parent,false),mUser,mQuestionList);
                holder.addOnClickListener();
                return holder;
            case TYPE_TAIL:
                final TailViewHolder tailViewHolder = new TailViewHolder(inflater.inflate(R.layout.item_tail,parent,false));
                tailViewHolder.getLoadTextView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String param = "page=" + mQuestionList.size()/10 + "&token="+ mUser.getToken();
                        tailViewHolder.load(ApiConstant.QUESTION_LIST,param,QuestionListRvAdapter.this,TailViewHolder.TYPE_QUESTION);
                    }
                });
                return tailViewHolder;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       switch (getItemViewType(position)){
           case TYPE_QUESTION:
               QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
               questionViewHolder.update(mQuestionList.get(position));
               break;
           case TYPE_TAIL:
               String param = "page=" + mQuestionList.size()/10 + "&token="+ mUser.getToken();
               ((TailViewHolder) holder).load(ApiConstant.QUESTION_LIST,param,QuestionListRvAdapter.this,TailViewHolder.TYPE_QUESTION);
               break;
       }
    }

    public void refreshQuestionList(ArrayList<Question> newQuestionList){
        mQuestionList.clear();
        addQuestion(newQuestionList);
    }

    public void addQuestion(ArrayList<Question> questionList){
        mQuestionList.addAll(questionList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size()+1;
    }

}
