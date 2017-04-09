package com.xiaomaoqiu.now.bussiness.Device;

import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;

/**
 * Created by long on 17/4/9.
 */

public class DeviceInfoPresenter {
    //获取设备信息
    public void getDeviceInfo(){
        ApiUtils.getApiService().getDeviceInfo(UserInstance.getUserInstance().getUid(),UserInstance.getUserInstance().getToken(), PetInfoInstance.getPetInfoInstance().)
    }
}
