package com.xiaomaoqiu.pet.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.HttpCode;
import com.xiaomaoqiu.pet.utils.HttpUtil;
import com.xiaomaoqiu.pet.widgets.ActivityEx;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class RegisterActivity extends ActivityEx {
    public static String TAG_PHONE_NUM = "phone_num";
    public static String TAG_PASSWORD = "password";

    EditText m_editPhone;
    EditText m_editVerifyCode;
    EditText m_editPsw1,m_editPsw2;

    TextView m_tvError;

    int      m_nWaitTime=60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.register_user));
        setContentView(R.layout.activity_register);

        m_editPhone = (EditText)findViewById(R.id.edit_phone);
        m_editVerifyCode = (EditText)findViewById(R.id.edit_verify_code);
        m_editPsw1 = (EditText)findViewById(R.id.edit_password1);
        m_editPsw2 = (EditText)findViewById(R.id.edit_password2);
        m_tvError =(TextView)findViewById(R.id.tv_error);

        final TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                m_tvError.setVisibility(View.INVISIBLE);
                return false;
            }
        };

        m_editPhone.setOnEditorActionListener( onEditorActionListener );
        m_editPsw1.setOnEditorActionListener(onEditorActionListener);
        m_editPsw2.setOnEditorActionListener(onEditorActionListener);

        m_editVerifyCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    m_editVerifyCode.setText("");
                }
            }
        });

    }

    private boolean checkInput()
    {

        try {
            String psw1 = m_editPsw1.getText().toString();
            String psw2 = m_editPsw2.getText().toString();

            if(psw1.length()<6)
            {
                throw new Exception(getString(R.string.error_password_length_too_low));
            }

            if (!psw1.equals(psw2)) {
                throw new Exception(getString(R.string.error_password_confirm));
            }
            if (m_editPhone.getText().toString().length()!=11)
            {
                throw new Exception(getString(R.string.error_phone_invalid));
            }

            if(m_editVerifyCode.getText().toString().isEmpty())
            {
                throw new Exception(getString(R.string.error_empty_verify_code));
            }
            return true;
        }catch(Exception e)
        {
            m_tvError.setText(e.getMessage());
            m_tvError.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public void onRegisterClick(View v)
    {
        if(!checkInput()) return;

        HttpUtil.get2("user.register",new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                HttpCode ret = HttpCode.valueOf(response.optInt("status",-1));
                if(ret==HttpCode.EC_SUCCESS)
                {//注册成功
                    Toast.makeText(m_tvError.getContext(),"注册成功!",Toast.LENGTH_LONG).show();
                    Intent data = new Intent();
                    data.putExtra(TAG_PHONE_NUM, m_editPhone.getText().toString());
                    data.putExtra(TAG_PASSWORD, m_editPsw1.getText().toString());
                    setResult(1, data);
                    finish();
                }else if(ret == HttpCode.EC_INVALID_ARGS)
                {//无效输入
                    m_tvError.setText(getString(R.string.error_invalid_argument));
                    m_tvError.setVisibility(View.VISIBLE);
                }else if(ret == HttpCode.EC_INVALID_VERIFY_CODE)
                {//验证码错误
                    m_tvError.setText(getString(R.string.error_invalid_verify_code));
                    m_tvError.setVisibility(View.VISIBLE);
                }else if(ret == HttpCode.EC_USER_ALREADY_REGISTERED)
                {//已经注册
                    m_tvError.setText(getString(R.string.error_user_already_register));
                    m_tvError.setVisibility(View.VISIBLE);
                }
            }
        },m_editPhone.getText().toString(),m_editPsw1.getText().toString(),m_editVerifyCode.getText().toString());
    }

    public void onFetchVerifyCodeClick(View v)
    {
        if (m_editPhone.getText().toString().length()!=11)
        {
            m_tvError.setText(getString(R.string.error_phone_invalid));
            m_tvError.setVisibility(View.VISIBLE);
            return;
        }
        HttpUtil.get2("user.get_verify_code",new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                HttpCode ret = HttpCode.valueOf(response.optInt("status",-1));
                if(ret==HttpCode.EC_SUCCESS)
                {//获取验证码成功，查收短信
                    Toast.makeText(RegisterActivity.this,"获取注册验证码成功，请查收短信",Toast.LENGTH_LONG).show();
                }else if(ret == HttpCode.EC_USER_ALREADY_REGISTERED)
                {
                    Toast.makeText(RegisterActivity.this,"获取注册验证码失败，已经注册",Toast.LENGTH_LONG).show();
                }else if(ret == HttpCode.EC_FREQ_LIMIT)
                {
                    Toast.makeText(RegisterActivity.this,"获取注册验证码失败，频率太高",Toast.LENGTH_LONG).show();
                }else
                {
                    Toast.makeText(RegisterActivity.this,"获取注册验证码失败，其它错误",Toast.LENGTH_LONG).show();
                }
            }
        },m_editPhone.getText(),1);
        v.setEnabled(false);

        m_nWaitTime = 60;
        final Button btn = (Button)v;
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(m_nWaitTime==0)
                {
                    btn.setEnabled(true);
                    btn.setText(getResources().getString(R.string.fetch_verify_code));
                }else
                {
                    m_nWaitTime--;
                    btn.setText(String.valueOf(m_nWaitTime));
                    btn.postDelayed(this, 1000);
                }
            }
        },1000);
            btn.setText(String.valueOf(m_nWaitTime));
    }


}
