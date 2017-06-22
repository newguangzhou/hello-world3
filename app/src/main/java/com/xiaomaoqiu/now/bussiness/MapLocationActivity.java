package com.xiaomaoqiu.now.bussiness;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.xiaomaoqiu.now.map.HomelocationInstance;
import com.xiaomaoqiu.now.map.main.MapLocationParser;
import com.xiaomaoqiu.now.map.main.addressParseListener;
import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/6/22.
 */

public class MapLocationActivity extends Activity {


    private TextureMapView mMapView;
    private TextView tv_location;
    private BaiduMap mBaiduMap;
    private Projection projection;
    private View btn_phone_center;
    private View iv_location;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        iv_location=findViewById(R.id.iv_location);
        tv_location= (TextView) findViewById(R.id.tv_location);
        btn_phone_center=findViewById(R.id.btn_phone_center);
        btn_phone_center.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                HomelocationInstance.getInstance().startPosition();
                Log.e("longtianlove","图标"+iv_location.getX()+":"+iv_location.getY()+":"+iv_location.getWidth()+":"+iv_location.getHeight());

            }
        });
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        HomelocationInstance.getInstance().init(mMapView);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                projection=mBaiduMap.getProjection();
                LatLng position=projection.fromScreenLocation(mapStatus.targetScreen);
                Log.e("longtianlove","地图"+mapStatus.targetScreen.x+":"+mapStatus.targetScreen.y);

                MapLocationParser.queryLocationDesc(position, new addressParseListener() {
                    @Override
                    public void onAddressparsed(String address) {
//                        Log.e("longtianlove","位置"+address);
                        tv_location.setText(address);
                    }
                });


                Log.e("longtianlove", position.toString());
            }
        });





    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.e("longtianlove-point","width:"+(mMapView.getWidth() / 2)+"height:"+mMapView.getHeight() /2);
    }
}
