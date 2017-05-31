package com.xiaomaoqiu.now.test;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.bussiness.bean.PetLocationBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 2017/4/19.
 */

public class TestInstance {
    public static TestInstance testInstance;

    private TestInstance() {
    }

    public static TestInstance getTestInstance() {
        if (testInstance == null) {
            testInstance = new TestInstance();
        }
        return testInstance;
    }
    public PetLocationBean basedata=new PetLocationBean();
    public PetLocationBean wifidata=new PetLocationBean();

    //获取宠物基站，wifi位置信息
    public void getTestLocation() {
        ApiUtils.getApiService().getTestLocation(
                UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id()
        ).enqueue(new XMQCallback<TestLocationBean>() {
            @Override
            public void onSuccess(Response<TestLocationBean> response, TestLocationBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    basedata=message.basedata;
                    wifidata=message.wifidata;

                    EventBus.getDefault().post(new EventManage.testPetLocation());
                }
            }

            @Override
            public void onFail(Call<TestLocationBean> call, Throwable t) {

            }
        });
    }

}
