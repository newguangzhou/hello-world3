package com.xiaomaoqiu.now.bussiness.Device;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.bean.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.WifiBean;
import com.xiaomaoqiu.now.bussiness.bean.WifiListBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.AppDialog;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;

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
//            "device_imei": "357396080000293"
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
            bean.hardware_version =SPUtil.getDeviceVersion();
            bean.sim_deadline=SPUtil.getSimDeadline();
            bean.battery_last_get_time=SPUtil.getLAST_START_WALK_TIME();
            instance.isDeviceExist = SPUtil.getIsDeviceExist();
            instance.packBean = bean;
            if (!TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
                WifiBean wifiBean = new WifiBean();
                wifiBean.wifi_ssid = UserInstance.getInstance().wifi_ssid;
                wifiBean.wifi_bssid = UserInstance.getInstance().wifi_bssid;
                wifiBean.is_homewifi = 1;
                instance.wiflist.data.add(wifiBean);
            }


//            instance.wiflist=SPUtil.getWifiList();
//            if(instance.wiflist==null){
//                instance.wiflist=new WifiListBean();
//            }
        }
        return instance;
    }

    //todo 这个东西比较特殊,没有包含到bean里面
    public float battery_level;
//
//    //固件版本
//    public String firmware_version;
//
//    public String device_imei;
//
//    public String deviceName;
//
//    public String iccid;

    public DeviceInfoBean packBean;


    //设备是否存在
    public boolean isDeviceExist = false;
    //最近获取时间
    public String lastGetTime = "";

    //wifi列表
    public WifiListBean wiflist = new WifiListBean();

    //保存设备信息
    public void saveDeviceInfo(DeviceInfoBean message) {
        instance.battery_level = message.battery_level / 100f;
        SPUtil.putBatteryLevel(battery_level);
        packBean.firmware_version = message.firmware_version;
        SPUtil.putFirmwareVersion(packBean.firmware_version);
        packBean.imei = message.imei;
        SPUtil.putDeviceImei(packBean.imei);
        packBean.sim_deadline=message.sim_deadline;
        SPUtil.putSimDeadline(packBean.sim_deadline);
        packBean.hardware_version =message.hardware_version;
        SPUtil.putDeviceVersion(packBean.hardware_version);


        UserInstance.getInstance().device_imei = packBean.imei;


        packBean.device_name = message.device_name;
        SPUtil.putDeviceName(packBean.device_name);
        packBean.iccid = message.iccid;
        SPUtil.putSimIccid(packBean.iccid);
        isDeviceExist = true;
        SPUtil.putIsDeviceExist(true);

        packBean.battery_last_get_time=message.battery_last_get_time;
        SPUtil.putBatteryLastGetTime(packBean.battery_last_get_time);
        lastGetTime = AppDialog.DateUtil.deviceInfoTime(packBean.battery_last_get_time);
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



        UserInstance.getInstance().device_imei = "";
        wiflist.data.clear();
        UserInstance.getInstance().wifi_ssid = "";
        SPUtil.putHomeWifiSsid("");
        UserInstance.getInstance().wifi_bssid = "";
        SPUtil.putHomeWifiMac("");



        SPUtil.putDeviceName("");
        packBean.iccid = "";
        SPUtil.putSimIccid("");
        isDeviceExist = false;
        SPUtil.putIsDeviceExist(false);
//        lastGetTime = AppDialog.DateUtil.deviceInfoTime(System.currentTimeMillis());
    }

    //获取设备信息
    public void getDeviceInfo() {
        ApiUtils.getApiService().getDeviceInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id()).enqueue(new XMQCallback<DeviceInfoBean>() {
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
                EventBus.getDefault().post(new EventManage.notifyDeviceStateChange());
            }

            @Override
            public void onFail(Call<DeviceInfoBean> call, Throwable t) {
            }
        });
    }

    //绑定设备
    public void bindDevice(String imei) {

        ApiUtils.getApiService().addDeviceInfo(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                imei,
                "xmq_test"
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret){
                    case EC_SUCCESS:
//                        getDeviceInfo();
                        EventBus.getDefault().post(new EventManage.bindDeviceSuccess());
                        Toast.makeText(PetAppLike.mcontext, "绑定成功", Toast.LENGTH_SHORT).show();
                        break;
                    case EC_ALREADY_FAV:
//                        ToastUtil.showAtCenter("设备已被绑定");
                        EventBus.getDefault().post(new EventManage.deviceAlreadyBind());
                        break;
                }
//                if (ret == HttpCode.EC_SUCCESS) {
//
//
//                } else {
//                    Toast.makeText(PetAppLike.mcontext, "网络问题，请重试", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {
            }
        });
    }

    //解除绑定
    public void unbindDevice() {
        ApiUtils.getApiService().removeDeviceInfo(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                DeviceInfoInstance.getInstance().packBean.imei
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    clearDeviceInfo();
                    EventBus.getDefault().post(new EventManage.unbindDeviceSuccess());
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });
    }


    public static int count=0;

    //发送获取wifi列表的命令
    public void sendGetWifiListCmd() {
        ApiUtils.getApiService().sendGetWifiListCmd(
                UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id()
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret){
                    case  EC_SUCCESS:
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                            getWifiList();
                        break;
                    case EC_OFFLINE:
                        EventBus.getDefault().post(new EventManage.DeviceOffline());
                        EventBus.getDefault().post(new EventManage.wifiListError());
                        break;
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {
                EventBus.getDefault().post(new EventManage.wifiListError());
            }
        });
    }


    //获取wifi列表
    public void getWifiList() {
        ApiUtils.getApiService().getWifiList(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id()
        ).enqueue(new XMQCallback<WifiListBean>() {
            @Override
            public void onSuccess(Response<WifiListBean> response, WifiListBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    if (message.data != null && message.data.size() > 0) {
                        wiflist.data = message.data;
//                        SPUtil.putWifiList(wiflist);
                        EventBus.getDefault().post(new EventManage.wifiListSuccess());
                    } else {
                        EventBus.getDefault().post(new EventManage.wifiListError());
//                        if((count++)<5) {
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            getWifiList();
//                        }
                    }
                }
            }

            @Override
            public void onFail(Call<WifiListBean> call, Throwable t) {
                EventBus.getDefault().post(new EventManage.wifiListError());
            }
        });
    }

}
