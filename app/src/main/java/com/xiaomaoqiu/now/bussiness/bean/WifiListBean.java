package com.xiaomaoqiu.now.bussiness.bean;

import com.xiaomaoqiu.now.base.BaseBean;

import java.util.ArrayList;

/**
 * Created by long on 2017/4/14.
 */

public class WifiListBean extends BaseBean {

    public static final long serialVersionUID = -5809782578272943999L;

    public ArrayList<WifiBean> data=new ArrayList<>();


    //获取列表的时间
    public long get_wifi_list_time=System.currentTimeMillis()/1000;
}
