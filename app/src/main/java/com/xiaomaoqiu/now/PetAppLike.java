package com.xiaomaoqiu.now;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.xiaomaoqiu.now.bussiness.pet.PetUtil;
import com.xiaomaoqiu.now.push.XMPushManagerInstance;
import com.xiaomaoqiu.now.util.Apputil;

public class PetAppLike extends DefaultApplicationLike {
    public static String TAG = "com.xiaomaoqiu.now";
    public PetAppLike(Application application, int tinkerFlags,
                      boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                      long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }



    public static Application mcontext;
    public static Environment environment;//当前环境
    public static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mcontext=getApplication();
        environment=Environment.Release;
        if (isMainProcess(getApplication())) {
            Fresco.initialize(mcontext);
            // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
            // 调试时，将第三个参数改为true
            Bugly.init(getApplication(), "5bd407f8b5", environment.bugly_log);

//        XMQCrashHandler handler = XMQCrashHandler.getMyCrashHandler();
//        handler.init(getApplication());
//        Thread.setDefaultUncaughtExceptionHandler(handler);

//        aboutBugly();


            //百度地图
            SDKInitializer.initialize(PetAppLike.mcontext);
            //宠物能量运算工具初始化
            PetUtil.getInstance().init();

            //添加growingio
            GrowingIO.startWithConfiguration(getApplication(), new Configuration()
                    .useID()
                    .trackAllFragments());
        }

    }

    public boolean isMainProcess(Context context) {
        return Apputil.getCurProcessName(context).equals("com.xiaomaoqiu.pet");
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }


//     public void aboutBugly(){
//        Context context = getApplicationContext();
//// 获取当前包名
//        String packageName = context.getPackageName();
//// 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//// 设置是否为上报进程
//        CrashReport.UserStrategy strategy = now CrashReport.UserStrategy(context);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//        //初始化bugly
//        CrashReport.initCrashReport(getApplicationContext(), "5eb6432b7a", true);
//
//    }
//    /**
//     * 获取进程号对应的进程名
//     *
//     * @param pid 进程号
//     * @return 进程名
//     */
//    private static String getProcessName(int pid) {
//        BufferedReader reader = null;
//        try {
//            reader = now BufferedReader(now FileReader("/proc/" + pid + "/cmdline"));
//            String processName = reader.readLine();
//            if (!TextUtils.isEmpty(processName)) {
//                processName = processName.trim();
//            }
//            return processName;
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException exception) {
//                exception.printStackTrace();
//            }
//        }
//        return null;
//    }

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
