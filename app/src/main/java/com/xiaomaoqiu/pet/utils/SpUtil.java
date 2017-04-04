package com.xiaomaoqiu.pet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.xiaomaoqiu.pet.PetAppLike;


/**
 * Created by Administrator on 2017/1/15.
 */

public class SpUtil {
    public static String SP_FILE_NAME="xmq_pet_config";

    public static SharedPreferences getSP() {
        SharedPreferences sp = PetAppLike.mcontext.getSharedPreferences(
                SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp;
    }

    public static boolean getBooleanConfig(String key, boolean defValue) {
        return getSP().getBoolean(key, defValue);
    }

    public static int getIntConfig(String key, int defValue) {
        return getSP().getInt(key, defValue);
    }

    public static SharedPreferences.Editor getSPEditor() {
        return getSP().edit();
    }

    public static long getLongConfig(String key, long defValue) {
        return getSP().getLong(key, defValue);
    }

    public static void setLongConfig(String key, long value) {
        getSP().edit().putLong(key, value).commit();
    }

    public static void setStringConfig(String key, String value) {
        getSP().edit().putString(key, value).commit();
    }

    public static void setIntConfig(String key, int value) {
        getSP().edit().putInt(key, value).commit();
    }

    public static void setBooleanConfig(String key, boolean value) {
        getSP().edit().putBoolean(key, value).commit();
    }

    public static String getStringConfig(String key) {
        return getSP().getString(key, "");
    }

    public static long getLongConfig(String key) {
        return getLongConfig(key, 0);
    }
}
