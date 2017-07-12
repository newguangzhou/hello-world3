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
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.bean.PetSleepInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.PetSportBean;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.main.MapInstance;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
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
import java.util.StringTokenizer;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2015/6/12.
 */

@SuppressLint("WrongConstant")
public class PetFragment extends BaseFragment implements View.OnClickListener {


    MaterialDesignPtrFrameLayout ptr_refresh;

    //    private HealthGoSportView mGoSportView;
    CircleProgressBar prog;
    TextView tvSportDone;
    TextView tvSportTarget;
    TextView tv_sleep_time;
    TextView tv_findpet;

    String strStart;
    String strEnd;

    String sportTarget = PetInfoInstance.getInstance().packBean.target_energy;
    int sportDone = 0;
    double percentage = 0;

    CheckIndex checkIndex;


    boolean isopen;//gps是否打开

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.main_tab_health, container, false);
        ptr_refresh = (MaterialDesignPtrFrameLayout) rootView.findViewById(R.id.ptr_refresh);
        prog = (CircleProgressBar) rootView.findViewById(R.id.prog_target_done_percentage);
        tvSportDone = (TextView) rootView.findViewById(R.id.tv_sport_done);
        tvSportTarget = (TextView) rootView.findViewById(R.id.tv_sport_target);
        tv_sleep_time = (TextView) rootView.findViewById(R.id.tv_sleep_time);
        tv_findpet = (TextView) rootView.findViewById(R.id.tv_findpet);

        rootView.setOnClickListener(this);
        rootView.findViewById(R.id.btn_sport_index).setOnClickListener(this);
        rootView.findViewById(R.id.btn_locate).setOnClickListener(this);
        rootView.findViewById(R.id.btn_health).setOnClickListener(this);
        rootView.findViewById(R.id.btn_sport).setOnClickListener(this);
        rootView.findViewById(R.id.btn_go_home).setOnClickListener(this);
        rootView.findViewById(R.id.btn_sleep).setOnClickListener(this);

        /**
         * 下拉刷新
         */
        ptr_refresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                PetInfoInstance.getInstance().getPetInfo();
            }
        });

        initProgress();

        isopen = MapInstance.getInstance().GPS_OPEN;
        if (isopen) {
            tv_findpet.setText("关闭搜寻");
        } else {
            tv_findpet.setText("紧急搜寻");
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
        prog.setProgress(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.notifyPetInfoChange event) {
        initProgress();
        PetInfoInstance.getInstance().getPetStatus();
        ptr_refresh.refreshComplete();
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
                    if (percentage > 100) {
                        prog.setMax(200);
                    } else if (percentage <= 100) {
                        prog.setMax(100);
                    }
                    prog.setProgress((int) percentage);
                    tvSportDone.setText(String.format("已消耗%d千卡", sportDone));
                    tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
                } else {
                    ToastUtil.showTost("获取当天数据失败");
                }
            }

            @Override
            public void onFail(Call<PetSportBean> call, Throwable t) {
                ToastUtil.showTost("获取当天数据失败");
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
                    }
                } else {
                    ToastUtil.showTost("获取当天数据失败");
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
        if (percentage > 100) {
            prog.setMax(200);
        } else if (percentage <= 100) {
            prog.setMax(100);
        }
        prog.setProgress((int) percentage);
        tvSportDone.setText(String.format("已消耗%d千卡", sportDone));
        tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
    }

    //目标运动量发生变化
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void sportTargetDataChange(EventManage.targetSportData event) {
        sportTarget = PetInfoInstance.getInstance().packBean.target_energy;
        tvSportTarget.setText(String.format("目标消耗" + sportTarget + "千卡"));
    }

    //更新去运动还是回家的view
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void updateActivityView(EventManage.atHomeOrtoSport event) {
        if (PetInfoInstance.getInstance().getAtHome()) {//回家
            getView().findViewById(R.id.btn_sport).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.btn_go_home).setVisibility(View.INVISIBLE);
        } else {//出去玩
            getView().findViewById(R.id.btn_sport).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.btn_go_home).setVisibility(View.VISIBLE);
        }
        Uri uri = Uri.parse(PetInfoInstance.getInstance().packBean.logo_url);
        if (AsynImgDialog.asynImg != null) {
            AsynImgDialog.asynImg.setImageURI(uri);
        }
    }

    //gps状态变化
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void updateGPSState(EventManage.GPS_CHANGE event) {
        isopen = MapInstance.getInstance().GPS_OPEN;
        if (isopen) {
            tv_findpet.setText("关闭搜寻");
        } else {
            tv_findpet.setText("紧急搜寻");
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
                isopen = MapInstance.getInstance().GPS_OPEN;
                if (isopen) {
//                    String conent = "是否关闭紧急追踪模式";
//                    DialogUtil.showTwoButtonDialog(getContext(),conent,"取消","确定",new View.OnClickListener(){
//
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    },new View.OnClickListener(){
//
//                        @Override
//                        public void onClick(View v) {
//                            tv_findpet.setText("紧急搜寻");
//                            ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 2).enqueue(new XMQCallback<PetStatusBean>() {
//                                @Override
//                                public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
//                                    MapInstance.getInstance().setGPSState(false);
//                                    EventBus.getDefault().post(new EventManage.GPS_CHANGE());
//
//                                    switch (message.pet_status) {
//                                        case Constants.PET_STATUS_COMMON:
//                                            if(!PetInfoInstance.getInstance().getAtHome()) {
//                                                PetInfoInstance.getInstance().setAtHome(true);
//                                            }
//                                            break;
//                                        case Constants.PET_STATUS_WALK:
//                                            if(PetInfoInstance.getInstance().getAtHome()) {
//                                                PetInfoInstance.getInstance().setAtHome(false);
//                                            }
//                                            break;
//                                        default:
//                                            if(!PetInfoInstance.getInstance().getAtHome()) {
//                                                PetInfoInstance.getInstance().setAtHome(true);
//                                            }
//                                            break;
//                                    }
//
//                                }
//
//                                @Override
//                                public void onFail(Call<PetStatusBean> call, Throwable t) {
//
//                                }
//                            });
//                        }
//                    });

//                    new DialogToast(getContext(), "是否关闭紧急追踪模式。", "确定", new View.OnClickListener() {
                    DialogToast.createDialogWithTwoButton(getContext(), "是否关闭紧急追踪模式。", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_findpet.setText("紧急搜寻");
                            ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 2).enqueue(new XMQCallback<PetStatusBean>() {
                                @Override
                                public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                    MapInstance.getInstance().setGPSState(false);
                                    EventBus.getDefault().post(new EventManage.GPS_CHANGE());

                                    switch (message.pet_status) {
                                        case Constants.PET_STATUS_COMMON:
                                            if (!PetInfoInstance.getInstance().getAtHome()) {
                                                PetInfoInstance.getInstance().setAtHome(true);
                                            }
                                            break;
                                        case Constants.PET_STATUS_WALK:
                                            if (PetInfoInstance.getInstance().getAtHome()) {
                                                PetInfoInstance.getInstance().setAtHome(false);
                                            }
                                            break;
                                        default:
                                            if (!PetInfoInstance.getInstance().getAtHome()) {
                                                PetInfoInstance.getInstance().setAtHome(true);
                                            }
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

//                    String conent = getContext().getResources().getString(R.string.map_is_findpet);
//                    DialogUtil.showTwoButtonDialog(getContext(),conent,"取消","确定",new View.OnClickListener(){
//
//                                @Override
//                                public void onClick(View v) {
//
//                                }
//                            },
//                            new View.OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//                                    tv_findpet.setText("关闭搜寻");
//                                    MapInstance.getInstance().startFindPet();
//                                    ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 1).enqueue(new XMQCallback<PetStatusBean>() {
//                                        @Override
//                                        public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
//                                            MapInstance.getInstance().setGPSState(true);
//                                            EventBus.getDefault().post(new EventManage.GPS_CHANGE());
//                                            checkIndex.changeLocatefragment();
//                                        }
//
//                                        @Override
//                                        public void onFail(Call<PetStatusBean> call, Throwable t) {
//
//                                        }
//                                    });
//                                    PetInfoInstance.getInstance().getPetLocation();
//                                }
//                            });


                    String conent = getContext().getResources().getString(R.string.map_is_findpet);
                    DialogToast.createDialogWithTwoButton(getContext(), conent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_findpet.setText("关闭搜寻");
                            MapInstance.getInstance().startFindPet();
                            ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 1).enqueue(new XMQCallback<PetStatusBean>() {
                                @Override
                                public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                    MapInstance.getInstance().setGPSState(true);
                                    EventBus.getDefault().post(new EventManage.GPS_CHANGE());
                                    checkIndex.changeLocatefragment();
                                }

                                @Override
                                public void onFail(Call<PetStatusBean> call, Throwable t) {

                                }
                            });
                            PetInfoInstance.getInstance().getPetLocation();
                        }
                    });
                }

                break;
            case R.id.btn_health:
                intent.setClass(getActivity(), SportIndexActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sport:
                if (MapInstance.getInstance().GPS_OPEN) {
                    ToastUtil.showTost("紧急搜索模式下不能使用该功能");
                    return;
                }
                AsynImgDialog.createGoSportDialig(getContext(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),Constants.TO_SPORT_ACTIVITY_TYPE).enqueue(new XMQCallback<BaseBean>() {
                            @Override
                            public void onSuccess(Response<BaseBean> response, BaseBean message) {

                            }

                            @Override
                            public void onFail(Call<BaseBean> call, Throwable t) {

                            }
                        });

                        MainActivity.getLocationWithOneMinute = true;
                        MainActivity.getLocationTime = 0;
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
                                        if (MainActivity.getLocationWithOneMinute && (MainActivity.getLocationTime++ <= 10)) {
                                            PetInfoInstance.getInstance().getPetLocation();
                                        }
                                    }
                                }
                            });
                            MainActivity.locationThread.start();
                        }

                        PetInfoInstance.getInstance().setAtHome(false);
                        checkIndex.changeLocatefragment();
                    }
                });
                break;
            case R.id.btn_go_home:
                if (MapInstance.getInstance().GPS_OPEN) {
                    ToastUtil.showTost("紧急搜索模式下不能使用该功能");
                    return;
                }
                AsynImgDialog.createGoHomeDialog(getContext(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),Constants.TO_HOME_ACTIVITY_TYPE).enqueue(new XMQCallback<BaseBean>() {
                            @Override
                            public void onSuccess(Response<BaseBean> response, BaseBean message) {

                            }

                            @Override
                            public void onFail(Call<BaseBean> call, Throwable t) {

                            }
                        });
                        MainActivity.getLocationWithOneMinute = true;
                        MainActivity.getLocationTime = 0;
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
                                        if (MainActivity.getLocationWithOneMinute && (MainActivity.getLocationTime++ <= 10)) {
                                            PetInfoInstance.getInstance().getPetLocation();
                                        }
                                    }
                                }
                            });
                            MainActivity.locationThread.start();
                        }

                        PetInfoInstance.getInstance().setAtHome(true);
                        checkIndex.changeLocatefragment();
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
