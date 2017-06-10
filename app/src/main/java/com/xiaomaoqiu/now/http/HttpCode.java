package com.xiaomaoqiu.now.http;

/**
 * Created by Administrator on 2015/6/29.
 */
public enum HttpCode {
    EC_UNDEFINED(-1),                 //未定义
    EC_SUCCESS(0),                    // 成功
    EC_FREQ_LIMIT(1),               // 频率限制
    EC_INVALID_VERIFY_CODE(2),      // 无效的验证码
    EC_USER_NOT_EXIST(3),           //用户不存在
    EC_PET_NOT_EXIST(4),            //宠物不存在
    EC_INVALID_TOKEN(5),            // 无效的token
    EC_USER_ALREADY_LOGINED(6),         // 用户已经登录
    EC_USER_NOT_LOGINED(7),         // 用户没有登录
    EC_SYS_ERROR(8),                // 系统错误
    EC_UNKNOWN_ERROR(9),           // 未知错误
    EC_INVALID_ARGS(10),            //无效的参数
    EC_ACCOUNT_FREEZED(11),         //账号被冻结
    EC_TOKEN_EXPIRED(12),           // token已过期
    EC_INVALID_PASS(14),            // 无效的密码
    EC_INVALID_SHOP_ID(16),         // 无效的店铺ID
    EC_INVALID_SHOP_GOODS_ID(17),   // 无效的商品ID
    EC_INVALID_AREA(18),            // 无效的区域
    EC_INVALID_LOCATION(19),        // 无效的位置信息
    EC_USER_ALREADY_REGISTERED(20),   // 已结注册
    EC_INVALID_FILE_TYPE(21),        //文件类型不对
    EC_DAY_REQUEST_COUNT_LIMITED(22),//每天的请求数量限制
    EC_SHOP_NOT_EXIST(23),           //店铺不存在
    EC_ALREADY_FAV(24),              //已经收藏或关注------设备被别人绑定了
    EC_NOT_FAV(25),                  //没有收藏或关注
    EC_INVALID_ACTIVITY(26),         //无效的活动
    EC_AUDITING(27),                 //审核中
    EC_AUDIT_FAILED(28),             //适合不通过
    EC_ACCOUNT_FREEZED_TEMP(29),     //账号被暂时冻结
    EC_DEVICE_NOT_EXIST(30),         //追踪器不存在
    EC_CURRENT_TIME_NOT_ALLOW(31),      //当前时段不允许
    EC_NODATA(32) ,                     //没有数据
    EC_LOGIN_IN_OTHER_PHONE(50),   //异地登录
    EC_OFFLINE(100)                    //设备离线
    ;

    HttpCode(int code) {
        value = code;
    }

    public int getValue() {
        return value;
    }

    public static HttpCode valueOf(int code) {
        switch (code) {
            case 0:
                return EC_SUCCESS;            // 成功
            case 1:
                return EC_FREQ_LIMIT;              // 频率限制
            case 2:
                return EC_INVALID_VERIFY_CODE;      // 无效的验证码
            case 3:
                return EC_USER_NOT_EXIST;           //用户不存在
            case 4:
                return EC_PET_NOT_EXIST;           //宠物不存在
            case 5:
                return EC_INVALID_TOKEN;           // 无效的token
            case 6:
                return EC_USER_ALREADY_LOGINED;        // 用户已经登录
            case 7:
                return EC_USER_NOT_LOGINED;        // 用户没有登录
            case 8:
                return EC_SYS_ERROR;                // 系统错误
            case 9:
                return EC_UNKNOWN_ERROR;           // 未知错误
            case 10:
                return EC_INVALID_ARGS;           //无效的参数
            case 11:
                return EC_ACCOUNT_FREEZED;         //账号被冻结
            case 12:
                return EC_TOKEN_EXPIRED;           // token已过期
            case 14:
                return EC_INVALID_PASS;            // 无效的密码
            case 16:
                return EC_INVALID_SHOP_ID;         // 无效的店铺ID
            case 17:
                return EC_INVALID_SHOP_GOODS_ID;   // 无效的商品ID
            case 18:
                return EC_INVALID_AREA;            // 无效的区域
            case 19:
                return EC_INVALID_LOCATION;        // 无效的位置信息
            case 20:
                return EC_USER_ALREADY_REGISTERED;   // 已结注册
            case 21:
                return EC_INVALID_FILE_TYPE;        //文件类型不对
            case 22:
                return EC_DAY_REQUEST_COUNT_LIMITED;//每天的请求数量限制
            case 23:
                return EC_SHOP_NOT_EXIST;           //店铺不存在
            case 24:
                return EC_ALREADY_FAV;              //已经收藏或关注
            case 25:
                return EC_NOT_FAV;                  //没有收藏或关注
            case 26:
                return EC_INVALID_ACTIVITY;         //无效的活动
            case 27:
                return EC_AUDITING;                //审核中
            case 28:
                return EC_AUDIT_FAILED;            //适合不通过
            case 29:
                return EC_ACCOUNT_FREEZED_TEMP;     //账号被暂时冻结
            case 30:
                return EC_DEVICE_NOT_EXIST;         //追踪器不存在
            case 31:
                return EC_CURRENT_TIME_NOT_ALLOW;      //当前时段不允许
            case 32:
                return EC_NODATA;                     //没有数据
            case 50:
                return EC_LOGIN_IN_OTHER_PHONE;    //异地登录
            case 100:
                return EC_OFFLINE;  //设备离线
            default:
                return EC_UNDEFINED;
        }
    }

    private int value;
}
