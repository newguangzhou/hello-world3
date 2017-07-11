package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.xiaomaoqiu.now.view.chart.TextAimView;
import com.xiaomaoqiu.now.view.chart.ThreePartLineView;
import com.xiaomaoqiu.now.bussiness.pet.ChartDataSetUtils;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.pet.R;

import java.util.ArrayList;
import java.util.List;

import mbg.chartviews.CustomLineChart;
import mbg.chartviews.onChartValueSelectListener;


/**
 * Created by long on 2016/4/12.
 */
public class SportIndexActivity extends BaseActivity implements IChartCallback {
    private CustomLineChart monthChart;
    private CustomLineChart weekChart;
    private TextView todayTip,WeekTip,monthTip;
    private SportChartIndexPresenter presenter;
    ThreePartLineView threePartLineView_sport;
    TextAimView textAimView_sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.sport_index));
        setContentView(R.layout.activity_sport_index);
        initView();
        initData();
    }

    private void initView(){
        threePartLineView_sport= (ThreePartLineView) findViewById(R.id.threePartLineView_sport);
        textAimView_sport= (TextAimView) findViewById(R.id.textAimView_sport);
        monthChart=(CustomLineChart)findViewById(R.id.line_chart_month);
        weekChart=(CustomLineChart)findViewById(R.id.line_chart_week);
        todayTip=(TextView)findViewById(R.id.sport_index_totay_tip);
        WeekTip=(TextView)findViewById(R.id.sport_index_week_tip);
        monthTip=(TextView)findViewById(R.id.sport_index_month_tip);
        monthChart.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                if(null != presenter){
                    presenter.showDatas(index,xLabel,values, SportChartIndexPresenter.FLAG_MONTH);
                }
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(SportChartIndexPresenter.FLAG_MONTH);
            }
        });

        weekChart.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                if(null != presenter){
                    presenter.showDatas(index,xLabel,values, SportChartIndexPresenter.FLAG_WEEK);
                }
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(SportChartIndexPresenter.FLAG_WEEK);
            }
        });
    }

    private void initData(){
        presenter=new SportChartIndexPresenter(this);
        presenter.queryDatas();
    }

    @Override
    public void onSuccessGetAxis(ArrayList<String> labels, boolean ismonth) {
        if(ismonth){
            monthChart.setXAxisLabels(labels);
        }else{
            weekChart.setXAxisLabels(labels);
        }
    }

    @Override
    public void onSuccessGetWeight(double targetSport, double edSport) {
        threePartLineView_sport.setData((int)(targetSport),(int)(edSport));
        int totalWidth=threePartLineView_sport.getWidth();
        int targetWidth=edSport>0.0?(int)((edSport*totalWidth)/targetSport):totalWidth;
        if(targetSport>=edSport) {
            textAimView_sport.setAim((int) edSport + "", (int) targetSport + "", targetWidth);
        }else{
            textAimView_sport.setAim( (int) targetSport + "", (int) edSport + "",targetWidth);
        }
        String tip="今日目标消耗为"+targetSport+"千卡，实际消耗为"+edSport+"千卡。";
        todayTip.setText(tip);
    }

    @Override
    public void onSuccessGetSecAxis(String lable, int backIndex, int total) {
        monthChart.setSecondXAis(lable,backIndex,total);
    }

    @Override
    public void onSuccessGetMonthDataSet(ArrayList<Entry> deepList, ArrayList<Entry> lightList) {
        LineDataSet deepDataset= ChartDataSetUtils.getDefaultBlueHoleDataSet(this);
        deepDataset.setValues(deepList);
        monthChart.addData(deepDataset);

        LineDataSet lightDataset=ChartDataSetUtils.getDefaultRedDataSetWithPoint(this);
        lightDataset.setValues(lightList);
        monthChart.addData(lightDataset);

        monthChart.drawChart();
    }

    @Override
    public void onSuccessGetWeekDataSet(ArrayList<? extends Entry> deepList, ArrayList<Entry> lightList) {
        LineDataSet deepDataset= ChartDataSetUtils.getDefaultBlueHoleDataSet(this);
        deepDataset.setValues((List<Entry>) deepList);
        weekChart.addData(deepDataset);

        LineDataSet lightDataset=ChartDataSetUtils.getDefaultRedDataSetWithPoint(this);
        lightDataset.setValues(lightList);
        weekChart.addData(lightDataset);

        weekChart.drawChart();
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
        String tip=data+"消耗目标为"+values.get(0)+"千卡，实际消耗为"+values.get(1)+"千卡。";
        if(SportChartIndexPresenter.FLAG_WEEK == flag){
            WeekTip.setText(tip);
        }else{
            monthTip.setText(tip);
        }
    }

    @Override
    public void onHideDataTip(int flag) {
        if(SportChartIndexPresenter.FLAG_WEEK == flag){
            WeekTip.setText("");
        }else{
            monthTip.setText("");
        }
    }
}
