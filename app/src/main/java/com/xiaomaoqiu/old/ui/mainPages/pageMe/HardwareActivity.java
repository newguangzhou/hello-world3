package com.xiaomaoqiu.old.ui.mainPages.pageMe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.old.dataCenter.DeviceInfo;
import com.xiaomaoqiu.old.dataCenter.HttpCode;
import com.xiaomaoqiu.old.dataCenter.LoginMgr;
import com.xiaomaoqiu.old.dataCenter.PetInfo;
import com.xiaomaoqiu.old.dataCenter.UserMgr;
import com.xiaomaoqiu.old.ui.dialog.DialogToast;
import com.xiaomaoqiu.old.utils.HttpUtil;
import com.xiaomaoqiu.old.widgets.ActivityEx;

import org.apache.http.Header;
import org.json.JSONObject;

public class HardwareActivity extends ActivityEx {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.title_hardware));
        setContentView(R.layout.activity_hardware);

        //todo 设备状态更新
        onDeviceInfoChanged(UserMgr.INSTANCE.getPetInfo().getDevInfo());
        findViewById(R.id.tv_unbind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast();
            }
        });


        findViewById(R.id.tv_sim_recharge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sim充值
            }
        });
    }

   //todo 设备状态更新
    public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
        if(null == deviceInfo){
            return;
        }
        TextView tv = (TextView) findViewById(R.id.tv_device_name);
        if(deviceInfo.getDeviceName() != null)
            tv.setText(deviceInfo.getDeviceName());
        else
            tv.setText("未绑定设备");

        tv = (TextView) findViewById(R.id.tv_imei);
        if(deviceInfo.getImei() != null)
            tv.setText(deviceInfo.getImei());
        else
            tv.setText("未绑定设备");

        tv = (TextView) findViewById(R.id.tv_hardware);
        if(deviceInfo.getFirmwareVersion() != null)
            tv.setText(deviceInfo.getFirmwareVersion());
        else
            tv.setText("未绑定设备");
    }

    private void showToast(){
        new DialogToast(this, "确认解除绑定？", "确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解除设备绑定
                final PetInfo petInfo = UserMgr.INSTANCE.getPetInfo();
                HttpUtil.get2("device.remove_device_info",new JsonHttpResponseHandler(){
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.v("http", "device.remove_device_info:" + response.toString());
                        HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                        if (ret == HttpCode.EC_SUCCESS) {
                            //将设备信息置空
                            petInfo.getDevInfo().initFromNull();
                            finish();
                        }
                    }
                }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(),petInfo.getDevInfo().getImei());
            }
        });
    }

    public static void skip(Context context){
        Intent intent=new Intent(context,HardwareActivity.class);
        context.startActivity(intent);
    }

}
