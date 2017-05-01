package com.xiaomaoqiu.now.push;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.util.LogUtil;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * Created by long on 17/5/1.
 */

public class XMPushManager {
    private static final String APP_ID = "2882303761517405077";
    private static final String APP_KEY = "5131740540077";
    private static final String APP_SECRET = "pp3yIljAmDibWw4EelYD0Q==";
    private static OnResult onRegisterResult;
    private static void openLogger(){
        //打开Logcat调试日志
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }
            @Override
            public void log(String content, Throwable t) {
                LogUtil.e("content="+content+" throwable="+t);
            }
            @Override
            public void log(String content) {
                LogUtil.e("content="+content);
            }
        };
        Logger.setLogger(PetAppLike.mcontext, newLogger);
    }

    public static void init(){
        //初始化push推送服务
        if(shouldInit()) {
            MiPushClient.registerPush(PetAppLike.mcontext, APP_ID, APP_KEY);
        }
        openLogger();
    }

    private static boolean shouldInit() {
        @SuppressLint("WrongConstant") ActivityManager am = ((ActivityManager) PetAppLike.mcontext.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = PetAppLike.mcontext.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

//    public static void checkManifest(){
//        MiPushClient.checkManifest(PetAppLike.mcontext);
//    }

    public static void resume(){
        MiPushClient.resumePush(PetAppLike.mcontext,null);
    }

    public static void stop(){
        MiPushClient.pausePush(PetAppLike.mcontext,null);
    }

    public static void register(String deviceId,OnResult onResult){
        MiPushClient.setAlias(PetAppLike.mcontext,deviceId,null);
        XMPushManager.onRegisterResult = onResult;
    }

    public static String getRegId(){
        return MiPushClient.getRegId(PetAppLike.mcontext);
    }

    public static OnResult getOnRegisterResult(){
        return onRegisterResult;
    }
}
