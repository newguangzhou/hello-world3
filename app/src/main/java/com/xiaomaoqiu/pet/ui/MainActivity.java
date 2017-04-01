package com.xiaomaoqiu.pet.ui;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.DeviceInfo;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.ui.mainPages.PageHealth;
import com.xiaomaoqiu.pet.ui.mainPages.PageLocate1;
import com.xiaomaoqiu.pet.ui.mainPages.PageMe;
import com.xiaomaoqiu.pet.ui.mainPages.pageMe.hardware.BindDevice;
import com.xiaomaoqiu.pet.widgets.BatteryView;
import com.xiaomaoqiu.pet.widgets.FragmentActivityEx;

public class MainActivity extends FragmentActivityEx implements DeviceInfo.Callback_DeviceInfo, View.OnClickListener {
    private static final String LTAG = MainActivity.class.getSimpleName();

	public static MainActivity instance = null;
	private static int mTabID[] ={
            R.id.tab_health,
            R.id.tab_locate,
            R.id.tab_me};

	private ImageView mHealthTabIcon,mLocateTabIcon,mMeTabIcon;


	private View mTabs[] = {null,null,null};

	private PageHealth mHealthFragment;
	private PageLocate1 mLocateFragment;
	private PageMe mMeFragment;

	@Override
	public int frameTemplate()
	{//没有标题栏
		return 0;
	}

	@Override
	public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
		if(!deviceInfo.getDeviceExist()){
			BindDevice.skipTo(MainActivity.this);
			return;
		}
		BatteryView batteryView = (BatteryView) findViewById(R.id.batteryView);
		batteryView.setBatteryLevel(deviceInfo.getBatteryLevel(),deviceInfo.getLastGetTime());
	}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    instance = this;
		initView();
		showFragment(0);
		for(int i=0;i<3;i++)
		{
			mTabs[i]=findViewById(mTabID[i]);
			mTabs[i].setOnClickListener(this);
		}

        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);

		UserMgr.INSTANCE.queryPetInfo();
	}

	private void initView(){
		mHealthTabIcon=(ImageView)findViewById(R.id.main_tab_health);
		mLocateTabIcon=(ImageView)findViewById(R.id.main_tab_locate);
		mMeTabIcon=(ImageView)findViewById(R.id.main_tab_me);
	}

	private void hideAllFragment(FragmentTransaction transaction){
		if(null != mHealthFragment){
			transaction.hide(mHealthFragment);
		}
		if(null != mLocateFragment){
			transaction.hide(mLocateFragment);
		}
		if(null != mMeFragment){
			transaction.hide(mMeFragment);
		}
		mHealthTabIcon.setSelected(false);
		mLocateTabIcon.setSelected(false);
		mMeTabIcon.setSelected(false);
	}

	private void showFragment(int index){
		FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
		hideAllFragment(transaction);
		switch (index){
			case 0:
				if(null == mHealthFragment){
					mHealthFragment=new PageHealth();
					transaction.add(R.id.fragment_container,mHealthFragment);
				}
				mHealthTabIcon.setSelected(true);
				transaction.show(mHealthFragment);
				break;
			case 1:
				if(null == mLocateFragment){
					mLocateFragment=new PageLocate1();
					transaction.add(R.id.fragment_container,mLocateFragment);
				}
				mLocateTabIcon.setSelected(true);
				transaction.show(mLocateFragment);
				break;
			case 2:
				if(null == mMeFragment){
					mMeFragment=new PageMe();
					transaction.add(R.id.fragment_container,mMeFragment);
				}
				mMeTabIcon.setSelected(true);
				transaction.show(mMeFragment);
				break;
		}
		transaction.commit();

	}


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
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


	long mBackClickTime=-1;
	@Override
	public void onBackPressed() {
		if(null != mHealthFragment && !mHealthFragment.isHidden() && mHealthFragment.isDialogShowing()){
			mHealthFragment.hideDialog();
			mBackClickTime=-1;
			return;
		}
		if(mBackClickTime == -1)
		{
			mBackClickTime = System.currentTimeMillis();
			Toast.makeText(this, R.string.exit_delay,Toast.LENGTH_LONG).show();
		}else
		{
			long clickInterval = System.currentTimeMillis() - mBackClickTime;
			if(clickInterval>2000)
			{
				mBackClickTime = System.currentTimeMillis();
				Toast.makeText(this, R.string.exit_delay,Toast.LENGTH_LONG).show();
			}else
			{
				finish();
			}
		}
	}

}
    
    

