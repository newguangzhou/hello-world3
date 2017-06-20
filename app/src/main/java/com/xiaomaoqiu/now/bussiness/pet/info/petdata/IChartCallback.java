package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/2.
 */

public interface IChartCallback {
    public void onSuccessGetAxis(ArrayList<String> labels,boolean ismonth);
    public void onSuccessGetWeight(double deep,double light);
    public void onSuccessGetSecAxis(String lable,int backIndex,int total);//第二个参数为倒数的index
    public void onSuccessGetMonthDataSet(ArrayList<Entry> deepList,ArrayList<Entry> lightList);
    public void onSuccessGetWeekDataSet(ArrayList<? extends Entry> deepList,ArrayList<Entry> lightList);
    public void onFail(String msg);
    public void onShowDataTip(String data, List<Float> values, int flag);//点击时提示数据
    public void onHideDataTip(int flag);
}
