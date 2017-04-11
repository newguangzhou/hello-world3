package com.xiaomaoqiu.now.bussiness.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.util.SPUtil;

import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.old.ui.dialog.ContactServiceDialog;
import com.xiaomaoqiu.pet.R;


@SuppressLint("WrongConstant")
public class LoginActivity extends BaseActivity implements LoginView {
    EditText m_editPhone; // 帐号编辑框
    EditText m_editVerifyCode;
    private ProgressDialog mProgressBar;
    private View login_btn_sendVerify;//发送验证码按钮
    private View login_btn_login;//登录按钮
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
        initView();
        initListener();
        initData();
        loginPresenter = new LoginPresenter(this);
    }

    void initView() {
        setTitle(getResources().getString(R.string.login));

        setContentView(R.layout.activity_login);

        m_editPhone = (EditText) findViewById(R.id.login_user_edit);
        m_editVerifyCode = (EditText) findViewById(R.id.login_edit_verify);
        login_btn_sendVerify = findViewById(R.id.login_btn_sendVerify);

        login_btn_login = findViewById(R.id.login_btn_login);
        btn_contact_service = findViewById(R.id.btn_contact_service);
    }

    void initListener() {
        login_btn_sendVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPhone = m_editPhone.getText().toString();
                if (strPhone.length() != 11) {
                    Toast.makeText(LoginActivity.this, R.string.tip_phone_format, Toast.LENGTH_LONG).show();
                    return;
                }
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
        Boolean bLogin = SPUtil.getLoginStatus();
        if (bLogin) {

            //todo 如果已经登录是什么样的业务逻辑,现在先处理成直接调转到主页
            Intent intent =new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
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

    @Override
    public void LoginSuccess() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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