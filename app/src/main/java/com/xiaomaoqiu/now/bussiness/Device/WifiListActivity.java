package com.xiaomaoqiu.now.bussiness.Device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.adapter.CheckStateAdapter;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bean.nocommon.WifiListBean;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by long on 2017/4/12.
 */

public class WifiListActivity extends BaseActivity {


    RecyclerView rv_wifilist;

    WifiListBean wifiListBean;

    CheckStateAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.title_home_wifi));
        setContentView(R.layout.activity_wifilist);

        initView();
        initData();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        rv_wifilist = (RecyclerView) findViewById(R.id.rv_wifilist);
        setNextView("下一步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
                    ToastUtil.showTost("您必须选择一个homewifi");
                    return;
                }
                Intent intent = new Intent(WifiListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        rv_wifilist.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckStateAdapter(DeviceInfoInstance.getInstance().wiflist, this);
        rv_wifilist.setAdapter(adapter);
    }

    private void initData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getWifiList(EventManage.wifiListSuccess event) {
        //刷新列表
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
