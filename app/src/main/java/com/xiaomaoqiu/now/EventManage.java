package com.xiaomaoqiu.now;

import com.xiaomaoqiu.now.bean.nocommon.PetSportBean;

/**
 * Created by long on 17/4/8.
 */

public class EventManage {

    //通知PetFragment可以获取运动信息了
    public static class notifyPetFramentGetActivityInfo {

    }

    //绑定设备成功之后
    public static class bindDeviceSuccess {

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

    //当天运动量
    public static class TodaySportData{
       public  PetSportBean.SportBean sportBean=new PetSportBean.SportBean();
    }
}
