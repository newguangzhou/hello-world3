package com.xiaomaoqiu.now.bussiness.user;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.LoginBean;
import com.xiaomaoqiu.now.bean.nocommon.MessageBean;
import com.xiaomaoqiu.now.bean.nocommon.UserBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

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
            tloginView.showDialog();
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
                    tloginView.dismissDialog();
                }

                @Override
                public void onFail(Call<MessageBean> call, Throwable t) {
                    Toast.makeText(PetAppLike.mcontext, "验证码发送失败", Toast.LENGTH_LONG).show();
                    tloginView.dismissDialog();
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
            ApiUtils.getApiService().login(phone, verifyCode, deviceType, deviceId).enqueue(new XMQCallback<LoginBean>() {
                @Override
                public void onSuccess(Response<LoginBean> response, LoginBean message) {
                    if (tloginView != null) {
                        tloginView.dismissDialog();
                    }
                    HttpCode ret = HttpCode.valueOf(message.status);
                    switch (ret) {
                        case EC_SUCCESS:
                            //保存登录状态
                            UserInstance.getInstance().saveLoginState(message, phone);


                           UserInstance.getInstance().getUserInfo();

                            break;
                        case EC_INVALID_VERIFY_CODE:
                            ToastUtil.showTost("验证码无效");
                            break;
                        default:
                            ToastUtil.showTost("登录异常");
                    }

                }

                @Override
                public void onFail(Call<LoginBean> call, Throwable t) {
                    if (tloginView != null) {
                        tloginView.dismissDialog();
                    }

                }
            });
        }
    }

    //退出登录
    public void logout() {
        ApiUtils.getApiService().logout(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken()).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {//退出登陆成功
                    UserInstance.getInstance().clearLoginInfo();
                    PetInfoInstance.getInstance().clearPetInfo();
                    DeviceInfoInstance.getInstance().clearDeviceInfo();
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
