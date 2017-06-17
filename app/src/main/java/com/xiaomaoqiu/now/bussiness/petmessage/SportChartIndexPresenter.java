package com.xiaomaoqiu.now.bussiness.petmessage;

import com.github.mikephil.charting.data.Entry;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.bussiness.bean.PetSportBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.AppDialog;
import com.xiaomaoqiu.now.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/2.
 */

public class SportChartIndexPresenter {
    public static final int FLAG_WEEK = 0;
    public static final int FLAG_MONTH = 1;
    public static final int WEEK_LENGTH = 7;
    public static final int MONTH_LENGTH = 30;

    private IChartCallback callback;

    private double todayDeep = 0, todayLight = 0;

    public SportChartIndexPresenter(IChartCallback callback) {
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
        Date date=new Date();
        PetSportBean.SportBean todayBean = timeEqualSportBean(date,sportDatas);
        todayDeep = todayBean.target_amount;
        todayLight = todayBean.reality_amount;
        callback.onSuccessGetWeight(todayDeep, todayLight);
        EventManage.TodaySportData event = new EventManage.TodaySportData();

        event.sportBean = todayBean;
        PetInfoInstance.getInstance().setTarget_step( (int)event.sportBean.target_amount);
        EventBus.getDefault().post(event);
    }

    //解析一周的运动数据
    void parseSportWeekAndMonthList(List<PetSportBean.SportBean> sportDatas, String format, boolean isMonth) {
        int days = isMonth ? MONTH_LENGTH : WEEK_LENGTH;
        int intrval=isMonth?2:1;
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
        boolean flag = false;
        for (int i = startIndex; i < days; i += intrval) {
            Date date = dates.get(i - startIndex);
            axisLabels.add(date.getDate() + format);
            PetSportBean.SportBean bean = timeEqualSportBean(date, sportDatas);
            deepList.add(new Entry(i - startIndex, (float) bean.target_amount));
            lightList.add(new Entry(i - startIndex, (float) bean.reality_amount ));

            if ((bean.target_amount > 0)) {
                flag = true;
            }
        }
        if(!flag){
            return;
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