package com.xiaomaoqiu.now.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.PerformanceTestCase;


import com.xiaomaoqiu.now.PetAppLike;

import java.util.Set;

/**
 * Created by long on 2016/4/18.
 */
public class SPUtil {


    //电话号码
    public static final String PHONE_NUMBER = "phone_number";
    //登录态
    public static final String LOGIN_STATUS = "login_status";
    //uid
    public static final String UID = "uid";
    //token
    public static final String TOKEN = "token";
    //宠物id
    public static final String PET_ID = "petId";
    //宠物name
    public static final String PET_NAME = "petName";
    //宠物描述
    public static final String PET_DESCRIPTION = "petDescription";
    //宠物是否在家
    public static final String PET_AT_HOME = "petAtHome";
    //体重
    public static final String PET_WEIGHT = "petWeight";

    //todo 两个imei
    //设备imei
    public static final String DEVICE_IMEI = "deviceImei";
    //target_step
    public static final String PET_TARGET_STEP = "targetStep";
    //性别
    public static final String SEX = "sex";
    //昵称
    public static final String PEI_NICK = "petNick";
    //生日
    public static final String BIRTHDAY = "birthday";
    //宠物头像
    public static final String PET_HEADER = "logo_url";
    //宠物类型id
    public static final String PET_TYPE_ID = "pet_type_id";


    //设备电量级别
    public static final String BATTERY_LEVEL = "battery_level";

    //firmware_version
    public static final String FIRMWARE_VERSION = "firmware_version";

    //设备名称
    public static final String DEVICE_NAME = "device_name";
    //sim卡iccid信息
    public static final String SIM_ICCID = "iccid";
    //设备是否存在
    public static final String DEVICE_EXIST = "device_exist";


    public static String getPhoneNumber() {
        return getString(PHONE_NUMBER);
    }

    public static void putPhoneNumber(String value) {
        putString(PHONE_NUMBER, value);
    }

    public static boolean getLoginStatus() {
        return getBoolean(LOGIN_STATUS);
    }

    public static void putLoginStatus(boolean status) {
        putBoolean(LOGIN_STATUS, status);
    }

    public static long getUid() {
        return getLong(UID);
    }

    public static void putUid(long uid) {
        putLong(UID, uid);
    }

    public static String getToken() {
        return getString(TOKEN);
    }

    public static void putToken(String token) {
        putString(TOKEN, token);
    }

    public static long getPetId() {
        return getLong(PET_ID);
    }

    public static void putPetId(long petid) {
        putLong(PET_ID, petid);
    }

    public static String getPetName() {
        return getString(PET_NAME);
    }

    public static void putPetName(String name) {
        putString(PET_NAME, name);
    }

    public static String getPetDescription() {
        return getString(PET_DESCRIPTION);
    }

    public static void putPetDescription(String description) {
        putString(PET_DESCRIPTION, description);
    }

    public static String getPetWeight() {
        return getString(PET_WEIGHT);
    }

    public static void putWeight(String weight) {
        putString(PET_WEIGHT, weight);
    }

    public static String getDeviceImei() {
        return getString(DEVICE_IMEI);
    }

    public static void putDeviceImei(String imei) {
        putString(DEVICE_IMEI, imei);
    }

    public static int getPetTargetStep() {
        return getInt(PET_TARGET_STEP);
    }

    public static void putPetTargetStep(int targetStep) {
        putInt(PET_TARGET_STEP, targetStep);
    }

    public static int getSex() {
        return getInt(SEX);
    }

    public static void putSex(int sex) {
        putInt(SEX, sex);
    }

    public static String getPeiNick() {
        return getString(PEI_NICK);
    }

    public static void putPetNick(String nick) {
        putString(PEI_NICK, nick);
    }

    public static String getBirthday() {
        return getString(BIRTHDAY);
    }

    public static void putBirthday(String birthday) {
        putString(BIRTHDAY, birthday);
    }

    public static String getPetHeader() {
        return getString(PET_HEADER);
    }

    public static void putPetHeader(String url) {
        putString(PET_HEADER, url);
    }

    public static int getPetTypeId() {
        return getInt(PET_TYPE_ID);
    }

    public static void putPetTypeId(int id) {
        putInt(PET_TYPE_ID, id);
    }

    public static float getBatteryLevel() {
        return getFloat(BATTERY_LEVEL);
    }

    public static void putBatteryLevel(float level) {
        putFloat(BATTERY_LEVEL, level);
    }

    public static String getFirmwareVersion() {
        return getString(FIRMWARE_VERSION);
    }

    public static void putFirmwareVersion(String firmwareVersion) {
        putString(FIRMWARE_VERSION, firmwareVersion);
    }

    public static String getDeviceName() {
        return getString(DEVICE_NAME);
    }

    public static void putDeviceName(String name) {
        putString(DEVICE_NAME, name);
    }

    public static String getSimIccid() {
        return getString(SIM_ICCID);
    }

    public static void putSimIccid(String iccid) {
        putString(SIM_ICCID, iccid);
    }

    public static boolean getIsDeviceExist() {
        return getBoolean(DEVICE_EXIST);
    }

    public static void putIsDeviceExist(boolean value) {
        putBoolean(DEVICE_EXIST, value);
    }

    public static boolean getPetAtHome() {
        return getBoolean(PET_AT_HOME);
    }

    public static void putPetAtHome(boolean value) {
        putBoolean(PET_AT_HOME, value);
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