package com.xiaomaoqiu.now.bussiness;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseFragmentActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.location.LocateFragment;
import com.xiaomaoqiu.now.bussiness.pet.PetFragment;
import com.xiaomaoqiu.now.bussiness.user.MeFrament;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressLint("WrongConstant")
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

	private static int mTabID[] ={
            R.id.tab_health,
            R.id.tab_locate,
            R.id.tab_me};

	private ImageView mHealthTabIcon,mLocateTabIcon,mMeTabIcon;


	private View mTabs[] = {null,null,null};

	private PetFragment mPetFragment;
	private LocateFragment mLocateFragment;
	private MeFrament mMeFragment;

	BatteryView batteryView;//右上角的电池

	@Override
	public int frameTemplate()
	{//没有标题栏
		return 0;
	}

	//todo 设备状态更新
	@Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
	public void onDeviceInfoChanged(EventManage.notifyDeviceStateChange event) {
//		if(!DeviceInfoInstance.getInstance().isDeviceExist){
//			Intent intent = new Intent(this, BindDeviceActivity.class);
//			startActivity(intent);
//			return;
//		}
		batteryView.setBatteryLevel(DeviceInfoInstance.getInstance().battery_level,
				DeviceInfoInstance.getInstance().lastGetTime);
	}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		initView();
		showFragment(0);
		for(int i=0;i<3;i++)
		{
			mTabs[i]=findViewById(mTabID[i]);
			mTabs[i].setOnClickListener(this);
		}

		EventBus.getDefault().register(this);
	}

	private void initView(){
		mHealthTabIcon=(ImageView)findViewById(R.id.main_tab_health);
		mLocateTabIcon=(ImageView)findViewById(R.id.main_tab_locate);
		mMeTabIcon=(ImageView)findViewById(R.id.main_tab_me);
		batteryView = (BatteryView) findViewById(R.id.batteryView);

		//点击弹出电池
		batteryView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				batteryView.pushBatteryDialog(DeviceInfoInstance.getInstance().battery_level,
						DeviceInfoInstance.getInstance().lastGetTime);
			}
		});
	}

	private void hideAllFragment(FragmentTransaction transaction){
		if(null != mPetFragment){
			transaction.hide(mPetFragment);
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
				if(null == mPetFragment){
					mPetFragment =new PetFragment();
					transaction.add(R.id.fragment_container, mPetFragment);
				}
				mHealthTabIcon.setSelected(true);
				transaction.show(mPetFragment);
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
		if(null != mPetFragment && !mPetFragment.isHidden() && mPetFragment.isDialogShowing()){
			mPetFragment.hideDialog();
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
    
    

