package com.xiaomaoqiu.now.http;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomaoqiu.now.Environment;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.Device.InitBindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.AddPetInfoActivity;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.push.XMPushManagerInstance;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaomaoqiu.now.http.HttpCode.EC_DEVICE_NOT_EXIST;
import static com.xiaomaoqiu.now.http.HttpCode.EC_INVALID_TOKEN;
import static com.xiaomaoqiu.now.http.HttpCode.EC_LOGIN_IN_OTHER_PHONE;
import static com.xiaomaoqiu.now.http.HttpCode.EC_PET_NOT_EXIST;

/**
 * Created by long on 17/4/7.
 */
@SuppressLint("WrongConstant")
public abstract class XMQCallback<T extends BaseBean> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() >= 200 && response.code() < 300) {
            T message = response.body();
            if (message != null) {

                HttpCode ret = HttpCode.valueOf(message.status);
                //如果token过期，直接跳转到登录页面
                if (EC_INVALID_TOKEN == ret) {
                    MainActivity.getLocationWithOneMinute=false;

                    //注销小米账号
                    XMPushManagerInstance.getInstance().stop();
                    ToastUtil.showTost("身份过期，请重新登录");
                    onFail(call, null);
                    UserInstance.getInstance().clearLoginInfo();
                    SPUtil.putHomeWifiMac("");
                    SPUtil.putHomeWifiSsid("");
                    PetInfoInstance.getInstance().clearPetInfo();
                    DeviceInfoInstance.getInstance().clearDeviceInfo();

                    SPUtil.putDeviceImei("");
                    Intent intent = new Intent(PetAppLike.mcontext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PetAppLike.mcontext.startActivity(intent);

                    return;
                }
                if(EC_LOGIN_IN_OTHER_PHONE==ret){
                    MainActivity.getLocationWithOneMinute=false;
                    //注销小米账号
                    XMPushManagerInstance.getInstance().stop();
                    UserInstance.getInstance().clearLoginInfo();
                    SPUtil.putHomeWifiMac("");
                    SPUtil.putHomeWifiSsid("");
                    PetInfoInstance.getInstance().clearPetInfo();
                    DeviceInfoInstance.getInstance().clearDeviceInfo();

                    SPUtil.putDeviceImei("");
                    Intent intent = new Intent(PetAppLike.mcontext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PetAppLike.mcontext.startActivity(intent);

//                    String remote_login_time= (String) formatBean.data.get("remote_login_time");
//                    String X_OS_Name= (String) formatBean.data.get("X_OS_Name");
                    PushEventManage.otherLogin event=new PushEventManage.otherLogin();
//                    event.remote_login_time=remote_login_time;
//                    event.X_OS_Name=X_OS_Name;

//                    OtherLoginBean tempbean=(OtherLoginBean)message;
                    event.X_OS_Name=message.device_model;
                    event.remote_login_time=message.date;
                    EventBus.getDefault().postSticky(event);

                    return;
                }


                //如果设备不存在，直接退到绑定设备页面
                if (EC_DEVICE_NOT_EXIST == ret) {
                    MainActivity.getLocationWithOneMinute=false;
//                    ToastUtil.showTost("请先填写您的宠物信息");
                    onFail(call, null);

                    DeviceInfoInstance.getInstance().clearDeviceInfo();
                    PetInfoInstance.getInstance().clearPetInfo();

                    Intent intent = new Intent(PetAppLike.mcontext, InitBindDeviceActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PetAppLike.mcontext.startActivity(intent);

                    return;
                }
                //如果宠物不存在了，直接退到添加宠物页面
                if (EC_PET_NOT_EXIST == ret) {
                    MainActivity.getLocationWithOneMinute=false;
//                    ToastUtil.showTost("请先填写您的宠物信息");
                    onFail(call, null);

                    PetInfoInstance.getInstance().clearPetInfo();
//                    DeviceInfoInstance.getInstance().clearDeviceInfo();


                    Intent intent = new Intent(PetAppLike.mcontext, AddPetInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PetAppLike.mcontext.startActivity(intent);

                    return;
                }
                if (PetAppLike.environment == Environment.Release) {
                    //上线状态下：此处加了统一对网络请求的异常捕获，不让用户崩溃。然后上传异常信息到bugly。
                    try {
                        onSuccess(response, message);
                    } catch (Exception e) {
                        DialogUtil.closeProgress();
                        CrashReport.postCatchedException(e);
                        Toast.makeText(PetAppLike.mcontext, "系统错误，请稍后……", Toast.LENGTH_SHORT).show();
                        Log.e(PetAppLike.TAG, "callback出错了" + e.getMessage());
                    }
                } else {
                    onSuccess(response, message);
                }
                return;
            }
        }
        ToastUtil.showTost("网络错误");

        onFail(call, null);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFail(call, t);
    }

    public abstract void onSuccess(Response<T> response, T message);

    public abstract void onFail(Call<T> call, Throwable t);
}
