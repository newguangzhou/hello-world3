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

public class AboutActivity extends BaseActivity implements View.OnClickListener{
    TextView tv_versionname;
    TextView tv_help;
    TextView tv_policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("关于");
        setContentView(R.layout.activity_about);
        tv_versionname = (TextView) findViewById(R.id.tv_versionname);
        tv_help= (TextView) findViewById(R.id.tv_help);
        tv_policy= (TextView) findViewById(R.id.tv_policy);
        tv_versionname.setText("版本信息：小毛球 " + Apputil.getVersionName(this));

        tv_help.setOnClickListener(this);
        tv_policy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this, BaseWebViewActivity.class);
        switch (v.getId()){
            case R.id.tv_help:
                intent.putExtra("web_url","http://www.xiaomaoqiu.com/supprot.html?nav=2");
                startActivity(intent);
                break;
            case R.id.tv_policy:
                intent.putExtra("web_url","http://www.xiaomaoqiu.com/proto_user.html");
                startActivity(intent);
                break;
        }
    }
}
