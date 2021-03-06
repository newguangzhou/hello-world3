package com.xiaomaoqiu.now.bussiness.Device;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.BaseWebViewHasBatteryViewActivity;
import com.xiaomaoqiu.now.bussiness.bean.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.user.ConfirmBatteryActivity;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeviceActivity extends BaseActivity {
    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
    }

    private View btn_go_back;
    BatteryView batteryView;
    TextView tv_newhardware;
    TextView tv_sim_deadline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.title_hardware));
        setContentView(R.layout.activity_hardware);
        btn_go_back = this.findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_sim_deadline= (TextView) findViewById(R.id.tv_sim_deadline);

        showMessageOnUI();
        findViewById(R.id.tv_unbind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindDeviceDialog();
            }
        });
        batteryView= (BatteryView) findViewById(R.id.batteryView);
        batteryView.setActivity(this);
        batteryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DeviceInfoInstance.getInstance().battery_level < 0) {
                    ToastUtil.showTost("追踪器离线");
                    return;
                }
                batteryView.pushBatteryDialog(DeviceInfoInstance.getInstance().battery_level,
                        DeviceInfoInstance.getInstance().lastGetTime);
            }
        });
        if (!DeviceInfoInstance.getInstance().online) {
//            ToastUtil.showTost("您的设备尚未开机！");
            batteryView.setDeviceOffline();
        }else{
            batteryView.showBatterylevel(DeviceInfoInstance.getInstance().battery_level,
                    DeviceInfoInstance.getInstance().lastGetTime);
        }

        findViewById(R.id.tv_sim_recharge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sim充值
                Intent intent=new Intent(DeviceActivity.this, BaseWebViewHasBatteryViewActivity.class);
                intent.putExtra("title","小毛球商城");
                intent.putExtra("web_url","https://www.xiaomaoqiu.com/wechat_shop.html");
                startActivity(intent);
            }
        });


        EventBus.getDefault().register(this);
    }

    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceOffline(EventManage.DeviceOffline event){
        batteryView.setDeviceOffline();
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceInfoChanged(EventManage.notifyDeviceStateChange event) {
        showMessageOnUI();
        batteryView = (BatteryView) findViewById(R.id.batteryView);
        if (!DeviceInfoInstance.getInstance().online) {
//            ToastUtil.showTost("您的设备尚未开机！");
            batteryView.setDeviceOffline();
            return;
        }
        batteryView.showBatterylevel(DeviceInfoInstance.getInstance().battery_level,
                DeviceInfoInstance.getInstance().lastGetTime);
    }

    //解绑成功
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void unbindDeviceSuccess(EventManage.unbindDeviceSuccess event){
        Intent intent = new Intent(PetAppLike.mcontext, ConfirmBatteryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PetAppLike.mcontext.startActivity(intent);

    }

    //显示设备信息
    void showMessageOnUI() {
        TextView tv = (TextView) findViewById(R.id.tv_device_name);
        tv_newhardware= (TextView) findViewById(R.id.tv_newhardware);
        DeviceInfoBean bean = DeviceInfoInstance.getInstance().packBean;
        if (!TextUtils.isEmpty(bean.device_name))
            tv.setText(bean.device_name);
        else {
            tv.setText("小毛球宠物追踪器");
        }
        if(!TextUtils.isEmpty(bean.hardware_version)){
            String[] a=bean.hardware_version.split("_");
            if((a.length>0)&&(!"".equals(a[a.length-1]))){
                tv_newhardware.setText(a[a.length-1]);
            }else{
                tv_newhardware.setText(bean.hardware_version);
            }

        }

        tv = (TextView) findViewById(R.id.tv_imei);
        if (!TextUtils.isEmpty(bean.imei))
            tv.setText(bean.imei);
        else {
            tv.setText("未绑定追踪器");
        }

        tv = (TextView) findViewById(R.id.tv_hardware_version);
        if (!TextUtils.isEmpty(bean.firmware_version)) {
            String[] a=bean.firmware_version.split("_");
            if((a.length>0)&&(!"".equals(a[a.length-1]))){
                tv.setText(a[a.length-1]);
            }else {
                tv.setText(bean.firmware_version);
            }
        } else {
            tv.setText("未绑定追踪器");
        }


        String deadline="";
        try{
            deadline=((DeviceInfoInstance.getInstance().packBean.sim_deadline).split(" "))[0];
        }catch (Exception e){
            deadline=   DeviceInfoInstance.getInstance().packBean.sim_deadline;
        }
        if(DeviceInfoInstance.getInstance().packBean.sim_deadline!=null&&!"".equals(DeviceInfoInstance.getInstance().packBean.sim_deadline)){
            tv_sim_deadline.setText("当前sim卡已经充值到"+deadline);
        }else{
            tv_sim_deadline.setText("请注意sim卡充值");
        }
    }

    private void bindDeviceDialog() {
        DialogUtil.showUnBindDialog(this,new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DeviceInfoInstance.getInstance().unbindDevice();
            }
        },new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
