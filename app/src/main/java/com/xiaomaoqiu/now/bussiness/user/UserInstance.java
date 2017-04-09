package com.xiaomaoqiu.now.bussiness.user;

import android.text.TextUtils;

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
//            userInstance.PPS = SPUtil.getPPS();
//            userInstance.bps = SPUtil.getBps();
//            userInstance.userMobile = SPUtil.getUserMobile();
//            userInstance.uId = SPUtil.getUId();
//            userInstance.uName = SPUtil.getUName();
////            userInstance.token = SPUtil.getToken();
//            userInstance.newImToken = SPUtil.getNewImToken();
//            if(!TextUtils.isEmpty(SPUtil.getGender())){
//                userInstance.gender= Integer.parseInt(SPUtil.getGender());
//            }
//            userInstance.company = SPUtil.getCompany();
//            userInstance.comId = SPUtil.getComId();
//            userInstance.talkFlag = SPUtil.getTalkFlag();
//            userInstance.nickName = SPUtil.getNickName();
//            userInstance.photoPath = SPUtil.getuPhotoPath();
//            userInstance.position = SPUtil.getPosition();
//            userInstance.hasJob= SPUtil.getKeyHasJob();
//            userInstance.autoTalkFlag=SPUtil.getAutoTalkFlag();
//            userInstance.cvCount = SPUtil.getCvcount();
        }
        return userInstance;
    }
    public boolean m_bLogin=false;
    public String  m_strNick ="";
    public String  m_strPhone ="";
    public String  m_strPsw="";
    public String  m_strToken="";
    public long    m_uid=0;


    public void login(long uid,String strToken,String strPhone)
    {
        m_bLogin=true;
        m_uid = uid;
        m_strToken = strToken;
        m_strPhone = strPhone;

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
