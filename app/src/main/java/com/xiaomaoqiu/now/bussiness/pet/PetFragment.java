package com.xiaomaoqiu.now.bussiness.pet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseFragment;
import com.xiaomaoqiu.now.bean.nocommon.PetSportBean;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.old.ui.dialog.DialogToast;
import com.xiaomaoqiu.old.ui.dialog.StartPetFindingDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageHealth.HealthIndexActivity;
import com.xiaomaoqiu.old.ui.mainPages.pageHealth.SleepIndexActivity;
import com.xiaomaoqiu.old.ui.mainPages.pageHealth.SportIndexActivity;
import com.xiaomaoqiu.old.ui.mainPages.pageHealth.health.HealthGoSportView;
import com.xiaomaoqiu.old.widgets.CircleProgressBar;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2015/6/12.
 */

@SuppressLint("WrongConstant")
public class PetFragment extends BaseFragment implements  View.OnClickListener {

    private HealthGoSportView mGoSportView;
    CircleProgressBar prog;
    TextView tvSportDone;
    TextView tvSportTarget;

    String strStart;
    String strEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.main_tab_health, container, false);
        mGoSportView = (HealthGoSportView) rootView.findViewById(R.id.tab_health_gosportview);

        prog = (CircleProgressBar) rootView.findViewById(R.id.prog_target_done_percentage);
        tvSportDone = (TextView) rootView.findViewById(R.id.tv_sport_done);
        tvSportTarget = (TextView) rootView.findViewById(R.id.tv_sport_target);


        rootView.setOnClickListener(this);
        rootView.findViewById(R.id.btn_sport_index).setOnClickListener(this);

        rootView.findViewById(R.id.btn_locate).setOnClickListener(this);

        rootView.findViewById(R.id.btn_health).setOnClickListener(this);


        rootView.findViewById(R.id.btn_sport).setOnClickListener(this);

        rootView.findViewById(R.id.btn_go_home).setOnClickListener(this);

        rootView.findViewById(R.id.btn_sleep).setOnClickListener(this);

        initProgress();
        EventBus.getDefault().register(this);

        return rootView;
    }


    public void initProgress() {
        long msEnd = System.currentTimeMillis();
        Date today = new Date(msEnd);

        strEnd = String.format("%s-%s-%s", today.getYear() + 1900, today.getMonth() + 1, today.getDate());
        strStart = strEnd;
        prog.setProgress(100);
    }

    //粘性事件，等待petid返回来再进行网络操作
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0, sticky = true)
    public void getActivityInfo(EventManage.notifyPetFramentGetActivityInfo event) {
        initProgress();
        ApiUtils.getApiService().getActivityInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id(), strStart, strEnd).enqueue(new XMQCallback<PetSportBean>() {
            @Override
            public void onSuccess(Response<PetSportBean> response, PetSportBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                if (ret == HttpCode.EC_SUCCESS) {
                    int sportTarget = 1000;
                    int sportDone = 100;
                    double percentage = 100;
                    if (message.data.size() > 0) {
                        PetSportBean.SportBean bean = message.data.get(0);
                        sportTarget = bean.target_amount;
                        sportDone = bean.reality_amount;
                        percentage = bean.percentage;
                    } else {
                        Toast.makeText(PetAppLike.mcontext, "当天尚无数据~", Toast.LENGTH_SHORT).show();

                    }
                    prog.setProgress((int) percentage);
                    tvSportDone.setText(String.format("已消耗%d卡", sportDone));
                    tvSportTarget.setText(String.format("目标消耗%d卡", sportTarget));
                } else {
                    Toast.makeText(PetAppLike.mcontext, "获取当天数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(Call<PetSportBean> call, Throwable t) {
                Toast.makeText(PetAppLike.mcontext, "获取当天数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //todo 更新逻辑
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onPetInfoChanged(EventManage.notifyPetInfoChange event) {
            if (PetInfoInstance.getInstance().getAtHome()) {//回家
                getView().findViewById(R.id.btn_sport).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.btn_go_home).setVisibility(View.INVISIBLE);
            } else {//出去玩
                getView().findViewById(R.id.btn_sport).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.btn_go_home).setVisibility(View.VISIBLE);
            }
        SimpleDraweeView imgLogo = (SimpleDraweeView) getActivity().findViewById(R.id.go_sport_head);
        Uri uri = Uri.parse(PetInfoInstance.getInstance().packBean.logo_url);
        imgLogo.setImageURI(uri);
//            AsyncImageTask.INSTANCE.loadImage(imgLogo, PetInfoInstance.getInstance().packBean.logo_url, this);
    }

    @Override
    public void onClick(View v) {
        if (mGoSportView.isShowing()) {
            mGoSportView.show(HealthGoSportView.STATUS_DEFAULT);
            return;
        }
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_sport_index:
                intent.setClass(getActivity(), SportIndexActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_locate:
               StartPetFindingDialog dialog = new StartPetFindingDialog(getActivity(),
                        new StartPetFindingDialog.OnDialogDismiss() {
                            @Override
                            public void onDismiss(int nID) {
                                if(nID == R.id.btn_ok) {
                                    Intent intent = new Intent(getActivity(), FindPetActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                        , R.style.MyDialogStyle);
                dialog.show();
                String conent = getContext().getResources().getString(R.string.map_is_findpet);
                DialogToast.createDialogWithTwoButton(getContext(), conent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            case R.id.btn_health:
                intent.setClass(getActivity(), HealthIndexActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sport:
                mGoSportView.show(HealthGoSportView.STATUS_SPORT);
                break;
            case R.id.btn_go_home:
                mGoSportView.show(HealthGoSportView.STATUS_BACK);
                break;
            case R.id.btn_sleep:
                intent.setClass(getActivity(), SleepIndexActivity.class);
                startActivity(intent);
                break;
        }
    }

    public boolean isDialogShowing() {
        if (null != mGoSportView) {
            return mGoSportView.isShowing();
        }
        return false;
    }

    public void hideDialog() {
        mGoSportView.show(HealthGoSportView.STATUS_DEFAULT);
    }




    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
