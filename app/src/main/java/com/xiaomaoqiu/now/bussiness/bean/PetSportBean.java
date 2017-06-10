package com.xiaomaoqiu.now.bussiness.bean;

import com.xiaomaoqiu.now.base.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 17/4/7.
 */

public class PetSportBean extends BaseBean {

    public List<SportBean> data = new ArrayList<SportBean>();

    public static class SportBean {
        public String date;
        public double target_amount;
        public int reality_amount;
        public double percentage;
    }

}
