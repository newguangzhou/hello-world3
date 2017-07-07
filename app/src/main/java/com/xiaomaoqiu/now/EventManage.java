package com.xiaomaoqiu.now;

import com.xiaomaoqiu.now.bussiness.bean.PetSportBean;

/**
 * Created by long on 17/4/8.
 */

public class EventManage {
    //小米push注册成功
    public static class XMPushRegister{

    }
    //小米设置uid成功
    public static class setAlias {

    }
    //小米设置uid成功
    public static class setAliasFail {

    }

    //设备离线
    public static  class DeviceOffline{

    }
    //绑定设备成功之后
    public static class bindDeviceSuccess {

    }
    //设备已被绑定之后
    public static class deviceAlreadyBind{
        public String old_account;
    }
    //设备重启成功
    public static class deviceReboot{

    }
    //解除绑定之后
    public static class unbindDeviceSuccess{

    }

    //获取设备信息成功之后
    public static class notifyDeviceStateChange {

    }
    //宠物信息添加成功
    public static class addPetInfoSuccess{

    }

    //宠物信息更新
    public static class notifyPetInfoChange {

    }

    //宠物信息更新回调
    public static class callbackUpdatePetInfo{
        public boolean updateHeader=false;

    }

    //用户信息获取成功
    public static class getUserInfoEvent {

    }

    //位置有更新
    public static class notifyPetLocationChange {
        //位置是不是为0
        public boolean isnull;
    }

    //测试位置获取
    public static class testPetLocation{

    }

    public static class atHomeOrtoSport{
    }

    //wifi列表获取成功
    public static class wifiListSuccess{

    }

    //WIFI列表获取失败
    public static class wifiListError{}

    //当天运动量
    public static class TodaySportData{
       public  PetSportBean.SportBean sportBean=new PetSportBean.SportBean();
    }

    //目标运动量变化
    public static class targetSportData{
    }

    //上传头像成功
    public static class uploadImageSuccess{}

    //GPS变化
    public static class GPS_CHANGE{

    }

    //手机和设备距离很近
    public static  class distanceClose{

    }

    //绑定流程中位置信息变更提醒
    public static class bindingLocationChanged{
        public double latitude;
        public double longitude;
    }

}
