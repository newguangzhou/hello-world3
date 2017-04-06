package com.xiaomaoqiu.old.tracer.listeners;

/**
 * Created by Administrator on 2017/1/15.
 */

public interface onStopTracerListener {
    void onSuccessStopTracer();
    void onFailStopTracer(String msg);
}
