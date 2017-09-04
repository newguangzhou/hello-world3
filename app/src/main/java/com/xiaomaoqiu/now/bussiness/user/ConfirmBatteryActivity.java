package com.xiaomaoqiu.now.bussiness.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.BaseWebViewActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitBindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.InitMapLocationActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.SplashActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.AddPetInfoActivity;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dragon on 2017/9/3.
 */

public class ConfirmBatteryActivity extends BaseActivity implements LogoutView {
    View tv_logout;
    View tv_next;


    //亮灯
    View fl_click_battery_full;
    View iv_ischecked_battery_none;
    View iv_ischecked_battery_full;
    boolean check_battery = false;
    //开机
    View fl_click_power_on;
    View iv_ischecked_power_off;
    View iv_ischecked_power_on;
    boolean check_poweron = false;
    //是否在家
    View fl_click_at_home;
    View iv_ischecked_at_home_not;
    View iv_ischecked_at_home;
    boolean check_athome = false;
    //用户协议
    View fl_click_provicy;
    View iv_ischecked_provicy_not;
    View iv_ischecked_provicy;
    boolean check_provicy = false;

    View tv_userbook_provicy;


    LoginPresenter loginPresenter;

    //设备重启指令发送
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceReboot(EventManage.PolicyAgree event) {

        Intent intent = new Intent();
        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei) || "-1".equals(UserInstance.getInstance().device_imei)) {
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
        if (UserInstance.getInstance().latitude == -1) {
            intent.setClass(ConfirmBatteryActivity.this, InitMapLocationActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (UserInstance.getInstance().agree_policy == 0) {
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
                DialogUtil.showTwoButtonDialog(ConfirmBatteryActivity.this, getString(R.string.dialog_exit_login_title), getString(R.string.dialog_exit_login_tab1), getString(R.string.dialog_exit_login_tab2), new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                            }
                        },

                        new View.OnClickListener() {

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

                if (check_battery && check_poweron && check_athome && check_provicy) {
                    //todo 需要进行判断
                    UserInstance.getInstance().agreePolicy();
                }else{
                    ToastUtil.showAtCenter("请确定以下内容");
                }
            }
        });


        initView();

        EventBus.getDefault().register(this);
        loginPresenter = new LoginPresenter(this);
    }

    void initView() {
        fl_click_battery_full = this.findViewById(R.id.fl_click_battery_full);
        fl_click_battery_full.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (check_battery) {
                    check_battery = false;
                    iv_ischecked_battery_none.setVisibility(View.VISIBLE);
                    iv_ischecked_battery_full.setVisibility(View.GONE);
                } else {
                    check_battery = true;
                    iv_ischecked_battery_none.setVisibility(View.GONE);
                    iv_ischecked_battery_full.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_ischecked_battery_none = this.findViewById(R.id.iv_ischecked_battery_none);
        iv_ischecked_battery_full = this.findViewById(R.id.iv_ischecked_battery_full);

        //-------------
        fl_click_power_on = this.findViewById(R.id.fl_click_power_on);
        fl_click_power_on.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (check_poweron) {
                    check_poweron = false;
                    iv_ischecked_power_off.setVisibility(View.VISIBLE);
                    iv_ischecked_power_on.setVisibility(View.GONE);
                } else {
                    check_poweron = true;
                    iv_ischecked_power_off.setVisibility(View.GONE);
                    iv_ischecked_power_on.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_ischecked_power_off = this.findViewById(R.id.iv_ischecked_power_off);
        iv_ischecked_power_on = this.findViewById(R.id.iv_ischecked_power_on);

        //-------------
        fl_click_at_home = this.findViewById(R.id.fl_click_at_home);
        fl_click_at_home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (check_athome) {
                    check_athome = false;
                    iv_ischecked_at_home_not.setVisibility(View.VISIBLE);
                    iv_ischecked_at_home.setVisibility(View.GONE);
                } else {
                    check_athome = true;
                    iv_ischecked_at_home_not.setVisibility(View.GONE);
                    iv_ischecked_at_home.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_ischecked_at_home_not = this.findViewById(R.id.iv_ischecked_at_home_not);
        iv_ischecked_at_home = this.findViewById(R.id.iv_ischecked_at_home);


        fl_click_provicy = this.findViewById(R.id.fl_click_provicy);
        fl_click_provicy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (check_provicy) {
                    check_provicy = false;
                    iv_ischecked_provicy_not.setVisibility(View.VISIBLE);
                    iv_ischecked_provicy.setVisibility(View.GONE);
                } else {
                    check_provicy = true;
                    iv_ischecked_provicy_not.setVisibility(View.GONE);
                    iv_ischecked_provicy.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_ischecked_provicy_not = this.findViewById(R.id.iv_ischecked_provicy_not);
        iv_ischecked_provicy = this.findViewById(R.id.iv_ischecked_provicy);
        tv_userbook_provicy=this.findViewById(R.id.tv_userbook_provicy);
        tv_userbook_provicy.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmBatteryActivity.this, BaseWebViewActivity.class);
                intent.putExtra("title","用户协议与隐私政策");
                intent.putExtra("web_url", "https://www.xiaomaoqiu.com/proto_user.html");
                startActivity(intent);
            }
        });
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
        DialogUtil.showTwoButtonDialog(this, getString(R.string.dialog_exit_app_title), getString(R.string.dialog_exit_app_tab1), getString(R.string.dialog_exit_login_tab2), new View.OnClickListener() {

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


}
