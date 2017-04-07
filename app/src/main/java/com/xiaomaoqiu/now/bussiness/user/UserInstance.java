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


}
