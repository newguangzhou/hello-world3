package com.xiaomaoqiu.pet.ui.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.HttpCode;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.ui.MainActivity;
import com.xiaomaoqiu.pet.ui.dialog.ContactServiceDialog;
import com.xiaomaoqiu.pet.utils.HttpUtil;
import com.xiaomaoqiu.pet.widgets.ActivityEx;

import org.apache.http.Header;
import org.json.JSONObject;
public class LoginActivity extends ActivityEx {
	EditText m_editPhone; // 帐号编辑框
    EditText m_editVerifyCode;
    private ProgressDialog mProgressBar;
    int     m_nWaitTime;

    private SharedPreferences m_Preferences;

    static String FILE_ACCOUNT = "account";
    static String FIELD_PHONE = "strPhone";
    static String FIELD_STATUS="status";

    public interface LoginHandler
    {
        void onLoginAck(HttpCode ret);
    }

    public boolean canGoBack()
    {
        return false;
    }

    @Override
    public int frameTemplate()
    {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.login));

        setContentView(R.layout.activity_login);

        m_editPhone = (EditText)findViewById(R.id.login_user_edit);
        m_editVerifyCode = (EditText)findViewById(R.id.login_edit_verify);

        m_Preferences  = getSharedPreferences(FILE_ACCOUNT, Context.MODE_PRIVATE);

        Boolean bLogin= m_Preferences.getBoolean(FIELD_STATUS, false);
        if(bLogin) {
            String strPhone = m_Preferences.getString(FIELD_PHONE, "");
            m_editPhone.setText(strPhone);
        }

        findViewById(R.id.btn_contact_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactServiceDialog dlg = new ContactServiceDialog(LoginActivity.this,R.style.MyDialogStyleBottom);
                dlg.show();
            }
        });
    }

    public static void doLogin(final String strPhone, final String strVerifyCode, final Context ctx,  final LoginHandler handler)
    {

        final int device_type=1;//android device
        String device_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);


        HttpUtil.get2("user.login", new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("http", "login:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {//登陆成功
                    long uid = response.optLong("uid", 0);
                    String strToken = response.optString("token");
                    LoginMgr.INSTANCE.login(uid, strToken, strPhone);
                }
                if(null != handler)
                {
                    handler.onLoginAck(ret);
                }
            }
        }, strPhone,strVerifyCode,device_type,device_id);

    }

    public void onLoginClick(View v) {
        if(!checkInputs()){
            return;
        }
        showDialog();
        final String strPhone= m_editPhone.getText().toString();

        SharedPreferences.Editor editor = m_Preferences.edit();
        editor.putString(FIELD_PHONE,strPhone);
        editor.putBoolean(FIELD_STATUS, false);
        editor.apply();

        doLogin(strPhone, m_editVerifyCode.getText().toString(), this, new LoginHandler() {
            @Override
            public void onLoginAck(HttpCode ret) {
                dismissDialog();
                if (ret == HttpCode.EC_SUCCESS) {//登陆成功
                    SharedPreferences.Editor editor = m_Preferences.edit();
                    editor.putBoolean(FIELD_STATUS, true);
                    editor.apply();
                    finish();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    String strErr = String.format(getString(R.string.login_failed), ret.getValue());
                    Toast.makeText(LoginActivity.this, strErr, Toast.LENGTH_LONG).show();
                }
            }
        });
      }

    /**
     * 检查是否输入完全
     * @return
     */
    private boolean checkInputs(){
        if(TextUtils.isEmpty(m_editPhone.getText().toString())){
            Toast.makeText(this,"请输入手机号！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(m_editVerifyCode.getText().toString())){
            Toast.makeText(this,"验证码为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onUserRegisterClick(View v)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == 1)
        {
            String strPhone = data.getStringExtra(RegisterActivity.TAG_PHONE_NUM);
            m_editPhone.setText(strPhone);
            //auto login
            onLoginClick(findViewById(R.id.login_btn_login));
        }
    }

    public void onFetchVerifyCodeClick(View v)
    {
        String strPhone = m_editPhone.getText().toString();
        if(strPhone.length()!=11)
        {
            Toast.makeText(this, R.string.tip_phone_format,Toast.LENGTH_LONG).show();
            return;
        }
        HttpUtil.get2("user.get_verify_code", new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {//成功
                    WaitForNextFetchCode(response.optInt("next_req_interval"));
                }else
                {
                    String msg = "获取验证码错误，status="+ret;
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        }, strPhone, 1);
    }

    void WaitForNextFetchCode(int nSecond)
    {
        final TextView btn = (TextView)findViewById(R.id.login_btn_sendVerify);
        m_nWaitTime = nSecond;
        btn.setText(String.valueOf(m_nWaitTime)+"S");
        btn.setEnabled(false);

        btn.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (m_nWaitTime == 1) {
                    btn.setEnabled(true);
                    btn.setText(getResources().getString(R.string.fetch_verify_code));
                } else {
                    m_nWaitTime--;
                    btn.setText(String.valueOf(m_nWaitTime)+"S");
                    btn.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void showDialog(){
        if(null == mProgressBar){
            mProgressBar=new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
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

    private void dismissDialog(){
        if(null == mProgressBar){
            return;
        }
        mProgressBar.dismiss();
    }

}
