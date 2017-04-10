package com.xiaomaoqiu.pet.ui.mainPages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.DeviceInfo;
import com.xiaomaoqiu.pet.dataCenter.PetInfo;
import com.xiaomaoqiu.pet.ui.dialog.AsynImgDialog;
import com.xiaomaoqiu.pet.ui.dialog.DialogToast;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter.IMapCallBack;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter.MapModule;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter.ILocateView;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter.LocatePresenter;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter.addressParseListener;
import com.xiaomaoqiu.pet.ui.mainPages.pageLocate.view.MapPetAvaterView;
import com.xiaomaoqiu.pet.widgets.FragmentEx;

import static com.xiaomaoqiu.pet.R.id.tv_location;

/**
 * Created by Administrator on 2017/1/14.
 */
@SuppressLint("WrongConstant")
public class LocateFragment extends FragmentEx implements View.OnClickListener, PetInfo.Callback_PetInfo
        , PetInfo.Callback_PetLocating,DeviceInfo.Callback_DeviceInfo,ILocateView, IMapCallBack {

    private ImageView mLightstatusView,mFindPetView,mWalkPetView;
    private MapPetAvaterView mapPetAvaterView;
    private TextView petLocation,walkpetNoticeView;
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

    private void initView(View root){
        mFindPetView=(ImageView)root.findViewById(R.id.btn_find_pet);
        mFindPetView.setOnClickListener(this);
        root.findViewById(R.id.btn_phone_center).setOnClickListener(this);
        root.findViewById(R.id.btn_pet_center).setOnClickListener(this);
        mWalkPetView=(ImageView)root.findViewById(R.id.btn_playing_pet);
        mWalkPetView.setSelected(false);
        mWalkPetView.setOnClickListener(this);
        mLightstatusView=(ImageView)root.findViewById(R.id.btn_open_light);
        mLightstatusView.setOnClickListener(this);
        mLightstatusView.setEnabled(false);
        petLocation=(TextView)root.findViewById(tv_location);
        petLocation.setText("");
        petLocContainer=(LinearLayout)root.findViewById(R.id.locate_addr_conotainer);
        walkpetNoticeView=(TextView)root.findViewById(R.id.locate_walk_notice);
    }

    private void initMapModule(View rootview){
        MapView mapView=(MapView)rootview.findViewById(R.id.bmapView);
        mMapMpdule=new MapModule(mapView);
        mMapMpdule.setMapCallBack(this);
        mapPetAvaterView=new MapPetAvaterView(rootview.getContext());
        mMapMpdule.setPetMarkerView(mapPetAvaterView);
        mMapMpdule.setMapcenterWithPhone();
        mMapMpdule.setAddressParseListener(new addressParseListener() {
            @Override
            public void onAddressparsed(String address) {
                petLocation.setText(address);
            }
        });
    }

    private void initData(){
        mPresenter=new LocatePresenter(this);
        mPresenter.onInit();
    }


    @Override
    public void onClick(View v) {
        if(null == mMapMpdule || null == mPresenter){
            return;
        }
        switch (v.getId()){
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
            case R.id.btn_open_light:
                //闪光灯
                showLightDialog(!mLightstatusView.isSelected());
                break;
        }
    }


    @Override
    public void onPetInfoChanged(PetInfo petInfo, int nFieldMask) {
        if((nFieldMask & petInfo.FieldMask_AtHome) != 0 )
        {
            if(null != mWalkPetView){
                mWalkPetView.setEnabled(true);
                mWalkPetView.setSelected(!petInfo.getAtHome());
            }
            if(null != mMapMpdule && !petInfo.getAtHome()){
                mMapMpdule.resetToWalkMode();
                walkpetNoticeView.setVisibility(View.VISIBLE);
                petLocContainer.setVisibility(View.GONE);
            }else{
                walkpetNoticeView.setVisibility(View.GONE);
                petLocContainer.setVisibility(View.VISIBLE);
            }
        }
        if((nFieldMask & PetInfo.FieldMask_Header) != 0)
        {
            mapPetAvaterView.setAvaterUrl(petInfo.getHeaderImg());
        }
    }

    @Override
    public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
        if(null != mPresenter){
            mPresenter.queryLightStatus();
        }
    }

    @Override
    public void onLocateResult(boolean bFound, double latitude, double longitude) {
        if(!bFound){
            onFail(getContext().getResources().getString(R.string.pet_locate_failed));
            if(null != mPresenter && mPresenter.isFindPetMode()){
                mFindPetView.setSelected(false);
            }
            return;
        }
        mFindPetView.setEnabled(true);
        mMapMpdule.setMapcenterWithPet(latitude,longitude);
    }

    @Override
    public void onShowPhoneLoc() {
        mMapMpdule.setMapMode(MapModule.MODE_WALK_PET);//遛狗模式
        mMapMpdule.setMapcenterWithPhone();
    }

    @Override
    public void onChangeLightStatus(boolean isOpen,boolean isFromView) {
        mLightstatusView.setEnabled(true);
        mLightstatusView.setSelected(isOpen);
        if(isFromView && !isOpen){
            new DialogToast(getContext(),"追踪器灯光已关闭。","确定",null);
        }
    }

    @Override
    public void onFail(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 狗丢了对话框
     * @param isOpen 是丢狗模式还是取消
     */
    private void showFindpetDialog(boolean isOpen){
        if(isOpen) {
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
                }
            });
        }else{
            mMapMpdule.onCloseFindPetMode();
            mFindPetView.setSelected(false);
            new DialogToast(getContext(),"已关闭紧急追踪模式。","确定",null);
        }
    }

    /**
     * 闪光灯开启关闭对话框
     * @param isOpen
     */
    private void showLightDialog(final boolean isOpen){
        if(isOpen){
            String conent=getContext().getResources().getString(R.string.map_light_open);
            DialogToast.createDialogWithTwoButton(getContext(), conent, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null != mPresenter){
                        mPresenter.setLightStatus(isOpen);
                    }
                }
            });
        }else{
            if(null != mPresenter){
                mPresenter.setLightStatus(false);
            }
        }
    }

    /**
     * 遛狗按钮点击
     * @param isStart
     */
    private void showWalkPetDialog(boolean isStart){
        if(null == mMapMpdule){
            return;
        }
        if(isStart){
            AsynImgDialog.createGoSportDialig(getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMapMpdule.startWalkPet())
                    {
                        mWalkPetView.setEnabled(false);
                    }
                }
            });
        }else{
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
        if(null != mMapMpdule){
            mMapMpdule.onDestroy();
        }
        if(null != mPresenter){
            mPresenter.release();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(null != mMapMpdule){
            mMapMpdule.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != mMapMpdule){
            mMapMpdule.onResume();
        }
    }

    @Override
    public void onTraceSuccess(boolean isStart) {
        if(null == mPresenter){
            return;
        }
        if(isStart){
            mPresenter.goSport();
        }else{
            mPresenter.goHome();
        }
    }
}
