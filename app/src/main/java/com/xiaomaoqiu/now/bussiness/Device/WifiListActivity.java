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
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.WifiBean;
import com.xiaomaoqiu.now.bean.nocommon.WifiListBean;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

import static com.tencent.bugly.crashreport.CrashReport.testJavaCrash;
import static com.xiaomaoqiu.now.http.HttpCode.EC_SUCCESS;

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
        DeviceInfoInstance.getInstance().getWifiList();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        rv_wifilist = (RecyclerView) findViewById(R.id.rv_wifilist);
        setNextView("下一步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//todo  之后删除
                Intent intent = new Intent(WifiListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
//todo  之后删除

                ApiUtils.getApiService().setHomeWifi(UserInstance.getInstance().getUid(),
                        UserInstance.getInstance().getToken(),
                        wifi_ssid,
                        wifi_bssid
                ).enqueue(new XMQCallback<BaseBean>() {
                    @Override
                    public void onSuccess(Response<BaseBean> response, BaseBean message) {
                        HttpCode ret=HttpCode.valueOf(message.status);
                        if(ret==EC_SUCCESS){
                            PetInfoInstance.getInstance().getPackBean().wifi_bssid=wifi_bssid;
                            PetInfoInstance.getInstance().getPackBean().wifi_ssid=wifi_ssid;
                            Intent intent = new Intent(WifiListActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }

                    @Override
                    public void onFail(Call<BaseBean> call, Throwable t) {

                    }
                });
                if (TextUtils.isEmpty(wifi_bssid)) {
                    ToastUtil.showTost("您必须选择一个homewifi");
                    return;
                }

            }
        });
        rv_wifilist.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckStateAdapter(DeviceInfoInstance.getInstance().wiflist, this);
        rv_wifilist.setAdapter(adapter);
    }

    String wifi_bssid;//homewifi   mac
    String wifi_ssid;//wifi名称
    private void initData() {
//        //销毁
//        WifiListBean wifiListBean = new WifiListBean();
//        wifiListBean.data = new ArrayList<WifiBean>();
//        for (int i = 0; i < 5; i++) {
//            WifiBean wifiBean = new WifiBean();
//            wifiBean.wifi_bssid = i + "----" + i;
//            wifiBean.wifi_ssid = i + "----" + i;
//            wifiListBean.data.add(wifiBean);
//        }
//        adapter.mdatas = wifiListBean.data;
//
//        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new CheckStateAdapter.OnItemClickListener(){

            @Override
            public void OnItemClick(View view, CheckStateAdapter.StateHolder holder, int position) {
                adapter.mdatas.get(position).Is_homewifi=!adapter.mdatas.get(position).Is_homewifi;

                if(adapter.mdatas.get(position).Is_homewifi){
                    wifi_bssid=adapter.mdatas.get(position).wifi_bssid;
                    wifi_ssid=adapter.mdatas.get(position).wifi_ssid;
                }
            }
        });

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
