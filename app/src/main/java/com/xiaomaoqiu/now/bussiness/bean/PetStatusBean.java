package com.xiaomaoqiu.now.bussiness.bean;

import com.xiaomaoqiu.now.base.BaseBean;

/**
 * Created by long on 2017/4/12.
 */

public class PetStatusBean extends BaseBean {
    public int pet_status;
    public int pet_is_in_home;//1 在家  0 不在家
    public int device_status;//0表示离线，1表示在线



    //最近电量获取时间
    public long battery_last_get_time;

    public int battery_level;

    public int battery_status;
}
