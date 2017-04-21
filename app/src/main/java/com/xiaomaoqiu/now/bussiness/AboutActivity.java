package com.xiaomaoqiu.now.bussiness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.util.Apputil;
import com.xiaomaoqiu.pet.R;

import org.w3c.dom.Text;

public class AboutActivity extends BaseActivity {
 TextView tv_versionname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("关于");
        setContentView(R.layout.activity_about);
        tv_versionname= (TextView) findViewById(R.id.tv_versionname);
        tv_versionname.setText("版本信息：小毛球 "+Apputil.getVersionName(this));
    }
}
