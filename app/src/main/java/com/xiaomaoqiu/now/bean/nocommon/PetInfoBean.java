package com.xiaomaoqiu.now.bean.nocommon;

import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;

/**
 * Created by long on 17/4/7.
 */

public class PetInfoBean extends BaseBean{
//    public int status;

    public long pet_id;

    public String name;

    public String description;

    public String weight;

    public String device_imei;

    public int target_step;

    public int sex;

    public String nick;

    public String birthday;

    public String logo_url;

    public int pet_type_id;

    public String enerty_type;


    //wifi 名称
    public String wifi_ssid;
    //wifi mac地址
    public String wifi_bssid;


    public PetInfoInstance.MyDate dateFormat_birthday=new PetInfoInstance.MyDate(2000, 1, 1);


}
