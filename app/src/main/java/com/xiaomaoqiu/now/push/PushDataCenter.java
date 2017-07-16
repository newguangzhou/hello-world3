package com.xiaomaoqiu.now.push;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.map.main.MapInstance;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by long on 2017/5/15.
 */

public class PushDataCenter {
    private static PushDataCenter instance;

    private PushDataCenter() {

    }

    public static PushDataCenter getInstance() {
        if (instance == null) {
            instance = new PushDataCenter();
        }
        return instance;
    }

    public static final String USER = "user";
    public static final String DEVICE = "device";
    public static final String PET = "pet";
    public static final String EXTRA = "extra";

    public static class User {
        public static final String REMOTE_LOGIN = "remote-login";
    }

    public static class Device {
        public static final String OFFLINE = "offline";
        public static final String ONLINE = "online";
        public static final String COMMON_BATTERY = "common-battery";
        public static final String LOW_BATTERY = "low-battery";
        public static final String ULTRA_LOW_BATTERY = "ultra-low-battery";

    }

    public static class Pet {
        public static final String LOCATIONCHANGE = "location-change";
        public static final String NOT_HOME = "not-home";//宠物离开家了
        public static final String AT_HOME = "home";//宠物在家了
    }

    RemoteMessageBean formatBean;

    public void notifyData(String message) {

        if (!SPUtil.getHomePage()) {
            //如果不能进主页,直接不处理了
            return;
        }


//        ToastUtil.showTost("收到小米推送消息：" + message);
        formatBean = JSON.parseObject(message, RemoteMessageBean.class);
        if (formatBean == null) {
            return;
        }
        switch (formatBean.type) {
            case USER:
                dealUser();
                break;
            case DEVICE:
                dealDevice();
                break;
            case PET:
                dealPet();
                break;
            case EXTRA:
                dealExtra();
                break;
        }
    }

    /**
     * 处理用户相关
     */
    public void dealUser() {
        switch (formatBean.signal) {
            case User.REMOTE_LOGIN:
                UserInstance.getInstance().clearLoginInfo();
                SPUtil.putHomeWifiMac("");
                SPUtil.putHomeWifiSsid("");
                PetInfoInstance.getInstance().clearPetInfo();
                DeviceInfoInstance.getInstance().clearDeviceInfo();

                SPUtil.putDeviceImei("");
                Intent intent = new Intent(PetAppLike.mcontext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PetAppLike.mcontext.startActivity(intent);

                String remote_login_time = (String) formatBean.data.get("remote_login_time");
                String X_OS_Name = (String) formatBean.data.get("X_OS_Name");
                PushEventManage.otherLogin event = new PushEventManage.otherLogin();
                event.remote_login_time = remote_login_time;
                event.X_OS_Name = X_OS_Name;


                EventBus.getDefault().postSticky(event);
                break;
        }
    }

    /**
     * 处理设备相关
     */
    public void dealDevice() {
        switch (formatBean.signal) {
            case Device.OFFLINE:
                EventBus.getDefault().post(new PushEventManage.deviceOffline());
                EventBus.getDefault().post(new EventManage.DeviceOffline());
                break;
            case Device.ONLINE:
                DeviceInfoInstance.getInstance().battery_level = ((int) formatBean.data.get("battery_level")) / 100f;
                DeviceInfoInstance.getInstance().lastGetTime = (String) formatBean.data.get("datetime");
                EventBus.getDefault().post(new PushEventManage.deviceOnline());
                break;
            case Device.COMMON_BATTERY:
                DeviceInfoInstance.getInstance().battery_level = ((int) formatBean.data.get("battery_level")) / 100f;
                DeviceInfoInstance.getInstance().lastGetTime = (String) formatBean.data.get("datetime");
                EventBus.getDefault().post(new PushEventManage.commonBattery());
                break;
            case Device.LOW_BATTERY:
                DeviceInfoInstance.getInstance().battery_level =((int)formatBean.data.get("battery_level")/100f);
                DeviceInfoInstance.getInstance().lastGetTime = (String) formatBean.data.get("datetime");
                EventBus.getDefault().post(new PushEventManage.batteryLowLevel());
                break;
            case Device.ULTRA_LOW_BATTERY:
                DeviceInfoInstance.getInstance().battery_level = ((int)formatBean.data.get("battery_level")/100f);
                DeviceInfoInstance.getInstance().lastGetTime = (String) formatBean.data.get("datetime");
                EventBus.getDefault().post(new PushEventManage.batterySuperLowLevel());
                break;
        }
    }

    /**
     * 处理宠物相关
     */
    public void dealPet() {
        switch (formatBean.signal) {
            case Pet.LOCATIONCHANGE:
                PushEventManage.locationChange event = new PushEventManage.locationChange();
                PetInfoInstance.getInstance().latitude = Double.valueOf((String) formatBean.data.get("latitude"));
                PetInfoInstance.getInstance().location_time = Long.valueOf((int) formatBean.data.get("location_time"));
                PetInfoInstance.getInstance().longitude = Double.valueOf((String) formatBean.data.get("longitude"));
                PetInfoInstance.getInstance().radius = Double.valueOf((int) formatBean.data.get("radius"));
                EventBus.getDefault().post(event);
                break;
            case Pet.NOT_HOME:
                if (PetInfoInstance.getInstance().getAtHome()) {
                    PetInfoInstance.getInstance().setAtHome(false);
//                    ToastUtil.showTost("宠物离开家了");
//                    if (!MapInstance.GPS_OPEN) {
                        EventBus.getDefault().post(new PushEventManage.petNotHome());
//                    }
                }
                break;
            case Pet.AT_HOME:
                if (!PetInfoInstance.getInstance().getAtHome()) {
//                    ToastUtil.showTost("宠物到家了");
                PetInfoInstance.getInstance().setAtHome(true);
                EventBus.getDefault().post(new PushEventManage.petAtHome());

                }
                break;
        }
    }

    /**
     * 处理其他内容
     */
    public void dealExtra() {

    }
}
