package com.xiaomaoqiu.old.dataCenter;


/**
 * Created by Administrator on 2015/6/17.
 */
public enum LoginMgr {
    INSTANCE;

    private boolean m_bLogin=false;
    private String  m_strNick ="";
    private String  m_strPhone ="";
    private String  m_strPsw="";
    private String  m_strToken="";
    private long    m_uid=0;






    public boolean isLogin()
    {
        return m_bLogin;
    }

    public void login(long uid,String strToken,String strPhone)
    {
        m_bLogin=true;
        m_uid = uid;
        m_strToken = strToken;
        m_strPhone = strPhone;

    }

    public void logout()
    {
        m_bLogin=false;
        m_strNick ="";
        m_strPsw="";
    }

    public long getUid()
    {
        return m_uid;
    }

    public String getToken()
    {
        return m_strToken;
    }


    public String getPsw()
    {
        return m_strPsw;
    }


    public void setPhone(String strPhone){
        m_strPhone = strPhone;
    }

    public String getPhone()
    {
        return m_strPhone;
    }

}
