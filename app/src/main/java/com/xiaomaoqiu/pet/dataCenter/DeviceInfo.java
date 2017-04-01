package com.xiaomaoqiu.pet.dataCenter;

import com.xiaomaoqiu.pet.notificationCenter.NotificationCenter;
import com.xiaomaoqiu.pet.utils.DateUtil;

import org.json.JSONObject;

/**
 * Created by huangjx on 2016/6/26.
 */
public class DeviceInfo {
    public interface Callback_DeviceInfo
    {
        void onDeviceInfoChanged(DeviceInfo deviceInfo);
    }
    private boolean isDeviceExist=false;

    private String strDeviceName="";    //设备名称
    private String strImei="";          //设备Imei
    private float  fBatteryLevel;    //电池电量
    private String strFirmwareVersion="";//固件版本
    private String strIccid="";            //sim卡iccid信息

    private String lastGetTime="";

    public void initFromJson(JSONObject jsonObject)
    {
        strDeviceName = jsonObject.optString("device_name");
        strImei = jsonObject.optString("imei");
        strIccid = jsonObject.optString("iccid");
        fBatteryLevel = (float)jsonObject.optInt("battery_level",10)/100;
        strFirmwareVersion = jsonObject.optString("firmware_version");
        lastGetTime= DateUtil.deviceInfoTime(System.currentTimeMillis());
        isDeviceExist = true;
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }

    public void initFromNull(){
        strDeviceName = "";
        strImei = "";
        strIccid = "";
        strFirmwareVersion = "";
        lastGetTime= DateUtil.deviceInfoTime(System.currentTimeMillis());
        isDeviceExist=false;
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }

    public boolean getDeviceExist(){
        return isDeviceExist;
    }

    public void setDeviceExist(boolean exist){
        isDeviceExist=exist;
    }

    public String getLastGetTime(){
        return lastGetTime;
    }
    public void setLastGetTime(long mills){
        lastGetTime=DateUtil.deviceInfoTime(mills);
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }

    public String getDeviceName() {return strDeviceName;}
    public void setDeviceName(String strDeviceName){
        this.strDeviceName = strDeviceName;
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }

    public String getImei(){return strImei;}

    public void setImei(String strImei)
    {
        this.strImei = strImei;
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }
    public float getBatteryLevel(){return fBatteryLevel;}
    public void setBatteryLevel(float fBatteryLevel){
        this.fBatteryLevel = fBatteryLevel;
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }

    public String getFirmwareVersion(){return strFirmwareVersion;}
    public void setFirmwareVersion(String strFirmwareVersion)
    {
        this.strFirmwareVersion = strFirmwareVersion;
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }

    public String getIccid() {return strIccid;}
    public void setIccid(String strIccid)
    {
        this.strIccid = strIccid;
        NotificationCenter.INSTANCE.getObserver(Callback_DeviceInfo.class).onDeviceInfoChanged(this);
    }
}
