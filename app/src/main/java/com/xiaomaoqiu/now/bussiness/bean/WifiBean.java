package com.xiaomaoqiu.now.bussiness.bean;

import com.xiaomaoqiu.now.base.BaseBean;

/**
 * Created by long on 2017/4/14.
 */

public class WifiBean extends BaseBean {

    //wifi 名称
    public String wifi_ssid;
    //wifi mac地址
    public String wifi_bssid;
    //wifi场强大小
    public double wifi_power;
    //是否是homewifi
    public int is_homewifi;
}
