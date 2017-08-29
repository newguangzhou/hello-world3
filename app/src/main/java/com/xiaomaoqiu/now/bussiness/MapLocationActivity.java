package com.xiaomaoqiu.now.bussiness;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.Device.MeWifiListActivity;
import com.xiaomaoqiu.now.bussiness.adapter.AddressAdapter;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.HomelocationInstance;
import com.xiaomaoqiu.now.map.main.MapLocationParser;
import com.xiaomaoqiu.now.map.main.addressParseListener;
import com.xiaomaoqiu.now.util.MIUIUtils;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.MapPetAtHomeView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

import static com.xiaomaoqiu.now.http.HttpCode.EC_SUCCESS;

/**
 * Created by long on 2017/6/22.
 */

public class MapLocationActivity extends Activity {


    View btn_go_back;
    TextView tv_next;

    EditText et_search;
    View btn_cancel;

    private MapView mMapView;
    private RecyclerView rv_addresslist;
    private View ll_noname_address;
    private TextView tv_location;
    private View iv_selected;
    private BaiduMap mBaiduMap;
    private Projection projection;
    private View btn_phone_center;
    private MapPetAtHomeView iv_location;

    public static double longitude;
    public static double latitude;
    public static double touch_longitude;
    public static double touch_latitude;

    AddressAdapter adapter;
    ArrayList<PoiInfo> poiInfos;


    TranslateAnimation translateAnimation;

    PoiSearch mPoiSearch;
    PoiCitySearchOption citySearchOption;
    PoiNearbySearchOption nearbySearchOption;


    public static boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        isFirst = true;
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_next.setText("保存");
        tv_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(latitude > 0)) {
                    ToastUtil.showTost("请设置常住地位置");
                    return;
                }
                ApiUtils.getApiService().setHomeLocation(UserInstance.getInstance().getUid(),
                        UserInstance.getInstance().getToken(),
                        longitude, latitude
                ).enqueue(new XMQCallback<BaseBean>() {
                    @Override
                    public void onSuccess(Response<BaseBean> response, BaseBean message) {
                        HttpCode ret = HttpCode.valueOf(message.status);
                        if (ret == EC_SUCCESS) {
                            EventManage.notifyPetLocationChange event = new EventManage.notifyPetLocationChange();
                            UserInstance.getInstance().latitude = latitude;
                            UserInstance.getInstance().longitude = longitude;
                            SPUtil.putHOME_LATITUDE(latitude + "");
                            SPUtil.putHOME_LONGITUDE(longitude + "");
                            EventBus.getDefault().post(event);

                            ApiUtils.getApiService().setHomeWifi(UserInstance.getInstance().getUid(),
                                    UserInstance.getInstance().getToken(),
                                    MeWifiListActivity.wifi_ssid,
                                    MeWifiListActivity.wifi_bssid,
                                    PetInfoInstance.getInstance().getPet_id(),
                                    MeWifiListActivity.common_wifi_body
                            ).enqueue(new XMQCallback<BaseBean>() {
                                @Override
                                public void onSuccess(Response<BaseBean> response, BaseBean message) {
                                    HttpCode ret = HttpCode.valueOf(message.status);
                                    if (ret == EC_SUCCESS) {
                                        PetInfoInstance.getInstance().getPackBean().wifi_bssid = MeWifiListActivity.wifi_bssid;
                                        PetInfoInstance.getInstance().getPackBean().wifi_ssid = MeWifiListActivity.wifi_ssid;
                                        UserInstance.getInstance().wifi_bssid = MeWifiListActivity.wifi_bssid;
                                        UserInstance.getInstance().wifi_ssid = MeWifiListActivity.wifi_ssid;
                                        SPUtil.putHomeWifiMac(MeWifiListActivity.wifi_bssid);
                                        SPUtil.putHomeWifiSsid(MeWifiListActivity.wifi_ssid);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFail(Call<BaseBean> call, Throwable t) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onFail(Call<BaseBean> call, Throwable t) {

                    }
                });
            }
        });
        et_search = (EditText) findViewById(R.id.et_search);

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {


            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {

                    String searchString = et_search.getText().toString();

                    mPoiSearch.searchInCity((citySearchOption)
                            .city(HomelocationInstance.getInstance().city)
                            .keyword(searchString)
                            .pageNum(10));

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                    }
                }
                return false;
            }

        });
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_location = (MapPetAtHomeView) findViewById(R.id.iv_location);
        try {
            iv_location.setAvaterUrl(PetInfoInstance.getInstance().getLogo_url());
        } catch (Exception e) {

        }
        ll_noname_address = findViewById(R.id.ll_noname_address);
        ll_noname_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HomelocationInstance.getInstance().setCenter(new LatLng(touch_latitude, touch_longitude), 1000);
                latitude = touch_latitude;
                longitude = touch_longitude;
                iv_selected.setVisibility(View.VISIBLE);
                adapter.mposition = -1;
                adapter.notifyDataSetChanged();
            }
        });
        tv_location = (TextView) findViewById(R.id.tv_location);
        iv_selected = findViewById(R.id.iv_selected);
        rv_addresslist = (RecyclerView) findViewById(R.id.rv_addresslist);
        rv_addresslist.setLayoutManager(new LinearLayoutManager(this));
        poiInfos = new ArrayList<>();
        adapter = new AddressAdapter(this, poiInfos);
        adapter.setOnItemClickListener(new AddressAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, AddressAdapter.StateHolder holder, int position) {
                HomelocationInstance.getInstance().setCenter(poiInfos.get(position).location, 1000);
                if (position != -1) {
                    iv_selected.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();

                if (poiInfos != null && poiInfos.size() > position) {
                    PoiInfo bean = poiInfos.get(position);
                    double[] temp = HomelocationInstance.bd09_To_Gcj02(bean.location.latitude, bean.location.longitude);
                    latitude = temp[0];
                    longitude = temp[1];
                }

            }
        });
        rv_addresslist.setAdapter(adapter);


        btn_phone_center = findViewById(R.id.btn_phone_center);
        btn_phone_center.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HomelocationInstance.getInstance().startPosition();

                Log.e("longtianlove", "图标" + iv_location.getX() + ":" + iv_location.getY() + ":" + iv_location.getWidth() + ":" + iv_location.getHeight());

            }
        });
        mMapView = (MapView) findViewById(R.id.bmapView);
//        if(MIUIUtils.isMIUI()){
//            mMapView. setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            mMapView.setBackgroundColor(Color.parseColor("#ffffff"));
//        }
        mBaiduMap = mMapView.getMap();
//        UiSettings UiSettings = mBaiduMap.getUiSettings();
//        UiSettings.setRotateGesturesEnabled(true);//打开旋转
//        UiSettings.setScrollGesturesEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                iv_location.startAnimation(translateAnimation);
                projection = mBaiduMap.getProjection();
                if (projection == null) {
                    return;
                }
                int indexy = (int) (iv_location.getY() / 2);
                Point aimPoint = new Point(mapStatus.targetScreen.x, mapStatus.targetScreen.y + indexy);
                final LatLng position = projection.fromScreenLocation(aimPoint);
                Log.e("longtianlove", "地图" + mapStatus.targetScreen.x + ":" + mapStatus.targetScreen.y);

                MapLocationParser.queryLocationDesc(position, new addressParseListener() {
                    @Override
                    public void onAddressparsed(String address) {
                        tv_location.setText(address);
                        String[] tempStrings = address.split("市");
                        if (tempStrings.length < 2) {
                            return;
                        }
                        String temp = address.split("市")[1];
                        if (temp.length() > 2) {
                            temp = temp.substring(0, 2);
                        }
                        mPoiSearch.searchInCity((citySearchOption)
                                .city(HomelocationInstance.getInstance().city)
                                .keyword(temp)
                                .pageNum(10));
                    }
                });

                Log.e("longtianlove", position.toString());
                touch_latitude = position.latitude;
                touch_longitude = position.longitude;
                if (iv_selected.getVisibility() == View.VISIBLE) {
                    double[] temp = HomelocationInstance.bd09_To_Gcj02(position.latitude, position.longitude);
                    latitude = temp[0];
                    longitude = temp[1];
                }
            }
        });
        HomelocationInstance.getInstance().init(mMapView);

        initAnim();
        initData();
//        HomelocationInstance.getInstance().startPosition();


        EventBus.getDefault().register(this);
    }

    public void initAnim() {
        translateAnimation = new TranslateAnimation(0, 0, 0, -50);
        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(new AnticipateOvershootInterpolator());
        iv_location.startAnimation(translateAnimation);
    }

    public void initData() {
        mPoiSearch = PoiSearch.newInstance();

        citySearchOption = new PoiCitySearchOption();
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            public void onGetPoiResult(PoiResult result) {
                //获取POI检索结果
                poiInfos = (ArrayList<PoiInfo>) result.getAllPoi();
                adapter.mdatas = poiInfos;
                adapter.notifyDataSetChanged();
            }

            public void onGetPoiDetailResult(PoiDetailResult result) {
                //获取Place详情页检索结果
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        nearbySearchOption = new PoiNearbySearchOption();

    }

    //宠物信息更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.bindingLocationChanged event) {
        final LatLng position = new LatLng(event.latitude, event.longitude);
        MapLocationParser.queryLocationDesc(position, new addressParseListener() {
            @Override
            public void onAddressparsed(String address) {
                tv_location.setText(address);
                String[] tempStrings = address.split("市");
                if (tempStrings.length < 2) {
                    return;
                }
                String temp = address.split("市")[1];
                if (temp.length() > 2) {
                    temp = temp.substring(0, 2);
                }
                mPoiSearch.searchInCity((citySearchOption)
                        .city(HomelocationInstance.getInstance().city)
                        .keyword(temp)
                        .pageNum(10));
            }
        });
        double[] temp = HomelocationInstance.bd09_To_Gcj02(position.latitude, position.longitude);
        latitude = temp[0];
        longitude = temp[1];
    }


    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.e("longtianlove-point","width:"+(mMapView.getWidth() / 2)+"height:"+mMapView.getHeight() /2);
        if (isFirst) {
            HomelocationInstance.getInstance().setHomeCenter();
            isFirst=false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        HomelocationInstance.getInstance().Destroy();
        if (mBaiduMap != null) {
            mBaiduMap.clear();
            mBaiduMap = null;
        }
        EventBus.getDefault().unregister(this);
    }
}
