package com.xiaomaoqiu.now.bussiness;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseFragmentActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.location.LocateFragment;
import com.xiaomaoqiu.now.bussiness.pet.CheckIndex;
import com.xiaomaoqiu.now.bussiness.pet.PetFragment;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.MeFrament;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.main.MapInstance;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.now.view.DialogToast;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    }

    //设备状态更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceInfoChanged(EventManage.notifyDeviceStateChange event) {
        if (DeviceInfoInstance.getInstance().battery_level < 0) {
            ToastUtil.showTost("您的设备尚未开机！");
            batteryView.setDeviceOffline();
            return;
        }
        batteryView.setBatteryLevel(DeviceInfoInstance.getInstance().battery_level,
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
        DialogUtil.showTwoButtonDialog(this, "已找到宠物?", "NO", "YES", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                },
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 2).enqueue(new XMQCallback<PetStatusBean>() {
                            @Override
                            public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                MapInstance.getInstance().setGPSState(false);
                                EventBus.getDefault().post(new EventManage.GPS_CHANGE());


                                switch (message.pet_status) {
                                    case Constants.PET_STATUS_COMMON:
                                        if (!PetInfoInstance.getInstance().getAtHome()) {
                                            PetInfoInstance.getInstance().setAtHome(true);
                                        }
                                        break;
                                    case Constants.PET_STATUS_WALK:
                                        if (PetInfoInstance.getInstance().getAtHome()) {
                                            PetInfoInstance.getInstance().setAtHome(false);
                                        }
                                        break;
                                    default:
                                        if (!PetInfoInstance.getInstance().getAtHome()) {
                                            PetInfoInstance.getInstance().setAtHome(true);
                                        }
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

    //todo 小米推送正常电量
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getCommonBattery(PushEventManage.commonBattery event) {
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
        DialogUtil.showDeviceOfflineDialog(this);
    }

    //todo 小米推送
    //设备重新在线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void receivePushDeviceOnline(PushEventManage.deviceOnline event) {
        EventBus.getDefault().post(new EventManage.notifyDeviceStateChange());
        DialogUtil.closeDeviceOfflineDialog();
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
    }

    private void initView() {
        mHealthTabIcon = (ImageView) findViewById(R.id.main_tab_health);
        mLocateTabIcon = (ImageView) findViewById(R.id.main_tab_locate);
        mMeTabIcon = (ImageView) findViewById(R.id.main_tab_me);
        include_header = findViewById(R.id.include_header);
        sdv_header = (SimpleDraweeView) findViewById(R.id.sdv_header);
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
                if (DeviceInfoInstance.getInstance().battery_level < 0) {
                    ToastUtil.showTost("您的设备尚未开机！");
                    return;
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
    //是否每一分钟都发送位置信息
    public static volatile boolean getLocationWithOneMinute;
    //是否超过次数限制
    public static volatile int getLocationTime;

    private void showFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllTabIcon(transaction);
        switch (index) {
            case 0:
                include_header.setVisibility(View.VISIBLE);
                if (mPetFragment == null) {
                    mPetFragment = new PetFragment();
                    transaction.add(R.id.fragment_container, mPetFragment, PetFragment.class.getName());
                }
                transaction.show(mPetFragment).commit();
                mHealthTabIcon.setSelected(true);
                getLocationWithOneMinute = false;
                getLocationTime = 0;
                break;
            case 1:
                include_header.setVisibility(View.INVISIBLE);
                if (mLocateFragment == null) {
                    mLocateFragment = new LocateFragment();
                    transaction.add(R.id.fragment_container, mLocateFragment, LocateFragment.class.getName());
                }
                getLocationWithOneMinute = true;
                getLocationTime = 0;
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
                                if (getLocationWithOneMinute && (getLocationTime++ <= 10)) {
                                    PetInfoInstance.getInstance().getPetLocation();
                                }
                            }
                        }
                    });
                    locationThread.start();
                }

                transaction
                        .show(mLocateFragment).commit();
                mLocateTabIcon.setSelected(true);
                break;
            case 2:
                include_header.setVisibility(View.INVISIBLE);
                if (mMeFragment == null) {
                    mMeFragment = new MeFrament();
                    transaction.add(R.id.fragment_container, mMeFragment, MeFrament.class.getName());
                }
                getLocationWithOneMinute = false;
                getLocationTime = 0;
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
        DialogToast.createDialogWithTwoButton(this, "确定要退出小毛球吗？", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );


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
}
    
    

