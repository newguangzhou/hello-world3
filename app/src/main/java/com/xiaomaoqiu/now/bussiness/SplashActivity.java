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
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.AddPetInfoActivity;
import com.xiaomaoqiu.now.bussiness.user.ConfirmBatteryActivity;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.push.XMPushManagerInstance;
import com.xiaomaoqiu.now.util.Apputil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.pet.R;
import com.xiaomi.mipush.sdk.MiPushClient;

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
        toWhere();//判断跳转逻辑

    }


    //判断跳转逻辑
    void toWhere() {
        if (!SPUtil.getLoginStatus()) {
            SPUtil.putAPP_VERSION(Apputil.getVersionCode()+"");
            PetAppLike.mainHandler = new Handler(getMainLooper());
            PetAppLike.mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);

        }else {
//            if(!SPUtil.getAPP_VERSION().equals(Apputil.getVersionCode()+"")){
                MiPushClient.registerPush(PetAppLike.mcontext, XMPushManagerInstance.APP_ID, XMPushManagerInstance.APP_KEY);
                SPUtil.putAPP_VERSION(Apputil.getVersionCode()+"");
//            }
            EventBus.getDefault().register(this);
            //获取基本信息
            UserInstance.getInstance().getUserInfo();
        }
    }

    //网络获取用户信息成功
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void next(EventManage.getUserInfoEvent event) {
        EventBus.getDefault().unregister(this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
        Intent intent = new Intent();
        if(UserInstance.getInstance().agree_policy ==0){
            intent.setClass(SplashActivity.this, ConfirmBatteryActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei)||"-1".equals(UserInstance.getInstance().device_imei)) {
//            intent.setClass(SplashActivity.this, InitBindDeviceActivity.class);
            intent.setClass(SplashActivity.this, ConfirmBatteryActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (!(UserInstance.getInstance().pet_id > 0)) {
            intent.setClass(SplashActivity.this, AddPetInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            intent.setClass(SplashActivity.this, InitWifiListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(UserInstance.getInstance().latitude==-1){
            intent.setClass(SplashActivity.this, InitMapLocationActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        intent.setClass(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
