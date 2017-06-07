package com.xiaomaoqiu.now.push;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
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
        public static final String ONLINE="online";
        public static final String LOW_BATTERY = "low-battery";
        public static final String ULTRA_LOW_BATTERY = "ultra-low-battery";

    }

    public static class Pet {
        public static final String LOCATIONCHANGE = "location-change";
    }

    RemoteMessageBean formatBean;

    public void notifyData(String message) {
        ToastUtil.showTost("收到小米推送消息：" + message);
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

                EventBus.getDefault().postSticky(new PushEventManage.otherLogin());
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
                DeviceInfoInstance.getInstance().battery_level= (float) formatBean.data.get("battery_level");
                DeviceInfoInstance.getInstance().lastGetTime= (String) formatBean.data.get("battery_last_get_time");
                EventBus.getDefault().post(new PushEventManage.deviceOnline());
                break;
            case Device.LOW_BATTERY:
                DeviceInfoInstance.getInstance().battery_level= (float) formatBean.data.get("battery_level");
                EventBus.getDefault().post(new PushEventManage.batteryLowLevel());
                break;
            case Device.ULTRA_LOW_BATTERY:
                DeviceInfoInstance.getInstance().battery_level= (float) formatBean.data.get("battery_level");
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
                PetInfoInstance.getInstance().latitude = (double) formatBean.data.get("latitude");
                PetInfoInstance.getInstance().location_time = (long) formatBean.data.get("location_time");
                PetInfoInstance.getInstance().longitude = (double) formatBean.data.get("longitude");
                PetInfoInstance.getInstance().radius = (double) formatBean.data.get("radius");
                EventBus.getDefault().post(event);
                break;
        }
    }

    /**
     * 处理其他内容
     */
    public void dealExtra() {

    }
}
