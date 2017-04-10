package com.xiaomaoqiu.now.map.tracer.listeners;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by Administrator on 2017/1/15.
 */

public interface onTracingListener {
    void onGetCurTrancePos(List<LatLng> points);
}
