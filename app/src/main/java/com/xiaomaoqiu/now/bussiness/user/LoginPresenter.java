package com.xiaomaoqiu.now.bussiness.user;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.MessageBean;
import com.xiaomaoqiu.now.bean.nocommon.UserBean;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */
@SuppressLint("WrongConstant")
public class LoginPresenter {
    private WeakReference<LoginView> loginView;
    private WeakReference<LogoutView> logoutView;

    LoginPresenter(LoginView loginView) {
        this.loginView = new WeakReference<>(loginView);
    }

    public LoginPresenter(LogoutView logoutView) {
        this.logoutView = new WeakReference<LogoutView>(logoutView);
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
                    HttpCode ret = HttpCode.valueOf(message.status);
                    switch (ret) {
                        case EC_SUCCESS:
                            if (tloginView != null) {
                                tloginView.getVerifyNextTime(message.next_req_interval);
                            }
                            break;
                        case EC_FREQ_LIMIT:
                            ToastUtil.showTost("发送验证码频率多");
                            break;
                        default:
                            ToastUtil.showTost("网络出错");

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
                    HttpCode ret = HttpCode.valueOf(message.status);
                    switch (ret) {
                        case EC_SUCCESS:
                            //保存登录状态
                            UserInstance.getUserInstance().saveLoginState(message, phone);
                            tloginView.LoginSuccess();
                            break;
                        case EC_INVALID_VERIFY_CODE:
                            ToastUtil.showTost("验证码无效");
                            break;
                        default:
                            ToastUtil.showTost("登录异常");
                    }

                }

                @Override
                public void onFail(Call<UserBean> call, Throwable t) {
                    if (tloginView != null) {
                        tloginView.dismissDialog();
                    }
                    Toast.makeText(PetAppLike.mcontext, "网络错误", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    //退出登录
    public void logout() {
        ApiUtils.getApiService().logout(UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken()).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {//退出登陆成功
                    UserInstance.getUserInstance().clearLoginState();
                    final LogoutView togoutView = logoutView.get();
                    if (togoutView != null) {
                        //退出登录成功
                        togoutView.success();
                    }
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });
    }
}
