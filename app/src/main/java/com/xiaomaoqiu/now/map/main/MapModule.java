package com.xiaomaoqiu.now.map.main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.xiaomaoqiu.now.map.tracer.PetTrancer;
import com.xiaomaoqiu.now.map.tracer.listeners.onStartTracerListener;
import com.xiaomaoqiu.now.map.tracer.listeners.onStopTracerListener;
import com.xiaomaoqiu.now.map.tracer.listeners.onTracingListener;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.presenter.addressParseListener;
import com.xiaomaoqiu.old.utils.CollectionUtil;
import com.xiaomaoqiu.pet.R;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/1/13.
 */
@SuppressLint("WrongConstant")
public class MapModule implements BDLocationListener, onTracingListener, onStartTracerListener, onStopTracerListener {
    private static final String TAG = "MapModule";
    public static final int MODE_NORMAL = 0;//定位模式
    public static final int MODE_FINDING_PET = 1;//找狗模式
    public static final int MODE_FINDED_PET = 2;//找到狗
    public static final int MODE_WALK_PET = 3;//遛狗模式
    public static final int MODE_WALKING_PET = 4;//正在遛狗
    public static final int MODE_WALKPET_END = 5;//结束遛狗

    private static final int FIND_PET_LOC = 0;//找狗模式下定位成功
    private static final int FIND_PET_SERVICE = 1;//从服务器获取宠物位置成功

    private int mMode = MODE_NORMAL;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private Marker mPetMarker, mPhoneMarker;//狗狗位置和手机位置
    private Marker mWalkStartMarker, mWalkStopMarker;//遛狗的起点和终点
    private Polyline mWalkPolyline;//遛狗路线
    private Polyline mFindPolyline;//找狗连线
    private View mPetmarkerView;

    private int mWalkModeFlag = 0;//遛狗模式标识

    private int mFindPetFlag = 0;//找狗标识，位运算判断是否已经获取到狗的位置并且定位成功

    public MapModule(MapView mapView) {
        if (null == mapView) {
            return;
        }
        this.mapView = mapView;
        initMap();
    }

    private void initMap() {
        mapView.showZoomControls(false);//不显示放大缩小控件
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//只显示普通地图就好
        initLocation();
        PetTrancer.getInstance().setStartTracerListener(this);
        PetTrancer.getInstance().setTracingListener(this);
        PetTrancer.getInstance().setStopTracerListener(this);
    }


    /**
     * 初始化定位
     */
    public void initLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocationClient = new LocationClient(mapView.getContext().getApplicationContext());
        mLocationClient.registerLocationListener(this);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setAddrType("all");// 返回的定位结果包含地址信息,(注意如果想获取关于地址的信息，这里必须进行设置)
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        // 设置定位方式的优先级。
        // 当gps可用，而且获取了定位结果时，不再发起网络请求，直接返回给用户坐标。这个选项适合希望得到准确坐标位置的用户。如果gps不可用，再发起网络请求，进行定位。
        option.setPriority(LocationClientOption.GpsFirst);
        // option.setPriority(LocationClientOption.NetWorkFirst);
        mLocationClient.setLocOption(option);
    }


    public void setMapMode(int mode) {
        this.mMode = mode;
    }


    public int getMapMode() {
        return mMode;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.i(TAG, "locTime:" + SystemClock.uptimeMillis());
        if (!isGpsSuccess(bdLocation.getLocType())) {
            //TODO 定位失败弹窗提示
            return;
        }

        //todo 这里出问题了
        if (MODE_NORMAL == mMode || MODE_FINDED_PET == mMode || MODE_WALKING_PET == mMode) {
            setPhonePos(bdLocation);
        } else if (MODE_FINDING_PET == mMode) {
            //TODO 找狗
            setPhonePosWithFindMode(bdLocation);
        } else if (MODE_WALK_PET == mMode) {
            //TODO 遛狗
            showStartMarker(bdLocation);
        } else if (MODE_WALKPET_END == mMode) {
            showStopMarker(bdLocation);
        }
    }

    /**
     * 判断定位是否成功
     *
     * @param type
     * @return
     */
    private boolean isGpsSuccess(int type) {
        switch (type) {
            case BDLocation.TypeGpsLocation://Gps定位成功
            case BDLocation.TypeNetWorkLocation://网络定位成功
            case BDLocation.TypeOffLineLocation://离线定位成功
                return true;
            default:
                return false;
        }
    }


    /************
     *
     *
     *定位模式
     */

    /**
     * 设置手机定位为地图中心
     */
    public void setMapcenterWithPhone() {
        if (null == mPhoneMarker) {
            initPhoneMarker();
        }
        startLocListener(1000);
    }

    /**
     * 绘制手机位置
     *
     * @param location
     */
    private void setPhonePos(BDLocation location) {
        LatLng postion = new LatLng(location.getLatitude(), location.getLongitude());
        float f = mBaiduMap.getMaxZoomLevel();//19.0

//float m = mBaiduMap.getMinZoomLevel();//3.0

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(postion, f-2);

        mBaiduMap.animateMapStatus(u);


        if (MODE_FINDING_PET != mMode) {
            setCenter(postion, 300);
        }
        mPhoneMarker.setPosition(postion);
//        if (MODE_FINDED_PET >= mMode) {
        mPhoneMarker.setVisible(true);
//        } else {
//            mPhoneMarker.setVisible(false);
//        }
        stopLocListener();
    }

    /**
     * 初始化手机位置地图标识
     */
    private void initPhoneMarker() {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_location_phone);
        OverlayOptions options = new MarkerOptions()
                .icon(bitmapDescriptor)
                .draggable(false)
                .position(new LatLng(0, 0))
                .visible(false);
        mBaiduMap.addOverlay(options);
        Log.e("longtianlove","图片调用");
        mPhoneMarker = (Marker) (mBaiduMap.addOverlay(options));
    }

    /**
     * 宠物位置标识的View
     *
     * @param view
     */
    public void setPetMarkerView(View view) {
        if (null == view) {
            return;
        }
        mPetmarkerView = view;
    }

    /**
     * 设置宠物位置为地图中心
     */
    public void setMapcenterWithPet(double latitude, double longitude) {
        if (null == mPetmarkerView) {
            return;
        }
        if (null == mPetMarker) {
            initPetMarker();
        }
        LatLng sourceLatLng=new LatLng(latitude,longitude);

        LatLng desLatLng=converterLatLng(sourceLatLng);



        mPetMarker.setPosition(desLatLng);
        if(MODE_FINDED_PET >= mMode) {
            mPetMarker.setVisible(true);
        }else{
            mPetMarker.setVisible(false);
        }
        MapLocationParser.queryLocationDesc(desLatLng,addressListener);
        if(MODE_FINDING_PET ==  mMode){
            setPetPosWithFindMode();
        }
        setCenter(desLatLng,300);

    }

    /**
     * 初始化宠物位置地图标识
     */
    private void initPetMarker() {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(mPetmarkerView);
        OverlayOptions options = new MarkerOptions()
                .icon(bitmapDescriptor)
                .draggable(false)
                .position(new LatLng(0, 0))
                .visible(false);
        mPetMarker = (Marker) (mBaiduMap.addOverlay(options));
    }


    /************
     *
     *找狗模式
     *
     */

    /**
     * 开始找宠物
     */
    public void startFindPet() {
        if (MODE_WALK_PET == mMode || MODE_WALKING_PET == mMode || MODE_WALKPET_END == mMode) {
            stopWalkPetWithoutLoc();
        }
        setMapMode(MODE_FINDING_PET);
        startLocListener(1000);
    }

    /**
     * 找狗模式下绘制手机位置
     */
    private void setPhonePosWithFindMode(BDLocation location) {
        setPhonePos(location);
        mFindPetFlag = mFindPetFlag | (1 << FIND_PET_LOC);
        onFindPet();
    }

    /**
     * 从服务器获取到狗狗位置
     */
    private void setPetPosWithFindMode() {
        mFindPetFlag = mFindPetFlag | (1 << FIND_PET_SERVICE);
//        showToast("狗狗找到了！");
        onFindPet();
    }

    private void showToast(String msg) {
        Toast.makeText(mapView.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 找到宠物
     */
    private void onFindPet() {
        if (3 != mFindPetFlag) {
            //二进制位011
            return;
        }
        ArrayList<LatLng> points = new ArrayList<>();
        points.add(mPhoneMarker.getPosition());
        points.add(mPetMarker.getPosition());
        PolylineOptions options = new PolylineOptions()
                .points(points)
                .color(Color.rgb(250, 90, 95))
                .width(7)
                .visible(true);
        mFindPolyline = (Polyline) (mBaiduMap.addOverlay(options));
        mFindPetFlag = 0;
        setMapMode(MODE_FINDED_PET);
    }

    /**
     * 关闭紧急追踪模式
     */
    public void onCloseFindPetMode() {
        if (null == mFindPolyline) {
            return;
        }
        mFindPolyline.remove();
        setMapMode(MODE_NORMAL);
    }


    /**********************************
     * ********************************
     * 遛狗模式
     *
     */

    /**
     * 再次进入系统还原
     */
    private boolean isResetWalkMode = false;//是否是重新进入遛狗模式

    public void resetToWalkMode() {
        if (MODE_WALKPET_END == mMode || MODE_WALK_PET == mMode || MODE_WALKING_PET == mMode) {
            return;
        }
        isResetWalkMode = true;
        setMapMode(MODE_WALK_PET);
        PetTrancer.getInstance().init(mapView.getContext());
        PetTrancer.getInstance().queryHistoryTrackByRecord();
    }

    /**
     * 开启遛狗模式
     */
    public boolean startWalkPet() {
        if (MODE_FINDING_PET == mMode || MODE_FINDED_PET == mMode) {
            showToast("当前为紧急搜寻模式！");
            return false;
        }
        if (null != mPetMarker) {
            mPetMarker.setVisible(false);
        }
        if (null != mPhoneMarker) {
            mPhoneMarker.setVisible(false);
        }
        if (null != mWalkStopMarker) {
            mWalkStopMarker.setVisible(false);
        }
        setMapMode(MODE_WALK_PET);
        startLocListener(1000);
        PetTrancer.getInstance().init(mapView.getContext());
        PetTrancer.getInstance().startTraceWithTimer();
        return true;
    }

    /**
     * 停止遛狗模式
     */
    public void stopWalkPet() {
        PetTrancer.getInstance().stopTimerTracer();
        setMapMode(MODE_WALKPET_END);
        startLocListener(1000);
    }

    /**
     * 停止遛狗模式直接进入找狗模式
     */
    public void stopWalkPetWithoutLoc() {
        if (null != mWalkStartMarker) {
            mWalkStartMarker.setVisible(false);
        }
        if (null != mWalkStopMarker) {
            mWalkStopMarker.setVisible(false);
        }
        mWalkModeFlag = 1;
        PetTrancer.getInstance().stopTimerTracer();
    }

    /**
     * 添加遛狗点
     *
     * @param location
     */
    private void showStartMarker(BDLocation location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        showStartMarker(position);
    }

    private void showStartMarker(LatLng position) {
        if (null == mWalkStartMarker) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_location_wal_start);
            MarkerOptions options = new MarkerOptions()
                    .icon(descriptor)
                    .visible(false)
                    .position(position);
            mWalkStartMarker = (Marker) (mBaiduMap.addOverlay(options));
        } else {
            mWalkStartMarker.setPosition(position);
        }
        mWalkStartMarker.setVisible(false);
        stopLocListener();
        setMapMode(MODE_WALKING_PET);
        mWalkModeFlag++;
        if (!isResetWalkMode) {
            onLocSuccAndTraceSuc(true);
        } else {
            isResetWalkMode = false;
        }
    }

    private void showStopMarker(BDLocation location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        ;
        if (null == mWalkStopMarker) {
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_location_wal_end);
            MarkerOptions options = new MarkerOptions()
                    .icon(descriptor)
                    .visible(false)
                    .position(pos);
            mWalkStopMarker = (Marker) (mBaiduMap.addOverlay(options));
        } else {
            mWalkStopMarker.setPosition(pos);
        }
        mWalkStopMarker.setVisible(false);
        stopLocListener();
        mWalkModeFlag++;
        onLocSuccAndTraceSuc(false);
    }


    /**
     * 按动画移动到中心点
     *
     * @param centerPoint
     */
    private void setCenter(LatLng centerPoint, int delayMills) {
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(centerPoint);
        mBaiduMap.animateMapStatus(mapStatusUpdate, delayMills);
    }

    /**
     * 开始位置监听
     */
    private void startLocListener(int scan) {
        if (null != mLocationClient && !mLocationClient.isStarted()) {
            mLocationClient.getLocOption().setScanSpan(scan);
            mLocationClient.start();
        }
    }

    /**
     * 停止位置监听
     */
    private void stopLocListener() {
        if (null != mLocationClient && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    private addressParseListener addressListener;

    public void setAddressParseListener(addressParseListener listener) {
        this.addressListener = listener;
    }

    /**
     * MapView生命周期
     */
    public void onDestroy() {
        if (null != mapView) {
            mapView.onDestroy();
        }
        if (null != mLocationClient) {
            mLocationClient.unRegisterLocationListener(this);
        }
        mapCallBack = null;
        PetTrancer.getInstance().stopTranceTimer();
    }

    public void onResume() {
        if (null != mapView) {
            mapView.onResume();
        }
    }

    public void onPause() {
        if (null != mapView) {
            mapView.onResume();
        }
        stopLocListener();
    }

    private IMapCallBack mapCallBack;

    public void setMapCallBack(IMapCallBack callBack) {
        this.mapCallBack = callBack;
    }

    @Override
    public void onSuccessStartTrace() {
        //开启遛狗模式成功
        mWalkModeFlag++;
        onLocSuccAndTraceSuc(true);
    }

    @Override
    public void onFailStartTrace(String msg) {
        showToast(msg);
    }

    @Override
    public void onGetCurTrancePos(List<LatLng> points) {
        if (CollectionUtil.size(points) <= 1) {
            return;
        }
        if (isResetWalkMode) {
            showStartMarker(points.get(0));
        }
        if (null != mWalkPolyline) {
            mWalkPolyline.remove();
        }
        PolylineOptions options = new PolylineOptions()
                .color(Color.rgb(245, 180, 180))
                .width(7)
                .points(points)
                .visible(true);
        mWalkPolyline = (Polyline) (mBaiduMap.addOverlay(options));
        LatLng lastPoint = points.get(points.size() - 1);
        setCenter(lastPoint, 100);
    }

    @Override
    public void onSuccessStopTracer() {
        mWalkModeFlag++;
        onLocSuccAndTraceSuc(false);
    }

    @Override
    public void onFailStopTracer(String msg) {
        showToast(msg);
    }

    /**
     * 定位成功并且启动轨迹或者停止轨迹完成
     *
     * @param isStart
     */
    private void onLocSuccAndTraceSuc(boolean isStart) {
        if (mWalkModeFlag < 2) {
            return;
        }
        if (null != mWalkStopMarker && !isStart) {
            mWalkStopMarker.setVisible(true);
        }
        if (null != mWalkStartMarker && isStart) {
            mWalkStartMarker.setVisible(true);
        }
        if (null != mapCallBack) {
            mapCallBack.onTraceSuccess(isStart);
        }
        mWalkModeFlag = 0;
    }






    //  // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
    public LatLng converterLatLng(LatLng sourceLatLng ) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
// sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }
}
