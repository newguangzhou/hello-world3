package com.xiaomaoqiu.pet.ui.mainPages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.HttpCode;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.dataCenter.PetInfo;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.ui.dialog.StartPetFindingDialog;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.FindPetActivity;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter.MapModule;
import com.xiaomaoqiu.pet.utils.HttpUtil;
import com.xiaomaoqiu.pet.widgets.FragmentEx;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PageLocate extends FragmentEx
		implements View.OnClickListener
		, PetInfo.Callback_PetInfo
		, PetInfo.Callback_PetLocating
{
	private MapModule mMapModule;

	// 地图相关
	MapView mMapView;
	BaiduMap mBaiduMap;

	// 定位相关
	private LocationClient mLocationClient;

	BDLocation mLocation;		//手机定位
	boolean isFirstLoc = true;// 是否首次定位
	List<LatLng> points = new ArrayList<LatLng>();
	List<LatLng> points_tem = new ArrayList<LatLng>();

	OverlayOptions options;
	Marker mPetMarker;
	// 定时器相关，定时检查GPS是否开启（这里只须检查mLocationClient是否启动）
	Handler handler = new Handler();
	// 是否停止定位服务
	boolean isStopLocClient = false;

	int mMapType = BaiduMap.MAP_TYPE_NORMAL;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.main_tab_locate, container, false);
		// 测试点，发布前去掉
		// points.add(new LatLng(34.2376, 108.9923));

		// 启动计时器(每3秒检测一次)
		handler.postDelayed(new MyRunable(), 3000);

		// 初始化地图
		mMapView = (MapView) rootView.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(mMapType);

		BitmapDescriptor mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);

		//在地图上增加一个用于显示宠物位置的Marker,坐标任意。
		LatLng pos = new LatLng(0.0,0.0);
		OverlayOptions overlayOptions = new MarkerOptions().position(pos)
				.icon(mIconMaker).zIndex(5);
		mPetMarker= (Marker) (mBaiduMap.addOverlay(overlayOptions));
		mPetMarker.setVisible(false);//先隐藏

		// UI初始化
		rootView.findViewById(R.id.btn_phone_center).setOnClickListener(this);
		rootView.findViewById(R.id.btn_pet_center).setOnClickListener(this);
		rootView.findViewById(R.id.btn_open_light).setOnClickListener(this);
		rootView.findViewById(R.id.btn_find_pet).setOnClickListener(this);
		// 初始化定位信息
		initLocation();

		if(UserMgr.INSTANCE.getPetInfo().getAtHome() == false)
		{//宠物不在家，显示手机定位
			isStopLocClient = false;
			if (!mLocationClient.isStarted()) {
				mLocationClient.start();
			}
		}else if(UserMgr.INSTANCE.getPetInfo().getPetID() != -1)
		{//宠物在家，显示宠物位置
			UserMgr.INSTANCE.queryPetLocation();
		}
        return rootView;
	}


	void setLightStatus(boolean bOpenLight)
	{
		HttpUtil.get2("device.swicth_light", new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			//{"status": 0, }
			Log.v("http", "device.swicth_light:" + response.toString());
			HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
			if (ret == HttpCode.EC_SUCCESS) {
			}
		}

	}, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(), UserMgr.INSTANCE.getPetInfo().getDevInfo().getImei(),bOpenLight?1:0);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.btn_phone_center:
				if(mLocation != null){
					LatLng ll = new LatLng(mLocation.getLatitude(),
							mLocation.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaiduMap.animateMapStatus(u);
				}
				break;
			case R.id.btn_pet_center:
				UserMgr.INSTANCE.queryPetLocation();
				break;
			case R.id.btn_open_light:
				{
					CheckBox btnOpenLight = (CheckBox)v;
					setLightStatus(btnOpenLight.isChecked());
				}
				break;
			/*case R.id.btn_map_mode:
				if(mMapType == BaiduMap.MAP_TYPE_NORMAL)
					mMapType = BaiduMap.MAP_TYPE_SATELLITE;
				else
					mMapType = BaiduMap.MAP_TYPE_NORMAL;
				mBaiduMap.setMapType(mMapType);
				break;*/
			case R.id.btn_find_pet:
				StartPetFindingDialog dialog = new StartPetFindingDialog(getActivity(),
						new StartPetFindingDialog.OnDialogDismiss() {
							@Override
							public void onDismiss(int nID) {
								if(nID == R.id.btn_ok) {
									Intent intent = new Intent(getActivity(), FindPetActivity.class);
									startActivity(intent);
								}
							}
						}
						, R.style.MyDialogStyle);
				dialog.show();
				break;
		}

	}

	@Override
	public void onPetInfoChanged(PetInfo petInfo, int nFieldMask) {
		if((nFieldMask & petInfo.FieldMask_AtHome) != 0 )
		{
			if(petInfo.getAtHome())
			{//回家,状态栏显示宠物位置
				isStopLocClient = true;
				if (mLocationClient.isStarted()) {
					mLocationClient.stop();
					drawEnd(points);
				}
			}else {//出去玩
				isStopLocClient = false;
				if (!mLocationClient.isStarted()) {
					mLocationClient.start();
				}
				TextView tvTip = (TextView) getView().findViewById(R.id.tv_location);
				tvTip.setText(getString(R.string.pet_playing));
			}
		}
	}

	@Override
	public void onLocateResult(boolean bFound, double latitude, double longitude) {
		if(bFound) {
			LatLng ll = new LatLng(latitude,longitude);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mPetMarker.setPosition(ll);
			mPetMarker.setVisible(true);
			mBaiduMap.animateMapStatus(u);
		}else
		{
			Toast.makeText(getActivity(), R.string.pet_locate_failed,Toast.LENGTH_LONG).show();
		}
	}

	class MyRunable implements Runnable {

		public void run() {
			if (!mLocationClient.isStarted()) {
				mLocationClient.start();
			}
			if (!isStopLocClient) {
				handler.postDelayed(this, 3000);
			}

		}
	}

	/**
	 * 根据数据绘制轨迹
	 *
	 * @param points2
	 */
	protected void drawMyRoute(List<LatLng> points2) {
		OverlayOptions options = new PolylineOptions().color(0xAAFF0000)
				.width(10).points(points2);
		mBaiduMap.addOverlay(options);
	}

	// 初始化定位
	public void initLocation() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(new MyLocationListenner());

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

	/**
	 * 定位SDK监听函数
	 */
	class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			mLocation = location;
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			MyLocationData locData = new MyLocationData.Builder().accuracy(0)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng point = new LatLng(location.getLatitude(),
					location.getLongitude());

			points.add(point);
			if (isFirstLoc) {
				//points.add(point);
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}

			if (points.size() == 5) {

				// 这里绘制起点
				drawStart(points);
			} else if (points.size() > 7) {
				points_tem = points.subList(points.size() - 4, points.size());
				options = new PolylineOptions().color(0xAAFF0000).width(6)
						.points(points_tem);
				mBaiduMap.addOverlay(options);
			}

/*
			TextView tvLoc = (TextView) PageLocate.this.getView().findViewById(R.id.tv_location);
			if(location.getAddrStr()!=null)
				tvLoc.setText(location.getAddrStr());
			else
				tvLoc.setText(R.string.get_loc_string_null);
*/
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}

	}

	@Override
    public void onStop() {
		isStopLocClient = true;
		mLocationClient.stop();

		super.onStop();
	}

	/**
	 * 绘制起点，取前n个点坐标的平均值绘制起点
	 *
	 * @param points2
	 */
	public void drawStart(List<LatLng> points2) {
		double myLat = 0.0;
		double myLng = 0.0;

		for (LatLng ll : points2) {
			myLat += ll.latitude;
			myLng += ll.longitude;
		}
		LatLng avePoint = new LatLng(myLat / points2.size(), myLng
				/ points2.size());
		points.add(avePoint);
		options = new DotOptions().center(avePoint).color(0xAA00ff00)
				.radius(15);
		mBaiduMap.addOverlay(options);

	}

	/**
	 * 绘制终点。
	 *
	 * @param points2
	 */
	protected void drawEnd(List<LatLng> points2) {
		double myLat = 0.0;
		double myLng = 0.0;
		if (points2.size() > 5) {// points肯定大于5，其实不用判断
			for (int i = points2.size() - 5; i < points2.size(); i++) {
				LatLng ll = points2.get(i);
				myLat += ll.latitude;
				myLng += ll.longitude;

			}
			LatLng avePoint = new LatLng(myLat / 5, myLng / 5);
			options = new DotOptions().center(avePoint).color(0xAAff00ff)
					.radius(15);
			mBaiduMap.addOverlay(options);
		}

	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
    public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
    public void onDestroy() {
		// 退出时销毁定位
		mLocationClient.stop();
		isStopLocClient = true;
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
