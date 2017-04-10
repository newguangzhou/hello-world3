package com.xiaomaoqiu.old.ui.mainPages.pageMe.hardware;

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
import com.xiaomaoqiu.now.EventManager;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.old.utils.HttpUtil;
import com.xiaomaoqiu.pet.R;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/31.
 */

public class BindDeviceActivity extends BaseActivity {

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
                DeviceInfoInstance.getInstance().bindDevice(inputImei.getText().toString());
            }
        });
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView(){
        inputImei=(EditText)findViewById(R.id.bind_device_input_imei);
        sweepBt=(Button)findViewById(R.id.bind_device_button);
        sweepBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZXingActivity.skipToAsResult(BindDeviceActivity.this,REQUEST_SWEEP_CODE);
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
                    PetInfoInstance.getPetInfoInstance().getPetInfo();
//                    DeviceActivity.skip(BindDeviceActivity.this);
                    finish();
                }
                else{
                    showToast("设备绑定失败，请稍后重试！");
                }
            }
        }, UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken(),ImeiId,"xmq_test");
    }


    public static void skipTo(Context context){
        Intent intent=new Intent(context,BindDeviceActivity.class);
        context.startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0, sticky = false)
    public  void toDeviceActivity(EventManager.bindDeviceSuccess event){
        Intent intent=new Intent(this,DeviceActivity.class);
        startActivity(intent);
        finish();
    }
}
