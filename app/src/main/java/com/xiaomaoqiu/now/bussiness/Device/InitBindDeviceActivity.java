package com.xiaomaoqiu.now.bussiness.Device;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.SplashActivity;
import com.xiaomaoqiu.now.bussiness.pet.AddPetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.LoginPresenter;
import com.xiaomaoqiu.now.bussiness.user.LogoutView;
import com.xiaomaoqiu.now.bussiness.user.RebootActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.view.ContactServiceDialog;
import com.xiaomaoqiu.now.view.DialogToast;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.hardware.ZXingActivity;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/31.
 */

public class InitBindDeviceActivity extends BaseActivity  implements LogoutView {

    public static final int REQUEST_SWEEP_CODE=0;

    private EditText inputImei;
    private Button sweepBt;

    LoginPresenter loginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //未进入主页
        SPUtil.putHome(false);


        setTitle(getString(R.string.bind_device));
        setContentView(R.layout.activity_bind_device);
        setNextView("下一步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DoubleClickUtil.isFastMiniDoubleClick()) {
                    return;
                }
                if(inputImei == null || TextUtils.isEmpty(inputImei.getText().toString())){
                    showToast("请输入IMEI码！");
                    return;
                }
                String imei=inputImei.getText().toString();
                DeviceInfoInstance.getInstance().bindDevice(imei);
            }
        });
        initView();
        EventBus.getDefault().register(this);
        loginPresenter=new LoginPresenter(this);

    }

    private void initView(){
        View btnGoBack = findViewById(R.id.btn_go_back);
        btnGoBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogToast.createDialogWithTwoButton(InitBindDeviceActivity.this, "确认退出登录？", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                loginPresenter.logout();
                            }
                        }
                );
            }
        });
        inputImei=(EditText)findViewById(R.id.bind_device_input_imei);
        sweepBt=(Button)findViewById(R.id.bind_device_button);
        sweepBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZXingActivity.skipToAsResult(InitBindDeviceActivity.this,REQUEST_SWEEP_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_SWEEP_CODE == requestCode && RESULT_OK ==resultCode){
            parseSweepResult(data);
        }
    }

    private void parseSweepResult(Intent data){
        if(null == data){
            return;
        }
        Bundle bundle=data.getExtras();
        if(null == bundle){
            return;
        }
        if(bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS){
            //解析成功
            String Imei=bundle.getString(CodeUtils.RESULT_STRING);
            if(isZXresultCorrect(Imei)) {
                DeviceInfoInstance.getInstance().bindDevice(Imei);
            }else{
                showToast("IMEI码错误，请正确扫码！");
            }
        }
    }

    /**
     * 检查IMEI是否符合规范
     * @param result
     * @return
     */
    private boolean isZXresultCorrect(String result){
        String regex = "([a-zA-Z0-9]{15})";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(result);
        return m.matches();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public  void toDeviceActivity(EventManage.bindDeviceSuccess event){
        EventBus.getDefault().unregister(this);
        if (!(UserInstance.getInstance().pet_id > 0)) {
            Intent intent=new Intent();
            intent.setClass(InitBindDeviceActivity.this, AddPetInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            Intent intent = new Intent(this, InitWifiListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(UserInstance.getInstance().has_reboot==0){
            Intent intent=new Intent(this, RebootActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        Intent intent=new Intent(InitBindDeviceActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(0, 0);
        startActivity(intent);

    }
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void deviceAlreadyBind(EventManage.deviceAlreadyBind event){
        DialogUtil.showDeviceAlreadyBindedDialog(InitBindDeviceActivity.this,event.old_account);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void success() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    }
