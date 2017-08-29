package com.xiaomaoqiu.now.bussiness.pet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.base.BaseFragment;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.ThreadUtil;
import com.xiaomaoqiu.now.bussiness.bean.PetSleepInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.PetSportBean;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.main.MapInstance;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.DialogToast;
import com.xiaomaoqiu.now.view.refresh.MaterialDesignPtrFrameLayout;
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.AsynImgDialog;
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.HealthIndexActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.SleepIndexActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.SportIndexActivity;
import com.xiaomaoqiu.now.view.CircleProgressBar;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.Date;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2015/6/12.
 */

@SuppressLint("WrongConstant")
public class PetFragment extends BaseFragment implements View.OnClickListener {


//    MaterialDesignPtrFrameLayout ptr_refresh;

    CircleProgressBar prog;
    TextView tvSportDone;
    TextView tvSportTarget;
    TextView tv_sleep_time;
    TextView tv_findpet;

    String strStart;
    String strEnd;

    String sportTarget = PetInfoInstance.getInstance().packBean.target_energy;
    public static double sportDone = 0.0;
    double percentage = 0;

    CheckIndex checkIndex;


    View label1;//百分号显示还是不显示



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.main_tab_health, container, false);
//        ptr_refresh = (MaterialDesignPtrFrameLayout) rootView.findViewById(R.id.ptr_refresh);
        prog = (CircleProgressBar) rootView.findViewById(R.id.prog_target_done_percentage);
        tvSportDone = (TextView) rootView.findViewById(R.id.tv_sport_done);
        tvSportTarget = (TextView) rootView.findViewById(R.id.tv_sport_target);
        tv_sleep_time = (TextView) rootView.findViewById(R.id.tv_sleep_time);
        tv_findpet = (TextView) rootView.findViewById(R.id.tv_findpet);
        label1=rootView.findViewById(R.id.label1);

        rootView.setOnClickListener(this);
        rootView.findViewById(R.id.btn_sport_index).setOnClickListener(this);
        rootView.findViewById(R.id.btn_locate).setOnClickListener(this);
        rootView.findViewById(R.id.btn_health).setOnClickListener(this);
        rootView.findViewById(R.id.btn_sport).setOnClickListener(this);
        rootView.findViewById(R.id.btn_go_home).setOnClickListener(this);
        rootView.findViewById(R.id.btn_sleep).setOnClickListener(this);

//        /**
//         * 下拉刷新
//         */
//        ptr_refresh.setPtrHandler(new PtrDefaultHandler() {
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//                PetInfoInstance.getInstance().getPetInfo();
//            }
//        });

        initProgress();
        prog.setProgress(0);
        switch (PetInfoInstance.getInstance().PET_MODE) {
            case Constants.PET_STATUS_COMMON:
                rootView.findViewById(R.id.btn_sport).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.btn_go_home).setVisibility(View.INVISIBLE);
                tv_findpet.setText(getString(R.string.to_find));
                break;
            case Constants.PET_STATUS_WALK:
                rootView.findViewById(R.id.btn_sport).setVisibility(View.INVISIBLE);
                rootView.findViewById(R.id.btn_go_home).setVisibility(View.VISIBLE);
                tv_findpet.setText(getString(R.string.to_find));
                break;
            case Constants.PET_STATUS_FIND:
                tv_findpet.setText("关闭搜寻");
                break;
            default:

                break;
        }
        EventBus.getDefault().register(this);

        initData();

        return rootView;
    }

    void initData() {
        checkIndex = (CheckIndex) getActivity();
        PetInfoInstance.getInstance().getPetInfo();

    }

    public void initProgress() {
        long msEnd = System.currentTimeMillis();
        Date today = new Date(msEnd);
        strEnd = String.format("%s-%s-%s", today.getYear() + 1900, today.getMonth() + 1, today.getDate());
        strStart = strEnd;
        prog.over100=false;
        label1.setVisibility(View.VISIBLE);

    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.notifyPetInfoChange event) {
        initProgress();
        PetInfoInstance.getInstance().getPetStatus();
//        ptr_refresh.refreshComplete();
        sportTarget = PetInfoInstance.getInstance().packBean.target_energy;
        tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
        ApiUtils.getApiService().getActivityInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id(), strStart, strEnd).enqueue(new XMQCallback<PetSportBean>() {
            @Override
            public void onSuccess(Response<PetSportBean> response, PetSportBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {

                    if (message.data.size() > 0) {
                        PetSportBean.SportBean bean = message.data.get(0);
                        DecimalFormat df = new DecimalFormat("0.00");//格式化
                        sportTarget = df.format(bean.target_amount);
                        PetInfoInstance.getInstance().setTarget_step((int) bean.target_amount);
                        PetInfoInstance.getInstance().packBean.reality_amount = bean.reality_amount;
                        PetInfoInstance.getInstance().percentage = bean.percentage;
                        sportDone = bean.reality_amount;
                        percentage = bean.percentage;
                    } else {
//                        ToastUtil.showTost("当天尚无数据~");

                    }
                    if (percentage >=100) {
                        prog.over100=true;
                        label1.setVisibility(View.GONE);
//                        prog.setMax(200);
                    } else if (percentage <100) {
//                        prog.setMax(100);
                        prog.over100=false;
                        label1.setVisibility(View.VISIBLE);
                    }
                    prog.setProgress((int) percentage);
                    tvSportDone.setText("已消耗"+ sportDone+"千卡");
                    tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
                } else {
//                    ToastUtil.showTost("获取当天数据失败");
                }
            }

            @Override
            public void onFail(Call<PetSportBean> call, Throwable t) {
//                ToastUtil.showTost("获取当天数据失败");
            }
        });
        ApiUtils.getApiService().getSleepInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id(), strStart, strEnd).enqueue(new XMQCallback<PetSleepInfoBean>() {
            @Override
            public void onSuccess(Response<PetSleepInfoBean> response, PetSleepInfoBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    if (message.data.size() > 0) {
                        PetSleepInfoBean.SleepBean bean = message.data.get(0);
                        double allSleepTime = bean.deep_sleep + bean.light_sleep;
                        PetInfoInstance.getInstance().deep_sleep = bean.deep_sleep;
                        PetInfoInstance.getInstance().light_sleep = bean.light_sleep;
                        DecimalFormat df = new DecimalFormat("0.00");//格式化
                        String sleepTimeString = df.format(allSleepTime);
                        tv_sleep_time.setText("今日休息" + sleepTimeString + "小时");
                    }else{
                        PetInfoInstance.getInstance().deep_sleep = 0;
                        PetInfoInstance.getInstance().light_sleep = 0;
                        tv_sleep_time.setText("今日休息" + 0 + "小时");
                    }
                } else {
//                    ToastUtil.showTost("获取当天数据失败");
                }
            }

            @Override
            public void onFail(Call<PetSleepInfoBean> call, Throwable t) {

            }
        });
    }

    //今日运动数据更新
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void todaySportData(EventManage.TodaySportData event) {
        sportTarget = event.sportBean.target_amount + "";
        sportDone = event.sportBean.reality_amount;
        percentage = event.sportBean.percentage;
        if (percentage >= 100) {
            prog.over100=true;
            label1.setVisibility(View.GONE);
//            prog.setMax(200);
        } else if (percentage <100) {
            prog.over100=false;
            label1.setVisibility(View.VISIBLE);
//            prog.setMax(100);
        }
        prog.setProgress((int) percentage);
        tvSportDone.setText("已消耗"+sportDone+"千卡");
        tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
    }

    //目标运动量发生变化
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void sportTargetDataChange(EventManage.targetSportData event) {
        double tmp=Double.valueOf(PetInfoInstance.getInstance().packBean.target_energy);
        if(!(tmp>0)){
            return;
        }
        percentage =sportDone*100/tmp;
        if (percentage >= 100) {
            prog.over100=true;
            label1.setVisibility(View.GONE);
//            prog.setMax(200);
        } else if (percentage <100) {
            prog.over100=false;
            label1.setVisibility(View.VISIBLE);
//            prog.setMax(100);
        }
        prog.setProgress((int) percentage);

        sportTarget = PetInfoInstance.getInstance().packBean.target_energy;
        tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
    }

    //更新去运动还是回家的view
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void updateActivityView(EventManage.petModeChanged event) {
        switch (PetInfoInstance.getInstance().PET_MODE) {
            case Constants.PET_STATUS_COMMON:
                getView().findViewById(R.id.btn_sport).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.btn_go_home).setVisibility(View.INVISIBLE);
                tv_findpet.setText(getString(R.string.to_find));
                break;
            case Constants.PET_STATUS_WALK:
                getView().findViewById(R.id.btn_sport).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.btn_go_home).setVisibility(View.VISIBLE);
                tv_findpet.setText(getString(R.string.to_find));
                break;
            case Constants.PET_STATUS_FIND:
                tv_findpet.setText("关闭搜寻");
                break;
            default:

                break;
        }
        Uri uri = Uri.parse(PetInfoInstance.getInstance().packBean.logo_url);
        if (AsynImgDialog.asynImg != null) {
            AsynImgDialog.asynImg.setImageURI(uri);
        }
    }

    @Override
    public void onClick(View v) {
        if (DoubleClickUtil.isFastMiniDoubleClick()) {
            return;
        }
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_sport_index:
                intent.setClass(getActivity(), HealthIndexActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_locate:
                if(!DeviceInfoInstance.getInstance().online){
                    ToastUtil.showTost("追踪器已离线，此功能暂时无法使用");
                    return;
                }
                if (PetInfoInstance.getInstance().PET_MODE == Constants.PET_STATUS_FIND) {
                    DialogToast.createDialogWithTwoButton(getContext(), "确认关闭紧急搜寻？", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_findpet.setText(getString(R.string.to_find));
                            ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.GPS_CLOSE).enqueue(new XMQCallback<PetStatusBean>() {
                                @Override
                                public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                    HttpCode ret = HttpCode.valueOf(message.status);
                                    switch (ret) {
                                        case EC_SUCCESS:
                                            if(DeviceInfoInstance.getInstance().online!=true) {
                                                DeviceInfoInstance.getInstance().online = true;
                                                EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                            }



                                            //关闭地图位置刷新
                                            MapInstance.getInstance().stopLocListener();
                                            PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_COMMON);
                                            EventBus.getDefault().post(new EventManage.petModeChanged());
                                            break;
                                        case EC_OFFLINE:
                                            DeviceInfoInstance.getInstance().online=false;
                                            EventBus.getDefault().post(new EventManage.DeviceOffline());
                                            break;
                                    }


                                }

                                @Override
                                public void onFail(Call<PetStatusBean> call, Throwable t) {

                                }
                            });

                        }
                    });
                } else {
                    MapInstance.getInstance().openTime = 1;
                    String conent = getContext().getResources().getString(R.string.open_find_tip);
                    DialogUtil.openGPSDialog(getContext(),  new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_findpet.setText("关闭搜寻");
                            MapInstance.getInstance().startFindPet();
                            ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.GPS_OPEN).enqueue(new XMQCallback<PetStatusBean>() {
                                @Override
                                public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                    HttpCode ret = HttpCode.valueOf(message.status);
                                    switch (ret) {
                                        case EC_SUCCESS:
                                            if(DeviceInfoInstance.getInstance().online!=true) {
                                                DeviceInfoInstance.getInstance().online = true;
                                                EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                            }
//                                    MapInstance.getInstance().setGPSState(true);
//                                    EventBus.getDefault().post(new EventManage.GPS_CHANGE());
                                            //五分钟之内不去判断
                                            ThreadUtil.open_gps_donot_check_Thread();
//                                            //开启地图位置刷新
//                                            MapInstance.getInstance().startLoc();
                                            PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_FIND);
                                            EventBus.getDefault().post(new EventManage.petModeChanged());
                                            checkIndex.changeLocatefragment();
                                            PetInfoInstance.getInstance().getPetLocation();

                                            break;
                                        case EC_OFFLINE:
                                            DeviceInfoInstance.getInstance().online=false;
                                            EventBus.getDefault().post(new EventManage.DeviceOffline());
                                            break;
                                    }
                                }

                                @Override
                                public void onFail(Call<PetStatusBean> call, Throwable t) {

                                }
                            });
                        }
                    });
                }

                break;
            case R.id.btn_health:
                intent.setClass(getActivity(), SportIndexActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sport:
                if (PetInfoInstance.getInstance().PET_MODE == Constants.PET_STATUS_FIND) {
                    ToastUtil.showTost("紧急搜寻模式下，无法使用该功能");
                    return;
                }
                if(!DeviceInfoInstance.getInstance().online){
                    ToastUtil.showTost("追踪器已离线，此功能暂时无法使用");
                    return;
                }

                AsynImgDialog.createGoSportDialig(getContext(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AsynImgDialog.startSalary = sportDone;
                        AsynImgDialog.startTime = (new Date()).getTime();
                        SPUtil.putSportStartTime(AsynImgDialog.startTime);
                        ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.TO_SPORT_ACTIVITY_TYPE).enqueue(new XMQCallback<BaseBean>() {
                            @Override
                            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                                PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_WALK);
                                EventManage.petModeChanged event = new EventManage.petModeChanged();
                                event.pet_mode = Constants.PET_STATUS_WALK;
                                EventBus.getDefault().post(event);
                            }

                            @Override
                            public void onFail(Call<BaseBean> call, Throwable t) {

                            }
                        });

                        MainActivity.getLocationWithOneMinute = true;
//                        MainActivity.getLocationTime = 0;
                        if (MainActivity.locationThread == null) {
                            MainActivity.locationThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (true) {
                                        try {
                                            Thread.sleep(60000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (MainActivity.getLocationWithOneMinute&&DeviceInfoInstance.getInstance().online) {
                                            PetInfoInstance.getInstance().getPetLocation();
                                        }
                                    }
                                }
                            });
                            MainActivity.locationThread.start();
                        }
                        checkIndex.changeLocatefragment();
                    }
                });
                break;
            case R.id.btn_go_home:
//                if (MapInstance.getInstance().GPS_OPEN) {
                if (PetInfoInstance.getInstance().PET_MODE == Constants.PET_STATUS_FIND) {
                    ToastUtil.showTost("紧急搜寻模式下，无法使用该功能");
                    return;
                }
                AsynImgDialog.createGoHomeDialog(getContext(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.TO_HOME_ACTIVITY_TYPE).enqueue(new XMQCallback<BaseBean>() {
                            @Override
                            public void onSuccess(Response<BaseBean> response, BaseBean message) {

                                initProgress();
//                                ptr_refresh.refreshComplete();
                                sportTarget = PetInfoInstance.getInstance().packBean.target_energy;
                                tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
                                ApiUtils.getApiService().getActivityInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                                        PetInfoInstance.getInstance().getPet_id(), strStart, strEnd).enqueue(new XMQCallback<PetSportBean>() {
                                    @Override
                                    public void onSuccess(Response<PetSportBean> response, PetSportBean message) {
                                        HttpCode ret = HttpCode.valueOf(message.status);
                                        if (ret == HttpCode.EC_SUCCESS) {

                                            if (message.data.size() > 0) {
                                                PetSportBean.SportBean bean = message.data.get(0);
                                                DecimalFormat df = new DecimalFormat("0.00");//格式化
                                                sportTarget = df.format(bean.target_amount);
                                                PetInfoInstance.getInstance().setTarget_step((int) bean.target_amount);
                                                PetInfoInstance.getInstance().packBean.reality_amount = bean.reality_amount;
                                                PetInfoInstance.getInstance().percentage = bean.percentage;
                                                sportDone = bean.reality_amount;
                                                percentage = bean.percentage;
                                                AsynImgDialog.stopSalary = sportDone;
                                                AsynImgDialog.stopTime = (new Date()).getTime();
                                                if ((AsynImgDialog.stopSalary - AsynImgDialog.startSalary) > 0) {
                                                    AsynImgDialog.startTime=SPUtil.getSportStartTime();
                                                    long sporttime = (AsynImgDialog.stopTime - AsynImgDialog.startTime) / 60000;
                                                    double salary=(AsynImgDialog.stopSalary - AsynImgDialog.startSalary);
                                                    String salaryText2= df.format(salary);
                                                    String salaryText = PetInfoInstance.getInstance().getNick() + "刚才运动了" + sporttime + "分钟，消耗了" + salaryText2+ "卡路里";
                                                    DialogUtil.showOneButtonDialog(getActivity(), salaryText, new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    });
                                                }

                                            } else {
//                        ToastUtil.showTost("当天尚无数据~");

                                            }
                                            if (percentage >=100) {
                                                prog.over100=true;
                                                label1.setVisibility(View.GONE);
//                                                prog.setMax(200);
                                            } else if (percentage < 100) {
                                                prog.over100=false;
                                                label1.setVisibility(View.VISIBLE);
//                                                prog.setMax(100);
                                            }
                                            prog.setProgress((int) percentage);
                                            tvSportDone.setText("已消耗"+sportDone+"千卡");
                                            tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
                                        } else {
//                                            ToastUtil.showTost("获取当天数据失败");
                                        }
                                    }

                                    @Override
                                    public void onFail(Call<PetSportBean> call, Throwable t) {
//                                        ToastUtil.showTost("获取当天数据失败");
                                    }
                                });

                                PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_COMMON);
                                EventManage.petModeChanged event = new EventManage.petModeChanged();
                                event.pet_mode = Constants.PET_STATUS_COMMON;
                                EventBus.getDefault().post(event);
                            }

                            @Override
                            public void onFail(Call<BaseBean> call, Throwable t) {

                            }
                        });


                        MainActivity.getLocationWithOneMinute = true;
                        if (MainActivity.locationThread == null) {
                            MainActivity.locationThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (true) {
                                        try {
                                            Thread.sleep(60000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (MainActivity.getLocationWithOneMinute&&DeviceInfoInstance.getInstance().online) {
                                            PetInfoInstance.getInstance().getPetLocation();
                                        }
                                    }
                                }
                            });
                            MainActivity.locationThread.start();
                        }
                    }
                });
                break;
            case R.id.btn_sleep:
                intent.setClass(getActivity(), SleepIndexActivity.class);
                startActivity(intent);
                break;
        }
    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
