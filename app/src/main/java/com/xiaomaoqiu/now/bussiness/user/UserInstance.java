package com.xiaomaoqiu.now.bussiness.user;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.bussiness.bean.LoginBean;
import com.xiaomaoqiu.now.bussiness.bean.UserBean;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.SPUtil;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */

public class UserInstance {
    private static UserInstance userInstance;

    private UserInstance() {
    }

    public static UserInstance getInstance() {
        if (userInstance == null) {
            userInstance = new UserInstance();
            userInstance.m_bLogin = SPUtil.getLoginStatus();
            userInstance.m_strPhone = SPUtil.getPhoneNumber();
            userInstance.m_uid = SPUtil.getUid();
            userInstance.m_strToken = SPUtil.getToken();

            userInstance.pet_id = SPUtil.getPetId();
            userInstance.device_imei = SPUtil.getDeviceImei();
            userInstance.wifi_bssid = SPUtil.getHomeWifiMac();
            userInstance.wifi_ssid = SPUtil.getHomeWifiSsid();
            userInstance.has_reboot=SPUtil.getHasReboot();

            userInstance.longitude=Double.valueOf(SPUtil.getHOME_LONGITUDE());
            userInstance.latitude=Double.valueOf(SPUtil.getHOME_LATITUDE());

        }
        return userInstance;
    }

    public boolean m_bLogin = false;
    public String m_strPhone = "";
    public String m_strToken = "";
    public long m_uid = 0;


    //判断业务流程相关
    public long pet_id;

    public String device_imei;

    public String wifi_bssid;

    public String wifi_ssid;

    public int has_reboot;//更新数据后是否已经重启过设备，0：未重启，1：已重启

    public double longitude;//home_longitude

    public double latitude;//home_latitude

    //获取用户基本信息
    public void getUserInfo() {
        //获取用户基本信息
        ApiUtils.getApiService().getUserInfo(UserInstance.getInstance().getUid(),
                UserInstance.getInstance().getToken()
        ).enqueue(new XMQCallback<UserBean>() {
            @Override
            public void onSuccess(Response<UserBean> response, UserBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        UserInstance.getInstance().saveUserInfo(message);
                        break;
                }
                EventBus.getDefault().post(new EventManage.getUserInfoEvent());

            }

            @Override
            public void onFail(Call<UserBean> call, Throwable t) {
                EventBus.getDefault().post(new EventManage.getUserInfoEvent());
            }
        });

    }

    public void saveLoginState(LoginBean message, String strPhone) {
        m_bLogin = true;
        m_uid = message.uid;
        m_strToken = message.token;
        m_strPhone = strPhone;

        SPUtil.putPhoneNumber(strPhone);
        SPUtil.putLoginStatus(true);
        SPUtil.putUid(message.uid);
        SPUtil.putToken(message.token);
    }


    public void clearLoginInfo() {
        m_bLogin = false;
        m_uid = -1;
        m_strToken = "";
        SPUtil.putLoginStatus(false);
        SPUtil.putUid(m_uid);
        SPUtil.putToken("");

        longitude=-1;
        SPUtil.putHOME_LONGITUDE("-1");
        latitude=-1;
        SPUtil.putHOME_LATITUDE("-1");

    }

    public void saveUserInfo(UserBean userBean) {
        device_imei = userBean.device_imei;
        SPUtil.putDeviceImei(device_imei);
        pet_id = userBean.pet_id;
        SPUtil.putPetId(pet_id);
        wifi_bssid = userBean.wifi_bssid;
        SPUtil.putHomeWifiMac(wifi_bssid);
        wifi_ssid = userBean.wifi_ssid;
        SPUtil.putHomeWifiSsid(wifi_ssid);
        has_reboot = userBean.has_reboot;
        SPUtil.putHasReboot(has_reboot);

        longitude=userBean.longitude;
        SPUtil.putHOME_LONGITUDE(longitude+"");

        latitude=userBean.latitude;
        SPUtil.putHOME_LATITUDE(latitude+"");


    }

    public long getUid() {
        return m_uid;
    }

    public String getToken() {
        return m_strToken;
    }

}
