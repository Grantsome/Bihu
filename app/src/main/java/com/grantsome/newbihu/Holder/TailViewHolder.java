package com.grantsome.newbihu.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.grantsome.newbihu.Adapter.AnswerListRvAdapter;
import com.grantsome.newbihu.Adapter.FavoriteListRvAdapter;
import com.grantsome.newbihu.Adapter.QuestionListRvAdapter;
import com.grantsome.newbihu.R;
import com.grantsome.newbihu.Util.HttpUtils;
import com.grantsome.newbihu.Util.JsonParser;
import com.grantsome.newbihu.Util.ToastUtils;

/**
 * Created by tom on 2017/4/28.
 */

public class TailViewHolder extends RecyclerView.ViewHolder{

    public static final int TYPE_QUESTION = 0;

    public static final int TYPE_ANSWER = 1;

    public static final int TYPE_FAVORITE = 2;

    private TextView mLoadTextView;

    private boolean loading;

    public TailViewHolder(View itemView) {
        super(itemView);
        mLoadTextView = (TextView) itemView.findViewById(R.id.load);
        loading = false;
    }

    public TextView getLoadTextView(){
        return mLoadTextView;
    }

    public void load(String address,String param,final RecyclerView.Adapter adapter,final int type){
        int n = 1;
        if(type==TYPE_ANSWER){
            n = 2;
        }
        if((adapter.getItemCount()-n)%10!=0){
            mLoadTextView.setText(R.string.load_finish_no_more);
            return;
        }
        if(loading){
            return;
        }
        mLoadTextView.setText(R.string.loading);
        loading = true;
        HttpUtils.sendHttpRequest(address, param, new HttpUtils.Callback() {
            @Override
            public void onResponse(HttpUtils.Response response) {
                loading = false;
                if(response.isSuccess()){
                    String key = (type == TYPE_ANSWER?"answers":"questions");
                    String data = JsonParser.getElement(response.bodyString(),key);
                    if(data==null||data.equals("null")||data.equals("[]")){
                        mLoadTextView.setText(R.string.load_finish_no_more);
                        return;
                    }
                    if(type== TYPE_QUESTION){
                        ((QuestionListRvAdapter) adapter).addQuestion(JsonParser.getQuestionList(response.bodyString()));
                    }else if (type==TYPE_ANSWER) {
                        ((AnswerListRvAdapter) adapter).addAnswer(JsonParser.getAnswerList(response.bodyString()));
                    }else if(type==TYPE_FAVORITE){
                        ((FavoriteListRvAdapter) adapter).addFavorite(JsonParser.getQuestionList(response.bodyString()));
                    }
                }else {
                    ToastUtils.showError(response.message());
                    mLoadTextView.setText(R.string.load_fail);
                }
            }

            @Override
            public void onFail(Exception e) {
                loading = false;
                ToastUtils.showError(e.toString());
                mLoadTextView.setText(R.string.load_fail);
            }
        });
    }


}
