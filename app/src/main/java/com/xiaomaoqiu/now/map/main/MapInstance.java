package com.xiaomaoqiu.now.map.main;

import android.graphics.Color;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.view.MapPhoneAvaterView;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.presenter.addressParseListener;
import com.xiaomaoqiu.now.view.MapPetAvaterView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

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
                    GPS_OPEN = SPUtil.getGPS_OPEN();
                    phoneLatitude = Double.valueOf(SPUtil.getPhoneLatitude());
                    phoneLongitude = Double.valueOf(SPUtil.getPhoneLongitude());
                    petLatitude = Double.valueOf(SPUtil.getPetLatitude());
                    petLongitude = Double.valueOf(SPUtil.getPetLongtitude());
                }
            }
        }
        return instance;
    }


    public static double phoneLatitude;
    public static double phoneLongitude;
    public static double petLatitude;
    public static double petLongitude;

    //    public static String mode_map;//地图模式
    public static boolean GPS_OPEN;//GPS是否开启

    private MapView mapView;
    private MapPetAvaterView mapPetAvaterView;
    private Marker mPetMarker, mPhoneMarker;//狗狗位置和手机位置
    private Polyline mFindPolyline;//找狗连线

    private BaiduMap mBaiduMap;


    private LocationClient mLocationClient;
    CircleOptions mCircleOptions;


    BitmapDescriptor petbitmapDescriptor;
    BitmapDescriptor phonebitmapDescriptor;

    public void init(MapView mapView) {
        this.mapView = mapView;
        initMap();
        petbitmapDescriptor = BitmapDescriptorFactory.fromView(mapPetAvaterView);
        phonebitmapDescriptor = BitmapDescriptorFactory.fromView(new MapPhoneAvaterView(PetAppLike.mcontext));
        setPhonePos();
    }

    public void setPetAvaterView(MapPetAvaterView mapPetAvaterView) {
        this.mapPetAvaterView = mapPetAvaterView;
    }

    private void initMap() {
        mapView.showZoomControls(false);//不显示放大缩小控件
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//只显示普通地图就好
        mBaiduMap.setBuildingsEnabled(true);//设置是否允许楼块效果

//        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
//            @Override
//            public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
//                if (b) {
//                    // 进入室内图
//                    // 通过获取回调参数 mapBaseIndoorMapInfo 来获取室内图信息，包含楼层信息，室内ID等
//                } else {
//                    // 移除室内图
//                }
//            }
//        });

//        mBaiduMap.setIndoorEnable(true);//设置是否显示室内图, 默认室内图不显示
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
        OverlayOptions options = new MarkerOptions()
                .icon(petbitmapDescriptor)
                .draggable(true)
                .position(new LatLng(petLatitude, petLongitude))
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
        OverlayOptions options = new MarkerOptions()
                .icon(phonebitmapDescriptor)
                .draggable(true)
                .position(new LatLng(phoneLatitude, phoneLongitude))
                .visible(true);
        mPhoneMarker = (Marker) (mBaiduMap.addOverlay(options));

    }

    LatLng desLatLng;
    double radius = 20;

    /**
     * 设置宠物位置为地图中心
     */
    public void setPetLocation(double latitude, double longitude, double radius) {


        LatLng sourceLatLng = new LatLng(latitude, longitude);

        desLatLng = converterLatLng(sourceLatLng);
        petLatitude = desLatLng.latitude;
        petLongitude = desLatLng.longitude;
        SPUtil.putPetLatitude(petLatitude + "");
        SPUtil.putPhoneLongitude(petLongitude + "");

        mPetMarker.setPosition(desLatLng);
        MapLocationParser.queryLocationDesc(desLatLng, addressListener);
        setCenter(desLatLng, 300);
        this.radius = radius;
        refreshMap();


    }

    /**
     * 刷新地图
     */
    public void refreshMap() {
        mBaiduMap.clear();
        initPhoneMarker();
        initPetMarker();
        if (desLatLng == null) {
            desLatLng = new LatLng(petLatitude, petLongitude);
        }
        mCircleOptions = new CircleOptions()
                .center(desLatLng) // 圆心坐标
                .radius((int) radius) // 半径 单位 米
                .visible(true)
//                .stroke(new Stroke(2, Color.parseColor("#ffffff"))) // 设置边框 Stroke 参数 宽度单位像素默认5px 颜色
                .fillColor(Color.parseColor("#1B2e68AA")); // 设置圆的填充颜色
        mBaiduMap.addOverlay(mCircleOptions);
        if (GPS_OPEN) {
            ArrayList<LatLng> points = new ArrayList<>();
            points.add(mPhoneMarker.getPosition());
            points.add(mPetMarker.getPosition());
            PolylineOptions options = new PolylineOptions()
                    .points(points)
                    .color(Color.rgb(250, 90, 95))
                    .width(7)
                    .visible(true);
            mFindPolyline = (Polyline) (mBaiduMap.addOverlay(options));
        } else {
            if (mFindPolyline != null) {
                mFindPolyline.remove();
            }
        }

        calculateDistance();
    }

    /**
     * 开始位置监听
     */
    public void startLocListener(int scan) {
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
        setGPSState(true);
        startLocListener(1000);
    }

    /**
     * 绘制手机位置
     */
    private void setPhonePos() {
        LatLng postion = new LatLng(phoneLatitude, phoneLongitude);
        float f = mBaiduMap.getMaxZoomLevel();//19.0
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(postion, f - 2);
        mBaiduMap.animateMapStatus(u);
        if (!GPS_OPEN) {
            setCenter(postion, 300);
        }
        refreshMap();
        mPhoneMarker.setPosition(postion);
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


    public void setGPSState(boolean flag) {
        GPS_OPEN = flag;
        SPUtil.putGPS_OPEN(GPS_OPEN);

        if (!GPS_OPEN) {
            if (mFindPolyline != null) {
                mFindPolyline.remove();
            }
        }


    }


    //是否以手机为中心
    public static boolean showPhoneCenter=false;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        phoneLatitude = bdLocation.getLatitude();
        phoneLongitude = bdLocation.getLongitude();
        SPUtil.putPhoneLatitude(phoneLatitude + "");
        SPUtil.putPhoneLongitude(phoneLongitude + "");
        if(showPhoneCenter) {
            setPhonePos();
        }
        if (!PetInfoInstance.getInstance().getAtHome()) {
            //todo 如果不在家，就设置为另一个头像

        }
        stopLocListener();
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


    //将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
    public LatLng converterLatLng(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    //计算距离,根据用户指定的两个坐标点，计算这两个点的实际地理距离
    public void calculateDistance() {
        LatLng phoneLatLng = new LatLng(phoneLatitude, phoneLongitude);
        LatLng petLatLng = new LatLng(petLatitude, petLongitude);
        //计算p1、p2两点之间的直线距离，单位：米
        if(GPS_OPEN&&DistanceUtil.getDistance(phoneLatLng, petLatLng)<1000){
            EventBus.getDefault().post(new EventManage.distanceClose());
        }
    }

}
