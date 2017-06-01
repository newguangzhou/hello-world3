package com.xiaomaoqiu.now.push;

import com.alibaba.fastjson.JSON;
import com.xiaomaoqiu.now.util.ToastUtil;

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

    }

    public static class Device {
        public static final String OFFLINE = "offline";
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

        }
    }

    /**
     * 处理设备相关
     */
    public void dealDevice() {

        switch (formatBean.signal) {
            case Device.OFFLINE:

                break;
            case Device.LOW_BATTERY:

                break;
            case Device.ULTRA_LOW_BATTERY:

                break;
        }
    }

    /**
     * 处理宠物相关
     */
    public void dealPet() {
        switch (formatBean.signal) {
            case Pet.LOCATIONCHANGE:

                break;
        }
    }

    /**
     * 处理其他内容
     */
    public void dealExtra() {

    }
}
