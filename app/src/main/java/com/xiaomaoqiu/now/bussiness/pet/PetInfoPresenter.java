package com.xiaomaoqiu.now.bussiness.pet;

import com.xiaomaoqiu.now.bean.nocommon.PetInfoBean;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */

public class PetInfoPresenter {

    //获取宠物基本信息
    public void getPetInfo() {
        ApiUtils.getApiService().getPetInfo(UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken())
                .enqueue(new XMQCallback<PetInfoBean>() {
                    @Override
                    public void onSuccess(Response<PetInfoBean> response, PetInfoBean message) {
                        HttpCode ret = HttpCode.valueOf(message.status);
                        if (ret == HttpCode.EC_SUCCESS) {
                            PetInfoInstance.getPetInfoInstance().savePetInfo(message);

                        }
                    }

                    @Override
                    public void onFail(Call<PetInfoBean> call, Throwable t) {

                    }
                });
    }


}
