package com.xiaomaoqiu.now.bussiness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitBindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.WifiListActivity;
import com.xiaomaoqiu.now.bussiness.pet.AddPetInfoActivity;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/4/13.
 */

public class SplashActivity extends BaseActivity {


    public int frameTemplate()
    {//没有标题栏
        return 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
    }

    Intent intent = new Intent();

    @Override
    protected void onResume() {
        super.onResume();
        toWhere();//判断跳转逻辑
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 3000);


    }

    //判断跳转逻辑
    void toWhere() {
        if (!SPUtil.getLoginStatus()) {
            intent.setClass(SplashActivity.this, LoginActivity.class);
            return;
        }

        if (!(UserInstance.getInstance().pet_id > 0)) {
            intent.setClass(SplashActivity.this, AddPetInfoActivity.class);
            return;
        }
        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei)) {
            intent.setClass(SplashActivity.this, InitBindDeviceActivity.class);
            return;
        }

        //todo 判断是否有homewifi

        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            intent = new Intent(SplashActivity.this, WifiListActivity.class);
            return;
        }


        //todo 都有的话就直接跳转到主页
        intent.setClass(SplashActivity.this, MainActivity.class);
        return;
    }
}
