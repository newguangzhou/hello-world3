package com.xiaomaoqiu.now;

/**
 * Created by long on 17/4/6.
 */

public enum Environment {
    Debug(false),Release(true);
    private Environment(boolean bugly_log){
        this.bugly_log=bugly_log;
    }
    //bugly是否打开log
    public boolean bugly_log;

}
