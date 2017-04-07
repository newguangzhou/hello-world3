package com.xiaomaoqiu.now.bussiness.user;

import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.XMQCallback;

import java.lang.ref.WeakReference;

/**
 * Created by long on 17/4/7.
 */

public class LoginPresenter {
    private WeakReference<LoginView> loginView;
    private UserInstance userInstance;
    LoginPresenter(LoginView loginView){
        this.loginView=new WeakReference<>(loginView);
        }
    public void login(String phone,String verifyCode,int deviceType,String deviceId){
        LoginView tloginView=loginView.get();
        if(tloginView!=null){
            tloginView.showDialog();
            ApiUtils.getApiService().login(phone,verifyCode,deviceType,deviceId).enqueue(new XMQCallback<UserBean>(){

                @Override
                public void onFail(Call call, Throwable t) {

                }

                @Override
                public void onSuccess(Response response, Object commJson) {

                }
            })
        }
    }
}
