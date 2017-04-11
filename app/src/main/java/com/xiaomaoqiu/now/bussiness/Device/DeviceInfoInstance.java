package com.xiaomaoqiu.now.bussiness.Device;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManager;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.old.utils.DateUtil;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/9.
 */
@SuppressLint("WrongConstant")
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
            DeviceInfoBean bean = new DeviceInfoBean();
            instance.battery_level = SPUtil.getBatteryLevel();
            bean.imei = SPUtil.getDeviceImei();
            bean.firmware_version = SPUtil.getFirmwareVersion();
            bean.device_name = SPUtil.getDeviceName();
            bean.iccid = SPUtil.getSimIccid();
            instance.isDeviceExist = SPUtil.getIsDeviceExist();
            instance.packBean=bean;
        }
        return instance;
    }

    //todo 这个东西比较特殊,没有包含到bean里面
    public float battery_level;
//
//    //固件版本
//    public String firmware_version;
//
//    public String imei;
//
//    public String deviceName;
//
//    public String iccid;

  public   DeviceInfoBean packBean;


    //设备是否存在
    public boolean isDeviceExist = false;
    //最近获取时间
    public String lastGetTime = "";

    //保存设备信息
    public void saveDeviceInfo(DeviceInfoBean message) {
        instance.battery_level = message.battery_level / 100f;
        SPUtil.putBatteryLevel(battery_level);
        packBean. firmware_version = message.firmware_version;
        SPUtil.putFirmwareVersion(packBean.firmware_version);
        packBean. imei = message.imei;
        SPUtil.putDeviceImei(packBean.imei);
        packBean.device_name = message.device_name;
        SPUtil.putDeviceName(packBean.device_name);
        packBean.iccid = message.iccid;
        SPUtil.putSimIccid(packBean.iccid);
        isDeviceExist = true;
        SPUtil.putIsDeviceExist(true);
        lastGetTime = DateUtil.deviceInfoTime(System.currentTimeMillis());
        EventBus.getDefault().post(new EventManager.notifyDeviceStateChange());
    }

    //清空设备信息
    public void clearDeviceInfo() {
        battery_level = 0.0f;
        SPUtil.putBatteryLevel(0);
        packBean.firmware_version = "";
        SPUtil.putFirmwareVersion("");
        packBean.imei = "";
        SPUtil.putDeviceImei("");
        packBean.device_name = "";
        SPUtil.putDeviceName("");
        packBean.iccid = "";
        SPUtil.putSimIccid("");
        isDeviceExist = false;
        SPUtil.putIsDeviceExist(false);
        lastGetTime = DateUtil.deviceInfoTime(System.currentTimeMillis());
        EventBus.getDefault().post(new EventManager.notifyDeviceStateChange());
    }

    //获取设备信息
    public void getDeviceInfo() {
        ApiUtils.getApiService().getDeviceInfo(UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken(), PetInfoInstance.getInstance().getPet_id()).enqueue(new XMQCallback<DeviceInfoBean>() {
            @Override
            public void onSuccess(Response<DeviceInfoBean> response, DeviceInfoBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    saveDeviceInfo(message);
                } else if (ret == HttpCode.EC_DEVICE_NOT_EXIST) {
                    //追踪器不存在
                    //清空信息
                    clearDeviceInfo();
                    ToastUtil.showTost("追踪器不存在，本地信息已清空");
                }
            }

            @Override
            public void onFail(Call<DeviceInfoBean> call, Throwable t) {
            }
        });
    }

    //绑定设备
    public void bindDevice(String imei) {

        ApiUtils.getApiService().addDeviceInfo(UserInstance.getUserInstance().getUid(),
                UserInstance.getUserInstance().getToken(),
                imei,
                "xmq_test"
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    PetInfoInstance.getInstance().getPetInfo();
                    EventBus.getDefault().post(new EventManager.bindDeviceSuccess());
                    Toast.makeText(PetAppLike.mcontext, "绑定成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PetAppLike.mcontext, "网络问题，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {
            }
        });
    }

    //解除绑定
    public void unbindDevice() {
        ApiUtils.getApiService().removeDeviceInfo(UserInstance.getUserInstance().getUid(),
                UserInstance.getUserInstance().getToken(),
                DeviceInfoInstance.getInstance().packBean.imei
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    clearDeviceInfo();
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });
    }

}
