package com.xiaomaoqiu.now.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;


import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bussiness.bean.WifiListBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by long on 2016/4/18.
 */
public class SPUtil {


    public static final String CRASHTIME = "crashTime";
    public static final String LAST_START_WALK_TIME="last_walk_pet_time";


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
    //宠物运动量计算类型
    public static final String TARGET_ENERGY ="target_energy";
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
    //宠物运动量
    public static final String SPORT_TARGET="sport_target";



    //设备电量级别
    public static final String BATTERY_LEVEL = "battery_level";
    //设备电量获取时间
    public static final String BATTERY_LAST_GET_TIME="battery_last_get_time";

    //设备wifi列表
    public static final String DEVICE_WIFI_LIST = "device_wifi_list";

    //firmware_version
    public static final String FIRMWARE_VERSION = "firmware_version";

    //设备名称
    public static final String DEVICE_NAME = "device_name";
    //设备版本号
    public static final String DEVICE_VERSION="hardware_version";
    //sim卡iccid信息
    public static final String SIM_ICCID = "iccid";
    //设备是否存在
    public static final String DEVICE_EXIST = "device_exist";
    //设备sim卡充值到期时间
    public static final String SIM_DEADLINE="sim_deadline";

    //HOME_WIFI_MAC
    public static final String HOME_WIFI_MAC = "home_wifi_mac";
    //home_wifi_ssid
    public static final String HOME_WIFI_SSID="home_wifi_ssid";

    //home_longitude
    public static final String HOME_LONGITUDE="home_longitude";
    //home_latitude
    public static final String HOME_LATITUDE="home_latitude";

    //是否重启
    public static final String HAS_REBOOT="has_reboot";

    //地图定位模式
    public static final String MAP_MODE="map_mode";

    //纬度
    public static final String PHONE_LATITUDE ="phone_latitude";
    //经度
    public static final String PHONE_LONGITUDE ="phone_longitude";
    public static final  String PET_LATITUDE="pet_latitude";
    public static final String PET_LONGTITUDE="pet_longitude";

    //gps是否开启
    public static final String GPS_OPEN="gps_open";

    public static final String PET_MODE="PET_MODE";

    //是否进入主页
    public static final String HOME_PAGE ="home";


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
    public static String getTargetEnergy(){
        return getString(TARGET_ENERGY);
    }
    public static void putEnergyType(String energy_type){
        putString(TARGET_ENERGY,energy_type);
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

    public  static int getSportTarget(){
        return getInt(SPORT_TARGET);
    }

    public static void putSportTarget(int sportTarget){
        putInt(SPORT_TARGET,sportTarget);
    }

    public static float getBatteryLevel() {
        return getFloat(BATTERY_LEVEL);
    }

    public static void putBatteryLevel(float level) {
        putFloat(BATTERY_LEVEL, level);
    }

    public static float getBatteryLastGetTime(){
        return getFloat(BATTERY_LAST_GET_TIME);
    }
    public static void putBatteryLastGetTime(float time){
        putFloat(BATTERY_LAST_GET_TIME,time);
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

    public static String getDeviceVersion(){
        return getString(DEVICE_VERSION);
    }
    public static void putDeviceVersion(String deviceversion){
        putString(DEVICE_VERSION,deviceversion);
    }

    public static String getSimIccid() {
        return getString(SIM_ICCID);
    }

    public static void putSimIccid(String iccid) {
        putString(SIM_ICCID, iccid);
    }

    public static String getSimDeadline(){
        return getString(SIM_DEADLINE);
    }
    public static void putSimDeadline(String simdeadline){
        putString(SIM_DEADLINE,simdeadline);
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

    public static String getHomeWifiMac() {
        return getString(HOME_WIFI_MAC);
    }

    public static void putHomeWifiMac(String value) {
        putString(HOME_WIFI_MAC, value);
    }

    public static String getHOME_LONGITUDE(){
        return getString(HOME_LONGITUDE,"-1");
    }
    public static void putHOME_LONGITUDE(String longitudeString){
        putString(HOME_LONGITUDE,longitudeString);
    }
    public static String getHOME_LATITUDE(){
        return getString(HOME_LATITUDE,"-1");
    }
    public static void putHOME_LATITUDE(String latitudeString){
        putString(HOME_LATITUDE,latitudeString);
    }

    public static String getHomeWifiSsid(){
        return getString(HOME_WIFI_SSID);
    }
    public static void putHomeWifiSsid(String ssid){
        putString(HOME_WIFI_SSID,ssid);
    }

    public static void putHasReboot(int hasreboot){
        putInt(HAS_REBOOT,hasreboot);
    }
    public static int getHasReboot(){
        return getInt(HAS_REBOOT);
    }


    public static WifiListBean getWifiList(){
        return (WifiListBean) getSerializable(DEVICE_WIFI_LIST);
    }
    public static void putWifiList(WifiListBean wifiListBean) {
        putSerializable(DEVICE_WIFI_LIST, wifiListBean);
    }

//    public static String getMode_Map(){
//        return getString(MAP_MODE,Mode_Map.Normal);
//    }
//    public static void putMode_Map(String value){
//        putString(MAP_MODE,value);
//    }
    public static String getPhoneLatitude(){
        return getString(PHONE_LATITUDE,"0");
    }
    public static void putPhoneLatitude(String latitude){
        putString(PHONE_LATITUDE,latitude);
    }
    public static String getPhoneLongitude(){
        return getString(PHONE_LONGITUDE,"0");
    }
    public static void putPhoneLongitude(String value){
        putString(PHONE_LONGITUDE,value);
    }

    public static String getPetLatitude(){
        return getString(PET_LATITUDE,"0");
    }
    public static void putPetLatitude(String latitude){
        putString(PET_LATITUDE,latitude);
    }
    public static String getPetLongtitude(){
        return getString(PET_LONGTITUDE,"0");
    }
    public static void putPetLongitude(String longitude){
        putString(PET_LONGTITUDE,longitude);
    }


    public static boolean getGPS_OPEN(){
        return getBoolean(GPS_OPEN);
    }
    public static void putGPS_OPEN(boolean value){
        putBoolean(GPS_OPEN,value);
    }

    public static int getPET_MODE(){
        return getInt(PET_MODE);
    }
    public static void putPET_MODE(int value){
        putInt(PET_MODE,value);
    }

    /**
     * 获取
     */
    public static long getCrashTime() {
        return getLong(CRASHTIME);
    }

    /**
     * 保存
     */
    public static void putCrashTime(long value) {
        putLong(CRASHTIME, value);
    }

    public static int getLAST_START_WALK_TIME(){
        return getInt(LAST_START_WALK_TIME,(int) (System.currentTimeMillis()/1000));
    }
    public static void putLAST_START_WALK_TIME(int value){
        putInt(LAST_START_WALK_TIME,value);
    }


    /**
     * 获取
     * @return
     */
    public static boolean getHomePage(){
        return getBoolean(HOME_PAGE);
    }

    /**
     * 保存
     * @return
     */
    public static void putHome(boolean isHome){
        putBoolean(HOME_PAGE,isHome);
    }


    /**
     * 任意key-value
     * @return
     */
    public static void putKey_Value(String key,boolean value){
        putBoolean(key,value);
    }

    /**
     * 任意key-value
     * @return
     */
    public static boolean getKey_Value(String key){
        return getBoolean(key);
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
        return getSP().getLong(key, -1);
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

    private static Serializable getSerializable(String key) {
        Serializable serializable = null;
        try {
            SharedPreferences sharedPreferences = getSP();
            String serializableString = sharedPreferences.getString(key, "");
            byte[] mobileBytes = Base64.decode(serializableString.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            try {
                serializable = (Serializable) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializable;
    }

    private static void putSerializable(String key, Serializable value) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(value);
            SharedPreferences sharedPreferences = getSP();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String mobilesString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            editor.putString(key, mobilesString);
            editor.commit();
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}