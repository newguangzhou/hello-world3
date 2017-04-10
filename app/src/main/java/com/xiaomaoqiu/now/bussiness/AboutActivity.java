package com.xiaomaoqiu.now.bussiness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.pet.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("关于");
        setContentView(R.layout.activity_about);
    }
}
