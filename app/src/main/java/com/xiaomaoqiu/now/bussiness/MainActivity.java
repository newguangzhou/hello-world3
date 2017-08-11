package com.xiaomaoqiu.now.bussiness;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseFragmentActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.location.LocateFragment;
import com.xiaomaoqiu.now.bussiness.pet.CheckIndex;
import com.xiaomaoqiu.now.bussiness.pet.PetFragment;
import com.xiaomaoqiu.now.bussiness.pet.info.PetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.MeFrament;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.main.MapInstance;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.AppDialog;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

@SuppressLint("WrongConstant")
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener, CheckIndex {

    private static int mTabID[] = {
            R.id.tab_health,
            R.id.tab_locate,
            R.id.tab_me};

    private ImageView mHealthTabIcon, mLocateTabIcon, mMeTabIcon;


    private View mTabs[] = {null, null, null};

    private PetFragment mPetFragment;
    private LocateFragment mLocateFragment;
    private MeFrament mMeFragment;

    View include_header;


    TextView tv_title;
    SimpleDraweeView sdv_header;
    BatteryView batteryView;//右上角的电池

    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
    }

    //宠物信息更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.notifyPetInfoChange event) {
        Uri uri = Uri.parse(PetInfoInstance.getInstance().getPackBean().logo_url);
        sdv_header.setImageURI(uri);
    }

    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceOffline(EventManage.DeviceOffline event) {
        batteryView.setDeviceOffline();
        DialogUtil.showDeviceOfflineDialog(this, "离线通知");
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

    //宠物头像更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.uploadImageSuccess event) {
        Uri uri = Uri.parse(PetInfoInstance.getInstance().getPackBean().logo_url);
        sdv_header.setImageURI(uri);
    }


    //位置很近
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void distanceCloseiInGPS(EventManage.distanceClose event) {
        String dialog_isfinded_title="找到"+PetInfoInstance.getInstance().getNick()+"了吗？";
        DialogUtil.showPetFindedDialog(this, getString(R.string.dialog_isfinded_title), getString(R.string.dialog_isfinded_tab1), getString(R.string.dialog_isfinded_tab2), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MapInstance.getInstance().openTime = 1;
                        ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.GPS_CLOSE).enqueue(new XMQCallback<PetStatusBean>() {
                            @Override
                            public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                HttpCode ret = HttpCode.valueOf(message.status);
                                switch (ret) {
                                    case EC_SUCCESS:
                                        if (DeviceInfoInstance.getInstance().online != true) {
                                            DeviceInfoInstance.getInstance().online = true;
                                            EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                        }
                                        PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_COMMON);
                                        EventBus.getDefault().post(new EventManage.petModeChanged());
                                        break;
                                    case EC_OFFLINE:
                                        DeviceInfoInstance.getInstance().online = false;
                                        EventBus.getDefault().post(new EventManage.DeviceOffline());
                                        break;
                                }

                            }

                            @Override
                            public void onFail(Call<PetStatusBean> call, Throwable t) {

                            }
                        });
                    }
                },
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showFragment(1);
                        PetInfoInstance.getInstance().getPetLocation();
                    }
                }
        );
    }

    //todo 小米推送正常电量
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getCommonBattery(PushEventManage.commonBattery event) {
        DialogUtil.lowBatteryIsClosed = false;
        DialogUtil.superLowBatteryIsClosed = false;
        DialogUtil.closeAllDialog();
        EventBus.getDefault().post(new EventManage.notifyDeviceStateChange());
    }


    //todo 小米推送
    //设备低电量
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void batteryLowlevel(PushEventManage.batteryLowLevel event) {
        EventBus.getDefault().post(new EventManage.notifyDeviceStateChange());
        DialogUtil.showLowBatteryDialog(this);
    }

    //todo 小米推送
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void batterySuperLowlevel(PushEventManage.batterySuperLowLevel event) {
        EventBus.getDefault().post(new EventManage.notifyDeviceStateChange());
        DialogUtil.showSuperLowBatteryDialog(this);
    }

    //todo 小米推送
    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void receivePushDeviceOffline(PushEventManage.deviceOffline event) {
        batteryView.setDeviceOffline();
        DialogUtil.showDeviceOfflineDialog(this, "离线通知");
    }

    //todo 小米推送
    //设备重新在线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void receivePushDeviceOnline(PushEventManage.deviceOnline event) {
        EventBus.getDefault().post(new EventManage.notifyDeviceStateChange());
        DialogUtil.closeDeviceOfflineDialog();
    }

    //todo 小米推送
    //宠物离开家了
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void petNotHome(PushEventManage.petNotHome event) {
        String name = PetInfoInstance.getInstance().getNick();
        if ("".equals(name)) {
            name = "宠物";
        }
        if (PetInfoInstance.getInstance().PET_MODE == Constants.PET_STATUS_COMMON) {
            DialogUtil.showSafeCautionDialog(this,getString(R.string.dialog_outhome_title), "小毛球监测到" + name + "安全存在风险", getString(R.string.dialog_outhome_tab1), getString(R.string.dialog_outhome_tab2),getString(R.string.dialog_outhome_tab3),
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    },
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showFragment(1);
                        }
                    },
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (PetInfoInstance.getInstance().PET_MODE != Constants.PET_STATUS_FIND) {
                                MapInstance.getInstance().startFindPet();
                                MapInstance.getInstance().openTime = 1;
                                ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.GPS_OPEN).enqueue(new XMQCallback<PetStatusBean>() {
                                    @Override
                                    public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                        HttpCode ret = HttpCode.valueOf(message.status);
                                        switch (ret) {
                                            case EC_SUCCESS:
                                                if (DeviceInfoInstance.getInstance().online != true) {
                                                    DeviceInfoInstance.getInstance().online = true;
                                                    EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                                }
                                                PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_FIND);
                                                EventBus.getDefault().post(new EventManage.petModeChanged());
                                                PetInfoInstance.getInstance().getPetLocation();
                                                showFragment(1);
                                                break;
                                            case EC_OFFLINE:
                                                DeviceInfoInstance.getInstance().online = false;
                                                EventBus.getDefault().post(new EventManage.DeviceOffline());
                                                break;
                                        }

                                    }

                                    @Override
                                    public void onFail(Call<PetStatusBean> call, Throwable t) {

                                    }
                                });

                            } else {
                                showFragment(1);
                            }

                        }
                    }
            );
        }
    }

    //todo 小米推送
    //宠物到家了
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void PetAtHome(PushEventManage.petAtHome event) {
        if (PetInfoInstance.getInstance().PET_MODE != Constants.PET_STATUS_COMMON) {
            DialogUtil.showPetAtHomeDialog(this, getString(R.string.dialog_inhome_title),  getString(R.string.dialog_inhome_tab1), getString(R.string.dialog_inhome_tab2), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showFragment(1);
                            PetInfoInstance.getInstance().getPetLocation();

                        }
                    },
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.GPS_CLOSE).enqueue(new XMQCallback<PetStatusBean>() {
                                @Override
                                public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                    HttpCode ret = HttpCode.valueOf(message.status);
                                    switch (ret) {
                                        case EC_SUCCESS:
                                            if (DeviceInfoInstance.getInstance().online != true) {
                                                DeviceInfoInstance.getInstance().online = true;
                                                EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                            }
                                            PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_COMMON);
                                            EventBus.getDefault().post(new EventManage.petModeChanged());
                                            break;
                                        case EC_OFFLINE:
                                            DeviceInfoInstance.getInstance().online = false;
                                            EventBus.getDefault().post(new EventManage.DeviceOffline());
                                            break;
                                    }
                                }

                                @Override
                                public void onFail(Call<PetStatusBean> call, Throwable t) {

                                }
                            });

                        }
                    }
            );
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //进入主页
        SPUtil.putHome(true);

        initView();
        if (savedInstanceState != null) {
            mPetFragment = (PetFragment) getSupportFragmentManager().findFragmentByTag(PetFragment.class.getName());
            mLocateFragment = (LocateFragment) getSupportFragmentManager().findFragmentByTag(LocateFragment.class.getName());
            mMeFragment = (MeFrament) getSupportFragmentManager().findFragmentByTag(MeFrament.class.getName());
            getSupportFragmentManager().beginTransaction()
                    .show(mPetFragment)
                    .hide(mLocateFragment)
                    .hide(mMeFragment).commit();
        } else {
            mPetFragment = new PetFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mPetFragment, PetFragment.class.getName())
                    .show(mPetFragment).commit();
        }


        for (int i = 0; i < 3; i++) {
            mTabs[i] = findViewById(mTabID[i]);
            mTabs[i].setOnClickListener(this);
        }
        mHealthTabIcon.setSelected(true);

        EventBus.getDefault().register(this);

        showDeadlineDialog();

        //每1分钟调用一次

        if (sportThread == null) {
            sportThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new EventManage.notifyPetInfoChange());
                    }
                }
            });
            sportThread.start();
        }
        //每分钟调用一次
        getLocationWithOneMinute = true;
        if (locationThread == null) {
            locationThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (getLocationWithOneMinute && DeviceInfoInstance.getInstance().online) {
                            PetInfoInstance.getInstance().getPetLocation();
                        }
                    }
                }
            });
            locationThread.start();
        }
    }

    private void initView() {
        mHealthTabIcon = (ImageView) findViewById(R.id.main_tab_health);
        mLocateTabIcon = (ImageView) findViewById(R.id.main_tab_locate);
        mMeTabIcon = (ImageView) findViewById(R.id.main_tab_me);
        include_header = findViewById(R.id.include_header);
        sdv_header = (SimpleDraweeView) findViewById(R.id.sdv_header);

        tv_title= (TextView) findViewById(R.id.tv_title);

        batteryView = (BatteryView) findViewById(R.id.batteryView);
        batteryView.setActivity(this);
        sdv_header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PetInfoActivity.class);
                startActivity(intent);
            }
        });
        Uri uri = Uri.parse(PetInfoInstance.getInstance().getPackBean().logo_url);
        sdv_header.setImageURI(uri);
        //点击弹出电池
        batteryView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!DeviceInfoInstance.getInstance().online) {
                    ToastUtil.showTost("您的设备尚未开机！");
                    return;
                }
                if (DeviceInfoInstance.getInstance().battery_level > 1.0f) {
                    PetInfoInstance.getInstance().getPetLocation();
                }
                batteryView.pushBatteryDialog(DeviceInfoInstance.getInstance().battery_level,
                        DeviceInfoInstance.getInstance().lastGetTime);
            }
        });
    }

    private void hideAllTabIcon(FragmentTransaction transaction) {
        if (null != mPetFragment) {
            transaction.hide(mPetFragment);
        }
        if (null != mLocateFragment) {
            transaction.hide(mLocateFragment);
        }
        if (null != mMeFragment) {
            transaction.hide(mMeFragment);
        }
        mHealthTabIcon.setSelected(false);
        mLocateTabIcon.setSelected(false);
        mMeTabIcon.setSelected(false);
    }

    public static Thread locationThread;
    public static Thread sportThread;
    //是否每一分钟都发送位置信息
    public static volatile boolean getLocationWithOneMinute;
    public static int select_index;

    private void showFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllTabIcon(transaction);
        switch (index) {
            case 0:
//                setTitle("小毛球");
                tv_title.setText("小毛球");
                include_header.setVisibility(View.VISIBLE);
                if (mPetFragment == null) {
                    mPetFragment = new PetFragment();
                    transaction.add(R.id.fragment_container, mPetFragment, PetFragment.class.getName());
                }
                transaction.show(mPetFragment).commit();
                mHealthTabIcon.setSelected(true);
//                getLocationWithOneMinute = false;
                select_index = 0;
                break;
            case 1:
//                setTitle("小毛球");
                tv_title.setText("小毛球");
                include_header.setVisibility(View.INVISIBLE);
                if (mLocateFragment == null) {
                    mLocateFragment = new LocateFragment();
                    transaction.add(R.id.fragment_container, mLocateFragment, LocateFragment.class.getName());
                }
//                getLocationWithOneMinute = true;
                select_index = 1;
//                if (locationThread == null) {
//                    locationThread = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            while (true) {
//                                try {
//                                    Thread.sleep(60000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                if (getLocationWithOneMinute&&DeviceInfoInstance.getInstance().online) {
//                                    PetInfoInstance.getInstance().getPetLocation();
//                                }
//                            }
//                        }
//                    });
//                    locationThread.start();
//                }

                transaction
                        .show(mLocateFragment).commit();
                mLocateTabIcon.setSelected(true);
                break;
            case 2:
//                setTitle("我");
                tv_title.setText("我");
                include_header.setVisibility(View.INVISIBLE);
                if (mMeFragment == null) {
                    mMeFragment = new MeFrament();
                    transaction.add(R.id.fragment_container, mMeFragment, MeFrament.class.getName());
                }
//                getLocationWithOneMinute = false;
                select_index = 2;
                transaction
                        .show(mMeFragment).commit();
                mMeTabIcon.setSelected(true);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_health:
                showFragment(0);
                break;
            case R.id.tab_locate:
                showFragment(1);
                break;
            case R.id.tab_me:
                showFragment(2);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        DialogUtil.showTwoButtonDialog(this, getString(R.string.dialog_exit_app_title), getString(R.string.dialog_exit_app_tab1), getString(R.string.dialog_exit_app_tab2), new View.OnClickListener() {

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
    }

    //展示sim卡过期弹窗
    void showDeadlineDialog() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sim_deadline = DeviceInfoInstance.getInstance().packBean.sim_deadline;
        try {
            Date deadlineDate = sdf.parse(sim_deadline);
            int deadlineYear = deadlineDate.getYear();
            int deadlineMonth = deadlineDate.getMonth();
            int deadlineDay = deadlineDate.getDay();
            //当前时间
            Date today = new Date();
            int todayYear = today.getYear();
            int todayMonth = today.getMonth();
            int todayDay = today.getDay();
            int result = ((deadlineYear - todayYear) * 12 + (deadlineMonth - todayMonth)) * 30 + deadlineDay - todayDay;

            if (result < 0) {
                if (!SPUtil.getKey_Value(result + "")) {
                    showSimdeadedDialog(this, result);
                }
            } else if (result <= 30) {
                if (!SPUtil.getKey_Value(result + "")) {
                    showSimdeadingDialog(this, result);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //sim卡即将过期
    public void showSimdeadingDialog(Context context, final int result) {
        final Dialog dialog = new AppDialog(context, R.layout.dialog_simdeadline, -1, -2, 0, Gravity.CENTER);
        TextView tv_deadline_message = (TextView) dialog.findViewById(R.id.tv_deadline_message);
        tv_deadline_message.setText("您的追踪器内置专用sim卡服务还有" + result + "天过期，为了您宠物的安全，请及时充值。");
        Button bt_confirm = (Button) dialog.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                SPUtil.putKey_Value(result + "", true);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //sim卡已经过期
    public void showSimdeadedDialog(Context context, final int result) {
        final Dialog dialog = new AppDialog(context, R.layout.dialog_simdeadline, -1, -2, 0, Gravity.CENTER);
        TextView tv_deadline_message = (TextView) dialog.findViewById(R.id.tv_deadline_message);
        tv_deadline_message.setText("您的追踪器内置专用sim卡服务已过期，请马上充值，否则将被停机，停机后您的宠物将失去安全保护。");

        Button bt_confirm = (Button) dialog.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                SPUtil.putKey_Value(result + "", true);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void changeLocatefragment() {
        showFragment(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (select_index == 1) {
            getLocationWithOneMinute = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (select_index == 1) {
            getLocationWithOneMinute = false;
        }
    }
}
    
    

