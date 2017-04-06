package com.xiaomaoqiu.old.dataCenter;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaomaoqiu.old.utils.HttpUtil;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by huangjx on 15/7/26.
 */
public enum UserMgr {
    INSTANCE;

    private PetInfo mPetInfo = new PetInfo();

    public void queryPetLocation()
    {
        HttpUtil.get2("pet.location", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //{"status": 0, "latitude": "113.3882", "longitude": "22.9432"}
                Log.v("http", "pet.location:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    //todo 执行方法
//                    NotificationCenter.INSTANCE.getObserver(PetInfo.Callback_PetLocating.class).onLocateResult(true,response.optDouble("latitude"), response.optDouble("longitude"));
                }else
                {
                    //todo 执行方法
//                    NotificationCenter.INSTANCE.getObserver(PetInfo.Callback_PetLocating.class).onLocateResult(false,0.0,0.0);
                }
            }

        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(), getPetInfo().getPetID());
    }

    public void queryPetInfo()
    {
        //查询宠物基本信息
        HttpUtil.get2("pet.get_pet_info", new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //{"status": 0, "pet_id": 1462953761, "description": "haha", "weight": "10.00", "sex": 1, "nick": "test", "birthday": "1970-01-01", "logo_url": "", "pet_type_id": 1}
                Log.v("http", "pet.get_pet_info:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    mPetInfo.initFromJson(response);

                    //查询宠物的设备信息
                    HttpUtil.get2("device.get_info",new JsonHttpResponseHandler(){
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.v("http", "device.get_info:" + response.toString());
                            HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                            if (ret == HttpCode.EC_SUCCESS) {
                                mPetInfo.getDevInfo().initFromJson(response);
                            }else if(ret == HttpCode.EC_DEVICE_NOT_EXIST){
                                mPetInfo.getDevInfo().initFromNull();
                            }
                        }
                    },LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(),mPetInfo.getPetID());
                }
            }
        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken());
    }

    public void setPetAtHome(boolean bAtHome)
    {
        mPetInfo.setAtHome(bAtHome);
        //todo 需要添加更新
//        mPetInfo.notifyChanged(PetInfo.FieldMask_AtHome);
    }

    public void updatePetInfo(final PetInfo petInfo,final int fieldMask)
    {
        RequestParams requestParams = new RequestParams();

        if((fieldMask & PetInfo.FieldMask_Name) != 0)
        {
            requestParams.add("nick",petInfo.getName());
        }
        if((fieldMask & PetInfo.FieldMask_Sex) != 0)
        {
            requestParams.add("sex",String.valueOf(petInfo.getSex()));
        }
        if((fieldMask & PetInfo.FieldMask_Birth) != 0)
        {
            PetInfo.Date birth= petInfo.getBirthday();
            requestParams.add("birthday",String.format("%04d-%02d-%02d", birth.year, birth.month, birth.day));
        }
        if((fieldMask & PetInfo.FieldMask_Desc) != 0)
        {
            requestParams.add("description",petInfo.getDesc());
        }
        if((fieldMask & PetInfo.FieldMask_Weight) != 0)
        {
            requestParams.add("weight",String.valueOf(petInfo.getWeight()));
        }
        if((fieldMask & PetInfo.FieldMask_Header) != 0)
        {
            requestParams.add("logo_url",petInfo.getHeaderImg());
        }
        if((fieldMask & PetInfo.FieldMask_TypeID) != 0)
        {
            requestParams.add("pet_type_id",String.valueOf(petInfo.getPetID()));
        }

            HttpUtil.get3("pet.update_pet_info",requestParams, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.v("http", "pet.update_pet_info:" + response.toString());
                    HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                    if (ret == HttpCode.EC_SUCCESS) {
                        if((fieldMask & PetInfo.FieldMask_Name) != 0)
                        {
                            mPetInfo.setName(petInfo.getName());
                        }
                        if((fieldMask & PetInfo.FieldMask_Sex) != 0)
                        {
                            mPetInfo.setSex(petInfo.getSex());
                        }
                        if((fieldMask & PetInfo.FieldMask_Birth) != 0)
                        {
                            mPetInfo.setBirthday(petInfo.getBirthday());
                        }
                        if((fieldMask & PetInfo.FieldMask_Desc) != 0)
                        {
                            mPetInfo.setDesc(petInfo.getDesc());
                        }
                        if((fieldMask & PetInfo.FieldMask_Weight) != 0)
                        {
                            mPetInfo.setWeight(petInfo.getWeight());
                        }
                        if((fieldMask & PetInfo.FieldMask_Header) != 0)
                        {
                            mPetInfo.setHeaderImg(petInfo.getHeaderImg());
                        }
                        if((fieldMask & PetInfo.FieldMask_TypeID) != 0)
                        {
                            mPetInfo.setPetID(petInfo.getPetID());
                        }
                        //todo 需要添加更新机制
//                        mPetInfo.notifyChanged(fieldMask);
                    }
                }
            }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(),mPetInfo.getPetID());

    }

    public PetInfo getPetInfo(){
        return mPetInfo;
    }
}
