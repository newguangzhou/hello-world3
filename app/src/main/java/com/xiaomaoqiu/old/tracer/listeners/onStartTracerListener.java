package com.xiaomaoqiu.old.tracer.listeners;

/**
 * Created by Administrator on 2017/1/15.
 */

public interface onStartTracerListener {
    void onSuccessStartTrace();
    void onFailStartTrace(String msg);
}
