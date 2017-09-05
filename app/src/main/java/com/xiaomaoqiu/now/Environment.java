package com.xiaomaoqiu.now;

/**
 * Created by long on 17/4/6.
 */

public enum Environment {
    Debug(false),Release(true);
    private Environment(boolean release){
        this.bugly_log=bugly_log;
        if(release){
            bugly_appid="80761b13d7";
        }else {
            bugly_appid="5bd407f8b5";
        }

    }
    //bugly是否打开log
    public boolean bugly_log;

    //线上
    public String bugly_appid="80761b13d7";

}
