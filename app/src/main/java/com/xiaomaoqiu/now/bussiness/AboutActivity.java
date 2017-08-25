package com.xiaomaoqiu.now.bussiness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.Apputil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    TextView tv_versionname;
    TextView tv_help;
    TextView tv_policy;
    View tv_suggest;

    private View btn_go_back;

    private BatteryView batteryView;//右上角的电池


    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceOffline(EventManage.DeviceOffline event) {
        batteryView.setDeviceOffline();
//        DialogUtil.showDeviceOfflineDialog(this, "离线通知");
    }

    //设备状态更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceInfoChanged(EventManage.notifyDeviceStateChange event) {
        if (!DeviceInfoInstance.getInstance().online) {
//            ToastUtil.showTost("您的设备尚未开机！");
            batteryView.setDeviceOffline();
            return;
        }
        batteryView.showBatterylevel(DeviceInfoInstance.getInstance().battery_level,
                DeviceInfoInstance.getInstance().lastGetTime);
    }

    //todo 小米推送
    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void receivePushDeviceOffline(PushEventManage.deviceOffline event) {
        batteryView.setDeviceOffline();
//        DialogUtil.showDeviceOfflineDialog(this, "离线通知");
    }


    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle("关于");
        setContentView(R.layout.activity_about);
        tv_versionname = (TextView) findViewById(R.id.tv_versionname);
        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_policy = (TextView) findViewById(R.id.tv_policy);
        tv_versionname.setText("版本信息：小毛球 " + Apputil.getVersionName(this));
        tv_suggest = findViewById(R.id.tv_suggest);

        tv_help.setOnClickListener(this);
        tv_policy.setOnClickListener(this);
        tv_suggest.setOnClickListener(this);
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        batteryView = (BatteryView) findViewById(R.id.batteryView);
        batteryView.setActivity(this);

        //点击弹出电池
        batteryView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!DeviceInfoInstance.getInstance().online) {
                    ToastUtil.showTost("追踪器离线");
                    return;
                }
                if (DeviceInfoInstance.getInstance().battery_level > 1.0f) {
                    PetInfoInstance.getInstance().getPetLocation();
                }
                batteryView.pushBatteryDialog(DeviceInfoInstance.getInstance().battery_level,
                        DeviceInfoInstance.getInstance().lastGetTime);
            }
        });

        if (!DeviceInfoInstance.getInstance().online) {
//            ToastUtil.showTost("您的设备尚未开机！");
            batteryView.setDeviceOffline();
        }else {
            batteryView.showBatterylevel(DeviceInfoInstance.getInstance().battery_level,
                    DeviceInfoInstance.getInstance().lastGetTime);
        }

        EventBus.getDefault().register(this);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, BaseWebViewActivity.class);
        switch (v.getId()) {
            case R.id.tv_help:
                intent.putExtra("title","使用帮助");
                intent.putExtra("web_url", "https://www.xiaomaoqiu.com/support.html?nav=2");
                startActivity(intent);
                break;
            case R.id.tv_policy:
                intent.putExtra("title","用户协议与隐私政策");
                intent.putExtra("web_url", "https://www.xiaomaoqiu.com/proto_user.html");
                startActivity(intent);
                break;
            case R.id.tv_suggest:
                Intent intent1 = new Intent(this, SuggestActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
