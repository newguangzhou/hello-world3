package com.xiaomaoqiu.now.push;

import java.util.HashMap;

/**
 * Created by long on 2017/5/3.
 */

public class RemoteMessage {
    /**
     * 推送类型：
     * user:用户相关
     * device:设备相关
     * pet:宠物相关
     * other:其他
     */
    public String type;

    public String signal;

    public HashMap data;


}
