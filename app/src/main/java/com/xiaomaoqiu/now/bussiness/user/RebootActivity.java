package com.xiaomaoqiu.now.bussiness.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.BaseWebViewActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.InitMapLocationActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by long on 2017/6/4.
 */

public class RebootActivity extends BaseActivity implements LogoutView {
    public int frameTemplate() {//没有标题栏
        return 0;
    }

    TextView btn_reboot;
    View btn_go_back;

//    CheckBox cb_usebook;
    View tv_userbook;
    View fl_click;
    View iv_ischecked;

    LoginPresenter loginPresenter;
    boolean isCheckd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //未进入主页
        SPUtil.putHome(false);

        setContentView(R.layout.activity_reboot);
        btn_go_back=findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetAppLike.mcontext, InitMapLocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PetAppLike.mcontext.startActivity(intent);
            }
        });
        btn_reboot = (TextView) this.findViewById(R.id.btn_reboot);
        btn_reboot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                boolean isCheckd=cb_usebook.isChecked();
                isCheckd=iv_ischecked.isShown();
                if(!isCheckd){
                    ToastUtil.showTost("请阅读并同意《用户协议与隐私政策》");
                    return;
                }
//                DeviceInfoInstance.getInstance().rebootDevice();
                UserInstance.getInstance().agreePolicy();
            }
        });
//        cb_usebook= (CheckBox) findViewById(R.id.cb_usebook);
        fl_click=findViewById(R.id.fl_click);
        fl_click.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (isCheckd){
                    iv_ischecked.setVisibility(View.GONE);
                    isCheckd=false;
                }else{
                    isCheckd=true;
                    iv_ischecked.setVisibility(View.VISIBLE);

                }
            }
        });
        iv_ischecked=findViewById(R.id.iv_ischecked);
        tv_userbook=findViewById(R.id.tv_userbook);
        tv_userbook.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //跳转到用户协议
                Intent intent=new Intent(RebootActivity.this, BaseWebViewActivity.class);
                intent.putExtra("web_url","http://www.xiaomaoqiu.com/proto_user.html");
                startActivity(intent);
            }
        });

        isCheckd=false;
        iv_ischecked.setVisibility(View.GONE);
        EventBus.getDefault().register(this);
        loginPresenter = new LoginPresenter(this);
    }

    //设备重启指令发送
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceReboot(EventManage.deviceReboot event) {

        Intent intent=new Intent(RebootActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void success() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DialogUtil.showTwoButtonDialog(RebootActivity.this,"确认退出登录？","取消","确定",new View.OnClickListener(){

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
//        DialogToast.createDialogWithTwoButton(RebootActivity.this, "确认退出登录？", new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        loginPresenter.logout();
//                    }
//                }
//        );
    }
}
