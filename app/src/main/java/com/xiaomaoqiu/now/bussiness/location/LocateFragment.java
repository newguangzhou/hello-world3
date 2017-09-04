package com.xiaomaoqiu.now.bussiness.location;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseBean;
import com.xiaomaoqiu.now.base.BaseFragment;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.ThreadUtil;
import com.xiaomaoqiu.now.bussiness.bean.DeviceInfoBean;
import com.xiaomaoqiu.now.bussiness.bean.PetSportBean;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.pet.PetFragment;
import com.xiaomaoqiu.now.bussiness.pet.PetInfo;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
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
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.AsynImgDialog;
import com.xiaomaoqiu.now.map.main.addressParseListener;
import com.xiaomaoqiu.now.view.MapPetAvaterView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

import static com.xiaomaoqiu.now.bussiness.MainActivity.getLocationWithOneMinute;


/**
 * Created by long on 2017/1/14.
 */
@SuppressLint("WrongConstant")
public class LocateFragment extends BaseFragment implements View.OnClickListener {


    MapView mapView;
    private ImageView mFindPetView, mWalkPetView;
    private MapPetAvaterView mapPetAvaterView;



    private TextView petLocation, walkpetNoticeView;
    private View tv_offline;//设备已离线
    private TextView tv_location_state;//当前位置信号不佳
    private TextView tv_at_home;//%%（宠物名）乖乖在家哦！
    private LinearLayout petLocContainer;

    View btn_phone_center;//手机图标

    private String locationString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_tab_locate, container, false);
        initView(rootView);
        initListener();
        initData();
        return rootView;
    }

    private void initView(View root) {
        mapView = (MapView) root.findViewById(R.id.bmapView);
        mFindPetView = (ImageView) root.findViewById(R.id.btn_find_pet);
        mFindPetView.setOnClickListener(this);
        btn_phone_center=root.findViewById(R.id.btn_phone_center);
        btn_phone_center.setOnClickListener(this);
        root.findViewById(R.id.btn_pet_center).setOnClickListener(this);
        mWalkPetView = (ImageView) root.findViewById(R.id.btn_playing_pet);

        mWalkPetView.setOnClickListener(this);

        mapPetAvaterView = new MapPetAvaterView(root.getContext());
        tv_offline=root.findViewById(R.id.tv_offline);
        petLocation = (TextView) root.findViewById(R.id.tv_location);
        petLocation.setText("");
        petLocContainer = (LinearLayout) root.findViewById(R.id.locate_addr_conotainer);
        walkpetNoticeView = (TextView) root.findViewById(R.id.locate_walk_notice);
        tv_location_state= (TextView) root.findViewById(R.id.tv_location_state);
        tv_at_home= (TextView) root.findViewById(R.id.tv_at_home);
        tv_at_home.setText(PetInfoInstance.getInstance().getNick()+"乖乖在家哦~");
    }

    void initListener() {
        MapInstance.getInstance().setAddressParseListener(new addressParseListener() {
            @Override
            public void onAddressparsed(String address) {
//                String textString = address;
                String textString = "位置获取时间：";
                if (PetInfoInstance.getInstance().location_time != 0) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String d = format.format(PetInfoInstance.getInstance().location_time * 1000);//unix时间戳转化为java的毫秒，然后转成时间
                    textString += d;
                }
                petLocation.setText(textString);
//                if(PetInfoInstance.getInstance().getAtHome()){
//                    textString+="\n"+PetInfoInstance.getInstance().getNick()+"乖乖在家哦~";
//                }
//                locationString=textString;
//                if(DeviceInfoInstance.getInstance().online) {
//                    petLocation.setText(textString);
//                }else{
//                    petLocation.setText(locationString+"(注意：追踪器目前已离线）");
//                }
            }
        });
    }

    private void initData() {
        EventBus.getDefault().register(this);
        MapInstance.getInstance().setPetAvaterView(mapPetAvaterView);
        MapInstance.getInstance().init(mapView);
        switch (PetInfoInstance.getInstance().PET_MODE){
            case Constants.PET_STATUS_COMMON:
                mWalkPetView.setVisibility(View.VISIBLE);
                btn_phone_center.setVisibility(View.GONE);
                mFindPetView.setSelected(false);
                mWalkPetView.setSelected(false);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
                updateDownSecondText();

//                tv_location_state.setVisibility(View.GONE);
                break;
            case Constants.PET_STATUS_WALK:
                mWalkPetView.setVisibility(View.VISIBLE);
                btn_phone_center.setVisibility(View.GONE);
                mFindPetView.setSelected(false);
                mWalkPetView.setSelected(true);
                walkpetNoticeView.setVisibility(View.VISIBLE);
                petLocContainer.setVisibility(View.GONE);
//                tv_location_state.setVisibility(View.GONE);
                break;
            case Constants.PET_STATUS_FIND:
                mWalkPetView.setVisibility(View.GONE);
                btn_phone_center.setVisibility(View.VISIBLE);
                mFindPetView.setSelected(true);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
                updateDownSecondText();
//                if(PetInfoInstance.getInstance().device_locator_status==2){
//                    tv_location_state.setVisibility(View.VISIBLE);
//                }else{
//                    tv_location_state.setVisibility(View.GONE);
//                }

                break;
            default:
                mWalkPetView.setVisibility(View.VISIBLE);
                mFindPetView.setSelected(false);
                mWalkPetView.setSelected(false);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
                updateDownSecondText();
//                tv_location_state.setVisibility(View.GONE);
                break;
        }

        PetInfoInstance.getInstance().getPetLocation();
    }

    //更新去运动还是回家的view
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void updateActivityView(EventManage.petModeChanged event) {
        switch (PetInfoInstance.getInstance().PET_MODE){
            case Constants.PET_STATUS_COMMON:
                mWalkPetView.setVisibility(View.VISIBLE);
                btn_phone_center.setVisibility(View.GONE);
                mFindPetView.setSelected(false);
                mWalkPetView.setSelected(false);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
                updateDownSecondText();
                break;
            case Constants.PET_STATUS_WALK:
                mWalkPetView.setVisibility(View.VISIBLE);
                btn_phone_center.setVisibility(View.GONE);
                mFindPetView.setSelected(false);
                mWalkPetView.setSelected(true);
                walkpetNoticeView.setVisibility(View.VISIBLE);
                petLocContainer.setVisibility(View.GONE);
                break;
            case Constants.PET_STATUS_FIND:
                mWalkPetView.setVisibility(View.GONE);
                mFindPetView.setSelected(true);
                btn_phone_center.setVisibility(View.VISIBLE);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
                updateDownSecondText();
//                if(PetInfoInstance.getInstance().device_locator_status==2){
//                    tv_location_state.setVisibility(View.VISIBLE);
//                }else{
//                    tv_location_state.setVisibility(View.GONE);
//                }
                break;
            default:
                mWalkPetView.setVisibility(View.VISIBLE);
                mFindPetView.setSelected(false);
                mWalkPetView.setSelected(false);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
                updateDownSecondText();
                break;
        }
        MapInstance.getInstance().refreshMap();
    }

    @Override
    public void onClick(View v) {
        if (DoubleClickUtil.isFastMiniDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_find_pet:
                //狗丢了
                showFindpetDialog();
                break;
            case R.id.btn_phone_center:
                //手机位置
                MapInstance.showPhoneCenter=true;
                MapInstance.getInstance().startLoc();
                break;
            case R.id.btn_pet_center:
                MapInstance.showPhoneCenter=false;
                if(PetInfoInstance.getInstance().PET_MODE==Constants.PET_STATUS_FIND) {
                    MapInstance.getInstance().startLocListener(1000);
                }
                if(PetInfoInstance.getInstance().getAtHome()){
                    MapInstance.getInstance().setPetLocation(UserInstance.getInstance().latitude,UserInstance.getInstance().longitude,0) ;
                }else {
                    MapInstance.getInstance().setPetLocation(PetInfoInstance.getInstance().latitude, PetInfoInstance.getInstance().longitude, PetInfoInstance.getInstance().radius);
                }
                break;
            case R.id.btn_playing_pet:
                if(!DeviceInfoInstance.getInstance().online){
                    ToastUtil.showTost("追踪器已离线，此功能暂时无法使用");
                    return;
                }
                //去运动
                showWalkPetDialog(!mWalkPetView.isSelected());
                break;
        }
    }


    //是回家还是在运动
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onPetInfoChanged(EventManage.atHomeOrNotHome event) {
        if (!PetInfoInstance.getInstance().getAtHome()) {
            PetInfoInstance.getInstance().getPetLocation();
        } else {
            MapInstance.getInstance().setPetLocation(UserInstance.getInstance().latitude,UserInstance.getInstance().longitude,0) ;
        }
        MapInstance.getInstance().petAtHomeView.setAvaterUrl(PetInfoInstance.getInstance().packBean.logo_url);
        MapInstance.getInstance().refreshMap();

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.uploadImageSuccess event) {
        MapInstance.getInstance().petAtHomeView.setAvaterUrl(PetInfoInstance.getInstance().packBean.logo_url);
        MapInstance.getInstance().petCommonNotAtHomeView.setAvaterUrl(PetInfoInstance.getInstance().packBean.logo_url);
//        MapInstance.getInstance().petAtHomeView.setAvaterUrl("file://com.xiaomaoqiu.pet"+event.path);
        Log.e("longtianlove",event.path);
        MapInstance.getInstance().refreshMap();
        MapInstance.getInstance().refreshMap();
        MapInstance.getInstance().refreshMap();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onLocateResult(EventManage.notifyPetLocationChange event) {
        if(PetInfoInstance.getInstance().PET_MODE==Constants.PET_STATUS_FIND) {
            MapInstance.getInstance().startLocListener(1000);
        }
        if(PetInfoInstance.getInstance().getAtHome()){
            MapInstance.getInstance().setPetLocation(UserInstance.getInstance().latitude,UserInstance.getInstance().longitude,0) ;
        }else {
            MapInstance.getInstance().setPetLocation(PetInfoInstance.getInstance().latitude, PetInfoInstance.getInstance().longitude, PetInfoInstance.getInstance().radius);
        }

//        if(PetInfoInstance.getInstance().PET_MODE==Constants.PET_STATUS_FIND) {
//            if (PetInfoInstance.getInstance().device_locator_status == 2) {
//                tv_location_state.setVisibility(View.VISIBLE);
//            } else {
//                tv_location_state.setVisibility(View.GONE);
//            }
//        }else {
//            tv_location_state.setVisibility(View.GONE);
//        }
        updateDownSecondText();

    }

    //todo 小米推送
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onLocateResult(PushEventManage.locationChange event) {


        if(!getLocationWithOneMinute){
            return;
        }
        if(PetInfoInstance.getInstance().PET_MODE==Constants.PET_STATUS_FIND){
            //手机位置
            MapInstance.getInstance().startLoc();
        }
        if(PetInfoInstance.getInstance().getAtHome()){
            MapInstance.getInstance().setPetLocation(UserInstance.getInstance().latitude,UserInstance.getInstance().longitude,0) ;
        }else {
            MapInstance.getInstance().setPetLocation(PetInfoInstance.getInstance().latitude, PetInfoInstance.getInstance().longitude, PetInfoInstance.getInstance().radius);
        }
    }

    //todo 小米推送
    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void receivePushDeviceOffline(PushEventManage.deviceOffline event) {
        updateDownSecondText();
////        petLocation.setText(locationString+"(注意：追踪器目前已离线）");
//        if(DeviceInfoInstance.getInstance().online){
//            tv_offline.setVisibility(View.GONE);//设备已离线
//            if(PetInfoInstance.getInstance().device_locator_status==2&&PetInfoInstance.getInstance().PET_MODE==Constants.PET_STATUS_FIND){
//                tv_location_state.setVisibility(View.VISIBLE);
//                tv_at_home.setVisibility(View.GONE);
//            }else{
//                tv_location_state.setVisibility(View.GONE);
//                tv_at_home.setVisibility(View.VISIBLE);
//            }
//        }else{
//            tv_offline.setVisibility(View.VISIBLE);//设备已离线
//            tv_location_state.setVisibility(View.GONE);//当前位置信号不佳
//            tv_at_home.setVisibility(View.GONE);//%%（宠物名）乖乖在家哦！
//        }

    }

    //todo 小米推送
    //设备重新在线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void receivePushDeviceOnline(PushEventManage.deviceOnline event) {
//        petLocation.setText(locationString);
        updateDownSecondText();
    }

    //刷新下面地址栏第二个text
    void updateDownSecondText(){
        if(DeviceInfoInstance.getInstance().online){
            tv_offline.setVisibility(View.GONE);//设备已离线
            if(PetInfoInstance.getInstance().device_locator_status==2&&PetInfoInstance.getInstance().PET_MODE==Constants.PET_STATUS_FIND){
                tv_location_state.setVisibility(View.VISIBLE);
                tv_at_home.setVisibility(View.GONE);
            }else{
                tv_location_state.setVisibility(View.GONE);
                if(PetInfoInstance.getInstance().getAtHome()) {
                    tv_at_home.setVisibility(View.VISIBLE);
                }else {
                    tv_at_home.setVisibility(View.GONE);
                }
            }
        }else{
            tv_offline.setVisibility(View.VISIBLE);//设备已离线
            tv_location_state.setVisibility(View.GONE);//当前位置信号不佳
            tv_at_home.setVisibility(View.GONE);//%%（宠物名）乖乖在家哦！
        }
    }

    /**
     * 狗丢了对话框
     */
    private void showFindpetDialog() {
        if(!DeviceInfoInstance.getInstance().online){
            ToastUtil.showTost("追踪器已离线，此功能暂时无法使用");
            return;
        }
        if (PetInfoInstance.getInstance().PET_MODE==Constants.PET_STATUS_FIND) {
            DialogToast.createDialogWithTwoButton(getContext(), "确认关闭紧急搜寻？", new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), UserInstance.getInstance().pet_id, Constants.GPS_CLOSE).enqueue(new XMQCallback<PetStatusBean>() {
                        @Override
                        public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                            HttpCode ret = HttpCode.valueOf(message.status);
                            switch (ret) {
                                case EC_SUCCESS:
                                    if(DeviceInfoInstance.getInstance().online!=true) {
                                        DeviceInfoInstance.getInstance().online = true;
                                        EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                    }
                                    mFindPetView.setSelected(false);

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

            MapInstance.getInstance().openTime=1;
            String content = getContext().getResources().getString(R.string.open_find_tip);


            DialogUtil.openGPSDialog(getContext(),  new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MapInstance.getInstance().startFindPet();
                    ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), UserInstance.getInstance().pet_id, Constants.GPS_OPEN).enqueue(new XMQCallback<PetStatusBean>() {
                        @Override
                        public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                            HttpCode ret = HttpCode.valueOf(message.status);
                            switch (ret) {
                                case EC_SUCCESS:
                                    if(DeviceInfoInstance.getInstance().online!=true) {
                                        DeviceInfoInstance.getInstance().online = true;
                                        EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                    }
                                    mFindPetView.setSelected(true);
                                    //五分钟之内不去判断
                                    ThreadUtil.open_gps_donot_check_Thread(300000);
//                                    ThreadUtil.open_gps_donot_check_Thread(1000);
//                                    //开启地图位置刷新
//                                    MapInstance.getInstance().startLoc();
                                    PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_FIND);
                                    EventBus.getDefault().post(new EventManage.petModeChanged());
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
    }


    /**
     * 遛狗按钮点击
     *
     * @param isStart
     */
    private void showWalkPetDialog(boolean isStart) {
        if (isStart) {
            AsynImgDialog.createGoSportDialig(getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsynImgDialog.startSalary = PetFragment.sportDone;
                    AsynImgDialog.startTime = (new Date()).getTime();
                    SPUtil.putSportStartTime(AsynImgDialog.startTime);
                    ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),UserInstance.getInstance().pet_id,Constants.TO_SPORT_ACTIVITY_TYPE).enqueue(new XMQCallback<BaseBean>() {
                        @Override
                        public void onSuccess(Response<BaseBean> response, BaseBean message) {
                            PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_WALK);
                            EventManage.petModeChanged event=new EventManage.petModeChanged();
                            event.pet_mode=Constants.PET_STATUS_WALK;
                            EventBus.getDefault().post(event);
                        }

                        @Override
                        public void onFail(Call<BaseBean> call, Throwable t) {

                        }
                    });
                    mWalkPetView.setSelected(true);
                }
            });
        } else {
            AsynImgDialog.createGoHomeDialog(getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),UserInstance.getInstance().pet_id,Constants.TO_HOME_ACTIVITY_TYPE).enqueue(new XMQCallback<BaseBean>() {
                        @Override
                        public void onSuccess(Response<BaseBean> response, BaseBean message) {
                            long msEnd = System.currentTimeMillis();
                            Date today = new Date(msEnd);
                            String strStart;
                            String strEnd;
                            strEnd = String.format("%s-%s-%s", today.getYear() + 1900, today.getMonth() + 1, today.getDate());
                            strStart = strEnd;
                            ApiUtils.getApiService().getActivityInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                                    UserInstance.getInstance().pet_id, strStart, strEnd).enqueue(new XMQCallback<PetSportBean>() {
                                @Override
                                public void onSuccess(Response<PetSportBean> response, PetSportBean message) {
                                    HttpCode ret = HttpCode.valueOf(message.status);
                                    if (ret == HttpCode.EC_SUCCESS) {

                                        if (message.data.size() > 0) {
                                            PetSportBean.SportBean bean = message.data.get(0);
                                            PetInfoInstance.getInstance().setTarget_step((int) bean.target_amount);
                                            PetInfoInstance.getInstance().packBean.reality_amount = bean.reality_amount;
                                            PetInfoInstance.getInstance().percentage = bean.percentage;
                                            PetFragment.sportDone = bean.reality_amount;
                                            AsynImgDialog.stopSalary = PetFragment.sportDone;
                                            AsynImgDialog.stopTime = (new Date()).getTime();
                                            AsynImgDialog.startTime=SPUtil.getSportStartTime();
                                            long sporttime = (AsynImgDialog.stopTime - AsynImgDialog.startTime) / 60000;
                                            if ((AsynImgDialog.stopSalary - AsynImgDialog.startSalary) > 0&&sporttime>0) {

                                                double salary=(AsynImgDialog.stopSalary - AsynImgDialog.startSalary);
                                                DecimalFormat df = new DecimalFormat("0.00");//格式化
                                                String salaryText2= df.format(salary);
                                                String salaryText = PetInfoInstance.getInstance().getNick() + "刚才运动了" + sporttime + "分钟，消耗了" + salaryText2 + "卡路里";
                                                DialogUtil.showOneButtonDialog(getActivity(), salaryText, new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                });
                                            }

                                        } else {
//                        ToastUtil.showTost("当天尚无数据~");

                                        }
                                    } else {
//                                        ToastUtil.showTost("获取当天数据失败");
                                    }
                                }

                                @Override
                                public void onFail(Call<PetSportBean> call, Throwable t) {
//                                    ToastUtil.showTost("获取当天数据失败");
                                }
                            });




                            PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_COMMON);
                            EventManage.petModeChanged event=new EventManage.petModeChanged();
                            event.pet_mode=Constants.PET_STATUS_COMMON;
                            EventBus.getDefault().post(event);
                        }

                        @Override
                        public void onFail(Call<BaseBean> call, Throwable t) {

                        }
                    });
                    mWalkPetView.setSelected(false);

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        MapInstance.getInstance().onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        MapInstance.getInstance().onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MapInstance.getInstance().onResume();
    }

}
