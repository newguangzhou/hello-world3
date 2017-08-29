package com.xiaomaoqiu.now.bussiness.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends BaseActivity {


    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
    }


    private View btn_go_back;

    private BatteryView batteryView;//右上角的电池


    ListView mLvMessage;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.me_message);
        setContentView(R.layout.activity_message);
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

                                           @Override
                                           public void onClick(View v) {
                                               finish();
                                           }
                                       }
        );
        mLvMessage = (ListView) findViewById(R.id.lv_message);
        mLvMessage.setAdapter(new MessageAdapter());
//        queryMessage();



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
        } else {
            batteryView.showBatterylevel(DeviceInfoInstance.getInstance().battery_level,
                    DeviceInfoInstance.getInstance().lastGetTime);
        }


        EventBus.getDefault().register(this);

    }

    public void queryMessage() {
//        HttpUtil.get2("msg.get_msg",new JsonHttpResponseHandler(){
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        HttpCode code = HttpCode.valueOf(response.optInt("status"));
//                        if (code == HttpCode.EC_SUCCESS) {
//                            MessageAdapter adapter = (MessageAdapter) MessageActivity.this.mLvMessage.getAdapter();
//                            adapter.initFromJson();
//                        }else
//                        {
//                            Toast.makeText(MessageActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        Toast.makeText(MessageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                UserInstance.getInstance().getUid(),UserInstance.getInstance().getToken());
    }

    class MessageAdapter extends BaseAdapter {
        class MessageData {
            public int msgType;
            public String msgContent;
        }

        List<MessageData> mData = new ArrayList<MessageData>();

        public void initFromJson() {
            mData.clear();

        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_message, null);
            }

            TextView tvContent = (TextView) convertView.findViewById(R.id.tv_message);
            tvContent.setText(mData.get(position).msgContent);
            return convertView;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
