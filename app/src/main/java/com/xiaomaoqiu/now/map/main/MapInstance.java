package com.xiaomaoqiu.now.map.main;

import android.view.View;

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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.presenter.addressParseListener;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.view.MapPetAvaterView;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.bean.PetInfo;
import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/5/8.
 */

public class MapInstance implements BDLocationListener {

    public static MapInstance instance;

    private MapInstance() {

    }

    public static MapInstance getInstance() {
        if (instance == null) {
            synchronized (MapInstance.class) {
                if (instance == null) {
                    instance = new MapInstance();
                    GPS_OPEN=SPUtil.getGPS_OPEN();
                    mLatitude=Double.valueOf(SPUtil.getLatitude());
                    mLongitude=Double.valueOf(SPUtil.getLongitude());
                }
            }
        }
        return instance;
    }


    public static double mLatitude;
    public static double mLongitude;

//    public static String mode_map;//地图模式
    public static boolean GPS_OPEN;//GPS是否开启

    private MapView mapView;
    private MapPetAvaterView mapPetAvaterView;
    private Marker mPetMarker, mPhoneMarker;//狗狗位置和手机位置
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;




    public void init(MapView mapView) {
        this.mapView = mapView;
        initMap();
        initPhoneMarker();
        initPetMarker();
        setPhonePos();
    }
    public void setPetAvaterView(MapPetAvaterView mapPetAvaterView){
        this.mapPetAvaterView =mapPetAvaterView;
    }
    private void initMap() {
        mapView.showZoomControls(false);//不显示放大缩小控件
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//只显示普通地图就好
        mBaiduMap.setBuildingsEnabled(true);//设置是否允许楼块效果
        mBaiduMap.setIndoorEnable(true);//设置是否显示室内图, 默认室内图不显示
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        initLocation();
    }

    /**
     * 初始化定位
     */
    public void initLocation() {

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
        mLocationClient.setLocOption(option);
    }


    /**
     * 初始化宠物位置地图标识
     */
    private void initPetMarker() {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(mapPetAvaterView);
        OverlayOptions options = new MarkerOptions()
                .icon(bitmapDescriptor)
                .draggable(true)
                .position(new LatLng(mLatitude, mLongitude))
                .visible(true);
        mPetMarker = (Marker) (mBaiduMap.addOverlay(options));
    }


    /**
     * 开始定位
     */
    public void startLoc() {
        startLocListener(1000);
    }

    /**
     * 初始化手机位置地图标识
     */
    private void initPhoneMarker() {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_location_phone);
        OverlayOptions options = new MarkerOptions()
                .icon(bitmapDescriptor)
                .draggable(true)
                .position(new LatLng(mLatitude, mLongitude))
                .visible(true);
        mBaiduMap.addOverlay(options);
        mPhoneMarker = (Marker) (mBaiduMap.addOverlay(options));
    }


    /**
     * 设置宠物位置为地图中心
     */
    public void setPetLocation(double latitude, double longitude) {
        LatLng sourceLatLng=new LatLng(latitude,longitude);

        LatLng desLatLng=converterLatLng(sourceLatLng);


        mPetMarker.setPosition(desLatLng);
        MapLocationParser.queryLocationDesc(desLatLng,addressListener);
        setCenter(desLatLng,300);

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


    private addressParseListener addressListener;

    public void setAddressParseListener(addressParseListener listener) {
        this.addressListener = listener;
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
//        mode_map=Mode_Map.GPS_OPEN;
        setGPSState(true);
        startLocListener(1000);
    }

    /**
     * 绘制手机位置
     *
     */
    private void setPhonePos() {
        LatLng postion = new LatLng(mLatitude, mLongitude);
        float f = mBaiduMap.getMaxZoomLevel();//19.0

//float m = mBaiduMap.getMinZoomLevel();//3.0

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(postion, f-2);

        mBaiduMap.animateMapStatus(u);


        if (!GPS_OPEN) {
            setCenter(postion, 300);
        }
        mPhoneMarker.setPosition(postion);
        stopLocListener();
    }



    /**
     * 停止位置监听
     */
    private void stopLocListener() {
        if (null != mLocationClient && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
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

//    public void setMapMode(String mode_map) {
//        this.mode_map = mode_map;
//        SPUtil.putMode_Map(mode_map);
//    }

    public void setGPSState(boolean flag){
        GPS_OPEN=flag;
        SPUtil.putGPS_OPEN(GPS_OPEN);
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        mLatitude=bdLocation.getLatitude();
        mLongitude=bdLocation.getLongitude();
        setPhonePos();
        if(!PetInfoInstance.getInstance().getAtHome()){
            //todo 如果不在家，就设置为另一个头像

        }
//        switch (mode_map){
//            case Mode_Map.Normal://定位模式
//                setPhonePos();
//                break;
//            case Mode_Map.GPS_OPEN://找狗模式
//                setPhonePos();
//                break;
//            case Mode_Map.Work_The_Dog://遛狗模式
//                setPhonePos();
//                break;
//        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

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
