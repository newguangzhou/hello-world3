package com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.pet.dataCenter.HttpCode;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.utils.HttpUtil;

import org.apache.http.Header;
import org.json.JSONObject;


/**
 * Created by Administrator on 2017/1/14.
 */

public class LocatePresenter {
    public static final String GET_LIGHT_STATUS="device.get_light_status";
    public static final String SET_LIGHT_STATUS="device.swicth_light";
    private ILocateView locateView;

    private boolean isFindPetMode=false;

    public LocatePresenter(ILocateView view){
        this.locateView=view;
    }

    public void onInit(){
        if(null == locateView){
            return;
        }
        if(UserMgr.INSTANCE.getPetInfo().getAtHome() == false)
        {
            //宠物不在家，显示手机定位(遛狗模式)
            locateView.onShowPhoneLoc();
        }/*else if(UserMgr.INSTANCE.getPetInfo().getPetID() != -1) {
            //宠物在家，查询宠物位置
            UserMgr.INSTANCE.queryPetLocation();
        }*/
        UserMgr.INSTANCE.queryPetLocation();
        queryLightStatus();

    }

    /**
     * 查询宠物位置
     */
    public void queryPetLocation(boolean isFindPetMode){
        this.isFindPetMode=isFindPetMode;
        UserMgr.INSTANCE.queryPetLocation();
    }

    public boolean isFindPetMode(){
        return isFindPetMode;
    }

    /**
     * 去运动
     */
    public void goSport(){
        HttpUtil.get2("pet.activity", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("http", "pet.activity:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    UserMgr.INSTANCE.setPetAtHome(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
            {
                onFail("网络连接失败！");
            }

        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(),UserMgr.INSTANCE.getPetInfo().getPetID(),1 );
    }

    /**
     * 回家
     */
    public void goHome(){
        HttpUtil.get2("pet.activity", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("http", "pet.activity:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    UserMgr.INSTANCE.setPetAtHome(true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                onFail("网络连接失败！");
            }

        },LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(),UserMgr.INSTANCE.getPetInfo().getPetID(),2 );
    }

    /**
     * 查询闪光灯状态
     */
    public void queryLightStatus(){
        if(null == locateView){
            return;
        }
        HttpUtil.get2(GET_LIGHT_STATUS,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(null == response){
                    onFail("查询闪光灯状态失败，请稍后重试！");
                    return;
                }
                Log.v("http", "pet.location:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    int status = response.optInt("light_status");
                    onSuccessGetLightStatus(status == 1,false);
                }else{
                    onFail("查询闪光灯状态失败，请稍后重试！");
                }
            }
        },LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(),UserMgr.INSTANCE.getPetInfo().getDevInfo().getImei());
    }

    /**
     * 改变闪光灯状态
     * @param bOpenLight
     */
    public void setLightStatus(final boolean bOpenLight){
        HttpUtil.get2(SET_LIGHT_STATUS, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //{"status": 0, }
                if(null == response){
                    onFailChangeLightStatus(bOpenLight);
                    return;
                }
                Log.v("http", "device.swicth_light:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    onSuccessGetLightStatus(bOpenLight,true);
                }else{
                    onFailChangeLightStatus(bOpenLight);
                }
            }

        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(), UserMgr.INSTANCE.getPetInfo().getDevInfo().getImei(),bOpenLight?1:0);
    }

    private void onSuccessGetLightStatus(boolean isOpen,boolean isFromView){
        if(null == locateView){
            return;
        }
        locateView.onChangeLightStatus(isOpen,isFromView);
    }

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
