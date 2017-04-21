package com.xiaomaoqiu.old.utils;

import java.util.List;

/**
 * Created by Administrator on 2017/1/15.
 */

public class CollectionUtil {

    public static int size(List list){
        if(null == list){
            return 0;
        }
        return list.size();
    }
}
