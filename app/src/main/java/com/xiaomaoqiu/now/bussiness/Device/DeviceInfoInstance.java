package com.xiaomaoqiu.now.bussiness.Device;

import com.xiaomaoqiu.now.bean.nocommon.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.old.dataCenter.DeviceInfo;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/9.
 */

public class DeviceInfoInstance {

    //    {
//        "status": 0,
//            "battery_level": 0,
//            "firmware_version": "X2_Plus_V1.1",
//            "imei": "357396080000293"
//    }
    public static DeviceInfoInstance instance;

    private DeviceInfoInstance() {
    }

    public static DeviceInfoInstance getInstance() {
        if (instance == null) {
            instance = new DeviceInfoInstance();
            instance.battery_level = SPUtil.getBatteryLevel();
            instance.imei = SPUtil.getDeviceImei();
            instance.firmware_version = SPUtil.getFirmwareVersion();
            instance.deviceName = SPUtil.getDeviceName();
            instance.iccid = SPUtil.getSimIccid();

        }
        return instance;
    }

    public int battery_level;

    //固件版本
    public String firmware_version;

    public String imei;

    public String deviceName;

    public String iccid;


    //保存设备信息
    public void saveDeviceInfo(DeviceInfoBean message) {
        battery_level = message.battery_level;
        SPUtil.putBatteryLevel(battery_level);
        firmware_version = message.firmware_version;
        SPUtil.putFirmwareVersion(firmware_version);
        imei = message.imei;
        SPUtil.putDeviceImei(imei);
        deviceName = message.device_name;
        SPUtil.putDeviceName(deviceName);
        iccid = message.iccid;
        SPUtil.putSimIccid(iccid);
    }
    //清空设备信息
    public void clearDeviceInfo(){
        battery_level = 0;
        SPUtil.putBatteryLevel(0);
        firmware_version = "";
        SPUtil.putFirmwareVersion("");
        imei = "";
        SPUtil.putDeviceImei("");
        deviceName = "";
        SPUtil.putDeviceName("");
        iccid = "";
        SPUtil.putSimIccid("");
    }

    //获取设备信息
    public  void getDeviceInfo() {
        ApiUtils.getApiService().getDeviceInfo(UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken(), PetInfoInstance.getPetInfoInstance().getPet_id()).enqueue(new XMQCallback<DeviceInfoBean>() {
            @Override
            public void onSuccess(Response<DeviceInfoBean> response, DeviceInfoBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    saveDeviceInfo(message);
                } else if (ret == HttpCode.EC_DEVICE_NOT_EXIST) {
                    //追踪器不存在
                    //清空信息
                    clearDeviceInfo();
                }
            }

            @Override
            public void onFail(Call<DeviceInfoBean> call, Throwable t) {

            }
        });
    }

}
