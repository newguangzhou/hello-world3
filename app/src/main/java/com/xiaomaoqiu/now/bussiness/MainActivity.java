package com.xiaomaoqiu.now.bussiness;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseFragmentActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.location.LocateFragment;
import com.xiaomaoqiu.now.bussiness.pet.CheckIndex;
import com.xiaomaoqiu.now.bussiness.pet.PetFragment;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.MeFrament;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.now.view.DialogToast;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressLint("WrongConstant")
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener,CheckIndex {

    private static int mTabID[] = {
            R.id.tab_health,
            R.id.tab_locate,
            R.id.tab_me};

    private ImageView mHealthTabIcon, mLocateTabIcon, mMeTabIcon;


    private View mTabs[] = {null, null, null};

    private PetFragment mPetFragment;
    private LocateFragment mLocateFragment;
    private MeFrament mMeFragment;

    SimpleDraweeView sdv_header;
    BatteryView batteryView;//右上角的电池

    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
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

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.uploadImageSuccess event) {
        Uri uri = Uri.parse(PetInfoInstance.getInstance().getPackBean().logo_url);
        sdv_header.setImageURI(uri);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        if(savedInstanceState!=null){
            mPetFragment= (PetFragment) getSupportFragmentManager().findFragmentByTag(PetFragment.class.getName());
            mLocateFragment= (LocateFragment) getSupportFragmentManager().findFragmentByTag(LocateFragment.class.getName());
            mMeFragment= (MeFrament) getSupportFragmentManager().findFragmentByTag(MeFrament.class.getName());
            getSupportFragmentManager().beginTransaction()
                    .show(mPetFragment)
                    .hide(mLocateFragment)
                    .hide(mMeFragment).commit();
        }else{
            mPetFragment = new PetFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,mPetFragment,PetFragment.class.getName())
                    .show(mPetFragment).commit();
        }



        for (int i = 0; i < 3; i++) {
            mTabs[i] = findViewById(mTabID[i]);
            mTabs[i].setOnClickListener(this);
        }

        EventBus.getDefault().register(this);
    }

    private void initView() {
        mHealthTabIcon = (ImageView) findViewById(R.id.main_tab_health);
        mLocateTabIcon = (ImageView) findViewById(R.id.main_tab_locate);
        mMeTabIcon = (ImageView) findViewById(R.id.main_tab_me);
        sdv_header= (SimpleDraweeView) findViewById(R.id.sdv_header);
        batteryView = (BatteryView) findViewById(R.id.batteryView);

        sdv_header.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PetInfoActivity.class);
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

    private void showFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllTabIcon(transaction);
        switch (index) {
            case 0:
                if(mPetFragment==null){
                    mPetFragment = new PetFragment();
                    transaction.add(R.id.fragment_container,mPetFragment,PetFragment.class.getName());
                }
                transaction.show(mPetFragment).commit();
                mHealthTabIcon.setSelected(true);
                break;
            case 1:
                if(mLocateFragment==null){
                    mLocateFragment = new LocateFragment();
                    transaction.add(R.id.fragment_container,mLocateFragment,LocateFragment.class.getName());
                }
                transaction
                        .show(mLocateFragment).commit();
                mLocateTabIcon.setSelected(true);
                break;
            case 2:
                if(mMeFragment==null){
                    mMeFragment = new MeFrament();
                    transaction.add(R.id.fragment_container,mMeFragment,MeFrament.class.getName());
                }
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
    
    

