package com.xiaomaoqiu.now.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xiaomaoqiu.now.PetAppLike;

import java.util.List;

/**
 * Created by long on 2017/4/17.
 */
@SuppressLint("WrongConstant")
public class Apputil {

    //SD卡下通用的存储路径：SD卡路径下：Android/data/包名；
    public static String sdNormalPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + Apputil.getPackageName(PetAppLike.mcontext);
    public static String imei;


    public static String getVersionName(Context context) {
        String versionName = "";
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(context), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取应用包名
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        if (context == null) {
            return "context为null";
        }
        int pid = android.os.Process.myPid();
        @SuppressLint("WrongConstant") ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        if (mActivityManager != null && runningAppProcessInfos != null && runningAppProcessInfos.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfos) {
                if (appProcess != null && appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }


    /**
     * 手机获取imei的方法
     */
    public static String getIMEI(Context context) {
        if (!TextUtils.isEmpty(imei)) return imei;
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(imei) || "000000000000000".equals(imei) || "0".equals(imei)) {
            //todo 获取手机imei
//            imei = SPUtil.getImeikey();
        }
        if (TextUtils.isEmpty(imei)) {
            StringBuilder stringBuilder = new StringBuilder("35");
            for (int i = 0; i < 13; i++) {
                stringBuilder.append(String.valueOf((int) (Math.random() * 10)));
            }
            //todo 获取手机imei
//            SPUtil.putImeikey(stringBuilder.toString());
        }
        return imei;
    }


}
