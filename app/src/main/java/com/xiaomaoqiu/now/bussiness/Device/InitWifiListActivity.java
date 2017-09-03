package com.xiaomaoqiu.now.bussiness.Device;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bussiness.InitMapLocationActivity;
import com.xiaomaoqiu.now.bussiness.adapter.CheckStateAdapter;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.bean.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.WifiBean;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.AddPetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.RebootActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.refresh.MaterialDesignPtrFrameLayout;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.xiaomaoqiu.now.http.HttpCode.EC_SUCCESS;

/**
 * Created by long on 2017/4/12.
 */
@SuppressLint("WrongConstant")
public class InitWifiListActivity extends BaseActivity {

    MaterialDesignPtrFrameLayout ptr_refresh;
    RecyclerView rv_wifilist;


    CheckStateAdapter adapter;
    public Object lock = new Object();


    TextView tv_next;
    View btn_go_back;


    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifilist);
        ptr_refresh = (MaterialDesignPtrFrameLayout) this.findViewById(R.id.ptr_refresh);
        initView();
        initData();

        EventBus.getDefault().register(this);
    }

    private void initView() {
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetAppLike.mcontext, AddPetInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PetAppLike.mcontext.startActivity(intent);

            }
        });

        tv_next= (TextView) findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(wifi_bssid==null||"".equals(wifi_bssid)){
                    ToastUtil.showTost("请选择wifi");
                    return;
                }

                String common_wifi="";
                try {
                    common_wifi  = JSON.toJSONString(adapter.mdatas);
                }catch (Exception e){

                }

                RequestBody common_wifi_body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),common_wifi);
                ApiUtils.getApiService().setHomeWifi(UserInstance.getInstance().getUid(),
                        UserInstance.getInstance().getToken(),
                        wifi_ssid,
                        wifi_bssid,
                        PetInfoInstance.getInstance().getPet_id(),
                        UserInstance.getInstance().device_imei,
                        DeviceInfoInstance.getInstance().GET_WIFI_LIST_TIME,
                        common_wifi_body
                ).enqueue(new XMQCallback<BaseBean>() {
                    @Override
                    public void onSuccess(Response<BaseBean> response, BaseBean message) {
                        HttpCode ret = HttpCode.valueOf(message.status);
                        if (ret == EC_SUCCESS) {
                            PetInfoInstance.getInstance().getPackBean().wifi_bssid = wifi_bssid;
                            PetInfoInstance.getInstance().getPackBean().wifi_ssid = wifi_ssid;
                            UserInstance.getInstance().wifi_bssid = wifi_bssid;
                            UserInstance.getInstance().wifi_ssid = wifi_ssid;
                            SPUtil.putHomeWifiMac(wifi_bssid);
                            SPUtil.putHomeWifiSsid(wifi_ssid);

                        }
                        if(UserInstance.getInstance().latitude==-1){
                            Intent intent = new Intent();
                            intent.setClass(InitWifiListActivity.this, InitMapLocationActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
//                        if (UserInstance.getInstance().agree_policy == 0) {
//                            Intent intent = new Intent(InitWifiListActivity.this, RebootActivity.class);
//                            startActivity(intent);
//                            finish();
//                            return;
//                        }

                        Intent intent = new Intent(InitWifiListActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();


                    }

                    @Override
                    public void onFail(Call<BaseBean> call, Throwable t) {

                    }
                });
            }
        });
//        tv_next.setEnabled(false);
//        tv_next.setTextColor(getResources().getColor(R.color.black));


        rv_wifilist = (RecyclerView) findViewById(R.id.rv_wifilist);
        rv_wifilist.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckStateAdapter(DeviceInfoInstance.getInstance().wiflist, this);
        rv_wifilist.setAdapter(adapter);

        /**
         * 下拉刷新
         */
        ptr_refresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getWifiTime=0;
//                tv_next.setEnabled(false);
//                tv_next.setTextColor(getResources().getColor(R.color.black));
                DialogUtil.showProgress(InitWifiListActivity.this, "");
                DeviceInfoInstance.getInstance().sendGetWifiListCmd();
            }
        });
    }

    String wifi_bssid;//homewifi   mac
    String wifi_ssid;//wifi名称

    private void initData() {
        adapter.setOnItemClickListener(new CheckStateAdapter.OnItemClickListener() {

            @Override
            public void OnItemClick(View view, CheckStateAdapter.StateHolder holder, int position) {
                synchronized (lock) {
                    if (position >= adapter.mdatas.size() || position < 0) {
                        return;
                    }
                    wifi_bssid = "";
                    wifi_ssid = "";
//                    tv_next.setEnabled(false);
//                    tv_next.setTextColor(getResources().getColor(R.color.black));
                    adapter.mdatas.get(position).is_homewifi = adapter.mdatas.get(position).is_homewifi == 0 ? 1 : 0;

                    if (adapter.mdatas.get(position).is_homewifi == 1) {
                        wifi_bssid = adapter.mdatas.get(position).wifi_bssid;
                        wifi_ssid = adapter.mdatas.get(position).wifi_ssid;
//                        tv_next.setEnabled(true);
//                        tv_next.setTextColor(getResources().getColor(R.color.white));
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
        DialogUtil.closeProgress();
//        tv_next.setEnabled(true);
//        tv_next.setTextColor(getResources().getColor(R.color.white));
        synchronized (lock) {
            //刷新列表
            adapter.mdatas = DeviceInfoInstance.getInstance().wiflist.data;
            if (adapter.mdatas.size() == 0) {
                ToastUtil.showTost("当前没有扫描到wifi");
            }
            adapter.notifyDataSetChanged();
        }
        ptr_refresh.refreshComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getWifiListError(EventManage.wifiListError event) {
        DialogUtil.closeProgress();
//        ToastUtil.showTost("获取最新wifi失败，请重新刷新");
        ptr_refresh.refreshComplete();

        if (getWifiTime++ < 10) {
            DeviceInfoInstance.getInstance().sendGetWifiListCmd();
        }else{
            getWifiTime=0;
//            tv_next.setEnabled(true);
//            tv_next.setTextColor(getResources().getColor(R.color.white));
        }

    }

    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceOffline(EventManage.DeviceOffline event) {
        DialogUtil.showDeviceOfflineDialog(this,"离线通知");
    }

    public static int getWifiTime = 0;

    @Override
    protected void onResume() {
        super.onResume();
        DialogUtil.showProgress(this, "");
        getWifiTime=0;
        DeviceInfoInstance.getInstance().sendGetWifiListCmd();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DialogUtil.closeProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onBackPressed() {
        DialogUtil.showTwoButtonDialog(this,getString(R.string.dialog_exit_app_title),getString(R.string.dialog_exit_app_tab1),getString(R.string.dialog_exit_login_tab2),new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                    }
                },
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
//        DialogToast.createDialogWithTwoButton(this, "确定要退出小毛球吗？", new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                }
//        );
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView9);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        assert spinner != null;
        spinner.start();
    }
}
