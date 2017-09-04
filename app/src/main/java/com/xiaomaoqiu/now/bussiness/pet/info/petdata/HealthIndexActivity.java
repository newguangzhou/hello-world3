package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.bean.PetSleepInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.PetSportBean;
import com.xiaomaoqiu.now.bussiness.bean.Summary;
import com.xiaomaoqiu.now.bussiness.pet.PetInfo;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.BatteryView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

@SuppressLint("WrongConstant")
public class HealthIndexActivity extends BaseActivity implements PickSportNumberDialog_RAW_Activity.OnPickNumberListener, View.OnClickListener {

    private View btn_go_back;
    public BatteryView batteryView;

    static int REQ_CODE_GO_OUT = 1;
    TextView tv_name;
    TextView tv_date;
    TextView tv_health_sport_ed;
    TextView tv_health_sport_message;
    Button bt_health_sport_message;
    View tv_tosport;
    View health_index_bt_change;

    TextView tv_health_sleep_deep;
    TextView tv_health_sleep_light;
    TextView tv_health_sleep_message;
    Button bt_health_sleep_message;
    View tv_tosleep;

    View ll_sport_all;
    View ll_sleep_all;
    View ll_nodata;


    String petName = PetInfoInstance.getInstance().getNick();
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

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.health_title));
        setContentView(R.layout.activity_new_health_index);
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
        ll_sport_all = findViewById(R.id.ll_sport_all);
        ll_sleep_all = findViewById(R.id.ll_sleep_all);
        ll_nodata = findViewById(R.id.ll_nodata);
        health_index_bt_change = findViewById(R.id.health_index_bt_change);
        health_index_bt_change.setOnClickListener(this);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_name.setText(petName + "健康日记");
        tv_date = (TextView) this.findViewById(R.id.tv_date);
        tv_health_sport_message = (TextView) this.findViewById(R.id.tv_health_sport_message);
        bt_health_sport_message = (Button) this.findViewById(R.id.bt_health_sport_message);

        tv_health_sport_ed = (TextView) this.findViewById(R.id.tv_health_sport_ed);
        tv_tosport = this.findViewById(R.id.tv_tosport);


        tv_tosport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HealthIndexActivity.this, SportIndexActivity.class);
                startActivity(intent);
            }
        });


        tv_health_sleep_deep = (TextView) findViewById(R.id.tv_health_sleep_deep);
        tv_health_sleep_light = (TextView) findViewById(R.id.tv_health_sleep_light);
        tv_health_sleep_message = (TextView) findViewById(R.id.tv_health_sleep_message);
        bt_health_sleep_message = (Button) findViewById(R.id.bt_health_sleep_message);
        tv_tosleep = findViewById(R.id.tv_tosleep);
        tv_tosleep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HealthIndexActivity.this, SleepIndexActivity.class);
                startActivity(intent);
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
        Date today = new Date();
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(today);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时间

        String strEnd = String.format("%s-%s-%s", dBefore.getYear() + 1900, dBefore.getMonth() + 1, dBefore.getDate());
        String strStart = strEnd;

        ApiUtils.getApiService().getActivityInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                UserInstance.getInstance().pet_id, strStart, strEnd).enqueue(new XMQCallback<PetSportBean>() {
            @Override
            public void onSuccess(Response<PetSportBean> response, PetSportBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {

                    if (message.data.size() > 0) {
                        ll_sport_all.setVisibility(View.VISIBLE);
                        ll_sleep_all.setVisibility(View.VISIBLE);
                        ll_nodata.setVisibility(View.GONE);
                        PetSportBean.SportBean bean = message.data.get(0);
                        DecimalFormat df = new DecimalFormat("0.00");//格式化
//                        sportTarget = df.format(bean.target_amount);
                        PetInfoInstance.getInstance().yesterday_target_amount = (int) bean.target_amount;
                        PetInfoInstance.getInstance().yesterday_reality_amount = bean.reality_amount;
                        PetInfoInstance.getInstance().yesterday_percentage = bean.percentage;

                        tv_health_sport_ed.setText(PetInfoInstance.getInstance().yesterday_percentage + "");
                        String text = "";
                        String sportMessage = "";
                        if (PetInfoInstance.getInstance().yesterday_percentage <= 100) {
                            text = PetInfoInstance.getInstance().getNick() + "昨天运动消耗了"
                                    + PetInfoInstance.getInstance().yesterday_reality_amount + "卡路里，离运动目标还有"
                                    + (100 - PetInfoInstance.getInstance().yesterday_percentage) + "%的距离";
                            sportMessage = "运动不足将会导致" + PetInfoInstance.getInstance().getNick() + "肥胖、心肺功能不足、沉郁、焦躁不安、啃咬家具、吠叫甚至攻击行为。为ta的健康，请努力完成运动目标。";

                        } else if (PetInfoInstance.getInstance().yesterday_percentage <= 110) {
                            text = PetInfoInstance.getInstance().getNick() + "昨天运动消耗了"
                                    + PetInfoInstance.getInstance().yesterday_reality_amount + "卡路里，完美完成运动目标";
                            sportMessage = "适当运动能保持" + PetInfoInstance.getInstance().getNick() + "健康，也让Ta得到足够的玩乐，帮助Ta发泄情绪。请继续保持良好的运动习惯。";
                        } else if (PetInfoInstance.getInstance().yesterday_percentage <= 115) {
                            text = PetInfoInstance.getInstance().getNick() + "昨天运动消耗" + PetInfoInstance.getInstance().yesterday_reality_amount + "卡里路，超额完成运动目标" + (PetInfoInstance.getInstance().yesterday_percentage - 100) + "%";
                            sportMessage = "适当提高运动强度值得鼓励的。但是过量的运动会让" + PetInfoInstance.getInstance().getNick() + "心脏、关节过度负荷，诱发疾病。建议密切观察ta日常表现。";
                        } else if (PetInfoInstance.getInstance().yesterday_percentage > 115) {
                            text = PetInfoInstance.getInstance().getNick() + "昨天运动消耗" + PetInfoInstance.getInstance().yesterday_reality_amount + "卡里路，超额完成运动目标" + (PetInfoInstance.getInstance().yesterday_percentage - 100) + "%";
                            sportMessage = "过高的运动量或让心脏过度负荷、肌肉疲劳、关节劳损，产生心肺功能障碍、骨关节脱臼、骨折等问题。建议让兽医评估" + PetInfoInstance.getInstance().getNick() + "身体情况后，再决定运动方案。";
                        }
                        tv_health_sport_message.setText(text);
                        bt_health_sport_message.setText(sportMessage);
                    } else {
//                        ToastUtil.showTost("当天尚无数据~");
                        ll_sport_all.setVisibility(View.GONE);
                        ll_sleep_all.setVisibility(View.GONE);
                        ll_nodata.setVisibility(View.VISIBLE);
                    }
                } else {
//                    ToastUtil.showTost("获取昨天数据失败");
                    ll_sport_all.setVisibility(View.GONE);
                    ll_sleep_all.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(Call<PetSportBean> call, Throwable t) {
                ToastUtil.showTost("获取昨天数据失败");
                ll_sport_all.setVisibility(View.GONE);
                ll_sleep_all.setVisibility(View.GONE);
                ll_nodata.setVisibility(View.VISIBLE);
            }
        });


        ApiUtils.getApiService().getSleepInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                UserInstance.getInstance().pet_id, strStart, strEnd).enqueue(new XMQCallback<PetSleepInfoBean>() {
            @Override
            public void onSuccess(Response<PetSleepInfoBean> response, PetSleepInfoBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    if (message.data.size() > 0) {
                        ll_sport_all.setVisibility(View.VISIBLE);
                        ll_sleep_all.setVisibility(View.VISIBLE);
                        ll_nodata.setVisibility(View.GONE);
                        PetSleepInfoBean.SleepBean bean = message.data.get(0);
                        PetInfoInstance.getInstance().yesterday_deep_sleep = bean.deep_sleep;
                        PetInfoInstance.getInstance().yesterday_light_sleep = bean.light_sleep;
                        DecimalFormat df = new DecimalFormat("0.00");//格式化

                        tv_health_sleep_deep.setText(PetInfoInstance.getInstance().yesterday_deep_sleep + "");
                        tv_health_sleep_light.setText(PetInfoInstance.getInstance().yesterday_light_sleep + "");

                        double sleepTime = PetInfoInstance.getInstance().yesterday_deep_sleep + PetInfoInstance.getInstance().yesterday_light_sleep;
                        String sleepTimeString = df.format(sleepTime);
                        String sleepText = "";
                        String sleepMessage = "";
                        if (sleepTime <= 2) {
                            sleepText = "休息严重不足";
                            sleepMessage = PetInfoInstance.getInstance().getNick() + "昨天休息" + sleepTimeString + "小时，休息时间严重不足，过度亢奋。提示Ta的情绪或者身体出现了异常，请尽快联系兽医。";
                        } else if (sleepTime <= 4) {
                            sleepText = "休息过少";
                            sleepMessage = PetInfoInstance.getInstance().getNick() + "昨天休息" + sleepTimeString + "小时，休息时间不足。注意让Ta多点休息，避免内脏和关节的损伤。";
                        } else if (sleepTime <= 10) {
                            sleepText = "休息正常";
                            sleepMessage = PetInfoInstance.getInstance().getNick() + "昨天休息" + sleepTimeString + "小时，与大多数同类动物休息时长相当。" + PetInfoInstance.getInstance().getNick() + "活力充足，放心带Ta去做运动吧。";
                        } else if (sleepTime > 10) {
                            sleepText = "嗜睡";
                            sleepMessage = PetInfoInstance.getInstance().getNick() + "昨天休息" + sleepTimeString + "小时，休息时间过长。沉郁、活力减少均提示身体出现异常，请尽快联系兽医。";
                        }
                        tv_health_sleep_message.setText("数据解读：" + sleepText);
                        bt_health_sleep_message.setText(sleepMessage);
                    } else {
                        ll_sport_all.setVisibility(View.GONE);
                        ll_sleep_all.setVisibility(View.GONE);
                        ll_nodata.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtil.showTost("获取昨天数据失败");
                }
            }

            @Override
            public void onFail(Call<PetSleepInfoBean> call, Throwable t) {

            }
        });


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dBefore);
        tv_date.setText(dateString + "(昨天)");

    }


    @Override
    public void onConfirmNumber(final String numberString) {
//        int length=numberString.length();
//        double number=Double.valueOf(numberString.substring(0,length-1))/100f;
//        final int target_energyInt=(int)(number*Double.valueOf(PetInfoInstance.getInstance().packBean.target_energy));
        final DecimalFormat df = new DecimalFormat("0.00");//格式化
        final double target_energyDouble = Double.valueOf(numberString);
        //设定运动目标
        ApiUtils.getApiService().setSportInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), UserInstance.getInstance().pet_id, (int) target_energyDouble, df.format(target_energyDouble)).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        ToastUtil.showTost("设定运动量成功");
                        EventManage.targetSportData event = new EventManage.targetSportData();
                        PetInfoInstance.getInstance().setTarget_step((int) target_energyDouble);
                        PetInfoInstance.getInstance().packBean.target_energy = df.format(target_energyDouble);
                        EventBus.getDefault().post(event);
                        break;

                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.health_index_bt_expert:
                break;
            case R.id.health_index_bt_change:
                PickSportNumberDialog_RAW_Activity dialog = new PickSportNumberDialog_RAW_Activity(HealthIndexActivity.this, R.style.MyDialogStyleBottom);
                dialog.setOnPickNumberListener(HealthIndexActivity.this);
                dialog.show();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
