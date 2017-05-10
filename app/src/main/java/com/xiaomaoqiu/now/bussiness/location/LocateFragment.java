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
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseFragment;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.main.MapInstance;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
import com.xiaomaoqiu.now.view.DialogToast;
import com.xiaomaoqiu.old.ui.dialog.AsynImgDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.presenter.addressParseListener;
import com.xiaomaoqiu.now.view.MapPetAvaterView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Administrator on 2017/1/14.
 */
@SuppressLint("WrongConstant")
public class LocateFragment extends BaseFragment implements View.OnClickListener {


    MapView mapView;
    private ImageView mFindPetView, mWalkPetView;
    private MapPetAvaterView mapPetAvaterView;
    private TextView petLocation, walkpetNoticeView;
    private LinearLayout petLocContainer;

    boolean isOpen;

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
        root.findViewById(R.id.btn_phone_center).setOnClickListener(this);
        root.findViewById(R.id.btn_pet_center).setOnClickListener(this);
        mWalkPetView = (ImageView) root.findViewById(R.id.btn_playing_pet);

        mWalkPetView.setOnClickListener(this);

        mapPetAvaterView=new MapPetAvaterView(root.getContext());
        petLocation = (TextView) root.findViewById(R.id.tv_location);
        petLocation.setText("");
        petLocContainer = (LinearLayout) root.findViewById(R.id.locate_addr_conotainer);
        walkpetNoticeView = (TextView) root.findViewById(R.id.locate_walk_notice);
    }

    void initListener() {
        MapInstance.getInstance().setAddressParseListener(new addressParseListener() {
            @Override
            public void onAddressparsed(String address) {
                String textString = address;

                if (PetInfoInstance.getInstance().location_time != 0) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String d = format.format(PetInfoInstance.getInstance().location_time * 1000);//unix时间戳转化为java的毫秒，然后转成时间
                    textString += d;
                }
                petLocation.setText(textString);
                Log.e("longtianlove", textString);
            }
        });
    }

    private void initData() {
        EventBus.getDefault().register(this);
        MapInstance.getInstance().setPetAvaterView(mapPetAvaterView);
        MapInstance.getInstance().init(mapView);
        isOpen = MapInstance.getInstance().GPS_OPEN;
        if(isOpen){
            mWalkPetView.setVisibility(View.GONE);
            mFindPetView.setSelected(true);
            walkpetNoticeView.setVisibility(View.GONE);
            petLocContainer.setVisibility(View.VISIBLE);
        }else{
            mWalkPetView.setVisibility(View.VISIBLE);
            mFindPetView.setSelected(false);
            if(!PetInfoInstance.getInstance().getAtHome()){
                mWalkPetView.setSelected(true);
                walkpetNoticeView.setVisibility(View.VISIBLE);
                petLocContainer.setVisibility(View.GONE);
            }else{
                mWalkPetView.setSelected(false);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
            }

        }
        MapInstance.getInstance(). startLocListener(1000);
        PetInfoInstance.getInstance().getPetLocation();
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
                MapInstance.getInstance().startLoc();
                break;
            case R.id.btn_pet_center:
                //狗狗位置
                PetInfoInstance.getInstance().getPetLocation();
                break;
            case R.id.btn_playing_pet:
                //去运动
                showWalkPetDialog(!mWalkPetView.isSelected());
                break;
        }
    }


    //是回家还是在运动
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onPetInfoChanged(EventManage.atHomeOrtoSport event) {

        if (null != mWalkPetView) {
            mWalkPetView.setSelected(!PetInfoInstance.getInstance().getAtHome());
        }

        if (!PetInfoInstance.getInstance().getAtHome()) {
            walkpetNoticeView.setVisibility(View.VISIBLE);
            petLocContainer.setVisibility(View.GONE);
        } else {
            walkpetNoticeView.setVisibility(View.GONE);
            petLocContainer.setVisibility(View.VISIBLE);
        }
        mapPetAvaterView.setAvaterUrl(PetInfoInstance.getInstance().packBean.logo_url);

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void getActivityInfo(EventManage.uploadImageSuccess event) {
        mapPetAvaterView.setAvaterUrl(PetInfoInstance.getInstance().packBean.logo_url);
    }


    //todo 回调逻辑
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onLocateResult(EventManage.notifyPetLocationChange event) {
        MapInstance.getInstance().setPetLocation(PetInfoInstance.getInstance().latitude, PetInfoInstance.getInstance().longitude,PetInfoInstance.getInstance().radius);
    }

    //gps状态变化
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void updateGPSState(EventManage.GPS_CHANGE event) {
        isOpen = MapInstance.getInstance().GPS_OPEN;
        if(isOpen){
            mWalkPetView.setVisibility(View.GONE);
            mFindPetView.setSelected(true);
            walkpetNoticeView.setVisibility(View.GONE);
            petLocContainer.setVisibility(View.VISIBLE);
        }else{
            mWalkPetView.setVisibility(View.VISIBLE);
            mFindPetView.setSelected(false);
            if(!PetInfoInstance.getInstance().getAtHome()){
                mWalkPetView.setSelected(true);
                walkpetNoticeView.setVisibility(View.VISIBLE);
                petLocContainer.setVisibility(View.GONE);
            }else{
                mWalkPetView.setSelected(false);
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * 狗丢了对话框
     *
     */
    private void showFindpetDialog() {
       isOpen= MapInstance.getInstance().GPS_OPEN;
        if (isOpen) {

            new DialogToast(getContext(), "是否关闭紧急追踪模式。", "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 2).enqueue(new XMQCallback<BaseBean>() {
                        @Override
                        public void onSuccess(Response<BaseBean> response, BaseBean message) {
                            MapInstance.getInstance().setGPSState(false);
                            mFindPetView.setSelected(false);
                            EventBus.getDefault().post(new EventManage.GPS_CHANGE());
                        }

                        @Override
                        public void onFail(Call<BaseBean> call, Throwable t) {

                        }
                    });

                }
            });
        } else {
            String conent = getContext().getResources().getString(R.string.map_is_findpet);
            DialogToast.createDialogWithTwoButton(getContext(), conent, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MapInstance.getInstance().startFindPet();
                    ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 1).enqueue(new XMQCallback<BaseBean>() {
                        @Override
                        public void onSuccess(Response<BaseBean> response, BaseBean message) {
                            mFindPetView.setSelected(true);
                            MapInstance.getInstance().setGPSState(true);
                            EventBus.getDefault().post(new EventManage.GPS_CHANGE());
                        }

                        @Override
                        public void onFail(Call<BaseBean> call, Throwable t) {

                        }
                    });
                    PetInfoInstance.getInstance().getPetLocation();
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
                    PetInfoInstance.getInstance().setAtHome(false);
                    mWalkPetView.setSelected(true);
                }
            });
        } else {
            AsynImgDialog.createGoHomeDialog(getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWalkPetView.setSelected(false);
                    PetInfoInstance.getInstance().setAtHome(true);

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
