package com.xiaomaoqiu.old.ui.mainPages.pageLocate;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.BitmapDescriptor;
//import com.baidu.mapapi.map.BitmapDescriptorFactory;
//import com.baidu.mapapi.map.MapStatus;
//import com.baidu.mapapi.map.MapStatusUpdate;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.Marker;
//import com.baidu.mapapi.map.MarkerOptions;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.map.OverlayOptions;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.geocode.GeoCodeResult;
//import com.baidu.mapapi.search.geocode.GeoCoder;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.old.dataCenter.LoginMgr;
import com.xiaomaoqiu.old.dataCenter.PetInfo;
import com.xiaomaoqiu.old.dataCenter.UserMgr;
import com.xiaomaoqiu.old.utils.HttpUtil;
import com.xiaomaoqiu.old.widgets.ActivityEx;


import org.apache.http.Header;
import org.json.JSONObject;

public class FindPetActivity extends ActivityEx implements View.OnClickListener{
//    // 地图相关
//    MapView mMapView;
//    BaiduMap mBaiduMap;
//    LocationClient mLocationClient;
//
//    LatLng  mPhoneLocation;
//    LatLng  mPetLocation;

    boolean mIsPetFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pet);
        initView();
    }

    void initView()
    {
//        // 初始化地图
//        mMapView = (MapView) findViewById(R.id.bmapView);
//        mBaiduMap = mMapView.getMap();
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        findViewById(R.id.btn_pet_center).setOnClickListener(this);
        findViewById(R.id.btn_phone_center).setOnClickListener(this);
//        initLocation();
        //开始找狗
        setPetFindMode(1);
        //启动定时器查询宠物位置
//        mMapView.postDelayed(mPetLocateRunnable,1000);
    }

    public void onClick(View v)
    {
        if(v.getId() == R.id.btn_phone_center)
        {
//            if(mPhoneLocation != null) {
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mPhoneLocation);
//                mBaiduMap.animateMapStatus(u);
//            }else
//            {
//                Toast.makeText(this,"定位中...",Toast.LENGTH_LONG).show();
//            }
        }else if(v.getId() == R.id.btn_pet_center)
        {
//            if(mPetLocation != null) {
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mPetLocation);
//                mBaiduMap.animateMapStatus(u);
//            }else
//            {
//                Toast.makeText(this,"宠物追踪中...",Toast.LENGTH_LONG).show();
//            }
        }
    }

    boolean mIsFirstLocation = true;
//    class MyLocationListener implements BDLocationListener {
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            // map view 销毁后不在处理新接收的位置
//            if (location == null || mMapView == null)
//                return;
//            // 如果不显示定位精度圈，将accuracy赋值为0即可
//            MyLocationData locData = now MyLocationData.Builder().accuracy(0)
//                    .latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
//            mPhoneLocation = now LatLng(location.getLatitude(),
//                    location.getLongitude());
//            if(mIsFirstLocation) {
//                mIsFirstLocation = false;
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mPhoneLocation);
//                mBaiduMap.animateMapStatus(u);
//            }
//        }
//
//        public void onReceivePoi(BDLocation poiLocation) {
//
//        }
//
//    }


//    // 初始化定位
//    public void initLocation() {
//        // 开启定位图层
//        mBaiduMap.setMyLocationEnabled(true);
//        // 定位初始化
//        mLocationClient = now LocationClient(this);
//        mLocationClient.registerLocationListener(now MyLocationListener());
//
//        LocationClientOption option = now LocationClientOption();
//        option.setOpenGps(true);// 打开gps
//        option.setAddrType("all");// 返回的定位结果包含地址信息,(注意如果想获取关于地址的信息，这里必须进行设置)
//        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(1000);
//
//        // 设置定位方式的优先级。
//        // 当gps可用，而且获取了定位结果时，不再发起网络请求，直接返回给用户坐标。这个选项适合希望得到准确坐标位置的用户。如果gps不可用，再发起网络请求，进行定位。
//        option.setPriority(LocationClientOption.GpsFirst);
//        mLocationClient.setLocOption(option);
//        mLocationClient.start();
//    }

    //@param mode: 1:开始找狗 2：找到了
    void setPetFindMode(int mode)
    {
        HttpUtil.get2("pet.find", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //{"status": 0}
                Log.v("http", "pet.find:" + response.toString());
            }

        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(), UserMgr.INSTANCE.getPetInfo().getPetID(),mode);
    }

    Runnable mPetLocateRunnable = new Runnable() {
        @Override
        public void run() {
            UserMgr.INSTANCE.queryPetLocation();//查询狗位置
        }
    };

   //todo  回调逻辑
    public void onLocateResult(boolean bFound, double latitude, double longitude) {
        if(!bFound) return;

//        mMapView.removeCallbacks(mPetLocateRunnable);
//        setPetFindMode(2);
//        mIsPetFound = true;
//
//        LatLng cenpt = now LatLng(latitude, longitude);
//
//        BitmapDescriptor mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
//        OverlayOptions overlayOptions = now MarkerOptions().position(cenpt)
//                .icon(mIconMaker).zIndex(5);
//        Marker mPetMarker= (Marker) (mBaiduMap.addOverlay(overlayOptions));
//
//        //定义地图状态
//        MapStatus mMapStatus = now MapStatus.Builder().target(cenpt).zoom(mBaiduMap.getMapStatus().zoom).build();
//        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//        //改变地图状态
//        mBaiduMap.animateMapStatus(mMapStatusUpdate);
//        queryLocationDesc(cenpt);
//
//        Toast.makeText(this,"宠物找到了",Toast.LENGTH_LONG).show();
    }

//    void queryLocationDesc(LatLng loc)
//    {
//        OnGetGeoCoderResultListener listener = now OnGetGeoCoderResultListener() {
//            // 地理编码查询结果回调函数
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult result) {
//            }
//                // 反地理编码查询结果回调函数
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
//                TextView tvLoc = (TextView) findViewById(R.id.tv_location);
//                tvLoc.setText(result.getAddress());
//            }
//        };
//        GeoCoder geoCoder = GeoCoder.newInstance();
//        // 设置地理编码检索监听者
//        geoCoder.setOnGetGeoCodeResultListener(listener);
//        geoCoder.reverseGeoCode(now ReverseGeoCodeOption().location(loc));
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(!mIsPetFound) return false;
        }

        return super.onKeyDown(keyCode, event);
    }
}
