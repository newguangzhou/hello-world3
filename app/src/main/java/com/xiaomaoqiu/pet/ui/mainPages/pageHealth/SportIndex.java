package com.xiaomaoqiu.pet.ui.mainPages.pageHealth;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.ui.mainPages.pageHealth.presenter.ChartIndexPresenter;
import com.xiaomaoqiu.pet.ui.mainPages.pageHealth.presenter.IChartCallback;
import com.xiaomaoqiu.pet.utils.ChartDataSetUtils;
import com.xiaomaoqiu.pet.widgets.ActivityEx;

import java.util.ArrayList;
import java.util.List;

import mbg.chartviews.CustomLineChart;
import mbg.chartviews.onChartValueSelectListener;


/**
 * Created by Administrator on 2016/4/12.
 */
public class SportIndex extends ActivityEx implements IChartCallback {
    private CustomLineChart monthChart;
    private CustomLineChart weekChart;
    private TextView todayTip,WeekTip,monthTip;
    private ChartIndexPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.sport_index));

        setContentView(R.layout.activity_sport_index);

        initView();
        initData();
    }

    private void initView(){
        monthChart=(CustomLineChart)findViewById(R.id.line_chart_month);
        weekChart=(CustomLineChart)findViewById(R.id.line_chart_week);
        todayTip=(TextView)findViewById(R.id.sport_index_totay_tip);
        WeekTip=(TextView)findViewById(R.id.sport_index_week_tip);
        monthTip=(TextView)findViewById(R.id.sport_index_month_tip);
        monthChart.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                if(null != presenter){
                    presenter.showDatas(index,xLabel,values,ChartIndexPresenter.FLAG_MONTH);
                }
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(ChartIndexPresenter.FLAG_MONTH);
            }
        });

        weekChart.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                if(null != presenter){
                    presenter.showDatas(index,xLabel,values,ChartIndexPresenter.FLAG_WEEK);
                }
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(ChartIndexPresenter.FLAG_WEEK);
            }
        });
    }

    private void initData(){
        presenter=new ChartIndexPresenter(this,false);
        presenter.queryDatas();
    }

    @Override
    public void onSuccessGetAxis(ArrayList<String> labels, boolean ismonth) {
        if(ismonth){
            if(null != labels && labels.size() >0) {
                String last = labels.get(labels.size() - 1);
                labels.set(labels.size()-1,last+"日");
            }
            monthChart.setXAxisLabels(labels);
        }else{
            weekChart.setXAxisLabels(labels);
        }
    }

    @Override
    public void onSuccessGetWeight(double deep, double light) {
        View vDoneSport = findViewById(R.id.v_sport_done);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) vDoneSport.getLayoutParams();
        layoutParams.weight = (float) deep / 1000;
        vDoneSport.setLayoutParams(layoutParams);

        View vSportRemain = findViewById(R.id.v_sport_remain);
        layoutParams = (LinearLayout.LayoutParams) vSportRemain.getLayoutParams();
        layoutParams.weight = (float) (deep - light) / 1000;
        vSportRemain.setLayoutParams(layoutParams);

        View vNoneSport = findViewById(R.id.v_sport_none);
        layoutParams = (LinearLayout.LayoutParams) vNoneSport.getLayoutParams();
        layoutParams.weight = (float) (1000 - deep - light) / 1000;
        vNoneSport.setLayoutParams(layoutParams);
        String tip="今日目标消耗为"+deep+"千卡，实际消耗为"+light+"千卡。";
        todayTip.setText(tip);
    }

    @Override
    public void onSuccessGetSecAxis(String lable, int backIndex, int total) {
        monthChart.setSecondXAis(lable,backIndex,total);
    }

    @Override
    public void onSuccessGetMonthDataSet(ArrayList<Entry> deepList, ArrayList<Entry> lightList) {
        LineDataSet deepDataset= ChartDataSetUtils.getDefaultBlueFillDataSet(this);
        deepDataset.setValues(deepList);
        monthChart.addData(deepDataset);

        LineDataSet lightDataset=ChartDataSetUtils.getDefaultRedDataSetWithoutPoint(this);
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
        if(ChartIndexPresenter.FLAG_WEEK == flag){
            WeekTip.setText(tip);
        }else{
            monthTip.setText(tip);
        }
    }

    @Override
    public void onHideDataTip(int flag) {
        if(ChartIndexPresenter.FLAG_WEEK == flag){
            WeekTip.setText("");
        }else{
            monthTip.setText("");
        }
    }
}
