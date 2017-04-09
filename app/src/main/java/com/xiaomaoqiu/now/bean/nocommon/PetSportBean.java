package com.xiaomaoqiu.now.bean.nocommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 17/4/7.
 */

public class PetSportBean extends BaseBean{

    public List<SportBean> data=new ArrayList<SportBean>();

    public static class SportBean{
       public  int target_amount;
       public  int reality_amount;
       public double percentage;
    }

}
