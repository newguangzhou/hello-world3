package com.xiaomaoqiu.now.bussiness.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitBindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.InitMapLocationActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.SplashActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.AddPetInfoActivity;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dragon on 2017/9/3.
 */

public class ConfirmBatteryActivity extends BaseActivity implements LogoutView{
    View tv_logout;
    View tv_next;


    LoginPresenter loginPresenter;
    //设备重启指令发送
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceReboot(EventManage.PolicyAgree event) {

        Intent intent = new Intent();
        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei)||"-1".equals(UserInstance.getInstance().device_imei)) {
            intent.setClass(ConfirmBatteryActivity.this, InitBindDeviceActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (!(UserInstance.getInstance().pet_id > 0)) {
            intent.setClass(ConfirmBatteryActivity.this, AddPetInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            intent.setClass(ConfirmBatteryActivity.this, InitWifiListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(UserInstance.getInstance().latitude==-1){
            intent.setClass(ConfirmBatteryActivity.this, InitMapLocationActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(UserInstance.getInstance().agree_policy ==0){
            intent.setClass(ConfirmBatteryActivity.this, RebootActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        intent.setClass(ConfirmBatteryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public int frameTemplate() {//没有标题栏
        return 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //未进入主页
        SPUtil.putHome(false);
        setContentView(R.layout.activity_confirm_battery);

        tv_logout = this.findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogUtil.showTwoButtonDialog(ConfirmBatteryActivity.this,getString(R.string.dialog_exit_login_title),getString(R.string.dialog_exit_login_tab1),getString(R.string.dialog_exit_login_tab2),new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {

                            }
                        },

                        new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                loginPresenter.logout();
                            }
                        }
                );


//                DialogToast.createDialogWithTwoButton(InitBindDeviceActivity.this, "确认退出登录？", new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                loginPresenter.logout();
//                            }
//                        }
//                );
            }
        });
        tv_next = this.findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {

                                       @Override
                                       public void onClick(View v) {


                                           //todo 需要进行判断
                                           UserInstance.getInstance().agreePolicy();
                                       }
                                   });

        EventBus.getDefault().register(this);
        loginPresenter = new LoginPresenter(this);
    }
    public void success() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                new View.OnClickListener(){

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



}
