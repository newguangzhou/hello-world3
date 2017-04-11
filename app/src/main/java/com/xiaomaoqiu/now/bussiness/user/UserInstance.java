package com.xiaomaoqiu.now.bussiness.user;

import android.text.TextUtils;

import com.xiaomaoqiu.now.bean.nocommon.UserBean;
import com.xiaomaoqiu.now.util.SPUtil;

/**
 * Created by long on 17/4/7.
 */

public class UserInstance {
    private static UserInstance userInstance;
    private UserInstance() {
    }

    public static UserInstance getUserInstance() {
        if (userInstance == null) {
            userInstance = new UserInstance();
            userInstance.m_bLogin=SPUtil.getLoginStatus();
            userInstance.m_strPhone=SPUtil.getPhoneNumber();
            userInstance.m_uid=SPUtil.getUid();
            userInstance.m_strToken=SPUtil.getToken();
        }
        return userInstance;
    }
    public boolean m_bLogin=false;
    public String  m_strPhone ="";
    public String  m_strToken="";
    public long    m_uid=0;


    public void saveLoginState(UserBean message, String strPhone)
    {
        m_bLogin=true;
        m_uid = message.uid;
        m_strToken = message.token;
        m_strPhone = strPhone;
        SPUtil.putPhoneNumber(strPhone);
        SPUtil.putLoginStatus(true);
        SPUtil.putUid(message.uid);
        SPUtil.putToken(message.token);
    }
    public void clearLoginState(){
        m_bLogin=false;
        m_uid =-1;
        m_strToken = "";
        SPUtil.putLoginStatus(false);
        SPUtil.putUid(m_uid);
        SPUtil.putToken("");
    }
    public long getUid()
    {
        return m_uid;
    }

    public String getToken()
    {
        return m_strToken;
    }

}
