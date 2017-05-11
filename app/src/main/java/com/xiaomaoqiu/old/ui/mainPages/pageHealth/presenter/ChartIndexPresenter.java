package com.xiaomaoqiu.old.ui.mainPages.pageHealth.presenter;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.bean.nocommon.PetSleepInfoBean;
import com.xiaomaoqiu.now.bean.nocommon.PetSportBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.AppDialog;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/2.
 */

public class ChartIndexPresenter {

    public static final String URL_GETSLEEP_STATUS = "pet.health.get_sleep_info";
    public static final String URL_GETSPROT_STATUS = "pet.health.get_activity_info";
    public static final String DEEP_SLEEP_FIELD_NAME = "deep_sleep";
    public static final String LIGHT_SLEEP_FIELD_NAME = "light_sleep";
    public static final String TARGET_AMOUNT_FIELD_NAME = "target_amount";
    public static final String REALITY_AMOUNT_FIELD_NAME = "reality_amount";
    public static final int FLAG_WEEK = 0;
    public static final int FLAG_MONTH = 1;
    public static final int WEEK_LENGTH = 7;
    public static final int MONTH_LENGTH = 30;


    private IChartCallback callback;

    private String httpUrl = "";
    private String largeFieldName = "";
    private String lowerFieldName = "";
    private boolean isSleep = false;
    private double todayDeep = 0, todayLight = 0;


    public ChartIndexPresenter(IChartCallback callback, boolean isSlepp) {
        this.callback = callback;
        this.isSleep = isSlepp;
        if (isSlepp) {
            httpUrl = URL_GETSLEEP_STATUS;
            largeFieldName = DEEP_SLEEP_FIELD_NAME;
            lowerFieldName = LIGHT_SLEEP_FIELD_NAME;
        } else {
            httpUrl = URL_GETSPROT_STATUS;
            largeFieldName = TARGET_AMOUNT_FIELD_NAME;
            lowerFieldName = REALITY_AMOUNT_FIELD_NAME;
        }
    }


    public void queryDatas() {
        long msEnd = System.currentTimeMillis();
        long span30 = 30l * 24 * 3600 * 1000;//30天:30*24*3600*1000
        long msStart = msEnd - span30;

        Date endDate = new Date(msEnd);
        Date startDate = new Date(msStart);

        String strEnd = String.format("%s-%s-%s", endDate.getYear() + 1900, endDate.getMonth() + 1, endDate.getDate());
        String strStart = String.format("%s-%s-%s", startDate.getYear() + 1900, startDate.getMonth() + 1, startDate.getDate());

//        HttpUtil.get2(httpUrl, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                //{"status": 0, "data": [{"date": "2016-04-16", "deep_sleep": "6.6", "light_sleep": "2.4"},{}]}
//                Log.v("http", httpUrl + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if(callback == null){
//                    return;
//                }
//                if(ret != HttpCode.EC_SUCCESS){
//                    onFail();
//                    return;
//                }
//                parseSuccess(response);
//
//            }
//
//        }, UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), strStart, strEnd);
        if (isSleep) {
            ApiUtils.getApiService().getSleepInfo(UserInstance.getInstance().getUid(),
                    UserInstance.getInstance().getToken(),
                    PetInfoInstance.getInstance().getPet_id(), strStart, strEnd
            ).enqueue(new XMQCallback<PetSleepInfoBean>() {
                @Override
                public void onSuccess(Response<PetSleepInfoBean> response, PetSleepInfoBean message) {
                    HttpCode ret = HttpCode.valueOf(message.status);
                    if (callback == null) {
                        return;
                    }
                    if (ret != HttpCode.EC_SUCCESS) {
                        return;
                    }
//                    parseSuccess(response);
                    parseSleepInfo(message);
                }

                @Override
                public void onFail(Call<PetSleepInfoBean> call, Throwable t) {

                }
            });
        } else {

            ApiUtils.getApiService().getActivityInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                    PetInfoInstance.getInstance().getPet_id(), strStart, strEnd).enqueue(new XMQCallback<PetSportBean>() {
                @Override
                public void onSuccess(Response<PetSportBean> response, PetSportBean message) {
                    HttpCode ret = HttpCode.valueOf(message.status);
                    if (callback == null) {
                        return;
                    }
                    if (ret != HttpCode.EC_SUCCESS) {
                        return;
                    }
                    parseSportInfo(message);
                }

                @Override
                public void onFail(Call<PetSportBean> call, Throwable t) {
                    ToastUtil.showTost("获取当天数据失败");
                }
            });
        }
    }

    //解析睡眠信息
    public void parseSleepInfo(PetSleepInfoBean message) {
        List<PetSleepInfoBean.SleepBean> sleepDatas = message.data;
        if (sleepDatas == null || sleepDatas.size() <= 0) {
            return;
        }
        //解析一天
        parseSleepToday(sleepDatas);

        //解析一周
        parseSleepWeekList(sleepDatas);

        //解析一个月
        parseSleepMonthList(sleepDatas, "");

    }

    //解析一天的睡眠数据
    void parseSleepToday(List<PetSleepInfoBean.SleepBean> sleepDatas) {
        int days = sleepDatas.size();
        Date date=new Date();
        PetSleepInfoBean.SleepBean todayBean =timeEqualSleepBean(date,sleepDatas);

        todayDeep = todayBean.deep_sleep;
        todayLight = todayBean.light_sleep;
        callback.onSuccessGetWeight(todayDeep, todayLight);
    }

    //解析一周
    void parseSleepWeekList(List<PetSleepInfoBean.SleepBean> sleepDatas) {
        ArrayList<String> axisLabels = new ArrayList<>();

        ArrayList<BarEntry> sleepList = new ArrayList<>();
        ArrayList<Date> dates = AppDialog.DateUtil.getPastDates(WEEK_LENGTH);

        int startIndex =0;
        for (int i = startIndex; i < WEEK_LENGTH; i++) {
            Date date = dates.get(i - startIndex);
            axisLabels.add(date.getDate() + "日");

            PetSleepInfoBean.SleepBean bean =timeEqualSleepBean(date,sleepDatas);
            sleepList.add(new BarEntry(i - startIndex, new float[]{(float) bean.deep_sleep, (float) bean.light_sleep}));
        }
        callback.onSuccessGetAxis(axisLabels, false);
        callback.onSuccessGetWeekDataSet(sleepList, null);
    }
    //获取bean
    public PetSleepInfoBean.SleepBean timeEqualSleepBean(Date date, List<PetSleepInfoBean.SleepBean> sleeptDatas) {
        for (PetSleepInfoBean.SleepBean bean : sleeptDatas) {
            String dateString = bean.date;
            if (equalDateDay(date, dateString)) {
                return bean;
            }
        }
        return new PetSleepInfoBean.SleepBean();
    }

    //解析一个月
    void parseSleepMonthList(List<PetSleepInfoBean.SleepBean> sleepDatas, String format) {
        int intrval = 2;
        ArrayList<String> axisLabels = new ArrayList<>();
        ArrayList<Entry> deepList = new ArrayList<>();
        ArrayList<Entry> lightList = new ArrayList<>();
        ArrayList<Date> dates = AppDialog.DateUtil.getPastDates(MONTH_LENGTH);
        Date curDate = AppDialog.DateUtil.getFirstDataOfCurMonth();
        String secondText = curDate.getMonth() + "月";
        int secondIndex = curDate.getDate();
        callback.onSuccessGetSecAxis(secondText, secondIndex, MONTH_LENGTH);
        int startIndex =0;
        for (int i = startIndex; i < MONTH_LENGTH; i += intrval) {
            Date date = dates.get(i - startIndex);
            axisLabels.add(date.getDate() + format);
//            JSONObject jsday = (JSONObject)jsdata.opt(i);
            PetSleepInfoBean.SleepBean bean = timeEqualSleepBean(date,sleepDatas);
            float deep = (float) bean.deep_sleep;
            float light = (float) bean.light_sleep;
            deepList.add(new Entry(i - startIndex, (float) bean.deep_sleep));
            lightList.add(new Entry(i - startIndex, deep + light));

        }
        callback.onSuccessGetAxis(axisLabels, true);
        callback.onSuccessGetMonthDataSet(deepList, lightList);
    }

    private void parseSuccess(JSONObject response) {
//        JSONArray jsdata = response.optJSONArray("data");
//        if(null ==jsdata || jsdata.length() <= 0){
//            onFail();
//            return;
//        }
//        int days = jsdata.length();
//        //设置今日睡眠
//        JSONObject jsToday = (JSONObject) jsdata.opt(days - 1);
//        todayDeep = jsToday.optDouble(largeFieldName, 1.0);
//        todayLight = jsToday.optDouble(lowerFieldName, 1.0);
//        callback.onSuccessGetWeight(todayDeep,todayLight);
//        if(isSleep){
//            parseSleepWeekList(jsdata);
//        }else {
//            parseList(jsdata, "日", false);//一周睡眠
//        }
//        parseList(jsdata,"",true);//30天睡眠
    }

    void parseSportInfo(PetSportBean message) {
        List<PetSportBean.SportBean> sportDatas = message.data;
        if (sportDatas == null || sportDatas.size() <= 0) {
            return;
        }
        //解析当天的运动数据
        parseSportToday(sportDatas);
        //解析一周的运动数据
        parseSportWeekAndMonthList(sportDatas, "日", false);
        //解析一个月的运动数据
        parseSportWeekAndMonthList(sportDatas, "日", true);


    }

    //解析当天的运动数据
    void parseSportToday(List<PetSportBean.SportBean> sportDatas) {
        int days = sportDatas.size();
        Date date=new Date();
        PetSportBean.SportBean todayBean = timeEqualSportBean(date,sportDatas);
        todayDeep = todayBean.target_amount;
        todayLight = todayBean.reality_amount;
        callback.onSuccessGetWeight(todayDeep, todayLight);
        EventManage.TodaySportData event = new EventManage.TodaySportData();

        event.sportBean = todayBean;
        PetInfoInstance.getInstance().setTarget_step( event.sportBean.target_amount);
        EventBus.getDefault().post(event);
    }

    //解析一周的运动数据
    void parseSportWeekAndMonthList(List<PetSportBean.SportBean> sportDatas, String format, boolean isMonth) {
        int days = isMonth ? MONTH_LENGTH : WEEK_LENGTH;
//        int intrval = isMonth ? 2 : 1;
        int intrval=1;
        ArrayList<String> axisLabels = new ArrayList<>();

        ArrayList<Entry> deepList = new ArrayList<>();
        ArrayList<Entry> lightList = new ArrayList<>();

        ArrayList<Date> dates = AppDialog.DateUtil.getPastDates(days);

        if (isMonth) {
            Date curDate = AppDialog.DateUtil.getFirstDataOfCurMonth();
            String secondText = curDate.getMonth() + "月";
            int secondIndex = curDate.getDate();
            callback.onSuccessGetSecAxis(secondText, secondIndex, days);
        }
        int startIndex = 0;
        for (int i = startIndex; i < days; i += intrval) {
            Date date = dates.get(i - startIndex);
            axisLabels.add(date.getDate() + format);
            PetSportBean.SportBean bean = timeEqualSportBean(date, sportDatas);
            deepList.add(new Entry(i - startIndex, (float) bean.target_amount));
            lightList.add(new Entry(i - startIndex, (float) bean.reality_amount ));
        }
        callback.onSuccessGetAxis(axisLabels, isMonth);
        if (isMonth) {
            callback.onSuccessGetMonthDataSet(deepList, lightList);
        } else {
            callback.onSuccessGetWeekDataSet(deepList, lightList);
        }
    }

    //获取bean
    public PetSportBean.SportBean timeEqualSportBean(Date date, List<PetSportBean.SportBean> sportDatas) {
        for (PetSportBean.SportBean bean : sportDatas) {
            String dateString = bean.date;
            if (equalDateDay(date, dateString)) {
                return bean;
            }
        }
        return new PetSportBean.SportBean();
    }

    //只要日期对，就可以
    boolean equalDateDay(Date date1, String date2) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        String changeResult=sdf.format(date1);
        return changeResult.equals(date2);
    }

//    //切换时间字符串
//    public Date changeFormat(String timeString) {
//        if (TextUtils.isEmpty(timeString)) {
//
//            return new Date(0, 0, 0);
//        }
//        int year;
//        int month;
//        int day;
//        try {
//            Scanner scanner = new Scanner(timeString);
//            scanner.useDelimiter("-");
//            year = scanner.nextInt();
//            month = scanner.nextInt();
//            day = scanner.nextInt();
//        } catch (Exception e) {
//            year = 0;
//            month = 0;
//            day = 0;
//        }
//        return new Date(year, month, day);
//    }


//    private void parseList(JSONArray jsdata, String format, boolean isMonth) {
//        int days = isMonth ? MONTH_LENGTH : WEEK_LENGTH;
//        int intrval = isMonth ? 2 : 1;
//        ArrayList<String> axisLabels = new ArrayList<>();
//
//        ArrayList<Entry> deepList = new ArrayList<>();
//        ArrayList<Entry> lightList = new ArrayList<>();
//
//        ArrayList<MyDate> dates = DateUtil.getPastDates(days);
//
//        if (isMonth) {
//            MyDate curDate = DateUtil.getFirstDataOfCurMonth();
//            String secondText = curDate.getMonth() + "月";
//            int secondIndex = curDate.getDate();
//            callback.onSuccessGetSecAxis(secondText, secondIndex, days);
//        }
//        int startIndex = jsdata.length() - days;
//        if (startIndex < 0) {
//            startIndex = 0;
//        }
//        for (int i = startIndex; i < jsdata.length(); i += intrval) {
//            MyDate date = dates.get(i - startIndex);
//            axisLabels.add(date.getDate() + format);
//            JSONObject jsday = (JSONObject) jsdata.opt(i);
//            float deep = (float) jsday.optDouble(largeFieldName);
//            float light = (float) jsday.optDouble(lowerFieldName);
//            deepList.add(new Entry(i - startIndex, (float) jsday.optDouble(largeFieldName)));
//            if (isSleep) {
//                lightList.add(new Entry(i - startIndex, deep + light));
//            } else {
//                lightList.add(new Entry(i - startIndex, (float) jsday.optDouble(lowerFieldName)));
//            }
//        }
//        callback.onSuccessGetAxis(axisLabels, isMonth);
//        if (isMonth) {
//            callback.onSuccessGetMonthDataSet(deepList, lightList);
//        } else {
//            callback.onSuccessGetWeekDataSet(deepList, lightList);
//        }
//    }


    /**
     * 显示某一固定日期数据
     *
     * @param index
     * @param xaixsLabel
     * @param values
     * @param flag
     */
    public void showDatas(int index, String xaixsLabel, List<Float> values, int flag) {
        if (null == callback) {
            return;
        }
        String month = getMonth(index, FLAG_MONTH == flag);
        xaixsLabel = xaixsLabel.replace("日", "");
        callback.onShowDataTip(month + xaixsLabel + "日", values, flag);
    }

    public String getMonth(int index, boolean isMonth) {
        if (isMonth) {
            index = MONTH_LENGTH - (index * 2 - 1);//在数据中真实索引

        } else {
            index = WEEK_LENGTH - index;
        }
        Date date = AppDialog.DateUtil.getPastDate(index);
        return (date.getMonth() + 1) + "月";
    }

    public double getTodayDeepValue() {
        return todayDeep;
    }

    public double getTodayLightValue() {
        return todayLight;
    }

    private void onFail() {
        callback.onFail("查询失败，请稍后重试！");
    }

    public void release() {
        callback = null;
    }

}