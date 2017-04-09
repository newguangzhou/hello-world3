package com.xiaomaoqiu.now.bussiness.pet;


import android.annotation.SuppressLint;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManager;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.PetInfoBean;
import com.xiaomaoqiu.now.bean.nocommon.PetLocationBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.old.dataCenter.DeviceInfo;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */
@SuppressLint("WrongConstant")
public class PetInfoInstance {
    private static PetInfoInstance petInfoInstance;

    private PetInfoInstance() {

    }

    public static PetInfoInstance getPetInfoInstance() {
        if (petInfoInstance == null) {
            petInfoInstance = new PetInfoInstance();
            petInfoInstance.pet_id = SPUtil.getPetId();
            petInfoInstance.description = SPUtil.getPetDescription();
            petInfoInstance.weight = SPUtil.getPetWeight();
            petInfoInstance.device_imei = SPUtil.getDeviceImei();
            petInfoInstance.target_step = SPUtil.getPetTargetStep();
            petInfoInstance.sex = SPUtil.getSex();
            petInfoInstance.nick = SPUtil.getPeiNick();
            petInfoInstance.birthday = SPUtil.getBirthday();
            petInfoInstance.logo_url = SPUtil.getPetHeader();
            petInfoInstance.pet_type_id = SPUtil.getPetTypeId();
        }
        return petInfoInstance;
    }


    public long pet_id;

    public String description;

    public String weight;

    public String device_imei;

    public int target_step;

    public int sex;

    public String nick;

    public String birthday;

    public String logo_url;

    public int pet_type_id;


    //***********位置信息
    public double latitude;

    public double location_time;

    public double longitude;


    public void savePetInfo(PetInfoBean message) {
        pet_id = message.pet_id;
        SPUtil.putPetId(pet_id);
        description = message.description;
        SPUtil.putPetDescription(description);
        weight = message.weight;
        SPUtil.putWeight(weight);
        device_imei = message.device_imei;
        SPUtil.putDeviceImei(device_imei);
        target_step = message.target_step;
        SPUtil.putPetTargetStep(target_step);
        sex = message.sex;
        SPUtil.putSex(sex);
        nick = message.nick;
        SPUtil.putPetNick(nick);
        birthday = message.birthday;
        SPUtil.putBirthday(birthday);
        logo_url = message.logo_url;
        SPUtil.putPetHeader(logo_url);
        pet_type_id = message.pet_type_id;
        SPUtil.putPetTypeId(pet_type_id);
    }


    //获取宠物基本信息
    public void getPetInfo() {
        ApiUtils.getApiService().getPetInfo(UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken())
                .enqueue(new XMQCallback<PetInfoBean>() {
                    @Override
                    public void onSuccess(Response<PetInfoBean> response, PetInfoBean message) {
                        HttpCode ret = HttpCode.valueOf(message.status);
                        if (ret == HttpCode.EC_SUCCESS) {
                            savePetInfo(message);
                            EventBus.getDefault().postSticky(new EventManager.notifyPetFramentGetActivityInfo());
                            //获取设备信息
                            DeviceInfoInstance.getInstance().getDeviceInfo();

                        } else {
                            Toast.makeText(PetAppLike.mcontext, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(Call<PetInfoBean> call, Throwable t) {
                        Toast.makeText(PetAppLike.mcontext, "网络错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getPetLocation() {
        ApiUtils.getApiService().getPetLocation(UserInstance.getUserInstance().getUid(),
                UserInstance.getUserInstance().getToken(),
                PetInfoInstance.getPetInfoInstance().getPet_id()
        ).enqueue(new XMQCallback<PetLocationBean>() {
            @Override
            public void onSuccess(Response<PetLocationBean> response, PetLocationBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    //TODO 发送一个位置变化的广播
                } else {
                    //TODO 发送一个位置为0的广播
                }

            }

            @Override
            public void onFail(Call<PetLocationBean> call, Throwable t) {
                Toast.makeText(PetAppLike.mcontext, "位置获取失败~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setPet_id(long pet_id) {
        this.pet_id = pet_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }

    public void setTarget_step(int target_step) {
        this.target_step = target_step;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public void setPet_type_id(int pet_type_id) {
        this.pet_type_id = pet_type_id;
    }

    public long getPet_id() {

        return pet_id;
    }

    public String getDescription() {
        return description;
    }

    public String getWeight() {
        return weight;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public int getTarget_step() {
        return target_step;
    }

    public int getSex() {
        return sex;
    }

    public String getNick() {
        return nick;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public int getPet_type_id() {
        return pet_type_id;
    }


}
