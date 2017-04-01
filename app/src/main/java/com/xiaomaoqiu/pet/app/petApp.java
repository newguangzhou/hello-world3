package com.xiaomaoqiu.pet.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.xiaomaoqiu.pet.config.Config;
import com.xiaomaoqiu.pet.dataCenter.DeviceInfo;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.dataCenter.PetInfo;
import com.xiaomaoqiu.pet.notificationCenter.NotificationCenter;

import java.util.List;

/**
 * Created by Administrator on 2015/6/17.
 */
public class petApp extends Application {
    public static String TAG = "com.xiaomaoqiu.pet";

    public static petApp app;
    @Override
    public void onCreate() {
        super.onCreate();
        Config.init(this);
        app=this;

        // 初始化百度地图SDK
        SDKInitializer.initialize(this);

        NotificationCenter.INSTANCE.addCallbacks(LoginMgr.Callback_Login.class);
        NotificationCenter.INSTANCE.addCallbacks(PetInfo.Callback_PetInfo.class);
        NotificationCenter.INSTANCE.addCallbacks(PetInfo.Callback_PetLocating.class);
        NotificationCenter.INSTANCE.addCallbacks(DeviceInfo.Callback_DeviceInfo.class);
        NotificationCenter.INSTANCE.addCallbacks(LoginMgr.Callback_Settings.class);

        Log.w(petApp.TAG, "app init success");
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
