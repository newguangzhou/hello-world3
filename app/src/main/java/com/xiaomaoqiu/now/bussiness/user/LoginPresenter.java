package com.xiaomaoqiu.now.bussiness.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.MessageBean;
import com.xiaomaoqiu.now.bean.nocommon.UserBean;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.old.dataCenter.LoginMgr;
import com.xiaomaoqiu.old.ui.MainActivity;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */
 @SuppressLint("WrongConstant")
public class LoginPresenter {
    private WeakReference<LoginView> loginView;
    private UserInstance userInstance;
    LoginPresenter(LoginView loginView){
        this.loginView=new WeakReference<>(loginView);
        }

    public void getVerifyCode(String phone,int deviceType){
        final LoginView tloginView=loginView.get();
if(tloginView!=null){
    ApiUtils.getApiService().getVerifyCode(phone,deviceType).enqueue(new Callback<MessageBean>() {
        @Override
        public void onResponse(Call<MessageBean> call, Response<MessageBean> response) {
            MessageBean bean=response.body();
            if(bean!=null){
                HttpCode ret = HttpCode.valueOf(response.body().status);
                if (ret == HttpCode.EC_SUCCESS) {//发送成功
                    if(tloginView!=null){
                        tloginView.getVerifyNextTime(bean.next_req_interval);
                    }
                }else{
                    Toast.makeText(PetAppLike.mcontext, "验证码发送失败", Toast.LENGTH_LONG).show();

                }
                }else{
                Toast.makeText(PetAppLike.mcontext, "验证码发送失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<MessageBean> call, Throwable throwable) {
            Toast.makeText(PetAppLike.mcontext, "验证码发送失败", Toast.LENGTH_LONG).show();
        }
    });
}

    }

    /**
     *
     * @param phone 手机号
     * @param verifyCode 验证码
     * @param deviceType 手机类型
     * @param deviceId
     */
    public void login(final String phone, String verifyCode, int deviceType, String deviceId){
        final LoginView tloginView=loginView.get();
        if(tloginView!=null){
            tloginView.showDialog();
            ApiUtils.getApiService().login(phone,verifyCode,deviceType,deviceId).enqueue(new Callback<UserBean>(){


                @Override
                public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                    if(tloginView!=null) {
                        tloginView.dismissDialog();
                    }

                    UserBean bean=response.body();
                    if(bean!=null){
                        HttpCode ret = HttpCode.valueOf(response.body().status);
                        if (ret == HttpCode.EC_SUCCESS) {//登陆成功
                            UserInstance.getUserInstance().login(bean.uid,bean.token,phone);
                            SPUtil.putPhoneNumber(phone);
                            SPUtil.putLoginStatus(true);
                           tloginView.LoginSuccess();
                    }else{
                            Toast.makeText(PetAppLike.mcontext,"登录失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserBean> call, Throwable throwable) {
                    if(tloginView!=null) {
                        tloginView.dismissDialog();
                    }
                }
            });
        }
    }
}
