package com.xiaomaoqiu.now.bussiness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.pet.R;

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
        feedback_opinion_ed= (EditText) findViewById(R.id.feedback_opinion_ed);
        feedback_mobile_ed= (EditText) findViewById(R.id.feedback_mobile_ed);


    }
}
