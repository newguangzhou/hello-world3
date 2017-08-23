package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.now.view.chart.ThreePartLineViewWithTotal;
import com.xiaomaoqiu.now.bussiness.pet.ChartDataSetUtils;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import mbg.chartviews.CustomBarChart;
import mbg.chartviews.CustomLineChart;
import mbg.chartviews.onChartValueSelectListener;

/**
 * Created by Administrator on 2016/4/12.
 */
public class SleepIndexActivity extends BaseActivity implements IChartCallback {


    public View btn_go_back;
    public BatteryView batteryView;

    private CustomLineChart monthChartView;
    private CustomBarChart weekChartView;
    private TextView todayTip, weekTip, monthTip;


    ThreePartLineViewWithTotal threePartLineView_sleep;
    private SleepChartIndexPresenter presenter;


    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceOffline(EventManage.DeviceOffline event) {
        batteryView.setDeviceOffline();
//        DialogUtil.showDeviceOfflineDialog(this, "离线通知");
    }

    //设备状态更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceInfoChanged(EventManage.notifyDeviceStateChange event) {
        if (!DeviceInfoInstance.getInstance().online) {
//            ToastUtil.showTost("您的设备尚未开机！");
            batteryView.setDeviceOffline();
            return;
        }
        batteryView.showBatterylevel(DeviceInfoInstance.getInstance().battery_level,
                DeviceInfoInstance.getInstance().lastGetTime);
    }

    //todo 小米推送
    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void receivePushDeviceOffline(PushEventManage.deviceOffline event) {
        batteryView.setDeviceOffline();
//        DialogUtil.showDeviceOfflineDialog(this, "离线通知");
    }


    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle("休息统计");
        setContentView(R.layout.activity_health_sleep);
        initView();
        initData();
    }

    private void initView() {
        btn_go_back = findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        threePartLineView_sleep = (ThreePartLineViewWithTotal) findViewById(R.id.threePartLineView_sleep);
        monthChartView = (CustomLineChart) findViewById(R.id.line_chart_month);
        weekChartView = (CustomBarChart) findViewById(R.id.bar_chart_week);
        todayTip = (TextView) findViewById(R.id.sleep_index_totay_tip);
        weekTip = (TextView) findViewById(R.id.sleep_index_week_tip);
        monthTip = (TextView) findViewById(R.id.sleep_index_month_tip);
        monthChartView.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                presenter.showMonthDatas(index, xLabel, values, SleepChartIndexPresenter.FLAG_MONTH);
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(SleepChartIndexPresenter.FLAG_MONTH);
            }
        });
        weekChartView.setOnChartValueSelectListener(new onChartValueSelectListener() {
            @Override
            public void onChartValueSelect(int index, String xLabel, List<Float> values) {
                presenter.showWeekDatas(index, xLabel, values, SleepChartIndexPresenter.FLAG_WEEK);
            }

            @Override
            public void onCancleSelect() {
                onHideDataTip(SleepChartIndexPresenter.FLAG_WEEK);
            }
        });

        batteryView = (BatteryView) findViewById(R.id.batteryView);
        batteryView.setActivity(this);

        //点击弹出电池
        batteryView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!DeviceInfoInstance.getInstance().online) {
                    ToastUtil.showTost("追踪器离线");
                    return;
                }
                if (DeviceInfoInstance.getInstance().battery_level > 1.0f) {
                    PetInfoInstance.getInstance().getPetLocation();
                }
                batteryView.pushBatteryDialog(DeviceInfoInstance.getInstance().battery_level,
                        DeviceInfoInstance.getInstance().lastGetTime);
            }
        });

        if (!DeviceInfoInstance.getInstance().online) {
//            ToastUtil.showTost("您的设备尚未开机！");
            batteryView.setDeviceOffline();
        } else {
            batteryView.showBatterylevel(DeviceInfoInstance.getInstance().battery_level,
                    DeviceInfoInstance.getInstance().lastGetTime);
        }

        EventBus.getDefault().register(this);
    }

    private void initData() {
        presenter = new SleepChartIndexPresenter(this);
        presenter.queryDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != presenter) {
            presenter.release();
        }
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onSuccessGetAxis(ArrayList<String> labels, boolean ismonth) {
        if (ismonth) {
            if (null != labels && labels.size() > 0) {
                String last = labels.get(labels.size() - 1);
                labels.set(labels.size() - 1, last + "日");
            }
            monthChartView.setXAxisLabels(labels);
        } else {
            weekChartView.setXAxisLabels(labels);
        }
    }

    @Override
    public void onSuccessGetWeight(double deep, double light) {
        threePartLineView_sleep.setData(deep, light);
        String tip = "今日深睡为" + deep + "小时，小憩为" + light + "小时。";
        todayTip.setText(tip);
    }

    @Override
    public void onSuccessGetSecAxis(String lable, int index, int totalindex) {
        monthChartView.setSecondXAis(lable, index, totalindex);
    }

    @Override
    public void onSuccessGetMonthDataSet(ArrayList<Entry> deepList, ArrayList<Entry> lightList) {

        LineDataSet deepDataset = ChartDataSetUtils.getDefaultRedDataSetWithPoint(this);
        deepDataset.setValues(deepList);
        monthChartView.addData(deepDataset);
        LineDataSet lightDataset = ChartDataSetUtils.getDefaultBlueDataSetFill(this);
        lightDataset.setValues(lightList);
        monthChartView.addData(lightDataset);

        monthChartView.drawChart();

    }

    @Override
    public void onSuccessGetWeekDataSet(ArrayList<? extends Entry> deepList, ArrayList<Entry> lightList) {
        BarDataSet dataSet = ChartDataSetUtils.getDefaultBarDateSet((List<BarEntry>) deepList, this);
        weekChartView.setData(dataSet);
        weekChartView.drawChart();
    }

    @Override
    public void onFail(String msg) {
        showToast(msg);
    }

    @Override
    public void onShowDataTip(String data, List<Float> values, int flag) {
        if (values == null || values.size() < 2) {
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化
        String tip = data + "深睡为" + df.format(values.get(0)) + "小时，小憩为" + (values.get(1)) + "小时。";
        if (SleepChartIndexPresenter.FLAG_WEEK == flag) {
            weekTip.setText(tip);
        } else {
            monthTip.setText(tip);
        }
    }

    @Override
    public void onShowWeekDataTip(String data, List<Float> values, int flag) {
        if (values == null || values.size() < 2) {
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化
        String tip = data + "深睡为" + df.format(values.get(0)) + "小时，小憩为" + (values.get(1)) + "小时。";
        if (SleepChartIndexPresenter.FLAG_WEEK == flag) {
            weekTip.setText(tip);
        } else {
            monthTip.setText(tip);
        }
    }

    @Override
    public void onShowMonthDataTip(String data, List<Float> values, int flag) {
        if (values == null || values.size() < 2) {
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化
        String tip = data + "深睡为" + df.format(values.get(0)-values.get(1)) + "小时，小憩为" + (values.get(1)) + "小时。";
        if (SleepChartIndexPresenter.FLAG_WEEK == flag) {
            weekTip.setText(tip);
        } else {
            monthTip.setText(tip);
        }
    }

    @Override
    public void onHideDataTip(int flag) {
        if (SleepChartIndexPresenter.FLAG_WEEK == flag) {
            weekTip.setText("");
        } else {
            monthTip.setText("");
        }
    }




}
