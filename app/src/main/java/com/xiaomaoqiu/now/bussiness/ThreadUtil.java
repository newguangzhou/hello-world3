package com.xiaomaoqiu.now.bussiness;

import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.push.PushEventManage;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 主要处理项目当中线程停止的
 * Created by 龙 on 2017/8/21.
 */

public class ThreadUtil {
    //刚开启紧急搜寻模式的前5分钟不做判断   1000 60 5
    public static Thread open_gps_donot_check_Thread;



    public static void open_gps_donot_check_Thread() {
        if (open_gps_donot_check_Thread == null) {
            try {
                open_gps_donot_check_Thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(300000);
                        } catch (Exception e) {

                        }

                        if (PetInfoInstance.getInstance().getAtHome()) {
                            EventBus.getDefault().post(new PushEventManage.petAtHome());
                        }
                        open_gps_donot_check_Thread = null;
                    }
                });
            } catch (Exception e) {

            }
            open_gps_donot_check_Thread.start();
        }

    }


}
