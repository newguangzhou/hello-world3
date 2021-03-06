package com.xiaomaoqiu.now;

/**
 * Created by long on 17/4/6.
 */

public class Constants {
    public final static String DEFAULT_TAG = "xiaomaoqiu";


    public static final int DEVICE_TYPE = 1;

    public static int Male = 1;
    public static int Female = 2;
    //去运动
    public static final int TO_SPORT_ACTIVITY_TYPE = 1;
    //回家
    public static final int TO_HOME_ACTIVITY_TYPE = 0;
    /**
     * pet_status
     * 0：正常 1：遛狗 2:寻狗
     */
    public static final int PET_STATUS_COMMON = 0;
    public static final int PET_STATUS_WALK = 1;
    public static final int PET_STATUS_FIND = 2;

    /**
     * 关闭紧急搜索  2
     * 开启紧急搜索  1
     */
    public static final int GPS_OPEN = 1;
    public static final int GPS_CLOSE = 2;

    /**
     * # 1 在家  0 不在家
     */
    public static final int PET_AT_HOME = 1;
    public static final int PET_OUT_HOME = 0;


    public static class Url {
        //        public static String Host = "http://47.93.249.1:9100/";
//        public static String File_Host="http://47.93.249.1:9700/";
//        public static String Host = "http://120.24.152.121:9100/";
        public static String Host = "https://gateway.xiaomaoqiu.com/";
        //        public static String File_Host = "http://120.24.152.121:9700/";
        public static String File_Host = "http://gateway.xiaomaoqiu.com:9700/";

        //用户相关
        public static class User {
            /**
             * 获取验证码
             */
            public static final String get_verify_code = "user/get_verify_code";
            /**
             * 获取用户基本信息
             */
            public static final String get_user_info = "user/get_base_infomation";
            /**
             * 登录
             */
            public static final String login = "user/login";
            /**
             * 退出登录
             */
            public static final String logout = "user/logout";
            /**
             * 注册
             */
            public static final String register = "user/register";

            /**
             * 提交建议
             */
            public static final String suggest = "user/suggest";

            /**
             * 是否同意用户协议
             */
            public static final String agree_policy = "user/agree_policy";


        }


        //宠物相关
        public static class Pet {
            /**
             * 健康摘要
             */
            public static final String summary = "pet/healthy/summary";
            /**
             * 睡眠信息
             */
            public static final String get_sleep_info = "pet/healthy/get_sleep_info";
            /**
             * 运动信息
             */
            public static final String get_activity_info = "pet/healthy/get_activity_info";
            /**
             * 设置运动信息
             */
            public static final String set_sport_info = "pet/healthy/set_sport_info";

            /**
             * 增加宠物信息
             */
            public static final String add_pet_info = "pet/add_pet_info";
            /**
             * 获取宠物信息
             */
            public static final String get_pet_info = "pet/get_pet_info";
            /**
             * 获取宠物的状态
             * pet_status : 0：正常 1：遛狗 2:寻狗
             */
            public static final String get_pet_stauts = "pet/get_pet_status";
            /**
             * 更新宠物信息
             */
            public static final String update_pet_info = "pet/update_pet_info";
            /**
             * 上传头像
             */
            public static final String upload_logo = "file/pet/upload_logo";
            /**
             * 位置
             */
            public static final String location = "pet/location";


        }


        //设备相关
        public static class Device {
            /**
             * 重启设备
             */
            public static final String reboot_device = "/device/reboot_device_cmd";

            /**
             * 获取设备信息
             */
            public static final String get_info = "device/get_info";


            //发送获取wifi的指令
            public static final String send_get_wifi_list_cmd = "device/send_get_wifi_list_cmd";
            /**
             * 获取wifi列表
             */
            public static final String get_wifi_list = "device/get_wifi_list";
            /**
             * 设置homewifi
             */
            public static final String set_home_wifi = "user/set_home_wifi";
            /**
             * 设置homelocation
             */
            public static final String set_home_location = "user/set_home_location";
//            /**
//             * 开关灯
//             */
//            public static final String swicth_light = "device/switch_light";
//            /**
//             * 获取灯的状态
//             */
//            public static final String get_light_status = "device/get_light_status";
            /**
             * 添加设备信息
             */
            public static final String add_device_info = "device/add_device_info";
            /**
             * 移除设备信息
             */
            public static final String remove_device_info = "device/remove_device_info";
            /**
             * 设置sim卡信息
             */
            public static final String set_sim_info = "device/set_sim_info";


        }


        //找狗模式
        public static class Action {
            public static final String findPet = "pet/find";

            /**
             * d宠物动作，去运动还是回家
             */
            public static final String toActivity = "pet/activity";


        }
    }
}
