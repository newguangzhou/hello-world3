package com.xiaomaoqiu.now.bussiness.location;

import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.LightStatusBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.presenter.ILocateView;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Administrator on 2017/1/14.
 */

public class LocatePresenter {
    private ILocateView locateView;

    private boolean isFindPetMode=false;

    public LocatePresenter(ILocateView view){
        this.locateView=view;
    }

    public void onInit(){
        if(null == locateView){
            return;
        }
        if(PetInfoInstance.getInstance().getAtHome() == false)
        {
            //宠物不在家，显示手机定位(遛狗模式)
            locateView.onShowPhoneLoc();
        }else if(PetInfoInstance.getInstance().getPet_id() != -1) {
            //宠物在家，查询宠物位置
            PetInfoInstance.getInstance().getPetLocation();
        }
        PetInfoInstance.getInstance().getPetLocation();
//        queryLightStatus();

    }

    /**
     * 查询宠物位置
     */
    public void queryPetLocation(boolean isFindPetMode){
        this.isFindPetMode=isFindPetMode;
        PetInfoInstance.getInstance().getPetLocation();
    }

    public boolean isFindPetMode(){
        return isFindPetMode;
    }

    /**
     * 去运动
     */
    public void goSport(){
        PetInfoInstance.getInstance().setAtHome(false);
//        HttpUtil.get2("pet.activity", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.v("http", "pet.activity:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    PetInfoInstance.getInstance().setAtHome(false);
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
//            {
//                onFail("网络连接失败！");
//            }
//
//        }, UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),1 );
//        ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(),
//                UserInstance.getInstance().getToken(),
//                PetInfoInstance.getInstance().getPet_id(),
//                Constants.TO_SPORT_ACTIVITY_TYPE
//        ).enqueue(new XMQCallback<BaseBean>() {
//            @Override
//            public void onSuccess(Response<BaseBean> response, BaseBean message) {
//                HttpCode ret = HttpCode.valueOf(message.status);
//                switch (ret) {
//                    case EC_SUCCESS:
//                        PetInfoInstance.getInstance().setAtHome(false);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFail(Call<BaseBean> call, Throwable t) {
//
//            }
//        });

    }

    /**
     * 回家
     */
    public void goHome(){
        PetInfoInstance.getInstance().setAtHome(true);
//        HttpUtil.get2("pet.activity", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.v("http", "pet.activity:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
////                    petInfoInstance.getInstance().setAtHome(true);
//                    PetInfoInstance.getInstance().setAtHome(true);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                onFail("网络连接失败！");
//            }
//
//        },UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),2 );
//        ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(),
//                UserInstance.getInstance().getToken(),
//                PetInfoInstance.getInstance().getPet_id(),
//                Constants.TO_HOME_ACTIVITY_TYPE
//                ).enqueue(new XMQCallback<BaseBean>() {
//            @Override
//            public void onSuccess(Response<BaseBean> response, BaseBean message) {
//                HttpCode ret = HttpCode.valueOf(message.status);
//                switch (ret) {
//                    case EC_SUCCESS:
//                        PetInfoInstance.getInstance().setAtHome(true);
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onFail(Call<BaseBean> call, Throwable t) {
//
//            }
//        });
    }

   /* *//**
     * 查询闪光灯状态
     *//*
    public void queryLightStatus(){
        if(null == locateView){
            return;
        }
//        HttpUtil.get2(GET_LIGHT_STATUS,new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                if(null == response){
//                    onFail("查询闪光灯状态失败，请稍后重试！");
//                    return;
//                }
//                Log.v("http", "pet.location:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    int status = response.optInt("light_status");
//                    onSuccessGetLightStatus(status == 1,false);
//                }else{
//                    onFail("查询闪光灯状态失败，请稍后重试！");
//                }
//            }
//        },UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),UserMgr.INSTANCE.getPetInfo().getDevInfo().getImei());

        ApiUtils.getApiService().getLightStatus(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                DeviceInfoInstance.getInstance().packBean.imei
                ).enqueue(new XMQCallback<LightStatusBean>() {
            @Override
            public void onSuccess(Response<LightStatusBean> response, LightStatusBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    onSuccessGetLightStatus(message.light_status == 1,false);
                }else{
                    ToastUtil.showTost("查询闪光灯状态失败，请稍后重试！");
                }
            }

            @Override
            public void onFail(Call<LightStatusBean> call, Throwable t) {

            }
        });
    }

    *//**
     * 改变闪光灯状态
     * @param
     *//*
    public void setLightStatus(final boolean bOpenLight){
//        HttpUtil.get2(SET_LIGHT_STATUS, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                //{"status": 0, }
//                if(null == response){
//                    onFailChangeLightStatus(bOpenLight);
//                    return;
//                }
//                Log.v("http", "device.swicth_light:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    onSuccessGetLightStatus(bOpenLight,true);
//                }else{
//                    onFailChangeLightStatus(bOpenLight);
//                }
//            }
//
//        }, UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), UserMgr.INSTANCE.getPetInfo().getDevInfo().getImei(),bOpenLight?1:0);

        ApiUtils.getApiService().switchLightStatus(UserInstance.getInstance().getUid(),UserInstance.getInstance().getToken(),
                DeviceInfoInstance.getInstance().packBean.imei,bOpenLight?1:0
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    onSuccessGetLightStatus(bOpenLight,true);
                }else{
                    onFailChangeLightStatus(bOpenLight);
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });
    }*/

//    private void onSuccessGetLightStatus(boolean isOpen,boolean isFromView){
//        if(null == locateView){
//            return;
//        }
//        locateView.onChangeLightStatus(isOpen,isFromView);
//    }

    private void onFailChangeLightStatus(boolean isOpen){
        if(null == locateView){
            return;
        }
        if(isOpen){
            locateView.onFail("开启闪光灯失败，请稍后重试！");
        }else{
            locateView.onFail("关闭闪光灯失败，请稍后重试！");
        }
    }

    private void onFail(String msg){
        if(null == locateView){
            return;
        }
        locateView.onFail(msg);
    }

    public void release(){
        if(null != locateView){
            locateView = null;
        }
    }

}
