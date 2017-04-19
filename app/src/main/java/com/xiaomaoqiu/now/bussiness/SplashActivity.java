package com.xiaomaoqiu.now.bussiness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitBindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.WifiListActivity;
import com.xiaomaoqiu.now.bussiness.pet.AddPetInfoActivity;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by long on 2017/4/13.
 */

public class SplashActivity extends BaseActivity {


    public int frameTemplate() {//没有标题栏
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        EventBus.getDefault().register(this);
        toWhere();//判断跳转逻辑
//        PetAppLike. mainHandler=new Handler(getMainLooper());
//        PetAppLike.mainHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(intent);
//                finish();
//            }
//        },1000);
    }


    //网络获取用户信息成功
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void next(EventManage.getUserInfoEvent event) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
        Intent intent = new Intent();
        if (!(UserInstance.getInstance().pet_id > 0)) {
            intent.setClass(SplashActivity.this, AddPetInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei)) {
            intent.setClass(SplashActivity.this, InitBindDeviceActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            intent.setClass(SplashActivity.this, WifiListActivity.class);
            startActivity(intent);
            finish();
        }


    }


    //判断跳转逻辑
    void toWhere() {
        Intent intent;
        intent = new Intent();
        if (!SPUtil.getLoginStatus()) {
            intent.setClass(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //获取基本信息
        UserInstance.getInstance().getUserInfo();

//        if (!(UserInstance.getInstance().pet_id > 0)) {
//            intent.setClass(SplashActivity.this, AddPetInfoActivity.class);
//            return;
//        }
//        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei)) {
//            intent.setClass(SplashActivity.this, InitBindDeviceActivity.class);
//            return;
//        }
//
//
//        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
//            intent = new Intent(SplashActivity.this, WifiListActivity.class);
//            return;
//        }
//
//
//        intent.setClass(SplashActivity.this, MainActivity.class);
//        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
