package com.xiaomaoqiu.now.bussiness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiService;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 2017/7/20.
 */

public class SuggestActivity extends BaseActivity {


    EditText feedback_opinion_ed;
    EditText feedback_mobile_ed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("意见反馈");
        setContentView(R.layout.activity_suggest);
        feedback_opinion_ed = (EditText) findViewById(R.id.feedback_opinion_ed);
        feedback_mobile_ed = (EditText) findViewById(R.id.feedback_mobile_ed);
        setNextView("保存", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message = feedback_opinion_ed.getText().toString().trim();
                String call_path = feedback_mobile_ed.getText().toString().trim();
                if ("".equals(message)) {
                    ToastUtil.showTost("请留下您的宝贵意见");
                    return;
                }
                ApiUtils.getApiService().suggest(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                        message, call_path).enqueue(new XMQCallback<BaseBean>() {
                    @Override
                    public void onSuccess(Response<BaseBean> response, BaseBean message) {
                        finish();
                    }

                    @Override
                    public void onFail(Call<BaseBean> call, Throwable t) {

                    }
                });
            }
        });

    }
}
