package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.xiaomaoqiu.now.bussiness.bean.PetSleepInfoBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.AppDialog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 2017/1/2.
 */

public class SleepChartIndexPresenter {
    public static final int FLAG_WEEK = 0;
    public static final int FLAG_MONTH = 1;
    public static final int WEEK_LENGTH = 7;
    public static final int MONTH_LENGTH = 30;


    private IChartCallback callback;

    private double todayDeep = 0, todayLight = 0;


    public SleepChartIndexPresenter(IChartCallback callback) {
        this.callback = callback;
    }


    public void queryDatas() {
        long msEnd = System.currentTimeMillis();
        long span30 = 30l * 24 * 3600 * 1000;//30天:30*24*3600*1000
        long msStart = msEnd - span30;

        Date endDate = new Date(msEnd);
        Date startDate = new Date(msStart);

        String strEnd = String.format("%s-%s-%s", endDate.getYear() + 1900, endDate.getMonth() + 1, endDate.getDate());
        String strStart = String.format("%s-%s-%s", startDate.getYear() + 1900, startDate.getMonth() + 1, startDate.getDate());
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
                parseSleepInfo(message);
            }

            @Override
            public void onFail(Call<PetSleepInfoBean> call, Throwable t) {

            }
        });

    }

    //解析睡眠信息
    public void parseSleepInfo(PetSleepInfoBean message) {
        List<PetSleepInfoBean.SleepBean> sleepDatas = message.data;
        if (sleepDatas == null || sleepDatas.size() <= 0) {
//            ToastUtil.showTost("当前睡眠数据为空");
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
        Date date = new Date();
        PetSleepInfoBean.SleepBean todayBean = timeEqualSleepBean(date, sleepDatas);

        todayDeep = todayBean.deep_sleep;
        todayLight = todayBean.light_sleep;
        callback.onSuccessGetWeight(todayDeep, todayLight);
    }

    //解析一周
    void parseSleepWeekList(List<PetSleepInfoBean.SleepBean> sleepDatas) {
        ArrayList<String> axisLabels = new ArrayList<>();

        ArrayList<BarEntry> sleepList = new ArrayList<>();
        ArrayList<Date> dates = AppDialog.DateUtil.getPastDates(WEEK_LENGTH);

        int startIndex = 0;
        boolean flag = false;
        for (int i = startIndex; i < WEEK_LENGTH; i++) {
            Date date = dates.get(i - startIndex);
            axisLabels.add(date.getDate() + "日");

            PetSleepInfoBean.SleepBean bean = timeEqualSleepBean(date, sleepDatas);
            sleepList.add(new BarEntry(i - startIndex, new float[]{(float) bean.deep_sleep, (float) bean.light_sleep}));
            if ((bean.deep_sleep > 0)) {
                flag = true;
            }
        }
        if(!flag){
            return;
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
        int startIndex = 0;
        boolean flag = false;
        for (int i = startIndex; i < MONTH_LENGTH; i += intrval) {
            Date date = dates.get(i - startIndex);
            axisLabels.add(date.getDate() + format);
//            JSONObject jsday = (JSONObject)jsdata.opt(i);
            PetSleepInfoBean.SleepBean bean = timeEqualSleepBean(date, sleepDatas);
//            float deep = (float) bean.deep_sleep;
//            float light = (float) bean.light_sleep;
            deepList.add(new Entry(i - startIndex, (float) bean.deep_sleep));
            lightList.add(new Entry(i - startIndex, (float) bean.light_sleep));
            if ((bean.deep_sleep > 0)) {
                flag = true;
            }
        }
        if(!flag){
            return;
        }
        callback.onSuccessGetAxis(axisLabels, true);
        callback.onSuccessGetMonthDataSet(deepList, lightList);
    }

    //只要日期对，就可以
    boolean equalDateDay(Date date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        String changeResult = sdf.format(date1);
        return changeResult.equals(date2);
    }

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

    public void release() {
        callback = null;
    }

}