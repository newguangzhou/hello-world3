package com.xiaomaoqiu.now.util;

import android.content.Context;
import android.content.SharedPreferences;


import com.xiaomaoqiu.now.PetAppLike;

import java.util.Set;

/**
 * Created by long on 2016/4/18.
 */
public class SPUtil {



     //电话号码
    public static final String PHONE_NUMBER = "phone_number";
//登录态
    public static final String LOGIN_STATUS="login_status";




    public static String getPhoneNumber() {
        return getString(PHONE_NUMBER);
    }
    public static void putPhoneNumber(String value){
        putString(PHONE_NUMBER,value);
    }
    public static boolean getLoginStatus(){
        return getBoolean(LOGIN_STATUS);
    }
    public static void putLoginStatus(boolean status){
        putBoolean(LOGIN_STATUS,status);
    }


    private static SharedPreferences getSP() {
        return PetAppLike.mcontext.getSharedPreferences("xmq-sp", Context.MODE_PRIVATE);
    }

    private static void putBoolean(String key, boolean value) {
        getSP().edit().putBoolean(key, value).commit();
    }

    private static void putInt(String key, int value) {
        getSP().edit().putInt(key, value).commit();
    }

    private static void putFloat(String key, float value) {
        getSP().edit().putFloat(key, value).commit();
    }

    private static void putLong(String key, long value) {
        getSP().edit().putLong(key, value).commit();
    }

    private static void putString(String key, String value) {
        getSP().edit().putString(key, value).commit();
    }

    private static void putStringSet(String key, Set<String> value) {
        getSP().edit().putStringSet(key, value).commit();
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        return getSP().getBoolean(key, defaultValue);
    }

    private static boolean getBoolean(String key) {
        return getSP().getBoolean(key, false);
    }

    private static int getInt(String key, int defaultValue) {
        return getSP().getInt(key, defaultValue);
    }

    private static int getInt(String key) {
        return getSP().getInt(key, 0);
    }

    private static float getFloat(String key, float defaultValue) {
        return getSP().getFloat(key, defaultValue);
    }

    private static float getFloat(String key) {
        return getSP().getFloat(key, 0);
    }

    private static long getLong(String key, long defaultValue) {
        return getSP().getLong(key, defaultValue);
    }

    private static long getLong(String key) {
        return getSP().getLong(key, 0);
    }

    private static String getString(String key, String defaultValue) {
        return getSP().getString(key, defaultValue);
    }

    private static String getString(String key) {
        return getSP().getString(key, "");
    }

    private static Set<String> getStringSet(String key, Set<String> defaultValue) {
        return getSP().getStringSet(key, defaultValue);
    }

    private static Set<String> getStringSet(String key) {
        return getSP().getStringSet(key, null);
    }

}