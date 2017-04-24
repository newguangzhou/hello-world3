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
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseFragment;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.map.main.IMapCallBack;
import com.xiaomaoqiu.now.map.main.MapModule;
import com.xiaomaoqiu.old.ui.dialog.AsynImgDialog;
import com.xiaomaoqiu.now.view.DialogToast;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.presenter.ILocateView;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.presenter.addressParseListener;
import com.xiaomaoqiu.old.ui.mainPages.pageLocate.view.MapPetAvaterView;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Administrator on 2017/1/14.
 */
@SuppressLint("WrongConstant")
public class LocateFragment extends BaseFragment implements View.OnClickListener
        , ILocateView, IMapCallBack {

    private ImageView  mFindPetView, mWalkPetView;
    private MapPetAvaterView mapPetAvaterView;
    private TextView petLocation, walkpetNoticeView;
    private LinearLayout petLocContainer;

    private MapModule mMapMpdule;
    private LocatePresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_tab_locate, container, false);
        initView(rootView);
        initMapModule(rootView);
        initData();
        return rootView;
    }

    private void initView(View root) {
        mFindPetView = (ImageView) root.findViewById(R.id.btn_find_pet);
        mFindPetView.setOnClickListener(this);
        root.findViewById(R.id.btn_phone_center).setOnClickListener(this);
        root.findViewById(R.id.btn_pet_center).setOnClickListener(this);
        mWalkPetView = (ImageView) root.findViewById(R.id.btn_playing_pet);
        mWalkPetView.setSelected(false);
        mWalkPetView.setOnClickListener(this);
//        mLightstatusView = (ImageView) root.findViewById(R.id.btn_open_light);
//        mLightstatusView.setOnClickListener(this);
//        mLightstatusView.setEnabled(false);
        petLocation = (TextView) root.findViewById(R.id.tv_location);
        petLocation.setText("");
        petLocContainer = (LinearLayout) root.findViewById(R.id.locate_addr_conotainer);
        walkpetNoticeView = (TextView) root.findViewById(R.id.locate_walk_notice);
    }

    private void initMapModule(View rootview) {
        MapView mapView = (MapView) rootview.findViewById(R.id.bmapView);
        mMapMpdule = new MapModule(mapView);
        mMapMpdule.setMapCallBack(this);
        mapPetAvaterView = new MapPetAvaterView(rootview.getContext());
        mMapMpdule.setPetMarkerView(mapPetAvaterView);
        mMapMpdule.setMapcenterWithPhone();
        mMapMpdule.setAddressParseListener(new addressParseListener() {
            @Override
            public void onAddressparsed(String address) {
                String textString=address;

                if(PetInfoInstance.getInstance().location_time!=0) {
                    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String d = format.format(PetInfoInstance.getInstance().location_time*1000);//unix时间戳转化为java的毫秒，然后转成时间
                    textString+=d;
                }

                petLocation.setText(textString);
            }
        });
    }

    private void initData() {
        EventBus.getDefault().register(this);
        mPresenter = new LocatePresenter(this);
        mPresenter.onInit();
    }


    @Override
    public void onClick(View v) {
        if (null == mMapMpdule || null == mPresenter) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_find_pet:
                //狗丢了
                showFindpetDialog(!mFindPetView.isSelected());
                break;
            case R.id.btn_phone_center:
                //手机位置
                mMapMpdule.setMapcenterWithPhone();
                break;
            case R.id.btn_pet_center:
                //狗狗位置
                mPresenter.queryPetLocation(true);
                break;
            case R.id.btn_playing_pet:
                //遛狗
                showWalkPetDialog(!mWalkPetView.isSelected());
                break;
//            case R.id.btn_open_light:
//                //闪光灯
////                showLightDialog(!mLightstatusView.isSelected());
//                break;
        }
    }


    //是回家还是在运动
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onPetInfoChanged(EventManage.atHomeOrtoSport event) {

            if (null != mWalkPetView) {
                mWalkPetView.setEnabled(true);
                mWalkPetView.setSelected(!PetInfoInstance.getInstance().getAtHome());
            }
            if (null != mMapMpdule && !PetInfoInstance.getInstance().getAtHome()) {
                mMapMpdule.resetToWalkMode();
                walkpetNoticeView.setVisibility(View.VISIBLE);
                petLocContainer.setVisibility(View.GONE);
            } else {
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
            }
//todo 修改头像之后解开
//        mapPetAvaterView.setAvaterUrl(PetInfoInstance.getInstance().packBean.logo_url);

    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
//    public void onDeviceInfoChanged(EventManage.notifyDeviceStateChange event) {
////        if (null != mPresenter) {
////            mPresenter.queryLightStatus();
////        }
//    }

    //todo 回调逻辑
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onLocateResult(EventManage.notifyPetLocationChange event) {
//        boolean bFound, double latitude, double longitude
        //todo bFound
//        if (!bFound) {
//            onFail(getContext().getResources().getString(R.string.pet_locate_failed));
//            if (null != mPresenter && mPresenter.isFindPetMode()) {
//                mFindPetView.setSelected(false);
//            }
//            return;
//        }
        mFindPetView.setEnabled(true);
        mMapMpdule.setMapcenterWithPet(PetInfoInstance.getInstance().latitude, PetInfoInstance.getInstance().longitude);
    }

    @Override
    public void onShowPhoneLoc() {
        mMapMpdule.setMapMode(MapModule.MODE_WALK_PET);//遛狗模式
        mMapMpdule.setMapcenterWithPhone();
    }

//    @Override
//    public void onChangeLightStatus(boolean isOpen, boolean isFromView) {
//        mLightstatusView.setEnabled(true);
//        mLightstatusView.setSelected(isOpen);
//        if (isFromView && !isOpen) {
//            new DialogToast(getContext(), "追踪器灯光已关闭。", "确定", null);
//        }
//    }

    @SuppressLint("WrongConstant")
    @Override
    public void onFail(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 狗丢了对话框
     *
     * @param isOpen 是丢狗模式还是取消
     */
    private void showFindpetDialog(boolean isOpen) {
        if (isOpen) {
            String conent = getContext().getResources().getString(R.string.map_is_findpet);
            DialogToast.createDialogWithTwoButton(getContext(), conent, new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mFindPetView.setSelected(true);
                    mFindPetView.setEnabled(false);
                    if (null != mMapMpdule) {
                        mMapMpdule.startFindPet();
                    }
                    if (null != mPresenter) {
                        mPresenter.queryPetLocation(true);
                    }

                    ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 1).enqueue(new XMQCallback<BaseBean>() {
                        @Override
                        public void onSuccess(Response<BaseBean> response, BaseBean message) {

                        }

                        @Override
                        public void onFail(Call<BaseBean> call, Throwable t) {

                        }
                    });
                }
            });
        } else {
            mMapMpdule.onCloseFindPetMode();
            mFindPetView.setSelected(false);
            new DialogToast(getContext(), "已关闭紧急追踪模式。", "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), 2).enqueue(new XMQCallback<BaseBean>() {
                        @Override
                        public void onSuccess(Response<BaseBean> response, BaseBean message) {

                        }

                        @Override
                        public void onFail(Call<BaseBean> call, Throwable t) {

                        }
                    });

                }
            });

        }
    }

//    /**
//     * 闪光灯开启关闭对话框
//     *
//     * @param isOpen
//     */
//    private void showLightDialog(final boolean isOpen) {
//        if (isOpen) {
//            String conent = getContext().getResources().getString(R.string.map_light_open);
//            DialogToast.createDialogWithTwoButton(getContext(), conent, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null != mPresenter) {
//                        mPresenter.setLightStatus(isOpen);
//                    }
//                }
//            });
//        } else {
//            if (null != mPresenter) {
//                mPresenter.setLightStatus(false);
//            }
//        }
//    }

    /**
     * 遛狗按钮点击
     *
     * @param isStart
     */
    private void showWalkPetDialog(boolean isStart) {
        if (null == mMapMpdule) {
            return;
        }
        if (isStart) {
            AsynImgDialog.createGoSportDialig(getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMapMpdule.startWalkPet()) {
                        mWalkPetView.setEnabled(false);
                    }
                }
            });
        } else {
            AsynImgDialog.createGoHomeDialog(getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWalkPetView.setEnabled(false);
                    mMapMpdule.stopWalkPet();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        if (null != mMapMpdule) {
            mMapMpdule.onDestroy();
        }
        if (null != mPresenter) {
            mPresenter.release();
        }

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (null != mMapMpdule) {
            mMapMpdule.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mMapMpdule) {
            mMapMpdule.onResume();
        }
    }

    //todo 没人调
    public void onTraceSuccess(boolean isStart) {
        if (null == mPresenter) {
            return;
        }
        if (isStart) {
            mPresenter.goSport();
        } else {
            mPresenter.goHome();
        }
    }
}
