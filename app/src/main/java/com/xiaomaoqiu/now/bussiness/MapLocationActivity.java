package com.xiaomaoqiu.now.bussiness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.user.RebootActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.HomelocationInstance;
import com.xiaomaoqiu.now.map.main.MapLocationParser;
import com.xiaomaoqiu.now.map.main.addressParseListener;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

import static com.xiaomaoqiu.now.http.HttpCode.EC_SUCCESS;

/**
 * Created by long on 2017/6/22.
 */

public class MapLocationActivity extends Activity {


    View btn_go_back;
    TextView tv_next;

    private TextureMapView mMapView;
    private TextView tv_location;
    private BaiduMap mBaiduMap;
    private Projection projection;
    private View btn_phone_center;
    private View iv_location;

    public double longitude;
    public double latitude;


    TranslateAnimation translateAnimation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetAppLike.mcontext, InitWifiListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PetAppLike.mcontext.startActivity(intent);
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
                            UserInstance.getInstance().latitude=latitude;
                            UserInstance.getInstance().longitude=longitude;
                            SPUtil.putHOME_LATITUDE(latitude+"");
                            SPUtil.putHOME_LONGITUDE(longitude+"");
                            EventBus.getDefault().post(event);
                        }
                        Intent intent = new Intent(MapLocationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFail(Call<BaseBean> call, Throwable t) {

                    }
                });
            }
        });
        iv_location = findViewById(R.id.iv_location);
        tv_location = (TextView) findViewById(R.id.tv_location);
        btn_phone_center = findViewById(R.id.btn_phone_center);
        btn_phone_center.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HomelocationInstance.getInstance().startPosition();
                Log.e("longtianlove", "图标" + iv_location.getX() + ":" + iv_location.getY() + ":" + iv_location.getWidth() + ":" + iv_location.getHeight());

            }
        });
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
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
                LatLng position = projection.fromScreenLocation(mapStatus.targetScreen);
                Log.e("longtianlove", "地图" + mapStatus.targetScreen.x + ":" + mapStatus.targetScreen.y);

                MapLocationParser.queryLocationDesc(position, new addressParseListener() {
                    @Override
                    public void onAddressparsed(String address) {
//                        Log.e("longtianlove","位置"+address);
                        tv_location.setText(address);
                    }
                });

                Log.e("longtianlove", position.toString());
                double[] temp=HomelocationInstance.bd09_To_Gcj02(position.latitude,position.longitude);
                latitude = temp[0];
                longitude = temp[1];


            }
        });
        HomelocationInstance.getInstance().init(mMapView);

        initAnim();


        EventBus.getDefault().register(this);
    }

    public void initAnim(){
        translateAnimation = new TranslateAnimation(0, 0, 0, -50);
        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(new AnticipateOvershootInterpolator());
        iv_location.startAnimation(translateAnimation);
    }
    //宠物信息更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.bindingLocationChanged event) {
        LatLng position=new LatLng(event.latitude,event.longitude);
        MapLocationParser.queryLocationDesc(position, new addressParseListener() {
            @Override
            public void onAddressparsed(String address) {
//                        Log.e("longtianlove","位置"+address);
                tv_location.setText(address);
            }
        });
        double[] temp=HomelocationInstance.bd09_To_Gcj02(position.latitude,position.longitude);
        latitude = temp[0];
        longitude = temp[1];
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.e("longtianlove-point","width:"+(mMapView.getWidth() / 2)+"height:"+mMapView.getHeight() /2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
