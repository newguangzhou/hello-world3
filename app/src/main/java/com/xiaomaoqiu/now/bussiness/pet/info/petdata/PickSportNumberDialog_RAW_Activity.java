package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.xiaomaoqiu.now.view.WheelView;
import com.xiaomaoqiu.pet.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjx on 2016/7/24.
 */
public class PickSportNumberDialog_RAW_Activity extends Dialog implements View.OnClickListener {
    WheelView mWvNumber;
    List<String> mItems = new ArrayList<String>();


    public interface OnPickNumberListener {
        void onConfirmNumber(String number);
    }

    OnPickNumberListener mOnPickNumberListener;

    public PickSportNumberDialog_RAW_Activity(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_pick_sport_number);

        mWvNumber = (WheelView) findViewById(R.id.wv_number);
//        DecimalFormat df = new DecimalFormat("0.00");//格式化
        for (int i = 80; i <= 125; i += 5)
            mItems.add(i+"%");
        mWvNumber.setItems(mItems);

        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    public void setOnPickNumberListener(OnPickNumberListener listener) {
        mOnPickNumberListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnPickNumberListener != null) {
            String str = mItems.get(mWvNumber.getSeletedIndex());
            mOnPickNumberListener.onConfirmNumber(str);
        }
        dismiss();
    }
}
