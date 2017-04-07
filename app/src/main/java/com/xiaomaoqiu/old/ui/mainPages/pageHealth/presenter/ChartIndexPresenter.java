package com.xiaomaoqiu.old.ui.mainPages.pageHealth.presenter;

import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.old.dataCenter.LoginMgr;
import com.xiaomaoqiu.old.dataCenter.UserMgr;
import com.xiaomaoqiu.old.utils.DateUtil;
import com.xiaomaoqiu.old.utils.HttpUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/2.
 */

public class ChartIndexPresenter {

    public static final String URL_GETSLEEP_STATUS="pet.health.get_sleep_info";
    public static final String URL_GETSPROT_STATUS="pet.health.get_sport_info";
    public static final String DEEP_SLEEP_FIELD_NAME="deep_sleep";
    public static final String LIGHT_SLEEP_FIELD_NAME="light_sleep";
    public static final String TARGET_AMOUNT_FIELD_NAME="target_amount";
    public static final String REALITY_AMOUNT_FIELD_NAME="reality_amount";
    public static final int FLAG_WEEK=0;
    public static final int FLAG_MONTH=1;
    public static final int WEEK_LENGTH=7;
    public static final int MONTH_LENGTH=30;


    private IChartCallback callback;

    private String httpUrl="";
    private String largeFieldName="";
    private String lowerFieldName="";
    private boolean isSleep=false;
    private double todayDeep=0,todayLight=0;


    public ChartIndexPresenter(IChartCallback callback, boolean isSlepp){
        this.callback=callback;
        this.isSleep=isSlepp;
        if(isSlepp){
            httpUrl=URL_GETSLEEP_STATUS;
            largeFieldName=DEEP_SLEEP_FIELD_NAME;
            lowerFieldName=LIGHT_SLEEP_FIELD_NAME;
        }else{
            httpUrl=URL_GETSPROT_STATUS;
            largeFieldName=TARGET_AMOUNT_FIELD_NAME;
            lowerFieldName=REALITY_AMOUNT_FIELD_NAME;
        }
    }


    public void queryDatas(){
        long msEnd = System.currentTimeMillis();
        long span30 = 30l*24*3600*1000;//30天:30*24*3600*1000
        long msStart = msEnd - span30;

        Date endDate = new Date(msEnd);
        Date startDate = new Date(msStart);

        String strEnd = String.format("%s-%s-%s",endDate.getYear()+1900,endDate.getMonth()+1,endDate.getDate());
        String strStart = String.format("%s-%s-%s",startDate.getYear()+1900,startDate.getMonth()+1,startDate.getDate());

        HttpUtil.get2(httpUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //{"status": 0, "data": [{"date": "2016-04-16", "deep_sleep": "6.6", "light_sleep": "2.4"},{}]}
                Log.v("http", httpUrl + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if(callback == null){
                    return;
                }
                if(ret != HttpCode.EC_SUCCESS){
                    onFail();
                    return;
                }
                parseSuccess(response);

            }

        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(), UserMgr.INSTANCE.getPetInfo().getPetID(), strStart, strEnd);
    }

    private void parseSuccess(JSONObject response){
        JSONArray jsdata = response.optJSONArray("data");
        if(null ==jsdata || jsdata.length() <= 0){
            onFail();
            return;
        }
        int days = jsdata.length();
        //设置今日睡眠
        JSONObject jsToday = (JSONObject) jsdata.opt(days - 1);
        todayDeep = jsToday.optDouble(largeFieldName, 1.0);
        todayLight = jsToday.optDouble(lowerFieldName, 1.0);
        callback.onSuccessGetWeight(todayDeep,todayLight);
        if(isSleep){
            parseSleepWeekList(jsdata);
        }else {
            parseList(jsdata, "日", false);//一周睡眠
        }
        parseList(jsdata,"",true);//30天睡眠
    }

    private void parseList(JSONArray jsdata,String format,boolean isMonth){
        int days= isMonth ? MONTH_LENGTH : WEEK_LENGTH;
        int intrval=isMonth ? 2 : 1;
        ArrayList<String> axisLabels=new ArrayList<>();

        ArrayList<Entry> deepList=new ArrayList<>();
        ArrayList<Entry> lightList=new ArrayList<>();

        ArrayList<Date> dates=DateUtil.getPastDates(days);

        if(isMonth) {
            Date curDate = DateUtil.getFirstDataOfCurMonth();
            String secondText = curDate.getMonth() + "月";
            int secondIndex = curDate.getDate();
            callback.onSuccessGetSecAxis(secondText,secondIndex,days);
        }
        int startIndex=jsdata.length()-days;
        if(startIndex<0){
            startIndex=0;
        }
        for(int i=startIndex; i<jsdata.length() ; i+=intrval){
            Date date=dates.get(i-startIndex);
            axisLabels.add(date.getDate()+format);
            JSONObject jsday = (JSONObject)jsdata.opt(i);
            float deep= (float) jsday.optDouble(largeFieldName);
            float light=(float) jsday.optDouble(lowerFieldName);
            deepList.add(new Entry(i-startIndex, (float) jsday.optDouble(largeFieldName)));
            if(isSleep) {
                lightList.add(new Entry(i - startIndex,deep+light));
            }
            else {
                lightList.add(new Entry(i - startIndex, (float) jsday.optDouble(lowerFieldName)));
            }
        }
        callback.onSuccessGetAxis(axisLabels,isMonth);
        if(isMonth){
            callback.onSuccessGetMonthDataSet(deepList,lightList);
        }else{
            callback.onSuccessGetWeekDataSet(deepList,lightList);
        }
    }

    private void parseSleepWeekList(JSONArray jsdata){
        ArrayList<String> axisLabels=new ArrayList<>();

        ArrayList<BarEntry> sleepList=new ArrayList<>();
        ArrayList<Date> dates=DateUtil.getPastDates(WEEK_LENGTH);

        int startIndex=jsdata.length()-WEEK_LENGTH;
        if(startIndex<0){
            startIndex=0;
        }
        for(int i=startIndex; i<jsdata.length() ; i++){
            Date date=dates.get(i-startIndex);
            axisLabels.add(date.getDate()+"日");
            JSONObject jsday = (JSONObject)jsdata.opt(i);
            sleepList.add(new BarEntry(i-startIndex, new float[]{(float) jsday.optDouble(largeFieldName),(float) jsday.optDouble(lowerFieldName)}));
        }
        callback.onSuccessGetAxis(axisLabels,false);
        callback.onSuccessGetWeekDataSet(sleepList,null);
    }


    /**
     * 显示某一固定日期数据
     * @param index
     * @param xaixsLabel
     * @param values
     * @param flag
     */
    public void showDatas(int index,String xaixsLabel,List<Float> values,int flag){
        if(null == callback){
            return;
        }
        String month=getMonth(index,FLAG_MONTH == flag);
        xaixsLabel=xaixsLabel.replace("日","");
        callback.onShowDataTip(month+xaixsLabel+"日",values,flag);
    }

    public String getMonth(int index,boolean isMonth){
        if(isMonth){
            index=MONTH_LENGTH - (index * 2 -1);//在数据中真实索引

        }else{
            index = WEEK_LENGTH - index;
        }
        Date date =DateUtil.getPastDate(index);
        return (date.getMonth()+1)+"月";
    }

    public double getTodayDeepValue(){
        return todayDeep;
    }
    public double getTodayLightValue(){
        return todayLight;
    }

    private void onFail(){
        callback.onFail("查询失败，请稍后重试！");
    }

    public void release(){
        callback=null;
    }

}
