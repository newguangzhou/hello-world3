package com.xiaomaoqiu.now.bussiness.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitBindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.pet.AddPetInfoActivity;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.view.ContactServiceDialog;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


@SuppressLint("WrongConstant")
public class LoginActivity extends BaseActivity implements LoginView {
    EditText m_editPhone; // 帐号编辑框
    EditText m_editVerifyCode;
    private ProgressDialog mProgressBar;
    private Button login_btn_sendVerify;//发送验证码按钮
    private Button login_btn_login;//登录按钮
    private View btn_contact_service;//联系客服

    private LoginPresenter loginPresenter;

    int messageWaitTime;

    public boolean canGoBack() {
        return false;
    }

    @Override
    public int frameTemplate() {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("longtianlove", "activity-login");
        initView();
        initListener();
        initData();
        loginPresenter = new LoginPresenter(this);
        EventBus.getDefault().register(this);
    }

    void initView() {
        setTitle(getResources().getString(R.string.login));

        setContentView(R.layout.activity_login);

        m_editPhone = (EditText) findViewById(R.id.login_user_edit);
        m_editVerifyCode = (EditText) findViewById(R.id.login_edit_verify);
        login_btn_sendVerify = (Button) findViewById(R.id.login_btn_sendVerify);

        login_btn_login = (Button) findViewById(R.id.login_btn_login);
        btn_contact_service = findViewById(R.id.btn_contact_service);
    }

    void initListener() {
        m_editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    login_btn_sendVerify.setEnabled(true);
                }else{
                    login_btn_sendVerify.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        m_editVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    login_btn_login.setEnabled(true);
                }else{
                    login_btn_login.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        login_btn_sendVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPhone = m_editPhone.getText().toString();
//                if (strPhone.length() != 11) {
//                    Toast.makeText(LoginActivity.this, R.string.tip_phone_format, Toast.LENGTH_LONG).show();
//                    return;
//                }

                loginPresenter.getVerifyCode(strPhone, Constants.DEVICE_TYPE);

            }
        });

        login_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInputs()) {
                    return;
                }
                String strPhone = m_editPhone.getText().toString();
                String verifyCode = m_editVerifyCode.getText().toString();
                String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                loginPresenter.login(strPhone, verifyCode, Constants.DEVICE_TYPE, device_id);


            }
        });

        btn_contact_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactServiceDialog dlg = new ContactServiceDialog(LoginActivity.this, R.style.MyDialogStyleBottom);
                dlg.show();
            }
        });
    }

    void initData() {
        String strPhone = SPUtil.getPhoneNumber();
        m_editPhone.setText(strPhone);
    }


    /**
     * 检查是否输入完全
     *
     * @return
     */
    private boolean checkInputs() {
        if (TextUtils.isEmpty(m_editPhone.getText().toString())) {
            Toast.makeText(this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(m_editVerifyCode.getText().toString())) {
            Toast.makeText(this, "验证码为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void showDialog() {
        if (null == mProgressBar) {
            mProgressBar = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    return true;
                }
            });
            mProgressBar.setMessage("正在登录，请稍候....");
            mProgressBar.setCancelable(false);
        }
        mProgressBar.show();
    }

    public void dismissDialog() {
        if (null == mProgressBar) {
            return;
        }
        mProgressBar.dismiss();
    }

    @Override
    public void getVerifyNextTime(int nSecond) {
        WaitForNextFetchCode(nSecond);

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void next(EventManage.getUserInfoEvent event) {
        Intent intent = new Intent();
        if (!(UserInstance.getInstance().pet_id > 0)) {
            intent.setClass(LoginActivity.this, AddPetInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei)) {
            intent.setClass(LoginActivity.this, InitBindDeviceActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            intent.setClass(LoginActivity.this, InitWifiListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        intent.setClass(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();


        EventBus.getDefault().unregister(this);


    }

    void WaitForNextFetchCode(int nSecond) {
        final TextView btn = (TextView) findViewById(R.id.login_btn_sendVerify);
        messageWaitTime = nSecond;
        btn.setText(String.valueOf(messageWaitTime) + "S");
        btn.setEnabled(false);

        btn.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (messageWaitTime == 1) {
                    btn.setEnabled(true);
                    btn.setText(getResources().getString(R.string.fetch_verify_code));
                } else {
                    messageWaitTime--;
                    btn.setText(String.valueOf(messageWaitTime) + "S");
                    btn.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}