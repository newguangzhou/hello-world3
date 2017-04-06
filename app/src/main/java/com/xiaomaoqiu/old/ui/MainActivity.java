package com.xiaomaoqiu.old.ui;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.old.dataCenter.DeviceInfo;
import com.xiaomaoqiu.old.dataCenter.UserMgr;
import com.xiaomaoqiu.old.ui.mainPages.HealthFragment;
import com.xiaomaoqiu.now.View.location.LocateFragment;
import com.xiaomaoqiu.old.ui.mainPages.MeFrament;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.hardware.BindDeviceActivity;
import com.xiaomaoqiu.old.widgets.BatteryView;
import com.xiaomaoqiu.old.widgets.FragmentActivityEx;

public class MainActivity extends FragmentActivityEx implements View.OnClickListener {
    private static final String LTAG = MainActivity.class.getSimpleName();

	public static MainActivity instance = null;
	private static int mTabID[] ={
            R.id.tab_health,
            R.id.tab_locate,
            R.id.tab_me};

	private ImageView mHealthTabIcon,mLocateTabIcon,mMeTabIcon;


	private View mTabs[] = {null,null,null};

	private HealthFragment mHealthFragment;
	private LocateFragment mLocateFragment;
	private MeFrament mMeFragment;

	@Override
	public int frameTemplate()
	{//没有标题栏
		return 0;
	}

	//todo 设备状态更新
	public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
		if(!deviceInfo.getDeviceExist()){
			BindDeviceActivity.skipTo(MainActivity.this);
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
					mHealthFragment=new HealthFragment();
					transaction.add(R.id.fragment_container,mHealthFragment);
				}
				mHealthTabIcon.setSelected(true);
				transaction.show(mHealthFragment);
				break;
			case 1:
				if(null == mLocateFragment){
					mLocateFragment=new LocateFragment();
					transaction.add(R.id.fragment_container,mLocateFragment);
				}
				mLocateTabIcon.setSelected(true);
				transaction.show(mLocateFragment);
				break;
			case 2:
				if(null == mMeFragment){
					mMeFragment=new MeFrament();
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
    
    

