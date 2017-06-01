package com.xiaomaoqiu.now.bussiness.bean;

import com.xiaomaoqiu.now.base.BaseBean;

/**
 * Created by long on 17/4/8.
 */

public class DeviceInfoBean extends BaseBean {
//    {
//        "status": 0,
//            "battery_level": 0,
//            "firmware_version": "X2_Plus_V1.1",
//            "device_imei": "357396080000293"
//    }


//    public int status;

    public int battery_level;

    //最近电量获取时间
    public long battery_last_get_time;


    //固件版本号
    public String firmware_version;

    public String imei;

    public String device_name;

    //sim卡iccid信息
    public String iccid;

    //sim卡充值到期时间
    public String sim_deadline;

    //设备版本号
    public String hardware_version;

}
