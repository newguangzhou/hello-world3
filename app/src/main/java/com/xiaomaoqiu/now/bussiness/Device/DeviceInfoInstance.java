package com.xiaomaoqiu.now.bussiness.Device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.bean.AlreadyBindDeviceBean;
import com.xiaomaoqiu.now.bussiness.bean.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.WifiBean;
import com.xiaomaoqiu.now.bussiness.bean.WifiListBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.AppDialog;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

import static com.xiaomaoqiu.now.bussiness.MainActivity.getLocationWithOneMinute;

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
            instance.battery_status=SPUtil.getBatteryStatus();
            bean.imei = SPUtil.getDeviceImei();
            bean.firmware_version = SPUtil.getFirmwareVersion();
            bean.device_name = SPUtil.getDeviceName();
            bean.iccid = SPUtil.getSimIccid();
            bean.hardware_version = SPUtil.getDeviceVersion();
            bean.sim_deadline = SPUtil.getSimDeadline();
            bean.battery_last_get_time = SPUtil.getLAST_START_WALK_TIME();
//            instance.isDeviceExist = SPUtil.getIsDeviceExist();
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

    public int battery_status;//电量级别
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


//    //设备是否存在
//    public boolean isDeviceExist = false;
    //最近获取时间
    public String lastGetTime = "";

    //wifi列表
    public WifiListBean wiflist = new WifiListBean();

    //设备是否离线
    public boolean online=true;

    //保存设备信息
    public void saveDeviceInfo(DeviceInfoBean message) {
        instance.battery_level = message.battery_level / 100f;
        SPUtil.putBatteryLevel(battery_level);
        instance.battery_status=message.battery_status;
        SPUtil.putBatteryStatus(message.battery_status);
        packBean.firmware_version = message.firmware_version;
        SPUtil.putFirmwareVersion(packBean.firmware_version);
        packBean.imei = message.imei;
        SPUtil.putDeviceImei(packBean.imei);
        packBean.sim_deadline = message.sim_deadline;
        if(!SPUtil.getSimDeadline().equals(packBean.sim_deadline)){
            for(int i=-30;i<=30;i++){
                SPUtil.putKey_Value(i+"",false);
            }
        }
        SPUtil.putSimDeadline(packBean.sim_deadline);
        packBean.hardware_version = message.hardware_version;
        SPUtil.putDeviceVersion(packBean.hardware_version);

        UserInstance.getInstance().device_imei = packBean.imei;

        packBean.device_name = message.device_name;
        SPUtil.putDeviceName(packBean.device_name);
        packBean.iccid = message.iccid;
        SPUtil.putSimIccid(packBean.iccid);
//        isDeviceExist = true;
//        SPUtil.putIsDeviceExist(true);

        packBean.battery_last_get_time = message.battery_last_get_time;
        SPUtil.putBatteryLastGetTime(packBean.battery_last_get_time);
        lastGetTime = AppDialog.DateUtil.deviceInfoTime(packBean.battery_last_get_time);
    }

    //清空设备信息
    public void clearDeviceInfo() {
        battery_level = 0.0f;
        SPUtil.putBatteryLevel(0);
        battery_status=0;
        SPUtil.putBatteryStatus(0);
        packBean.firmware_version = "";
        SPUtil.putFirmwareVersion("");
        packBean.imei = "";

        SPUtil.putDeviceImei("");
        packBean.device_name = "";


        UserInstance.getInstance().latitude=-1;
        UserInstance.getInstance().longitude=-1;
        SPUtil.putHOME_LONGITUDE("-1");
        SPUtil.putHOME_LATITUDE("-1");

        UserInstance.getInstance().device_imei = "";

        UserInstance.getInstance().agree_policy = 0;
        SPUtil.putAgreePolicy(0);

        wiflist.data.clear();
        UserInstance.getInstance().wifi_ssid = "";
        SPUtil.putHomeWifiSsid("");
        UserInstance.getInstance().wifi_bssid = "";
        SPUtil.putHomeWifiMac("");

        SPUtil.putDeviceName("");
        packBean.iccid = "";
        SPUtil.putSimIccid("");
//        isDeviceExist = false;
        SPUtil.putIsDeviceExist(false);
//        lastGetTime = AppDialog.DateUtil.deviceInfoTime(System.currentTimeMillis());
    }

    //获取设备信息
    public void getDeviceInfo() {
        ApiUtils.getApiService().getDeviceInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), UserInstance.getInstance().pet_id).enqueue(new XMQCallback<DeviceInfoBean>() {
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
    public void bindDevice(final Activity activity, final String imei) {
        DialogUtil.showProgress(activity,"正在搜索追踪器…");
        ApiUtils.getApiService().addDeviceInfo(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                imei,
                "xmq_test"
        ).enqueue(new XMQCallback<AlreadyBindDeviceBean>() {
            @Override
            public void onSuccess(Response<AlreadyBindDeviceBean> response, AlreadyBindDeviceBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);

                switch (ret) {
                    case EC_SUCCESS:
                        if(DeviceInfoInstance.getInstance().online!=true) {
                            DeviceInfoInstance.getInstance().online = true;
                            EventBus.getDefault().post(new PushEventManage.deviceOnline());
                        }
                        EventBus.getDefault().post(new EventManage.bindDeviceSuccess());
                        UserInstance.getInstance().device_imei=imei;
                        packBean.imei=imei;
                        SPUtil.putDeviceImei(imei);
                        Toast.makeText(PetAppLike.mcontext, "绑定成功", Toast.LENGTH_SHORT).show();
                        DialogUtil.closeProgress();

                        break;
                    case EC_ALREADY_FAV:
//                        ToastUtil.showAtCenter("设备已被绑定");
                        EventManage.deviceAlreadyBind event = new EventManage.deviceAlreadyBind();
                        event.old_account = message.old_account;
                        EventBus.getDefault().post(event);
                        DialogUtil.closeProgress();
                        break;
                    case EC_INVALID_ARGS:
                        ToastUtil.showTost("IMEI码有误");
                        DialogUtil.closeProgress();
                        break;
                    case EC_OFFLINE:
                        DeviceInfoInstance.getInstance().online=false;
                        EventBus.getDefault().post(new EventManage.DeviceOffline());
                        break;
                    default:
                        DialogUtil.closeProgress();
                        break;
                }
            }

            @Override
            public void onFail(Call<AlreadyBindDeviceBean> call, Throwable t) {
                DialogUtil.closeProgress();
            }
        });
    }

    //重启设备
    public void rebootDevice() {
        ApiUtils.getApiService().rebootDevice(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
//                DeviceInfoInstance.getInstance().packBean.imei,
                UserInstance.getInstance().device_imei,
//                PetInfoInstance.getInstance().getPet_id()
                UserInstance.getInstance().pet_id
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        //更新数据后是否已经重启过设备，0：未重启，1：已重启
                        UserInstance.getInstance().agree_policy =1;
                        SPUtil.putAgreePolicy(1);
                        EventBus.getDefault().post(new EventManage.deviceReboot());
                        break;
                    case EC_ALREADY_FAV:

                        break;
                }
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
                    getLocationWithOneMinute=false;
                    clearDeviceInfo();
                    //清空宠物信息
                    PetInfoInstance.getInstance().clearPetInfo();

                    EventBus.getDefault().post(new EventManage.unbindDeviceSuccess());
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });
    }

    //发送获取wifi列表的命令
    public void sendGetWifiListCmd() {
        ApiUtils.getApiService().sendGetWifiListCmd(
                UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
//                PetInfoInstance.getInstance().getPet_id()
                UserInstance.getInstance().pet_id
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
//                        if(DeviceInfoInstance.getInstance().online!=true) {
//                            DeviceInfoInstance.getInstance().online = true;
//                            EventBus.getDefault().post(new PushEventManage.deviceOnline());
//                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        getWifiList();
                        break;
                    case EC_OFFLINE:
//                        online=false;
//                        EventBus.getDefault().post(new EventManage.DeviceOffline());
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



    public static long GET_WIFI_LIST_TIME=System.currentTimeMillis()/1000;;
    //获取wifi列表
    public void getWifiList() {
        ApiUtils.getApiService().getWifiList(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
//                PetInfoInstance.getInstance().getPet_id()
                UserInstance.getInstance().pet_id
        ).enqueue(new XMQCallback<WifiListBean>() {
            @Override
            public void onSuccess(Response<WifiListBean> response, WifiListBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    GET_WIFI_LIST_TIME=message.get_wifi_list_time;
                    if (message.data != null && message.data.size() >= 0) {
                        wiflist.data = message.data;
                        EventBus.getDefault().post(new EventManage.wifiListSuccess());
                    } else {
                        EventBus.getDefault().post(new EventManage.wifiListError());
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
