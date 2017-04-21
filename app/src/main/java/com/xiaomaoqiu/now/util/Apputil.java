package com.xiaomaoqiu.now.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by long on 2017/4/17.
 */

public class Apputil {
    public static String getVersionName(Context context){
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
    public static String getPackageName(Context context){
        return context.getPackageName();
    }
}
