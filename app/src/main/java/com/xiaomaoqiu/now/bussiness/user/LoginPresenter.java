package com.xiaomaoqiu.now.bussiness.user;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.MessageBean;
import com.xiaomaoqiu.now.bean.nocommon.UserBean;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */
@SuppressLint("WrongConstant")
public class LoginPresenter {
    private WeakReference<LoginView> loginView;

    LoginPresenter(LoginView loginView) {
        this.loginView = new WeakReference<>(loginView);
    }

    /**
     * 获取验证码
     *
     * @param phone
     * @param deviceType
     */
    public void getVerifyCode(String phone, int deviceType) {
        final LoginView tloginView = loginView.get();
        if (tloginView != null) {
            ApiUtils.getApiService().getVerifyCode(phone, deviceType).enqueue(new XMQCallback<MessageBean>() {
                @Override
                public void onSuccess(Response<MessageBean> response, MessageBean message) {
                    HttpCode ret = HttpCode.valueOf(response.body().status);
                    if (ret == HttpCode.EC_SUCCESS) {//发送成功
                        if (tloginView != null) {
                            tloginView.getVerifyNextTime(message.next_req_interval);
                        }
                    } else {
                        Toast.makeText(PetAppLike.mcontext, "验证码发送失败", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFail(Call<MessageBean> call, Throwable t) {
                    Toast.makeText(PetAppLike.mcontext, "验证码发送失败", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    /**
     * 登录
     *
     * @param phone      手机号
     * @param verifyCode 验证码
     * @param deviceType 手机类型
     * @param deviceId
     */
    public void login(final String phone, String verifyCode, int deviceType, String deviceId) {
        final LoginView tloginView = loginView.get();
        if (tloginView != null) {
            tloginView.showDialog();
            ApiUtils.getApiService().login(phone, verifyCode, deviceType, deviceId).enqueue(new XMQCallback<UserBean>() {
                @Override
                public void onSuccess(Response<UserBean> response, UserBean message) {
                    if (tloginView != null) {
                        tloginView.dismissDialog();
                    }
                    HttpCode ret = HttpCode.valueOf(response.body().status);
                    if (ret == HttpCode.EC_SUCCESS) {//登陆成功
                        //保存登录状态
                        UserInstance.getUserInstance().saveLoginState(message,phone);
                        tloginView.LoginSuccess();
                    } else {
                        Toast.makeText(PetAppLike.mcontext, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail(Call<UserBean> call, Throwable t) {
                    if (tloginView != null) {
                        tloginView.dismissDialog();
                    }
                }
            });
        }
    }
}
