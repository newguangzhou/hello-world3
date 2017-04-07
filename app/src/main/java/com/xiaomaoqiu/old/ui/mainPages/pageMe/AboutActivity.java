package com.xiaomaoqiu.old.ui.mainPages.pageMe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.now.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("关于");
        setContentView(R.layout.activity_about);
    }

    public void onBtnCall(View v)
    {
        String strMobile = getString(R.string.tel_service);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strMobile));
        startActivity(intent);
    }
}
