package com.xiaomaoqiu.now.bussiness.bean;

import com.xiaomaoqiu.now.base.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 17/4/8.
 */

public class PetSleepInfoBean extends BaseBean {

    public List<SleepBean> data=new ArrayList<SleepBean>();

    public static class SleepBean{
        public double deep_sleep;
        public double light_sleep;
        public String date;
    }
}
