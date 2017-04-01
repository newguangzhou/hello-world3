package com.xiaomaoqiu.pet.ui.mainPages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.HttpCode;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.dataCenter.PetInfo;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.ui.dialog.DialogToast;
import com.xiaomaoqiu.pet.ui.mainPages.pageHealth.HealthIndex;
import com.xiaomaoqiu.pet.ui.mainPages.pageHealth.SleepIndex;
import com.xiaomaoqiu.pet.ui.mainPages.pageHealth.SportIndex;
import com.xiaomaoqiu.pet.ui.mainPages.pageHealth.health.HealthGoSportView;
import com.xiaomaoqiu.pet.utils.AsyncImageTask;
import com.xiaomaoqiu.pet.utils.HttpUtil;
import com.xiaomaoqiu.pet.widgets.CircleProgressBar;
import com.xiaomaoqiu.pet.widgets.FragmentEx;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Administrator on 2015/6/12.
 */


public class PageHealth extends FragmentEx implements PetInfo.Callback_PetInfo, View.OnClickListener, AsyncImageTask.ImageCallback {

    private HealthGoSportView mGoSportView;

    public PageHealth()
    {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.main_tab_health, container, false);
        mGoSportView=(HealthGoSportView)rootView.findViewById(R.id.tab_health_gosportview);
        rootView.setOnClickListener(this);
        rootView.findViewById(R.id.btn_sport_index).setOnClickListener(this);

        rootView.findViewById(R.id.btn_locate).setOnClickListener(this);

        rootView.findViewById(R.id.btn_health).setOnClickListener(this);


        rootView.findViewById(R.id.btn_sport).setOnClickListener(this);

        rootView.findViewById(R.id.btn_go_home).setOnClickListener(this);

        rootView.findViewById(R.id.btn_sleep).setOnClickListener(this);

        queryActivityInfo(rootView);
        return rootView;
    }


    void queryActivityInfo(final View v)
    {
        long msEnd = System.currentTimeMillis();
        Date today = new Date(msEnd);

        String strEnd = String.format("%s-%s-%s",today.getYear()+1900,today.getMonth()+1,today.getDate());
        String strStart = strEnd;
        CircleProgressBar prog=(CircleProgressBar)v.findViewById(R.id.prog_target_done_percentage);
        prog.setProgress(75);

        HttpUtil.get2("pet.health.get_sport_info", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //{"status": 0, "data": [{"date": "2016-04-16", "percentage": 26, "reality_amount": 154, "target_amount": 584},{}]}
                Log.v("http", "pet.health.get_sport_info:" + response.toString());
                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                if (ret == HttpCode.EC_SUCCESS) {
                    JSONArray jsdata = response.optJSONArray("data");
                    if (jsdata != null && jsdata.length() > 0) {
                        int days = jsdata.length();
                        //设置今日活动
                        JSONObject jsToday = (JSONObject) jsdata.opt(0);
                        int sportTarget = jsToday.optInt("target_amount", 1000);
                        int sportDone = jsToday.optInt("reality_amount", 100);
                        double percentage = jsToday.optDouble("percentage",100);

                        CircleProgressBar prog=(CircleProgressBar)v.findViewById(R.id.prog_target_done_percentage);
                        prog.setProgress((int) percentage);

                        TextView tvSportDone = (TextView)v.findViewById(R.id.tv_sport_done);
                        tvSportDone.setText(String.format(getResources().getString(R.string.pet_sport_done), sportDone));

                        TextView tvSportTarget = (TextView)v.findViewById(R.id.tv_sport_target);
                        tvSportTarget.setText(String.format(getResources().getString(R.string.pet_sport_target),sportTarget));
                    }
                }
            }

        }, LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken(), UserMgr.INSTANCE.getPetInfo().getPetID(), strStart, strEnd);

    }

    @Override
    public void onPetInfoChanged(PetInfo petInfo, int nFieldMask) {
        if((nFieldMask & petInfo.FieldMask_AtHome) != 0 )
        {
            if(petInfo.getAtHome())
            {//回家
                getView().findViewById(R.id.btn_sport).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.btn_go_home).setVisibility(View.INVISIBLE);
            }else
            {//出去玩
                getView().findViewById(R.id.btn_sport).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.btn_go_home).setVisibility(View.VISIBLE);
            }
        }
        if((nFieldMask & PetInfo.FieldMask_Header) != 0)
        {
            Log.v("petinfo","set pet header:"+petInfo.getHeaderImg());
            ImageView imgLogo = (ImageView)getActivity().findViewById(R.id.go_sport_head);
            AsyncImageTask.INSTANCE.loadImage(imgLogo, petInfo.getHeaderImg(), this);
        }
    }

    @Override
    public void onClick(View v) {
        if(mGoSportView.isShowing()){
            mGoSportView.show(HealthGoSportView.STATUS_DEFAULT);
            return;
        }
        Intent intent=new Intent();
        switch (v.getId()){
            case R.id.btn_sport_index:
                intent.setClass(getActivity(),SportIndex.class);
                startActivity(intent);
                break;
            case R.id.btn_locate:
               /* StartPetFindingDialog dialog = new StartPetFindingDialog(getActivity(),
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
                dialog.show();*/
                String conent = getContext().getResources().getString(R.string.map_is_findpet);
                DialogToast.createDialogWithTwoButton(getContext(), conent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            case R.id.btn_health:
                intent.setClass(getActivity(),HealthIndex.class);
                startActivity(intent);
                break;
            case R.id.btn_sport:
                mGoSportView.show(HealthGoSportView.STATUS_SPORT);
                break;
            case R.id.btn_go_home:
                mGoSportView.show(HealthGoSportView.STATUS_BACK);
                break;
            case R.id.btn_sleep:
                intent.setClass(getActivity(),SleepIndex.class);
                startActivity(intent);
                break;
        }
    }

    public boolean isDialogShowing(){
        if(null != mGoSportView){
            return mGoSportView.isShowing();
        }
        return false;
    }

    public void hideDialog(){
        mGoSportView.show(HealthGoSportView.STATUS_DEFAULT);
    }


    @Override
    public void imageLoaded(String url, Bitmap obj, ImageView view) {
        if(obj != null)
        {
            view.setImageBitmap(obj);
        }
    }
}
