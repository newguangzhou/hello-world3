package com.xiaomaoqiu.now.bussiness.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by long on 2017/6/4.
 */

public class RebootActivity extends BaseActivity {
    public int frameTemplate() {//没有标题栏
        return 0;
    }

    Button btn_reboot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboot);
        btn_reboot = (Button) this.findViewById(R.id.btn_reboot);
        btn_reboot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DeviceInfoInstance.getInstance().rebootDevice();
            }
        });
        EventBus.getDefault().register(this);
    }

    //设备重启指令发送
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceReboot(EventManage.deviceReboot event) {

        Intent intent=new Intent(RebootActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
