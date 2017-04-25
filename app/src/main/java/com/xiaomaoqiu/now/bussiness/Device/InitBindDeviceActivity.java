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
import com.xiaomaoqiu.now.bussiness.pet.PetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
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

public class InitBindDeviceActivity extends BaseActivity {

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
    }

    private void initView(){
        View btnGoBack = findViewById(R.id.btn_go_back);
        btnGoBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InitBindDeviceActivity.this, PetInfoActivity.class);
                startActivity(intent);
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

//    /**
//     * 绑定设备
//     * @param ImeiId
//     */
//    private void BindDeciveHttp(String ImeiId){
//        HttpUtil.get2("device.add_device_info",new JsonHttpResponseHandler(){
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.v("http", "device.add_device_info:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    PetInfoInstance.getInstance().getPetInfo();
////                    DeviceActivity.skip(BindDeviceActivity.this);
//                    finish();
//                }
//                else{
//                    showToast("设备绑定失败，请稍后重试！");
//                }
//            }
//        }, UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),ImeiId,"xmq_test");
//    }




    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public  void toDeviceActivity(EventManage.bindDeviceSuccess event){
        EventBus.getDefault().unregister(this);
        if(TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            Intent intent = new Intent(this, InitWifiListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        Intent intent=new Intent(InitBindDeviceActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(0, 0);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
