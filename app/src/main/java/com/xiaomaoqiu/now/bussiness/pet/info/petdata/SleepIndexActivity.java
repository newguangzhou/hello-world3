package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.view.chart.ThreePartLineViewWithTotal;
import com.xiaomaoqiu.now.bussiness.pet.ChartDataSetUtils;
import com.xiaomaoqiu.pet.R;

import java.util.ArrayList;
import java.util.List;

import mbg.chartviews.CustomBarChart;
import mbg.chartviews.CustomLineChart;
import mbg.chartviews.onChartValueSelectListener;

/**
 * Created by Administrator on 2016/4/12.
 */
public class SleepIndexActivity extends BaseActivity implements IChartCallback {

    private CustomLineChart monthChartView;
    private CustomBarChart weekChartView;
    private TextView todayTip,weekTip,monthTip;


    ThreePartLineViewWithTotal threePartLineView_sleep;
//    TextAimView textAimView_sleep;

    private SleepChartIndexPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.sleep_index));
        setContentView(R.layout.activity_health_sleep);
        initView();
        initData();
    }
    private void initView(){
        threePartLineView_sleep= (ThreePartLineViewWithTotal) findViewById(R.id.threePartLineView_sleep);
//        textAimView_sleep= (TextAimView) findViewById(R.id.textAimView_sleep);
        monthChartView=(CustomLineChart)findViewById(R.id.line_chart_month);
        weekChartView=(CustomBarChart)findViewById(R.id.bar_chart_week);
        todayTip=(TextView)findViewById(R.id.sleep_index_totay_tip);
        weekTip=(TextView)findViewById(R.id.sleep_index_week_tip);
        monthTip=(TextView)findViewById(R.id.sleep_index_month_tip);
        monthChartView.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                presenter.showDatas(index,xLabel,values, SleepChartIndexPresenter.FLAG_MONTH);
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(SleepChartIndexPresenter.FLAG_MONTH);
            }
        });
        weekChartView.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                presenter.showDatas(index,xLabel,values, SleepChartIndexPresenter.FLAG_WEEK);
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(SleepChartIndexPresenter.FLAG_WEEK);
            }
        });
    }

    private void initData(){
        presenter=new SleepChartIndexPresenter(this);
        presenter.queryDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != presenter){
            presenter.release();
        }
    }


    @Override
    public void onSuccessGetAxis(ArrayList<String> labels, boolean ismonth) {
        if(ismonth){
            if(null != labels && labels.size() >0) {
                String last = labels.get(labels.size() - 1);
                labels.set(labels.size()-1,last+"日");
            }
            monthChartView.setXAxisLabels(labels);
        }else{
            weekChartView.setXAxisLabels(labels);
        }
    }

    @Override
    public void onSuccessGetWeight(double deep, double light) {
//        light=10;
//        deep=100;
        threePartLineView_sleep.setData(deep,light);
        int totalWidth=threePartLineView_sleep.getWidth();
//        textAimView_sleep.setAim((int)light+"",(deep+light)+"", (int)((light*totalWidth)/(deep+light)));
        String tip="今日深度睡眠为"+deep+"小时，浅度睡眠为"+light+"小时。";
        todayTip.setText(tip);
    }

    @Override
    public void onSuccessGetSecAxis(String lable, int index, int  totalindex) {
            monthChartView.setSecondXAis(lable,index,totalindex);
    }

    @Override
    public void onSuccessGetMonthDataSet(ArrayList<Entry> deepList, ArrayList<Entry> lightList) {

        LineDataSet deepDataset= ChartDataSetUtils.getDefaultBlueHoleDataSet(this);
        deepDataset.setValues(deepList);
        monthChartView.addData(deepDataset);
        LineDataSet lightDataset=ChartDataSetUtils.getDefaultRedDataSetWithPoint(this);
        lightDataset.setValues(lightList);
        monthChartView.addData(lightDataset);

        monthChartView.drawChart();

    }

    @Override
    public void onSuccessGetWeekDataSet(ArrayList<? extends Entry> deepList, ArrayList<Entry> lightList) {
        BarDataSet dataSet=ChartDataSetUtils.getDefaultBarDateSet((List<BarEntry>) deepList,this);
        weekChartView.setData(dataSet);
        weekChartView.drawChart();
    }

    @Override
    public void onFail(String msg) {
        showToast(msg);
    }

    @Override
    public void onShowDataTip(String data, List<Float> values, int flag) {
        if(values == null || values.size() <2){
            return;
        }
        String tip=data+"深度睡眠为"+(values.get(0))+"小时，浅度睡眠为"+(values.get(1))+"小时。";
        if(SleepChartIndexPresenter.FLAG_WEEK == flag){
            weekTip.setText(tip);
        }else{
            monthTip.setText(tip);
        }
    }

    @Override
    public void onHideDataTip(int flag) {
        if(SleepChartIndexPresenter.FLAG_WEEK == flag){
            weekTip.setText("");
        }else{
            monthTip.setText("");
        }
    }
}
