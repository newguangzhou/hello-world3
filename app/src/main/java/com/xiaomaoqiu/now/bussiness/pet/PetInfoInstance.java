package com.xiaomaoqiu.now.bussiness.pet;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.PetInfoBean;
import com.xiaomaoqiu.now.bean.nocommon.PetLocationBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.test.TestInstance;
import com.xiaomaoqiu.now.test.TestLocationBean;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
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

    public long location_time;

    public double longitude;

    public double radius;

    public boolean petAtHome = true; //true - 宠物在家, false - 宠物活动中


    private PetInfoInstance() {

    }

    public static PetInfoInstance getInstance() {
        if (instance == null) {
            instance = new PetInfoInstance();
            PetInfoBean baseBean = new PetInfoBean();
            instance.packBean = new PetInfoBean();
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

    static public class MyDate {
        public MyDate(int y, int m, int d) {
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
        if (message.pet_id > 0) {
            packBean.pet_id = message.pet_id;
            SPUtil.putPetId(packBean.pet_id);
            UserInstance.getInstance().pet_id = packBean.pet_id;
        }
        if (!TextUtils.isEmpty(message.name)) {
            packBean.name = message.name;
            SPUtil.putPetName(packBean.name);
        }
        if (!TextUtils.isEmpty(message.description)) {
            packBean.description = message.description;
            SPUtil.putPetDescription(packBean.description);
        }
        if (!TextUtils.isEmpty(message.weight)) {
            packBean.weight = message.weight;
            SPUtil.putWeight(packBean.weight);
        }
        if (!TextUtils.isEmpty(message.device_imei)) {
            packBean.device_imei = message.device_imei;
            SPUtil.putDeviceImei(packBean.device_imei);
            UserInstance.getInstance().device_imei = packBean.device_imei;
        }
        if (message.target_step != 0) {
            packBean.target_step = message.target_step;
            SPUtil.putPetTargetStep(packBean.target_step);
        }
        packBean.sex = message.sex;
        SPUtil.putSex(packBean.sex);

        if (!TextUtils.isEmpty(message.nick)) {
            packBean.nick = message.nick;
            SPUtil.putPetNick(packBean.nick);
        }
        if (!TextUtils.isEmpty(message.birthday)) {
            packBean.birthday = message.birthday;
            SPUtil.putBirthday(packBean.birthday);
        }
        if (!TextUtils.isEmpty(message.logo_url)) {
            packBean.logo_url = message.logo_url;
            SPUtil.putPetHeader(packBean.logo_url);
        }
        packBean.pet_type_id = message.pet_type_id;
        SPUtil.putPetTypeId(packBean.pet_type_id);
        setDateFormat_birthday(packBean.birthday);


    }

    public void setDateFormat_birthday(String birthdayString) {
        if (TextUtils.isEmpty(birthdayString)) {

            return;
        }
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
        packBean.dateFormat_birthday = new MyDate(year, month, day);
    }


    //添加宠物信息
    public void addPetInfo(PetInfoBean petInfoBean) {
        ApiUtils.getApiService().addPetInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                petInfoBean.description, petInfoBean.weight, petInfoBean.sex, petInfoBean.nick,
                petInfoBean.birthday, petInfoBean.pet_type_id
        ).enqueue(new XMQCallback<PetInfoBean>() {
            @Override
            public void onSuccess(Response<PetInfoBean> response, PetInfoBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        savePetInfo(message);
                        EventBus.getDefault().post(new EventManage.addPetInfoSuccess());
                        break;
                    case EC_INVALID_ARGS:
                        ToastUtil.showTost("请填写完整信息");
                        break;
                    case EC_ALREADY_FAV:
                        ToastUtil.showTost("已绑定！");
                        break;
                }
            }

            @Override
            public void onFail(Call<PetInfoBean> call, Throwable t) {

            }
        });


    }

    //获取宠物基本信息
    public void getPetInfo() {
        ApiUtils.getApiService().getPetInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken())
                .enqueue(new XMQCallback<PetInfoBean>() {
                    @Override
                    public void onSuccess(Response<PetInfoBean> response, PetInfoBean message) {
                        HttpCode ret = HttpCode.valueOf(message.status);
                        switch (ret) {
                            case EC_SUCCESS:
                                savePetInfo(message);
                                EventBus.getDefault().post(new EventManage.notifyPetInfoChange());
                                //获取设备信息
                                DeviceInfoInstance.getInstance().getDeviceInfo();
                                break;
                            case EC_PET_NOT_EXIST:
//                                ToastUtil.showTost("您还没有宠物");
                                clearPetInfo();
                                EventBus.getDefault().post(new EventManage.notifyPetInfoChange());
                                break;
                            default:
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
        ApiUtils.getApiService().updatePetInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                petInfoBean.pet_id, petInfoBean.description,petInfoBean.weight,petInfoBean.sex,petInfoBean.nick,petInfoBean.birthday,petInfoBean.logo_url,petInfoBean.pet_type_id
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    ToastUtil.showTost("更新成功");
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


    public void clearPetInfo() {
        packBean.pet_id = 0;


        SPUtil.putPetId(0);

        UserInstance.getInstance().pet_id = 0;

        packBean.name = "";
        SPUtil.putPetName("");
        packBean.description = "";
        SPUtil.putPetDescription("");
        packBean.weight = "";
        SPUtil.putWeight("");
        packBean.device_imei = "";
        SPUtil.putDeviceImei("");
        packBean.target_step = 0;
        SPUtil.putPetTargetStep(packBean.target_step);
        packBean.sex = 2;
        SPUtil.putSex(2);
        packBean.nick = "";
        SPUtil.putPetNick("");
        packBean.birthday = "";
        SPUtil.putBirthday("");
        packBean.logo_url = "";
        SPUtil.putPetHeader("");
        packBean.pet_type_id = 0;
        SPUtil.putPetTypeId(0);
        setDateFormat_birthday("");
    }

    public void getPetLocation() {
        ApiUtils.getApiService().getPetLocation(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id()
        ).enqueue(new XMQCallback<PetLocationBean>() {
            @Override
            public void onSuccess(Response<PetLocationBean> response, PetLocationBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    if (message.latitude > 0.0d) {
                        EventManage.notifyPetLocationChange event = new EventManage.notifyPetLocationChange();
                        latitude = message.latitude;
                        location_time = message.location_time;
                        longitude = message.longitude;
                        radius = message.radius;
                        EventBus.getDefault().post(event);
                    } else {
                        ToastUtil.showTost("位置获取失败！");
                    }

                }
            }

            @Override
            public void onFail(Call<PetLocationBean> call, Throwable t) {
                Toast.makeText(PetAppLike.mcontext, "位置获取失败~", Toast.LENGTH_SHORT).show();
            }
        });

//        TestInstance.getTestInstance().getTestLocation();

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

    public void setDateFormat_birthday(MyDate date) {
        packBean.dateFormat_birthday = date;
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
        EventManage.atHomeOrtoSport event = new EventManage.atHomeOrtoSport();
        EventBus.getDefault().post(event);
    }

    public boolean getAtHome() {
        return petAtHome;
    }
}
