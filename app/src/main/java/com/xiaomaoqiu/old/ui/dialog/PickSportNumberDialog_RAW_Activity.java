package com.xiaomaoqiu.old.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.old.widgets.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjx on 2016/7/24.
 */
public class PickSportNumberDialog_RAW_Activity extends Dialog implements View.OnClickListener{
    WheelView mWvNumber;
    List<String> mItems = new ArrayList<String>();


    public interface OnPickNumberListener{
        void onConfirmNumber(int number);
    }

    OnPickNumberListener    mOnPickNumberListener;

    public PickSportNumberDialog_RAW_Activity(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_pick_sport_number);

        mWvNumber = (WheelView) findViewById(R.id.wv_number);

        for(int i=200;i<=1000;i+=50)
            mItems.add(String.valueOf(i));
        mWvNumber.setItems(mItems);

        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    public void setOnPickNumberListener(OnPickNumberListener listener)
    {
        mOnPickNumberListener = listener;
    }

    @Override
    public void onClick(View v) {
        if(mOnPickNumberListener!=null)
        {
            String str = mItems.get(mWvNumber.getSeletedIndex());
            mOnPickNumberListener.onConfirmNumber(Integer.valueOf(str));
        }
        dismiss();
    }
}
