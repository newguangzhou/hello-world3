package com.xiaomaoqiu.pet.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomaoqiu.pet.config.Config;
import com.xiaomaoqiu.pet.dataCenter.DeviceInfo;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.dataCenter.PetInfo;
import com.xiaomaoqiu.pet.notificationCenter.NotificationCenter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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





        aboutBugly();

    }
     public void aboutBugly(){
        Context context = getApplicationContext();
// 获取当前包名
        String packageName = context.getPackageName();
// 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
// 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        //初始化bugly
        CrashReport.initCrashReport(getApplicationContext(), "5eb6432b7a", true);

    }
    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

//    private boolean shouldInit() {
//        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
//        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
//        String mainProcessName = getPackageName();
//        int myPid = android.os.Process.myPid();
//        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
//            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
