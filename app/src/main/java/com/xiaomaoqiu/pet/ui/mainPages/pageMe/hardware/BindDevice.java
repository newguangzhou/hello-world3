package com.xiaomaoqiu.pet.ui.mainPages.pageMe.hardware;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.HttpCode;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.ui.mainPages.pageMe.HardwareActivity;
import com.xiaomaoqiu.pet.utils.HttpUtil;
import com.xiaomaoqiu.pet.widgets.ActivityEx;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/31.
 */

public class BindDevice extends ActivityEx {

    public static final int REQUEST_SWEEP_CODE=0;

    private EditText inputImei;
    private Button sweepBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.bind_device));
        setContentView(R.layout.activity_bind_device);
        setNextView("下一步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputImei == null || TextUtils.isEmpty(inputImei.getText().toString())){
                    showToast("请输入IMEI码！");
                    return;
                }
                BindDeciveHttp(inputImei.getText().toString());
            }
        });
        initView();
    }

    private void initView(){
        inputImei=(EditText)findViewById(R.id.bind_device_input_imei);
        sweepBt=(Button)findViewById(R.id.bind_device_button);
        sweepBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZXingActivity.skipToAsResult(BindDevice.this,REQUEST_SWEEP_CODE);
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
                BindDeciveHttp(Imei);
            }else{
                showToast("IMEI 码错误，请正确扫码！");
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

    /**
     * 绑定设备
     * @param ImeiId
     */
    private void BindDeciveHttp(String ImeiId){
        HttpUtil.get2("device.add_device_info",new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("http", "device.add_device_info:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    UserMgr.INSTANCE.queryPetInfo();
                    HardwareActivity.skip(BindDevice.this);
                    finish();
                }
                else{
                    showToast("设备绑定失败，请稍后重试！");
                }
            }
        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(),ImeiId,"xmq_test");
    }


    public static void skipTo(Context context){
        Intent intent=new Intent(context,BindDevice.class);
        context.startActivity(intent);
    }
}
