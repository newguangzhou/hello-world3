package com.xiaomaoqiu.now.bussiness.pet;


import android.annotation.SuppressLint;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManager;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.PetInfoBean;
import com.xiaomaoqiu.now.bean.nocommon.PetLocationBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */
@SuppressLint("WrongConstant")
public class PetInfoInstance {
    private static PetInfoInstance instance;
    public PetInfoBean packBean;

    //***********位置信息
    public double latitude;

    public double location_time;

    public double longitude;

    public double radius;

    public boolean petAtHome = true; //true - 宠物在家, false - 宠物活动中

    public Date dateFormat_birthday;

    private PetInfoInstance() {

    }

    public static PetInfoInstance getInstance() {
        if (instance == null) {
            instance = new PetInfoInstance();
            PetInfoBean baseBean = new PetInfoBean();
            baseBean.pet_id = SPUtil.getPetId();
            baseBean.name = SPUtil.getPetName();
            baseBean.description = SPUtil.getPetDescription();
            baseBean.weight = SPUtil.getPetWeight();
            baseBean.device_imei = SPUtil.getDeviceImei();
            baseBean.target_step = SPUtil.getPetTargetStep();
            baseBean.sex = SPUtil.getSex();
            baseBean.nick = SPUtil.getPeiNick();
            baseBean.birthday = SPUtil.getBirthday();
            baseBean.logo_url = SPUtil.getPetHeader();
            baseBean.pet_type_id = SPUtil.getPetTypeId();
            instance.setDateFormat_birthday(baseBean.birthday);
            instance.petAtHome = SPUtil.getPetAtHome();
            instance.packBean = baseBean;
        }
        return instance;
    }

    static public class Date {
        public Date(int y, int m, int d) {
            setDate(y, m, d);
        }

        public void setDate(int y, int m, int d) {
            year = y;
            month = m;
            day = d;
        }

        public int year;
        public int month;
        public int day;

        @Override
        public String toString() {
            return year + "-" + month + "-" + day;
        }
    }


    public void savePetInfo(PetInfoBean message) {
        packBean.pet_id = message.pet_id;
        SPUtil.putPetId(packBean.pet_id);
        packBean.name = message.name;
        SPUtil.putPetName(packBean.name);
        packBean.description = message.description;
        SPUtil.putPetDescription(packBean.description);
        packBean.weight = message.weight;
        SPUtil.putWeight(packBean.weight);
        packBean.device_imei = message.device_imei;
        SPUtil.putDeviceImei(packBean.device_imei);
        packBean.target_step = message.target_step;
        SPUtil.putPetTargetStep(packBean.target_step);
        packBean.sex = message.sex;
        SPUtil.putSex(packBean.sex);
        packBean.nick = message.nick;
        SPUtil.putPetNick(packBean.nick);
        packBean.birthday = message.birthday;
        SPUtil.putBirthday(packBean.birthday);
        packBean.logo_url = message.logo_url;
        SPUtil.putPetHeader(packBean.logo_url);
        packBean.pet_type_id = message.pet_type_id;
        SPUtil.putPetTypeId(packBean.pet_type_id);
        setDateFormat_birthday(packBean.birthday);


        EventBus.getDefault().post(new EventManager.notifyPetInfoChange());
    }

    public void setDateFormat_birthday(String birthdayString) {
        int year;
        int month;
        int day;
        try {
            Scanner scanner = new Scanner(birthdayString);
            scanner.useDelimiter("-");
            year = scanner.nextInt();
            month = scanner.nextInt();
            day = scanner.nextInt();
        } catch (Exception e) {
            year = 0;
            month = 0;
            day = 0;
        }
        dateFormat_birthday=new Date(year, month, day);
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
                            ToastUtil.showNetError();
                        }
                    }

                    @Override
                    public void onFail(Call<PetInfoBean> call, Throwable t) {
                    }
                });
    }

    //更新宠物信息
    public void updatePetInfo(final PetInfoBean petInfoBean) {
        ApiUtils.getApiService().updatePetInfo(UserInstance.getUserInstance().getToken(),
                petInfoBean.pet_id, petInfoBean.description, petInfoBean.weight, petInfoBean.sex,
                petInfoBean.nick, petInfoBean.birthday, petInfoBean.logo_url, petInfoBean.pet_type_id
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    savePetInfo(petInfoBean);
                } else {
                    ToastUtil.showTost("更新失败");
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {
            }
        });

    }

    public void getPetLocation() {
        ApiUtils.getApiService().getPetLocation(UserInstance.getUserInstance().getUid(),
                UserInstance.getUserInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id()
        ).enqueue(new XMQCallback<PetLocationBean>() {
            @Override
            public void onSuccess(Response<PetLocationBean> response, PetLocationBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                EventManager.notifyPetLocationChange event= new EventManager.notifyPetLocationChange();
                if (ret == HttpCode.EC_SUCCESS) {

                     event.isnull=false;
                    latitude=message.latitude;
                    location_time=message.location_time;
                    longitude=message.longitude;
                    radius=message.radius;
                } else {
                    event.isnull=true;
                }
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFail(Call<PetLocationBean> call, Throwable t) {
                Toast.makeText(PetAppLike.mcontext, "位置获取失败~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public PetInfoBean getPackBean() {
        return packBean;
    }

    public void setPet_id(long pet_id) {
        packBean.pet_id = pet_id;
    }

    public void setDescription(String description) {
        packBean.description = description;
    }

    public void setWeight(String weight) {
        packBean.weight = weight;
    }

    public void setDevice_imei(String device_imei) {
        packBean.device_imei = device_imei;
    }

    public void setTarget_step(int target_step) {
        packBean.target_step = target_step;
    }

    public void setSex(int sex) {
        packBean.sex = sex;
    }

    public void setNick(String nick) {
        packBean.nick = nick;
    }

    public void setBirthday(String birthday) {
        packBean.birthday = birthday;
    }

    public void setDateFormat_birthday(Date date) {
        dateFormat_birthday = date;
        packBean.birthday = date.toString();
    }

    public void setLogo_url(String logo_url) {
        packBean.logo_url = logo_url;
    }

    public void setPet_type_id(int pet_type_id) {
        packBean.pet_type_id = pet_type_id;
    }

    public long getPet_id() {

        return packBean.pet_id;
    }

    public String getDescription() {
        return packBean.description;
    }

    public String getWeight() {
        return packBean.weight;
    }

    public String getDevice_imei() {
        return packBean.device_imei;
    }

    public int getTarget_step() {
        return packBean.target_step;
    }

    public int getSex() {
        return packBean.sex;
    }

    public String getNick() {
        return packBean.nick;
    }

    public String getBirthday() {
        return packBean.birthday;
    }

    public String getLogo_url() {
        return packBean.logo_url;
    }

    public int getPet_type_id() {
        return packBean.pet_type_id;
    }

    public void setAtHome(boolean bAtHome) {
        this.petAtHome = bAtHome;
        SPUtil.putPetAtHome(bAtHome);
    }

    public boolean getAtHome() {
        return petAtHome;
    }
}
