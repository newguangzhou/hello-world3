package com.xiaomaoqiu.now.http;


import com.xiaomaoqiu.now.Constants;

/**
 * Created by long
 */
public interface ApiService {
    @GET(Constants.Url.User.login)
    Call<> login(@Query("phone") String phone,
                 @Query("verifyCode") String verifyCode,
                 @Query("deviceType") int deviceType,
                 @Query("deviceId") String deviceId
                 );


}
//    /**
//     * 获取热门职位
//     * http://appconfig.chinahr.com/app/getRecommendConfig
//     */
//    @POST("app/getRecommendConfig")
//    Call<String> getRecommendJob();
//
//    // 投递记录
//    @FormUrlEncoded
//    @POST("native/getDeliveryFeedback/{uid}")
//    Call<CommonNet<List<MessageDeliverBean>>> getDeliveryFeedback(@Path("uid") String uid,
//                                                                  @Field("state") String state,
//                                                                  @Field("page") String page);
//
//    /**
//     * B端职位管理
//     *
//     * @param page
//     * @param status
//     * @return
//     */
//    @GET("bjobmanager/app/job/list")
//    Call<JobManager> getJobManagerList(@Query("page") String page, @Query("status") String status);
//
//    /**
//     * B端推荐简历
//     */
//    @GET("multip/opAndCv")
//    Call<NewMultipRecommendBean> getBRecommendResume(@Query("page") String page,
//                                                     @Query("keyword") String keyword,
//                                                     @Query("jobId") String onlinejobid,
//                                                     @Query("role") String role
//    );
//    /**
//     * B端搜索结果页
//     */
//    @GET("cvapp/search")
//    Call<String> getBSearchResume(
//            @Query("keyword") String keyword,
//            @Query("live") String live,
//            @Query("workExp")String workExp,
//            @Query("degree")String degree,
//            @Query("reFreshTime")String reFreshTime,
//            @Query("page")String page,
//            @Query("searchType")String searchType
//    );
//
//    /**
//     *C端获取热词
//     */
//    @POST("common/getHotKeyWordDB")
//    Call<ResponseBody> getHotKeyWord();
//    /**
//     * B端热门职位
//     */
//    @GET("common/getHotJob")
//    Call<String> getHotJobs();
//
//    /**
//     * B端热词联想
//     */
//    @GET("searchsuggest_yingcaijob.do")
//    Call<ResponseBody> getKeyword(@Query("inputbox") String inputbox,
//                                  @Query("num") String num,
//                                  @Query("callback") String callback);
//
//    /**
//     * C端热词联想
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("app/job/associateWord")
//    Call<CommonNet<KeywordContainer>> getCKeyword(@Field("keyword") String keyword,
//                                  @Field("cityId") String cityId);
//
//
////    https://app.chinahr.com/app/job/associateWord
//
//    /**
//     * 完善个人信息接口
//     */
//    @FormUrlEncoded
//    @POST("buser/lackJob")
//    Call<CommonJson> lackJob(@Field("lackJob") String lackjob);
//
//    /**
//     * 检查公司名字
//     */
//    @FormUrlEncoded
//    @POST("buser/checkCompanyInfo")
//    Call<CommonOK> checkCompanyName(@Field("name") String name);
//
//    /**
//     * 完善公司地区信息
//     **/
//    @FormUrlEncoded
//    @POST("buser/setCompAddrInfo")
//    Call<CommonOK> setCompAddrInfo(@Field("location") String location, @Field("addr") String addr, @Field("compDesc") String compDesc);
//
//    /**
//     * ip定位
//     **/
//    @POST("native/nearCities")
//    Call<LocationIpBean> getLocationIp();
//    /**
//     * ip定位
//     **/
//    @POST("native/nearCities")
//    Call<String> getLocationIps();
//
//
//    /**
//     * 完善公司信息
//     **/
//    @FormUrlEncoded
//    @POST("buser/setCompanyInfo")
//    Call<CommonOK>  setCompanyInfo(@Field("name") String name,@Field("sizeId") int sizeId,@Field("typeId") int typeId,@Field("industryId") String industryId);
//
//    /**
//     * 完善个人信息接口
//     **/
//    @FormUrlEncoded
//    @POST("buser/setUserInfo")
//    Call<CommonOK>  updateBusinessInfo(@Field("nickname") String nickname, @Field("position") String position);
//
//
//    /**
//     * 极光推送上传ID
//     **/
//    @FormUrlEncoded
//    @POST("saveRegistrationId")
//    Call<CommonOK> postRegistrationID(@Field("registrationId") String registrationId);
//
//
//    /**
//     * 查询用户信息是否完整
//     **/
//    @GET("buser/getUserInfo")
//    Call<UserInfoContainer> getBUserInfo();
//
//    /**
//     * 芝麻信用提交用户信息
//     **/
//    @POST("bjobmanager/getZmUrl")
//    @FormUrlEncoded
//    Call<ZmContainer> postZmCredit(@Field("cardName") String userName, @Field("cardId") String idCard);
//
//
//    /**
//     * 获取芝麻信用分
//     **/
//    @FormUrlEncoded
//    @POST("bjobmanager/app/job/authRelation")
//    Call<String> bossCheckIfCanChat(@Field("qString") String qString, @Field("authVersion") String authVersion);
//
//    @POST("bjobmanager/zmget")
//    @FormUrlEncoded
//    Call<ZmScoreContainer> getZmScore(@Field("params") String param);
//
//    /**
//     * 进入应用实时埋点
//     **/
//    @POST("data/appOn")
//    @FormUrlEncoded
//    Call<CommonOK> pushEnterPointer(@Field("data") String param);
//
//    /**
//     * 数据埋点接口 基础埋点
//     **/
//    @POST("data/offline")
//    @FormUrlEncoded
//    Call<CommonOK> pushBasePointer(@Field("data") String param);
//
//    /**
//     * 推出应用实时埋点
//     **/
//    @POST("data/appOff")
//    @FormUrlEncoded
//    Call<CommonOK> pushOutPointer(@Field("data") String param);
//
//    /**
//     * 进入页面埋点
//     **/
//    @POST("data/pageIn")
//    @FormUrlEncoded
//    Call<CommonOK> pushInPagePointer(@Field("data") String param);
//
//    /**
//     * 退出页面埋点
//     **/
//    @POST("data/pageOut")
//    @FormUrlEncoded
//    Call<CommonOK> pushOutPagePointer(@Field("data") String param);
//
//    /**
//     * 推荐页实时埋点
//     **/
//    @POST("data/recommend")
//    @FormUrlEncoded
//    Call<CommonOK> pushRecommendPointer(@Field("data") String param);
//
//    /**
//     * 登出
//     **/
//    @GET("loginout")
//    Call<CommonOK>  loginOut();
//
//    /**
//     * C端普通登录和快捷登录
//     *
//     * @param input   手机号
//     * @param pwd     密码
//     * @param source  来源
//     * @param msgCode 验证码
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("login")
//    Call<UserModel> userLogin(@Field("input") String input, @Field("pwd") String pwd, @Field("source") String source, @Field("msgCode") String msgCode);
//
//    /**
//     * 动态登陆判断手机号是否注册过
//     * @param input 手机号
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("existLoginName")
//    Call<CommonOK> confirMobile(@Field("input") String input);
//
//    /**
//     * C端获取验证码
//     * @param mobile 手机号
//     * @param tmpid 验证码
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("sendMsg")
//    Call<CommonOK> getCode(@Field("mobile") String mobile,@Field("tmpid") String tmpid);
//
//    /**
//     * C端短信用户注册
//     * @param mobile 手机号
//     * @param pwd 密码
//     * @param msgCode 验证码
//     * @param t 标志位
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("c/register")
//    Call<UserModel> register(@Field("mobile") String mobile,@Field("pwd") String pwd,@Field("msgCode") String msgCode,@Field("t") String t);
//
//    /**
//     * 获取token
//     * @param source 来源0企业端  1求职者
//     * @return 解析的json
//     */
//    @POST("token")
//    @FormUrlEncoded
//    Call<UserModel> getToken(@Field("source") String source);
//
//    /**
//     * 企业端的是否可面试的设置 QY_DOMAIN
//     * @return 解析的json
//     */
//    @GET("cvapp/handle")
//    Call<String>  setResumeWaithandleStatus(@Query("cvid") String cvid,@Query("jobid") String jobid,@Query("status") String status);
//
//    /**
//     * 企业端的简历管理 QY_DOMAIN
//     * @return 解析的json
//     */
//    @GET("cvapp/cvlist")
//    Call<String>  getResumeManagerList(@Query("jobid") String jobid,@Query("status") String status,@Query("page") String page);
//
//    /**
//     * 职位搜索(4.0)   QYAPI_DOMAIN
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("app/job/getJobListInSearch")
//    Call<String> searchJob(@Field("keyword") String keyword,@Field("cityId") String cityId,@Field("industry") String industry,@Field("salary") String salary
//            ,@Field("refTime") String refTime,@Field("degree") String degree,@Field("work_age") String work_age,@Field("companyType") String companyType
//            ,@Field("workType") String workType,@Field("positionType") String positionType,@Field("currpage") String currpage);
//
//
//    /**
//     * 职位搜索(5.6.0)   QYAPI_DOMAIN
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("app/job/v2/getJobListInSearch")
//    Call<String> searchJobV2(@Field("keyword") String keyword,@Field("cityId") String cityId,@Field("industry") String industry,@Field("salary") String salary
//            ,@Field("refTime") String refTime,@Field("degree") String degree,@Field("work_age") String work_age,@Field("companyType") String companyType
//            ,@Field("workType") String workType,@Field("positionType") String positionType,@Field("currpage") String currpage,@Field("comId") String comId);
//
//
//    /**
//     * 职位搜索(4.0)   QYAPI_DOMAIN
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("app/job/getJobRecommend")
//    Call<String> getJobRecommend(@Field("keyword") String keyword,@Field("cityId") String cityId,@Field("industry") String industry,@Field("salary") String salary
//            ,@Field("refTime") String refTime,@Field("degree") String degree,@Field("work_age") String work_age,@Field("companyType") String companyType
//            ,@Field("workType") String workType,@Field("positionType") String positionType,@Field("currpage") String currpage);
//
//    /**
//     * 新版获取验证码   MYAPI_DOMAIN
//     * @param mobile 手机号加密
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("account/sendMsg")
//    Call<CommonOK> newGetCode(@Field("mobile") String mobile);
//
//    /**
//     * 创建简历第一步获取短信验证码   MYAPI_DOMAIN
//     * @param mobile 手机号加密
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("account/sendMsg")
//    Call<CommonOK> newGetCode(@Field("mobile") String mobile,@Field("smsType") String tmpid);
//
//    /**
//     * 创建简历第二步 MYAPI_DOMAIN
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("createCv/cvSecond")
//    Call<String> postMineResumeForJob(@Field("expid") String expid,@Field("eduid") String eduid,@Field("cvid") String cvid,@Field("degree") String degree
//            ,@Field("college") String college,@Field("major") String major,@Field("eduFinishYear") String eduFinishYear
//            ,@Field("JobName") String JobName,@Field("comName") String comName,@Field("starDate") String starDate
//            ,@Field("endDate") String endDate,@Field("comid") String comid,@Field("jobid") String jobid
//            ,@Field("expType") String expType,@Field("noPractice") String noPractice);
//
//    /**
//     * 更新密码接口
//     * @param input 手机号
//     * @param pwd 密码
//     * @param msgCode 验证码
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("c/updatePwd")
//    Call<UserModel> folderPassword(@Field("input") String input, @Field("pwd") String pwd,@Field("msgCode") String msgCode);
//
//    /**
//     * 第三方登录的接口
//     * @param source 来源
//     * @param platformId
//     * @param openId
//     * @param unionId
//     * @param accessToken
//     * @return 解析的json
//     */
//    @POST("login")
//    @FormUrlEncoded
//    Call<UserModel> getloginextralib(@Field("source") String source, @Field("platformId") String platformId,@Field("openId") String openId,@Field("unionId") String unionId,@Field("accessToken") String accessToken);
//
//    /**
//     * 微信获取token
//     * @param url url
//     * @return
//     */
//    @POST
//    Call<String> getWXAccessToken(@Url String url);
//
//
//    /**
//     * B端登录
//     * @param input 手机号
//     * @param pwd 密码
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("login")
//    Call<UserModel> companyLogin(@Field("input") String input, @Field("pwd") String pwd, @Field("source") String source, @Field("msgCode") String msgCode);
//
//    /**
//     * 判断企业端的页面是否注册过
//     * @param mobile 手机号
//     * @param pass 密码
//     * @param code 验证码
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("buser/app/getp/reset")
//    Call<UserModel> companyForgetPasswordCommit(@Field("mobile") String mobile, @Field("pass") String pass, @Field("code") String code);
//
//
//    /**
//     * 创建简历第一步
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("createCvV4/cvFirst")
//    Call<String> addMineResumePersonalInfo(@Field("name") String name, @Field("gender") String gender,
//                                           @Field("mobile") String mobile, @Field("email") String mailEncode,
//                                           @Field("expJobCategory") String expJobCategoryids,@Field("BirthTime") String BirthTime,
//                                           @Field("livingCity") String livingCity,@Field("workYear") String workYear,
//                                           @Field("imgCode") String imgCode,@Field("smsCode") String smsCode,
//                                           @Field("comid") String comid,@Field("jobid") String jobid,
//                                           @Field("cvid") String cvid);
//
//
//    /**
//     * 一键投递  MYAPI_DOMAIN
//     * @param uid
//     * @param jobids
//     * @param cvid
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("native/deliverBatchResume/{uid}")
//    Call<String> getOneDeilver(@Path("uid") String uid,@Field("jobids") String jobids,@Field("cvid") String cvid);
//
//
//    /**
//     * http://my.api.chinahr.com/deliver/batch
//     * C端批量投递
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("deliver/batch")
//    Call<String> getBatch_new(@Field("jobids") String jobids, @Field("source") int source, @Field("cvid") String cvid,@Field("recommendExt") String recommendExt);
//
//    // TODO: 2016/9/19 下面这个   5.4  删除
//
//    /**
//     * http://my.api.chinahr.com/deliver/batch
//     * C端批量投递
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("deliver/batch")
//    Call<CommonNet<BatchContainer> > getBatch(@Field("jobids") String jobids,@Field("source") int source,@Field("cvid") String cvid,@Field("recommendExt") String recommendExt);
//
//    /**
//     * 新的58的意见反馈 APPCONFING_DOMAIN
//     * @return 解析json
//     */
//    @FormUrlEncoded
//    @POST("feedback")
//    Call<String> getUserFeedback(@Field("callback") String callback,@Field("interfaceType") String interfaceType,@Field("comments") String comments
//            ,@Field("commentSource") String commentSource,@Field("contact") String contact,@Field("commentType") String commentType
//            ,@Field("userId") String userId,@Field("deviceType") String deviceType,@Field("deviceId") String deviceId
//            ,@Field("osVersion") String osVersion,@Field("appVersion") String appVersion,@Field("method") String method);
//
//    /**
//     * 获取token MYAPI_DOMAIN
//     * @return 解析json
//     */
//    @GET("cv/getCvToken")
//    Call<String> getResumeAttachmentPreviewToken(@Query("cvid") String cvid);
//
//    /**
//     * 获取url列表 APPCONFING_DOMAIN
//     * @return 解析json
//     */
//    @GET("common/getUrlList")
//    Call<String> getUrlList();
//
//    /**
//     * 沟通设置 QY_DOMAIN
//     * @return 解析json
//     */
//    @FormUrlEncoded
//    @POST("buser/app/talk/set")
//    Call<String> setCommunicateOpenClose(@Field("talkflag") String talkflag);
//
//    /**
//     * 沟通设置 QY_DOMAIN
//     * @return 解析json
//     */
//    @POST("bjobmanager/checkJobList")
//    Call<CheckHireContainer> checkJobList();
//
//    /**
//     * 企业端的职位详情操作职位 QY_DOMAIN
//     * @return 解析json
//     */
//    @GET("bjobmanager/app/job/handler")
//    Call<String> setCompanyJobStatus(@Query("jobId") String jobId,@Query("op") String op);
//
//    /**
//     * 企业端的职位详情页面  QY_DOMAIN
//     * @return 解析json
//     */
//    @GET("bjobmanager/app/job/getJob")
//    Call<String> getCompanyJobDetail(@Query("jobId") String jobId);
//
//    /**
//     * 获取职位详情  by 关系   QY_DOMAIN
//     * @return 解析json
//     */
//    @GET("bjobmanager/app/job/getCurrTalkJob")
//    Call<String> getJobDetailByRelation(@Query("cuserid") String cuserid);
//
//    /**
//     * 设置B端个人信息 QY_DOMAIN
//     * @return 解析json
//     */
//    @FormUrlEncoded
//    @POST("buser/app/info/set")
//    Call<CommonOK> setPersonalInfo(@Field("nickname") String nickname,@Field("position") String position,@Field("devcoe") String devcoe);
//
//    /**
//     * 判断企业端的页面是否注册过
//     * mobile 加密
//     * @return 解析json
//     */
//    @FormUrlEncoded
//    @POST("buser/app/getp/first")
//    Call<CommonOK> companyForgetPassword(@Field("mobile") String mobile);
//
//    /**
//     * 获取企业端忘记密码获取手机号验证码  QY_DOMAIN
//     * @param mobile 手机号加密
//     * @return 解析json
//     */
//    @FormUrlEncoded
//    @POST("buser/app/getp/gcode")
//    Call<CommonOK> getCompanyForgetPasswordMsgCode(@Field("mobile") String mobile);
//
//    /**
//     * 测试C端是否可以聊天
//     **/
//    @FormUrlEncoded
//    @POST("authRelation")
//    Call<String> clientCheckIfCanChat(@Field("qString") String qString, @Field("authVersion") String authVersion);
//
//
//    /**
//     * 获取芝麻信用结果--职位发布成功页面
//     */
//    @POST("bjobmanager/getZMResult")
//    Call<ZMPostJobContainer> getZMPostJobSuccess();
//
//    /**
//     * 从职位编辑页面获取芝麻信用结果--职位发布成功页面
//     */
//    @FormUrlEncoded
//    @POST("bjobmanager/edit/getZMResult")
//    Call<ZMPostJobContainer> getZMEditJobSuccess(@Field("jobId") String jobId);
//
//    /**
//     * 发布职位成功页面中批量沟通的接口
//     * @param qString 	加密字符串
//     * @param authVersion 加密算法版本（默认1）
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("bjobmanager/relations/batchCreateRelation")
//    Call<CommonNet<AuthRelationContainer>> getAuthRelation(@Field("qString") String qString,@Field("authVersion") String authVersion);
//
//    /**
//     * 发布职位第二步验证手机号是否绑定
//     * http://qy.app.chinahr.com/bjobmanager/checkAndSend
//     */
//    @FormUrlEncoded
//    @POST("bjobmanager/checkAndSend")
//    Call<String> postJobSecondCode(@Field("mobile") String mobile);
//
//    /**
//     * 发布职位第一步
//     * http://qy.app.chinahr.com/bjobmanager/createJobFirst
//     */
//    @FormUrlEncoded
//    @POST("bjobmanager/createJobFirst")
//    Call<String> postJobFirst(@Field("jobName") String jobName,@Field("workType") String workType,@Field("degId") String degId,
//                              @Field("minSalary") String minSalary,
//                              @Field("maxSalary") String maxSalary,
//                              @Field("expId") String expId,@Field("jobType") String jobType
//            ,@Field("workPlace") String workPlace,@Field("addr") String addr
//            ,@Field("payPlace") String payPlace);
//
//    /**
//     * 发布职位第二步
//     * http://qy.app.chinahr.com/bjobmanager/createJobSecond
//     */
//    @FormUrlEncoded
//    @POST("bjobmanager/createJobSecond")
//    Call<String> postJobSecond(@Field("mobile") String mobile,@Field("smsCode") String smsCode,@Field("contact") String contact,@Field("openMobileFlag") String openMobileFlag,
//                               @Field("email") String email,@Field("welfares") String welfares,@Field("jobDesc") String jobDesc);
//
//
//
//
//    /**
//     * 发布职位前调用
//     * http://qy.app.chinahr.com/bjobmanager/perCreateJob
//     */
//    @POST("bjobmanager/perCreateJob")
//    Call<String> preparePostJob();
//
//    /**
//     * 手机号注册
//     *http://qy.app.chinahr.com/buser/mobileReg
//     *  "mobile", resMobile, "name", name, "pwd", resPwd, "smscode", smscode
//     */
//    @FormUrlEncoded
//    @POST("buser/mobileReg")
//    Call<String> mobileRegister(@Field("mobile") String mobile,@Field("name") String name,
//                                @Field("pwd") String pwd,@Field("smscode") String smscode);
//
//    /**
//     * 校验验证码
//     * http://qy.app.chinahr.com/buser/checkSmsCode
//     */
//    @FormUrlEncoded
//    @POST("buser/checkSmsCode")
//    Call<CommonJson> verifyCode(@Field("mobile") String mobile,@Field("smscode") String smscode);
//
//    /**
//     * 校验手机号
//     * http://qy.app.chinahr.com/buser/checkMobile
//     */
//    @FormUrlEncoded
//    @POST("buser/checkMobile")
//    Call<CommonJson> verifyMobile(@Field("mobile") String mobile);
//
//
//    /**
//     * B端 设置关系
//     * QY_DOMAIN
//     *http://qy.app.chinahr.com/bjobmanager/app/job/create/relation
//     * @return
//     */
//    @GET("bjobmanager/app/job/create/relation")
//    Call<String> createBossRelation(@Query("buser") String buser,@Query("cuser") String cuser,
//                                    @Query("cvid") String cvid,@Query("jobid") String jobid,
//                                    @Query("jobName") String jobName,@Query("source") String source);
//
//    /**
//     * 查询投递和反馈接口
//     *http://my.api.chinahr.com/count/deliveryAndFeedback
//     */
//    @GET("count/deliveryAndFeedback")
//    Call<String> getDeliveryAndFeedback() ;
//
//
//    /**
//     * 获取广告运营的接口
//     * http://appconfig.chinahr.com/common/openLogo?role=%s
//     */
//    @GET("common/openLogo")
//    Call<String> getChinahrAd(@Query("role") String role);
//
//
//    /**
//     * C端 设置关系
//     *http://my.api.chinahr.com/create/relation
//     * MYDOMain
//     *
//     * @return
//     */
//    @GET("create/relation")
//    Call<String> createClientRelation(@Query("buser") String buser,@Query("cuser") String cuser,
//                                      @Query("cvid") String cvid,@Query("jobid") String jobid,
//                                      @Query("jobName") String jobName,@Query("source") String source);
//
//
//    /**
//     * 获取开机的接口
//     * http://appconfig.chinahr.com/common/getConfigData
//     */
//    @GET("common/getConfigData")
//    Call<String> getConfigData(@Query("role") String role);
//
//    /**
//     */
//    @POST("device/upload")
//    @FormUrlEncoded
//    Call<VersionJsonModel>  getVersion(
//            @Field("appVersion") String appVersion, @Field("deviceID") String deviceId, @Field("deviceName") String deviceName,
//            @Field("deviceOS") String deviceOs, @Field("deviceVersion") String deviceVersion, @Field("channelId") String channel,
//            @Field("userAgreement") String userAgreement, @Field("aboutUs") String aboutUS , @Field("filter") String filterDB,
//            @Field("hotKeyWord") String hotKeyWordDB, @Field("hotLocation") String hotLocationDB, @Field("industry") String industryDB
//            , @Field("location") String locationDB, @Field("position") String positionDB, @Field("salary") String salaryDB ,
//            @Field("auth") String auth, @Field("size") String size , @Field("welfare") String welfare, @Field("newIndustry")
//            String newIndustry, @Field("newPosition") String newPosition, @Field("proskill") String proSkill,
//            @Field("level") String level,@Field("bHotJob") String bHotJob,@Field("cHotJob") String cHotJob,
//            @Field("pushSwitch") int pushSwitch, @Field("question") String question);
//
//    @FormUrlEncoded
//    @POST("subscribe/upload")
//    Call<CommonJson> uploadSubscrit(@Field("deviceId") String deviceId,@Field("cityId") String cityId,@Field("salaryId") String salaryId,@Field("jobList") String jobList,@Field("industry") String industry);
//
//    /**
//     * 获取企业详情
//     * http://qy.api.chinahr.com/app/job/getCompanyDetail
//     */
//    @FormUrlEncoded
//    @POST("app/job/getCompanyDetail")
//    Call<String> getCompanyDetail(@Field("comId") String comId,@Field("currpage") String currpage);
//
//    /** 公司评价
//     * http://qy.api.chinahr.com/app/job/getCompanyComments
//     */
//    @FormUrlEncoded
//    @POST("app/job/getCompanyComments")
//    Call<String> getCompanyComments(@Field("comId") String comId,@Field("currpage") String currpage);
//
//    /** IM界面点击头像，获取职位详情
//     * http://qy.api.chinahr.com/app/job/getJobByRelation
//     */
//    @GET("app/job/getJobByRelation")
//    Call<NewJobDetailJson> getJobByRelation(@Query("buserid") String buserid,@Query("cuserid") String cuserid);
//
//    /** 获取职位详情
//     * http://qy.api.chinahr.com/app/job/getJobDetail
//     */
//    @FormUrlEncoded
//    @POST("app/job/getJobDetail")
//    Call<NewJobDetailJson> getJobDetail(@Field("jobId") String jobId,@Field("currpage") String currpage,@Field("recommendExt") String recommendExt);
//
//    /**
//     * 获取收藏列表接口
//     * http://my.api.chinahr.com/native/getFavoriteList/%s
//     */
//    @FormUrlEncoded
//    @POST("native/getFavoriteList/{uid}")
//    Call<String> getFavoriteList(@Path("uid") String uid,@Field("pagesize") String pagesize,@Field("page") String page);
//
//    /**
//     * 删除收藏列表接口
//     * http://my.api.chinahr.com/native/deleteFavorites/%s
//     */
//    @FormUrlEncoded
//    @POST("native/deleteFavorites/{uid}")
//    Call<String> deleteFavoriteList(@Path("uid") String uid,@Field("ids") String ids,@Field("recommendExt") String recommendExt);
//
//    /**
//     * 获取发现的接口
//     * http://qy.api.chinahr.com/app/job/getRecommendList
//     */
//    @FormUrlEncoded
//    @POST("app/job/getRecommendList")
//    Call<String> getFind(@Field("keyword") String keyword,@Field("cityId") String cityId,@Field("industry") String industry,
//                         @Field("salary") String salary,@Field("refTime") String refTime,@Field("degree") String degree,
//                         @Field("work_age") String work_age,@Field("companyType") String companyType,@Field("workType") String workType,
//                         @Field("currpage") String currpage);
//
//
//    /**
//     * 获取发现的接口
//     * http://qy.api.chinahr.com/app/job/getRecommendListV2
//     */
//    @FormUrlEncoded
//    @POST("app/job/getRecommendListV2")
//    Call<CommonNet<FindContainer>> getFindV2(@Field("keyword") String keyword,@Field("cityId") String cityId,@Field("industry") String industry, @Field("salary") String salary);
//
//    /**
//     * 收藏职位接口
//     * http://my.api.chinahr.com/native/v2/addFavorite
//     */
//    @FormUrlEncoded
//    @POST("native/v2/addFavorite")
//    Call<JobCollectBackBean> addFavorite(@Field("jobid") String jobid, @Field("jobName") String jobName, @Field("buid") String buid,@Field("recommendExt") String recommendExt);
//
//    /**
//     * 个人中心的接口
//     * http://my.api.chinahr.com/native/getUserInfo/%s
//     **/
//    @POST("native/getUserInfo/{uid}")
//    Call<CommonNet<CUserInfo>> getmeConfig(@Path("uid") String uid);
//
//
//    /**
//     * 修改手机号
//     */
//    @FormUrlEncoded
//    @POST("account/updateMobile")
//    Call<String>  updateMobile(@Field("mobile") String mobile, @Field("smsCode") String smsCode, @Field("imgCode") String imgCode);
//
//
//    /**
//     * 修改email
//     */
//    @FormUrlEncoded
//    @POST("account/updateEmail")
//    Call<String>  updateEmail(@Field("email") String email);
//
//
//
//    /**
//     * 消息中简历的接口
//     */
//    @FormUrlEncoded
//    @POST("native/getEnterpriseInvite/{uid}")
//    Call<String> getenterpriseinvite(@Path("uid")String uid,@Field("page")String page,@Field("type")String type);
//
//
//
//    /**
//     * http://my.api.chinahr.com/deliver/user
//     * 投递简历的接口
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("deliver/user")
//    Call<String> getDeliverResumeCvid(@Field("jobid")String jobid, @Field("jobName")String jobName,
//                                      @Field("buid")String buid, @Field("comid")String comid,
//                                      @Field("cvid")String cvid,@Field("source")String source,@Field("recommendExt") String recommendExt);
//
//    /**
//     * http://my.api.chinahr.com/deliver/user
//     * 投递简历的接口
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("deliver/user")
//    Call<DeliverySuccessJson> getDeliverResumeCvid_new(@Field("jobid")String jobid, @Field("jobName")String jobName,
//                                                       @Field("buid")String buid, @Field("comid")String comid,
//                                                       @Field("cvid")String cvid, @Field("source")String source);
//
//    /**
//     * 老的投递简历接口(不创建关系)
//     *
//     * @param joid
//     * @param comid
//     *
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("native/deliverResume/{uid}")
//    Call<String> delvierResumeOld(@Path("uid") String uid,@Field("jobid") String joid,
//                                  @Field("comid") String comid, @Field("source") int source);
//
//    /**
//     * 老的投递简历接口(不创建关系)
//     *
//     * @param joid
//     * @param comid
//     *
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("native/deliverResumeWithCvid/{uid}")
//    Call<String> delvierResumeOldByCvid(@Path("uid") String uid,@Field("jobid") String joid,
//                                        @Field("comid") String comid, @Field("cvid") String cvid,@Field("source") int source);
//
//    /**
//     * B端 获取简历详情
//     * @param cvid cvid
//     * @param source source
//     * @return
//     */
//    @GET("cvapp/cvdetails")
//    Call<ResumeDetailBeanData> getBResumeDetail(@Query("cvid") String cvid,
//                                                @Query("source") String source);
//
//    /**
//     * B端 获取简历详情
//     * @param cvid cvid
//     * @param source source
//     * @param jobid jobid
//     * @return
//     */
//    @GET("cvapp/cvdetails")
//    Call<ResumeDetailBeanData> getBResumeDetailJob(@Query("cvid") String cvid,
//                                                   @Query("source") String source,
//                                                   @Query("jobid") String jobid);
//
//    /**
//     * B端 简历详情获取未处理简历，并更新为待处理
//     * @param jobid
//     * @param cvid
//     * @return
//     */
//    @GET("cvapp/unhandle")
//    Call<String> getBUnhandleResume(@Query("jobid") String jobid, @Query("cvid") String cvid);
//
//    /**
//     * B端 设置简历状态
//     * @param cvid
//     * @param jobid
//     * @param status
//     * @return
//     */
//    @GET("cvapp/handle")
//    Call<CommonOK> setResumeWaithandleStatus(@Query("cvid") String cvid,
//                                             @Query("jobid") String jobid,
//                                             @Query("status") int status);
//
//    /**
//     * B端简历详情 by relation
//     * @param cuserid cuserid
//     * @return
//     */
//    @GET("cvapp/imCvDetails")
//    Call<ResumeDetailBeanData> getCvDetailByRelation(@Query("cuid") String cuserid);
//
//    /**
//     * C端 上传图片
//     * @param cvId cvId
//     */
//    @Multipart
//    @POST("cv/uploadPhoto")
//    Call<PhotoBean> uploadCImage(@Part("cvId") RequestBody cvId, @Part MultipartBody.Part file);
//
//    /**
//     * B端 上传图片（头像）
//     * @return
//     */
//    @Multipart
//    @POST("buser/app/pic/upload")
//    Call<PhotoBean> uploadBImage(@Part MultipartBody.Part file);
//
//    /**
//     * 下载apk (有两个路径，@url： 动态传入url解决)
//     * @return
//     */
//    @GET
//    Call<ResponseBody> downloadApk(@Url String url);
//
//    /** 获取简历列表
//     * http://my.api.chinahr.com/cv/cvs
//     */
//    @GET("cv/cvs")
//    Call<ResumeListBean> getMineResumeList();
//
//    /**
//     * 刷新简历
//     * http://my.api.chinahr.com/cv/refreshCv/%s
//     */
//    @GET("cv/refreshCv/{cvid}")
//    Call<BasicInfoBean> getRefreshResume(@Path("cvid") String cvid);
//
//    /**
//     * 设置默认简历
//     * http://my.api.chinahr.com/cv/setDefaultCv/%s
//     */
//    @GET("cv/setDefaultCv/{cvid}")
//    Call<String> setDefaultResume(@Path("cvid") String cvid);
//
//    /**
//     * 删除简历
//     * http://my.api.chinahr.com/cv/deleteCv/%s
//     */
//    @GET("cv/deleteCvV2/{cvid}")
//    Call<String> deleteResume(@Path("cvid") String cvid) ;
//
//    /**
//     * 获取简历编辑
//     * http://my.api.chinahr.com/cv/cvs/%s
//     */
//    @GET("cv/cvsv2/{cvid}")
//    Call<ResumeDetailBean> getMineResumeEdit(@Path("cvid") String cvid);
//
//    /**
//     * 获取简历编辑 by relation
//     * http://my.api.chinahr.com/cv/getCvByRelation
//     */
//    @GET("cv/getCvByRelation")
//    Call<ResumeDetailBean> getResumeEditByRelation(@Query("buserid") String buserid,@Query("cuserid") String cuserid,@Query("role") String role) ;
//
//    /**
//     * 创建简历第一步，获取基本信息
//     *http://my.api.chinahr.com/cv/cvBasicV2
//     */
//    @GET("cv/cvBasicV2")
//    Call<BasicInfoBean> getMineResumeBasicInfo();
//
//    /**
//     * 创建简历第三步
//     * http://my.api.chinahr.com/createCv/cvThird
//     */
//    @FormUrlEncoded
//    @POST("createCv/cvThird")
//    Call<String> postMineResumeShowBeautiful(@Field("comid") String comid,@Field("cvid") String cvid,@Field("expid") String expid,@Field("jobid") String jobid,
//                                             @Field("noPractice") String noPractice,@Field("expWorkDesc") String expWorkDesc,@Field("wonderfullPoint") String wonderfullPoint);
//
//    /**
//     * 创建简历第二步
//     * http://my.api.chinahr.com/createCv/cvSecond
//     */
//    @FormUrlEncoded
//    @POST("createCv/cvSecond")
//    Call<String> createMineResumeSecond(@Field("expid") String expid,@Field("eduid") String eduid,@Field("cvid") String cvid,
//                                        @Field("degree") String degree,@Field("college") String college,@Field("major") String major,
//                                        @Field("eduFinishYear") String eduFinishYear,@Field("JobName") String JobName,@Field("comName") String comName,
//                                        @Field("starDate") String starDate,@Field("endDate") String endDate,@Field("comid") String comid,
//                                        @Field("jobid") String jobid,@Field("expType") String expType,@Field("noPractice") String noPractice);
//
//    /**
//     * 创建简历第一步
//     * http://my.api.chinahr.com/createCvV2/cvFirst
//     */
//    @FormUrlEncoded
//    @POST("createCv/cvSecond")
//    Call<String> createMineResumeFirst(@Field("name") String name,@Field("gender") String gender,@Field("mobile") String encodeMobile,
//                                       @Field("BirthTime") String BirthTime,@Field("livingCity") String livingCity,@Field("workYear") String workYear,
//                                       @Field("imgCode") String imgCode,@Field("smsCode") String smsCode,
//                                       @Field("comid") String comid,@Field("jobid") String jobid,@Field("cvid") String cvid);
//
//    /**
//     * 添加我的简历中的求职意向
//     * http://my.api.chinahr.com/perfectCv/jobIntents
//     */
//    @FormUrlEncoded
//    @POST("perfectCv/jobIntents")
//    Call<String> addMineResumeJobIntents(@Field("cvId") String cvId,@Field("expJobType") String expJobType,@Field("expeLocal") String expeLocal,
//                                         @Field("expeJobCategory") String expeJobCategory,@Field("expeIndustry") String expeIndustry,
//                                         @Field("expComTypeId") String expComTypeId, @Field("expSalary") String expSalary,
//                                         @Field("negotiation") String negotiation,@Field("workStatus") String workStatus,
//                                         @Field("isExist") String isExist);
//
//    /**
//     * 添加我的简历中的工作经历
//     * http://my.api.chinahr.com/perfectCv/experiences
//     */
//    @FormUrlEncoded
//    @POST("perfectCv/experiences")
//    Call<String> addMineResumeExperiences(@Field("cvId") String cvId,@Field("id") String id,@Field("jobName") String jobName,
//                                          @Field("comName") String comName,@Field("jobTypeId") String jobTypeId,@Field("industryTypeId") String industryTypeId,
//                                          @Field("comTypeId") String comTypeId,@Field("startDate") String startDate,@Field("endDate") String endDate,
//                                          @Field("duty") String duty);
//
//    /**
//     * 添加我的简历中的实习经历
//     * http://my.api.chinahr.com/perfectCv/practices
//     */
//    @FormUrlEncoded
//    @POST("perfectCv/practices")
//    Call<String> addMineResumePractices(@Field("cvId") String cvId,@Field("id") String id,@Field("jobName") String jobName,
//                                        @Field("comName") String comName,@Field("startDate") String startDate,
//                                        @Field("endDate") String endDate, @Field("duty") String duty);
//
//    /**
//     * 添加我的简历中的教育经历
//     * http://my.api.chinahr.com/perfectCv/educations
//     * "id", id, "cvId", cvId, "degreeId", String.valueOf(degreeId), "college", college, "major", major,
//     "start", start, "end", end, "description", description
//     */
//    @FormUrlEncoded
//    @POST("perfectCv/educations")
//    Call<String> addMineResumeEducations(@Field("cvId") String cvId,@Field("id") String id,@Field("degreeId") String degreeId,
//                                         @Field("college") String college,@Field("major") String major,
//                                         @Field("start") String start, @Field("end") String end,@Field("description") String description);
//    /**
//     * 添加或更新我的简历中的项目经验
//     * id新建时不传，更新时传
//     */
//    @FormUrlEncoded
//    @POST("perfectCv/project")
//    Call<ProjectExperienceBackBeanV2> updateMineResumeProjectExperience(@Field("cvid")String cvid,
//                                                                        @Field("id")String id,
//                                                                        @Field("name")String name,
//                                                                        @Field("startDate")String startDate,
//                                                                        @Field("endDate")String endDate,
//                                                                        @Field("resp")String resp,
//                                                                        @Field("projDesc")String projDesc
//    );
//
//    /**
//     * 添加或更新我的简历中专业技能
//     * id,新建时不传,编辑时传
//     */
//    @FormUrlEncoded
//    @POST("perfectCv/proskill")
//    Call<ProSkillBackBean> updateMineResumeProSkill(@Field("cvid")String cvid,
//                                                    @Field("id")String id,
//                                                    @Field("techId")String techId,
//                                                    @Field("techName")String techName,
//                                                    @Field("levelId")String levelId,
//                                                    @Field("levelName")String levelName
//    );
//
//    /**更新或者添加证书
//     *
//     * @param cvid
//     * @param id 新建时不传,编辑时传
//     * @param certName
//     * @param time
//     * @param school
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("perfectCv/cert")
//    Call<CertBackBean> updateMineResumeCert(@Field("cvid")String cvid,
//                                            @Field("id")String id,
//                                            @Field("certName")String certName,
//                                            @Field("time")String time,
//                                            @Field("school")String school
//    );
//
//    /**
//     * 删除某条的工作经历
//     * http://my.api.chinahr.com/perfectCv/cvs/%s/delExperiences/%s
//     */
//    @POST("perfectCv/cvs/{cvid}/delExperiences/{expId}")
//    Call<String> deleteMineResumeExperience(@Path("cvid") String cvid,@Path("expId") String expId) ;
//
//    /**
//     * 删除某条的教育经历
//     * http://my.api.chinahr.com/perfectCv/cvs/%s/delEducations/%s
//     */
//    @POST("perfectCv/cvs/{cvid}/delEducations/{eduId}")
//    Call<String> deleteMineResumeEducation(@Path("cvid") String cvid,@Path("eduId") String eduId) ;
//
//    /**
//     * 删除某条的实习经历
//     * http://my.api.chinahr.com/perfectCv/cvs/%s/delPractices/%s
//     */
//    @POST("perfectCv/cvs/{cvid}/delPractices/{praId}")
//    Call<String> deleteMineResumedelPractice(@Path("cvid") String cvid,@Path("praId") String praId) ;
//
//    /**
//     * 删除项目经历
//     */
//    @POST("perfectCv/cvs/{cvid}/delProject/{projectExperienceId}")
//    Call<CommonJson> deleteMineResumeProjectExperience(@Path("cvid") String cvid,@Path("projectExperienceId") String projectExperienceId);
//
//    /**
//     * 删除专业技能
//     */
//    @POST("perfectCv/cvs/{cvid}/delProskill/{proSkillId}")
//    Call<CommonJson> deleteMineResumeProSkill(@Path("cvid")String cvid,
//                                              @Path("proSkillId")String proSkillId
//    );
//
//    /**
//     * 删除证书
//     */
//    @POST("perfectCv/cvs/{cvid}/delCert/{certId}")
//    Call<CommonJson> deleteMineResumeCert(@Path("cvid")String cvid,
//                                          @Path("certId")String certId
//    );
//
//
//    /**
//     * 修改简历名称
//     * http://my.api.chinahr.com/cv/updateCvName/%s
//     */
//    @FormUrlEncoded
//    @POST("cv/updateCvName/{cvid}")
//    Call<String> updateMineResumeName(@Path("cvid") String cvid,@Field("cvName") String cvName);
//
//    /**
//     * 获取简历的隐私设置
//     * http://my.api.chinahr.com/my/getprivacy
//     */
//    @GET("my/getprivacy")
//    Call<MineRsumePrivacyJson> getResumePrivacy() ;
//
//    /**
//     * 更改隐私设置开关
//     * http://my.api.chinahr.com/my/setprivacy
//     */
//    @FormUrlEncoded
//    @POST("my/setprivacy")
//    Call<MineRsumePrivacyJson> updateReSumePrivacy(@Field("privacy") String privacy);
//
//    /**
//     * 删除隐私设置中的企业名称
//     * http://my.api.chinahr.com/my/delscreen
//     */
//    @FormUrlEncoded
//    @POST("my/delscreen")
//    Call<CommonJson> deleteResumeCompanyPrivacy(@Field("txt") String txt);
//
//    /**
//     * 添加隐私设置中的企业名称
//     * http://my.api.chinahr.com/my/addscreen
//     */
//    @FormUrlEncoded
//    @POST("my/addscreen")
//    Call<CommonJson> addResumeCompanyPrivacy(@Field("txt") String txt);
//
//
//    /**
//     * 修改简历基本信息
//     * http://my.api.chinahr.com/cv/cvBasic
//     */
//    @FormUrlEncoded
//    @POST("cv/cvBasic")
//    Call<String> updateResumeBase(@Field("cvid") String cvid,@Field("name") String name,@Field("gender") String gender,
//                                  @Field("marry") String marry,@Field("mobile") String mobile,@Field("birth") String birth,
//                                  @Field("livingId") String livingId,@Field("workYear") String workYear,
//                                  @Field("wonderfullPoint") String wonderfullPoint,@Field("isOverseas") String isOverseas,
//                                  @Field("domicileId") String domicileId,@Field("email") String email,
//                                  @Field("qq") String qq,@Field("weixin") String weixin) ;
//
//    /**
//     * 创建简历第一步是否注册过
//     * http://my.api.chinahr.com/account/checkNumAndSendMsg
//     */
//    @FormUrlEncoded
//    @POST("account/checkNumAndSendMsg")
//    Call<String> createCheckMobile(@Field("mobile") String encodeMobile) ;
//
//    /**
//     * 职位编辑-获取职位信息 QY_DOMAIN
//     * @param jobId 职位的id
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("bjobmanager/edit/getJobInfo")
//    Call<JobEditContainer> getJobInfo(@Field("jobId") String jobId) ;
//
//    /**
//     * 提交编辑职位
//     * @return 解析的json
//     */
//    @FormUrlEncoded
//    @POST("bjobmanager/edit/modifyJob")
//    Call<PostJobSecondJson> commitJobInfo(@Field("jobId") String jobId,@Field("jobName") String jobName,@Field("jobTypes") String jobTypes,@Field("depmName") String depmName
//            ,@Field("workType") String workType,@Field("minSalary") String minSalary,@Field("maxSalary") String maxSalary,@Field("isNegotiate") String isNegotiate
//            ,@Field("expId") String expId,@Field("degId") String degId,@Field("workPlace") String workPlace,@Field("addr") String addr
//            ,@Field("payPlace") String payPlace,@Field("welfares") String welfares,@Field("jobDesc") String jobDesc,@Field("contact") String contact
//            ,@Field("mobile") String mobile,@Field("smsCode") String smsCode,@Field("openMobileFlag") String openMobileFlag,@Field("phone") String phone
//            ,@Field("openPhoneFlag") String oPhone,@Field("appEmail") String appEmail,@Field("openEmailFlag") String oEmail,@Field("isRecCv") String isRecCv) ;
//
//    @GET("cv/miniList")
//    Call<ResumeMiniListBean> getResumeList(@Query("recommendExt") String recommendExt);
//    /**
//     * http://qy.app.chinahr.com/cvapp/imDelivery
//     * B端 投递的简历
//     */
//    @GET("cvapp/imDelivery")
//    Call<String> getDeliveryResume();
//
//    /**
//     * http://appconfig.chinahr.com/push/getChannel
//     * 判断使用哪个push推送
//     */
//    @GET("push/getChannel")
//    Call<CommonNet<PushJson>> getPush(@Query("deviceName") String deviceName, @Query("deviceID") String deviceID);
//
//    /**
//     * AR活动列表页
//     */
//    @FormUrlEncoded
//    @POST("ar/getList")
//    Call<CommonNet<ARActivityListJson>> getARActivityList(
//            @Field("page")int page,@Field("role")String role
//    );
//
//    /**
//     * http://my.api.chinahr.com/setting/autoTalkFlag
//     * C端账号设置中群聊开启管理的接口
//     * @param autoTalkFlag autoTalkFlag  1为自动沟通,0为不自动沟通
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("setting/autoTalkFlag")
//    Call<CommonNet<CUserInfo>> setAutoTalkFlag(@Field("autoTalkFlag") int autoTalkFlag);
//
//    /** http://passport.app.chinahr.com/v1/buser/sendMsg
//     * B端快捷登录发送短信验证码
//     * @param mobile 手机号
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("v1/buser/sendMsg")
//    Call<CommonOK> sendMsgBoss(@Field("mobile") String mobile);
//
//    /*
//     * http://qy.app.chinahr.com/cvapp/v1/seekTalentSwitch
//     * B端求贤令设置开关
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("setting/autoSendFlag")
//    Call<CommonNet<CUserInfo>> setAutoSendFlag(@Field("autoSendFlag") int autoTalkFlag);
//
//    /**
//     * 端自动发送开关
//     * @return
//     */
//    @POST("cvapp/v1/getSeekTalent")
//    Call<CommonNet<AutoSendContainer>> getRecommendAutoSendList();
//
//    /**
//     * http://qy.app.chinahr.com/cvapp/v1/seekTalentSwitch
//     * B端求贤令设置开关
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("cvapp/v1/seekTalentSwitch")
//    Call<CommonNet<AutosendInfo>> setSeekTalentSwitch(@Field("switch") boolean seekTalentSwitch);
//
//
//    /**
//     * http://qy.app.chinahr.com/cvapp/v1/seekTalentSwitch
//     * B端求贤令设置招呼信息
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("cvapp/v1/seekTalentTip")
//    Call<CommonNet<AutosendInfo>> setSeekTalentTip(@Field("tips") String tips);
//
//    /**
//     * http://qy.app.chinahr.com/cvapp/v1/seekTalentInfo
//     * B端获取求贤令Info的接口
//     * @return
//     */
//    @GET("cvapp/v1/seekTalentInfo")
//    Call<CommonNet<AutosendInfo>> getSeekTalentInfo();
//
//    /**
//     * http://qy.app.chinahr.com/cvapp/v1/seekTalentList
//     * B端获取已经发送过求贤令列表
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("cvapp/v1/seekTalentList")
//    Call<CommonNet<AutoSendShow>> getSeekTalentList(@Field("offset") int  offset, @Field("pageSize") int  pageSize);
//
//    /**
//     * B端个人中心：微招聘获取数字
//     * app.chinahr.com/buser/count
//     * @return
//     */
//    @GET("buser/count")
//    Call<CommonNet<MicroCountBean>> getMiscroCount();
//    /**
//     * http://qy.app.chinahr.com/cvapp/v1/interested/list
//     * B端获取谁看过我列表
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("cvapp/v1/interested/list")
//    Call<CommonNet<SeeMeList>> getInterested(@Field("page") int  page);
//
//    /**
//     * C端 触发自动回复接口
//     * https://app.chinahr.com/my/sendAutoReply
//     * @param buid buid
//     * @return
//     */
//    @GET("my/sendAutoReply")
//    Call<CommonOK> sendAutoReply(@Query("buid") String buid);
//
//    /**
//     * C端 点击问题发送答案
//     * https://app.chinahr.com/my/sendAnswerById
//     * @param questionId questionId
//     * @param buid buid
//     * @return
//     */
//    @GET("my/sendAnswerById")
//    Call<CommonOK> sendAnswerById(@Query("id") String questionId, @Query("buid") String buid);
//
//    /**
//     * B端获取智能回复
//     */
//    @GET("buser/getAutoReplyByBuid")
//    Call<CommonNet<List>> getAutoReplyByBuid();
//
//    /**
//     * B端添加自动回复
//     */
//    @FormUrlEncoded
//    @POST("buser/addAutoReply ")
//    Call<CommonNet>  addAutoReply(@Field("id") String id,@Field("question") String question,@Field("answer") String answer);
//
//    /**
//     * B端删除自动回复
//     */
//    @FormUrlEncoded
//    @POST("buser/delAutoReply")
//    Call<CommonNet> delAutoReply(@Field("id") String id);
//
//    /**
//     * B端智能回复开关
//     */
//    @FormUrlEncoded
//    @POST("buser/setAutoReplyFlag")
//    Call<CommonNet> setAutoReplyFlag(@Field("flag") int flag);
//
//    /**
//     * 获取Tinkerpatch包相关的信息。
//     */
//    @FormUrlEncoded
//    @POST("cvapp/v1/interested/list")
//    Call<TinkerPatchInfo> getTinkerPatchInfo();
//
//    /**
//     * 上传Tinkerpatch包相关的信息，监控使用。
//     */
//    @FormUrlEncoded
//    @POST("cvapp/v1/interested/list")
//    Call<CommonJson> reportTinkerPatchInfo(@Field("info") String info);
//
//    @FormUrlEncoded
//    @POST("push/open")
//    Call<CommonJson> clickPush(@Field("appPushId") String  appPushId,@Field("action") int  action,
//                               @Field("type") int  type,@Field("popUpStatus") int  popUpStatus);
//
///*
//     * 获取AR活动开关
//     */
//
//    @GET("common/getActivitySwitch")
//    Call<CommonNet<List<ActivitySwitch>>> getARActivitySwitch();
//
//    /*
//     * http://qy.app.chinahr.com/cvapp/v1/interested/list
//     * B端获取谁看过我列表
//     * @return
//     */
//    @GET("my/ceping/checkReport")
//    Call<CommonNet<CpReport>> checkCePing();
//}
