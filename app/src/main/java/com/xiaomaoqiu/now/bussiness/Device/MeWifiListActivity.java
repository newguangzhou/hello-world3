package com.xiaomaoqiu.now.bussiness.Device;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.adapter.CheckStateAdapter;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.WifiBean;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.refresh.MaterialDesignPtrFrameLayout;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import retrofit2.Call;
import retrofit2.Response;

import static com.xiaomaoqiu.now.http.HttpCode.EC_SUCCESS;

/**
 * Created by long on 2017/4/12.
 */
@SuppressLint("WrongConstant")
public class MeWifiListActivity extends BaseActivity {

    MaterialDesignPtrFrameLayout ptr_refresh;
    RecyclerView rv_wifilist;



    CheckStateAdapter adapter;
    public Object lock=new Object();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.title_home_wifi));
        setContentView(R.layout.activity_wifilist);
        ptr_refresh= (MaterialDesignPtrFrameLayout) this.findViewById(R.id.ptr_refresh);
        initView();
        initData();

        EventBus.getDefault().register(this);
    }

    private void initView() {


        rv_wifilist = (RecyclerView) findViewById(R.id.rv_wifilist);
        setNextView("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(wifi_bssid)) {
                    ToastUtil.showTost("您必须选择一个homewifi");
                    return;
                }
                ApiUtils.getApiService().setHomeWifi(UserInstance.getInstance().getUid(),
                        UserInstance.getInstance().getToken(),
                        wifi_ssid,
                        wifi_bssid
                ).enqueue(new XMQCallback<BaseBean>() {
                    @Override
                    public void onSuccess(Response<BaseBean> response, BaseBean message) {
                        HttpCode ret = HttpCode.valueOf(message.status);
                        if (ret == EC_SUCCESS) {
                            PetInfoInstance.getInstance().getPackBean().wifi_bssid = wifi_bssid;
                            PetInfoInstance.getInstance().getPackBean().wifi_ssid = wifi_ssid;
                            finish();
                        }


                    }

                    @Override
                    public void onFail(Call<BaseBean> call, Throwable t) {

                    }
                });


            }
        });
        rv_wifilist.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckStateAdapter(DeviceInfoInstance.getInstance().wiflist, this);
        rv_wifilist.setAdapter(adapter);

        /**
         * 下拉刷新
         */
        ptr_refresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                DeviceInfoInstance.getInstance().sendGetWifiListCmd();
            }
        });
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

        adapter.setOnItemClickListener(new CheckStateAdapter.OnItemClickListener() {

            @Override
            public void OnItemClick(View view, CheckStateAdapter.StateHolder holder, int position) {


                synchronized (lock) {
                    if(position>=adapter.mdatas.size()||position<0){
                        return;
                    }
                    wifi_bssid="";
                    wifi_ssid="";
                    adapter.mdatas.get(position).is_homewifi = adapter.mdatas.get(position).is_homewifi == 0 ? 1 : 0;

                    if (adapter.mdatas.get(position).is_homewifi == 1) {
                        wifi_bssid = adapter.mdatas.get(position).wifi_bssid;
                        wifi_ssid = adapter.mdatas.get(position).wifi_ssid;

                        for (WifiBean bean : adapter.mdatas) {
                            if (!bean.wifi_bssid.equals(adapter.mdatas.get(position).wifi_bssid)) {
                                bean.is_homewifi = 0;
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getWifiList(EventManage.wifiListSuccess event) {
        synchronized (lock) {
            //刷新列表
            adapter.mdatas = DeviceInfoInstance.getInstance().wiflist.data;
            adapter.notifyDataSetChanged();
        }

        ptr_refresh.refreshComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getWifiListError(EventManage.wifiListError event) {
        ToastUtil.showTost("设备不在线，请启动");
        ptr_refresh.refreshComplete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceInfoInstance.getInstance().sendGetWifiListCmd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
