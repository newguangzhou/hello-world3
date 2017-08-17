package com.xiaomaoqiu.now.http;


import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.bean.AlreadyBindDeviceBean;
import com.xiaomaoqiu.now.bussiness.bean.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.LoginBean;
import com.xiaomaoqiu.now.bussiness.bean.MessageBean;
import com.xiaomaoqiu.now.bussiness.bean.PetInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.PetLocationBean;
import com.xiaomaoqiu.now.bussiness.bean.PetSportBean;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.bean.PictureBean;
import com.xiaomaoqiu.now.bussiness.bean.PetSleepInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.Summary;
import com.xiaomaoqiu.now.bussiness.bean.UserBean;
import com.xiaomaoqiu.now.bussiness.bean.WifiListBean;
import com.xiaomaoqiu.now.test.TestLocationBean;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by long
 */
public interface ApiService {
    /**
     * 手机号验证码登录
     * GET /user/saveLoginState?phone_num=18565353866&code=000000&device_type=1&device_token=644794830960f21d HTTP/1.1
     *
     * @param phone
     * @param verifyCode
     * @param deviceType
     * @param deviceId
     * @return
     */
    @GET(Constants.Url.User.login)
    Call<LoginBean> login(@Query("phone_num") String phone,
                          @Query("code") String verifyCode,
                          @Query("device_type") int deviceType,
                          @Query("device_token") String deviceId,
                          @Query("x_os_name") String x_os_name
    );

    /**
     * 获取验证码
     *
     * @param phone
     * @param deviceType
     * @return
     */
    @GET(Constants.Url.User.get_verify_code)
    Call<MessageBean> getVerifyCode(
            @Query("phone_num") String phone,
            @Query("type") int deviceType
    );

    /**
     * 获取用户基本信息
     *
     * @param uid
     * @param token
     * @return
     */
    @GET(Constants.Url.User.get_user_info)
    Call<UserBean> getUserInfo(
            @Query("uid") long uid,
            @Query("token") String token
    );


    //退出登录
    @GET(Constants.Url.User.logout)
    Call<BaseBean> logout(
            @Query("uid") long uid,
            @Query("token") String token
    );

    //增加宠物信息
    @GET(Constants.Url.Pet.add_pet_info)
    Call<PetInfoBean> addPetInfo(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("description") String description,
            @Query("weight") String weight,
            @Query("sex") int sex,
            @Query("nick") String nick,
            @Query("birthday") String birthday,
            @Query("pet_type_id") int pet_type_id,
            @Query("target_energy") String target_energy,
            @Query("recommend_energy") String recommend_energy,
            @Query("logo_url") String logo_url,
            @Query("imei") String imei

    );

    /**
     * 获取宠物信息
     * GET /pet/get_pet_info?uid=1462772127&token=74bbdb4db48e43c1cf1b59708aa89af4d2229150 HTTP/1.1
     *
     * @param uid
     * @param token
     * @return
     */
    @GET(Constants.Url.Pet.get_pet_info)
    Call<PetInfoBean> getPetInfo(@Query("uid") long uid,
                                 @Query("token") String token
    );

    //获取健康摘要
    // /pet/healthy/summary?uid=1462772127&token=3ad274401880013033544886746ceba9b2b08879&pet_id=1462786482 HTTP/1.1

    @GET(Constants.Url.Pet.summary)
    Call<Summary> getSummary(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("pet_id") long petId
    );


    /**
     * 查看宠物的运动信息
     * //GET /pet/healthy/get_activity_info?uid=1462772127&token=74bbdb4db48e43c1cf1b59708aa89af4d2229150&pet_id=1462786482&start_date=2017-4-7&end_date=2017-4-7 HTTP/1.1
     *
     * @param uid
     * @param token
     * @param petId
     * @param startDate
     * @param endDate
     * @return
     */
    @GET(Constants.Url.Pet.get_activity_info)
    Call<PetSportBean> getActivityInfo(@Query("uid") long uid,
                                       @Query("token") String token,
                                       @Query("pet_id") long petId,
                                       @Query("start_date") String startDate,
                                       @Query("end_date") String endDate
    );

    //设置运动数据
    @GET(Constants.Url.Pet.set_sport_info)
    Call<BaseBean> setSportInfo(@Query("uid") long uid,
                                @Query("token") String token,
                                @Query("pet_id") long petId,
                                @Query("target_step") int number,
                                @Query("target_energy") String target_energy
    );


    /**
     * 获取宠物的睡眠信息
     * http://120.24.152.121:9100/pet/healthy/get_sleep_info?uid=1462772127&token=3ad274401880013033544886746ceba9b2b08879&pet_id=1462786482&start_date=2017-3-9&end_date=2017-4-8
     *
     * @param uid
     * @param token
     * @param petId
     * @param start_date
     * @param end_date
     * @return
     */
    @GET(Constants.Url.Pet.get_sleep_info)
    Call<PetSleepInfoBean> getSleepInfo(@Query("uid") long uid,
                                        @Query("token") String token,
                                        @Query("pet_id") long petId,
                                        @Query("start_date") String start_date,
                                        @Query("end_date") String end_date
    );


    /**
     * 获取设备信息
     * http://120.24.152.121:9100/device/get_info?uid=1462772127&token=6ec0fa3507d6e205d8a8a6585d038ebaab7fac2b&pet_id=1462786482
     *
     * @param uid
     * @param token
     * @param petId
     * @return
     */
    @GET(Constants.Url.Device.get_info)
    Call<DeviceInfoBean> getDeviceInfo(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("pet_id") long petId
    );

    //绑定设备
    // URL	http://120.24.152.121:9100/device/add_device_info?uid=1462772127&token=a6468ef317503ac2f85221c013327040fe8ca1a3&imei=357396080000293&device_name=xmq_test
    @GET(Constants.Url.Device.add_device_info)
    Call<AlreadyBindDeviceBean> addDeviceInfo(@Query("uid") long uid,
                                              @Query("token") String token,
                                              @Query("imei") String imei,
                                              @Query("device_name") String deviceName
    );

    @GET(Constants.Url.Device.reboot_device)
    Call<BaseBean> rebootDevice(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("imei") String imei,
            @Query("pet_id") long petId
    );
    @GET(Constants.Url.User.agree_policy)
    Call<BaseBean> agreePolicy(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("imei") String imei,
            @Query("pet_id") long petId
    );

    /**
     * 发送获取wifi列表的指令
     */
    @GET(Constants.Url.Device.send_get_wifi_list_cmd)
    Call<BaseBean> sendGetWifiListCmd(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query(("pet_id")) long pet_id
    );

    /**
     * 获取wifi列表
     */
    @GET(Constants.Url.Device.get_wifi_list)
    Call<WifiListBean> getWifiList(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query(("pet_id")) long pet_id
    );

    /**
     * 设置homewifi
     */
    @POST(Constants.Url.Device.set_home_wifi)
    Call<BaseBean> setHomeWifi(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("wifi_ssid") String wifi_ssid,
            @Query("wifi_bssid") String wifi_bssid,
            @Query("pet_id") long petId,
            @Body RequestBody common_wifi

    );

    /**
     * 设置homelocation
     */
    @GET(Constants.Url.Device.set_home_location)
    Call<BaseBean> setHomeLocation(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("longitude") double longitude,
            @Query("latitude") double latitude
    );

    /**
     * 解除绑定
     * http://120.24.152.121:9100/device/remove_device_info?uid=1462772127&token=a6468ef317503ac2f85221c013327040fe8ca1a3&imei=357396080000293
     *
     * @param uid
     * @param token
     * @param imei
     * @return
     */
    @GET(Constants.Url.Device.remove_device_info)
    Call<BaseBean> removeDeviceInfo(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("imei") String imei
    );


//    /**
//    已经废弃
//     * 获取灯的状态
//     * http://120.24.152.121:9100/device/get_light_status?uid=1462772127&token=6ec0fa3507d6e205d8a8a6585d038ebaab7fac2b&imei=357396080000293&
//     *
//     * @param uid
//     * @param token
//     * @param device_imei
//     * @return
//     */
//    @GET(Constants.Url.Device.get_light_status)
//    Call<LightStatusBean> getLightStatus(@Query("uid") long uid,
//                                         @Query("token") String token,
//                                         @Query("device_imei") String device_imei
//    );


//    //http://120.24.152.121:9100/device/swicth_light?uid=1462772127&token=a6468ef317503ac2f85221c013327040fe8ca1a3&imei=357396080000293&light_status=1
//    @GET(Constants.Url.Device.swicth_light)
//    Call<BaseBean> switchLightStatus(@Query("uid") long uid,
//                                     @Query("token") String token,
//                                     @Query("device_imei") String device_imei,
//                                     @Query("light_status") int light_status
//    );

    /**
     * 获取宠物位置信息
     * GET /pet/location?uid=1462772127&token=514bc7dacae3e1164af799c84df57cb662a7378e&pet_id=1462786482 HTTP/1.1
     *
     * @param uid
     * @param token
     * @param petId
     * @return
     */
    @GET(Constants.Url.Pet.location)
    Call<PetLocationBean> getPetLocation(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("pet_id") long petId
    );


//    //测试使用
//    @GET(Constants.Url.Pet.location_test)
//    Call<TestLocationBean> getTestLocation(
//            @Query("uid") long uid,
//            @Query("token") String token,
//            @Query("pet_id") long petId
//    );

    /**
     * 获取宠物的状态
     * pet_status : 0：正常 1：遛狗 2:寻狗
     */
    @GET(Constants.Url.Pet.get_pet_stauts)
    Call<PetStatusBean> getPetStatus(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("pet_id") long petId
    );

    /**
     * 去运动或者回家
     * http://120.24.152.121:9100/pet/activity?uid=1462772127&token=a6468ef317503ac2f85221c013327040fe8ca1a3&pet_id=1462786482&activity_type=1
     * URL	http://120.24.152.121:9100/pet/activity?uid=1462772127&token=a6468ef317503ac2f85221c013327040fe8ca1a3&pet_id=1462786482&activity_type=2
     *
     * @param uid
     * @param token
     * @param petId
     * @param activity_type
     * @return
     */
    @GET(Constants.Url.Action.toActivity)
    Call<BaseBean> toActivity(@Query("uid") long uid,
                              @Query("token") String token,
                              @Query("pet_id") long petId,
                              @Query("activity_type") int activity_type
    );


    //上传头像
    @Multipart
    @POST(Constants.Url.Pet.upload_logo)
    Call<PictureBean> uploadLogo(
            @Query("uid") long uid,
            @Query("token") String token,
            @Part MultipartBody.Part file

    );


    //更新宠物信息
    @GET(Constants.Url.Pet.update_pet_info)
    Call<BaseBean> updatePetInfo(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("pet_id") long pet_id,
            @Query("description") String description,
            @Query("weight") String weight,
            @Query("sex") int sex,
            @Query("nick") String nick,
            @Query("birthday") String birthday,
            @Query("logo_url") String logo_url,
            @Query("pet_type_id") int pet_type_id,
            @Query("target_energy") String target_energy,
            @Query("recommend_energy") String recommend_energy,
            @Query("imei") String imei
    );

    //找狗模式
    @GET(Constants.Url.Action.findPet)
    Call<PetStatusBean> findPet(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("pet_id") long pet_id,
            @Query("find_status") int status
    );


    //上传建议
    @GET(Constants.Url.User.suggest)
    Call<BaseBean> suggest(
            @Query("uid") long uid,
            @Query("token") String token,
            @Query("message") String message,
            @Query("call_path") String call_path
    );

}