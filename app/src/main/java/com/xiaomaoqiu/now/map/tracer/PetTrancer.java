package com.xiaomaoqiu.now.map.tracer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.xiaomaoqiu.now.map.tracer.bean.HistoryTrackData;
import com.xiaomaoqiu.now.map.tracer.listeners.onStartTracerListener;
import com.xiaomaoqiu.now.map.tracer.listeners.onStopTracerListener;
import com.xiaomaoqiu.now.map.tracer.listeners.onTracingListener;
import com.xiaomaoqiu.old.dataCenter.DeviceInfo;
import com.xiaomaoqiu.old.dataCenter.PetInfo;
import com.xiaomaoqiu.old.dataCenter.UserMgr;
import com.xiaomaoqiu.old.utils.CollectionUtil;
import com.xiaomaoqiu.old.utils.JsonToObject;
import com.xiaomaoqiu.old.utils.SpUtil;


import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/1/15.
 */
@SuppressLint("WrongConstant")
public class PetTrancer {

    public static final String LAST_START_WALK_TIME="last_walk_pet_time";

    public static final long serviceId=132924;//轨迹服务的ID
    public static final int GATHER_INTERVAL=10;//轨迹采集周期
    public static final int PACK_INTERVAL=60;//轨迹打包上传周期
    public static final int PROTOCOL_TYPE=1;//http协议类型

    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private static final int UPLOAD_LOCATION = 2;


    private static PetTrancer ourInstance = new PetTrancer();

    public static PetTrancer getInstance() {
        return ourInstance;
    }

    private PetTrancer() {
    }

    private boolean isInited=false;
    private Trace trace;
    private LBSTraceClient client;
    private String entityName="";//设备名称
    private int traceType= UPLOAD_LOCATION;
    private OnTrackListener trackListener;//百度鹰眼轨迹
    private Subscription trackTimerSubscription;//定时监控
    private WeakReference<onStartTracerListener> startTracerListener;
    private WeakReference<onStopTracerListener> stopTracerListener;
    private WeakReference<onTracingListener> tracingListener;

    public void init(Context context){
        PetInfo petInfo= UserMgr.INSTANCE.getPetInfo();
        if(null == petInfo){
            isInited=false;
            showToast(context,"未查到宠物信息，请稍后重试！");
            return;
        }
        DeviceInfo deviceInfo=petInfo.getDevInfo();
        if(null == deviceInfo || TextUtils.isEmpty(deviceInfo.getImei())){
            isInited=false;
            showToast(context,"未绑定追踪器，请绑定后再使用!");
            return;
        }
        entityName=deviceInfo.getImei();
        initInner(context);
    }

    public void initInner(Context context){
        trace=new Trace(context.getApplicationContext(),serviceId,entityName,traceType);
        client=new LBSTraceClient(context.getApplicationContext());
        client.setInterval(GATHER_INTERVAL,PACK_INTERVAL);
        //设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);
        client.setProtocolType(PROTOCOL_TYPE);
        initListener();
        isInited=true;
    }
    private void initListener(){
        trackListener=new OnTrackListener() {
            @Override
            public void onRequestFailedCallback(String s) {

            }

            @Override
            public void onQueryHistoryTrackCallback(String s) {
                super.onQueryHistoryTrackCallback(s);
                parseHistorytraceData(s);
            }
        };
        client.setOnStartTraceListener(new OnStartTraceListener() {
            @Override
            public void onTraceCallback(int i, String s) {
                if(null == startTracerListener || null == startTracerListener.get()){
                    return;
                }
                switch (i){
                    case 0:
                    case 10006:
                        startTracerListener.get().onSuccessStartTrace();
                        break;
                    case 10000:
                    case 10001:
                    case 10002:
                        startTracerListener.get().onFailStartTrace("定位模式开启失败，请稍后重试！");
                        break;
                    case 10003:
                    case 10004:
                        startTracerListener.get().onFailStartTrace("网络可能有些问题，请检查后重试！");
                        break;
                }

            }

            @Override
            public void onTracePushCallback(byte b, String s) {

            }
        });
    }


    /**
     * 开始轨迹记录
     */
    public void setStartTracerListener(onStartTracerListener startTracerListener){
        this.startTracerListener=new WeakReference<onStartTracerListener>(startTracerListener);
    }

    public void setTracingListener(onTracingListener listener){
        this.tracingListener=new WeakReference<onTracingListener>(listener);
    }

    private int startTime = 0;
    public void startTraceWithTimer(){
        startTime= (int) (System.currentTimeMillis()/1000);
        SpUtil.setIntConfig(LAST_START_WALK_TIME, startTime);
        startTraceWithTimer(PACK_INTERVAL);
    }

    public void startTraceWithTimer(int delay){
        if(! isInited){
            return;
        }
        startTrace();
        if(null == trackTimerSubscription || trackTimerSubscription.isUnsubscribed()){
            trackTimerSubscription= Observable.interval(delay,PACK_INTERVAL, TimeUnit.SECONDS)
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Long aLong) {
                            int curTime= (int) (System.currentTimeMillis()/1000);
                            queryHistoryTrack(startTime, curTime);
                        }
                    });
        }
    }

    /**
     * 在ondesrty的时候一定要调
     */
    public void stopTranceTimer(){
        if(null == trackTimerSubscription || trackTimerSubscription.isUnsubscribed()){
            return;
        }
        trackTimerSubscription.unsubscribe();
    }

    /**
     * 停止带定时器的轨迹查询
     */
    public void stopTimerTracer(){
        stopTrace();
        stopTranceTimer();
    }

    public void startTrace(){
        if(!isInited){
            return;
        }
        client.startTrace(trace);
    }

    /**
     * 停止轨迹记录
     */
    public void setStopTracerListener(onStopTracerListener listener){
        this.stopTracerListener=new WeakReference<onStopTracerListener>(listener);
    }
    public void stopTrace(){
        if(!isInited){
            return;
        }
        client.stopTrace(trace, new OnStopTraceListener() {
            @Override
            public void onStopTraceSuccess() {
                if(null != stopTracerListener && null != startTracerListener.get()){
                    stopTracerListener.get().onSuccessStopTracer();
                }
            }

            @Override
            public void onStopTraceFailed(int i, String s) {
                if(null != stopTracerListener && null != startTracerListener.get()){
                    stopTracerListener.get().onFailStopTracer(s);
                }
            }
        });
    }

    /**
     * 查询历史轨迹接口
     * @param startTime
     * @param endTime
     */
    public void queryHistoryTrack(int startTime,int endTime){
        if(!isInited){
            return;
        }
        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;
        // 是否纠偏
        int isProcessed = 1;//1设置需要纠偏，为0则返回原始轨迹
        // 纠偏选项
        String processOption = "need_denoise=1,need_vacuate=1,need_mapmatch=1,transport_mode=3";//当前业务需求绑路,出行方式是步行
        client.queryHistoryTrack(serviceId, entityName, simpleReturn,isProcessed,processOption, startTime, endTime,pageSize,pageIndex,trackListener);

    }

    /**
     * 退出重新进入系统时启动
     */
    public void queryHistoryTrackByRecord(){
        startTime=SpUtil.getIntConfig(LAST_START_WALK_TIME, (int) (System.currentTimeMillis()/1000));
        //queryHistoryTrack(startTime,0);
        startTraceWithTimer(0);
    }

    /**
     * 解析历史数据
     * @param data
     */
    private void parseHistorytraceData(String data){
        if(null == tracingListener || null == tracingListener.get()){
            return;
        }
        HistoryTrackData historyTrackData= JsonToObject.toObject(data,HistoryTrackData.class);
        if(null == historyTrackData || historyTrackData.getStatus() != 0 ){
            return;
        }
        List<LatLng> points=historyTrackData.getListPoints();
        if(CollectionUtil.size(points) == 0 || null == tracingListener){
            return;
        }
        tracingListener.get().onGetCurTrancePos(points);

    }





    private void showToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

}
