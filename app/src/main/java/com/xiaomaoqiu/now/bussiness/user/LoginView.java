package com.xiaomaoqiu.now.bussiness.user;

/**
 * Created by long on 17/4/7.
 */

public interface LoginView {
    public void showDialog();
    public void dismissDialog();

    public void getVerifyNextTime(int nSecond);
    public void LoginSuccess();
}
