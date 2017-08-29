package com.xiaomaoqiu.now.bussiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/4/20.
 */

public class BaseWebViewActivity extends BaseActivity {

    @Override
    public int frameTemplate() {
        return 0;
    }

    View btn_go_back;
    TextView tv_title;
    WebView wv_net;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        btn_go_back=findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title= (TextView) findViewById(R.id.tv_title);
        wv_net= (WebView) findViewById(R.id.wv_net);



        WebSettings webSettings = wv_net.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //不能加载http与https混合内容的问题
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);


        //设置Web视图
        wv_net.setWebViewClient(new webViewClient ());
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
       String urlString= intent.getStringExtra("web_url");
        //加载需要显示的网页
        wv_net.loadUrl(urlString);
        if(!"".equals(title)){
            tv_title.setText(title);
        }

    }

    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_net.canGoBack()) {
//            wv_net.goBack(); //goBack()表示返回WebView的上一页面
//            return true;
//        }
        finish();//结束退出程序
        return false;
    }

    //Web视图
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
