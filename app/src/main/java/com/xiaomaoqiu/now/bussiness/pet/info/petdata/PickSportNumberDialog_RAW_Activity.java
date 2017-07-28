package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaomaoqiu.now.bussiness.pet.PetInfo;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
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
    List<String> energylist=new ArrayList<>();

    TextView tv_suggest;

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
        tv_suggest= (TextView) this.findViewById(R.id.tv_suggest);
        tv_suggest.setText("小毛球推荐卡路里"+PetInfoInstance.getInstance().getSuggest_energy()+"千卡");

        mWvNumber = (WheelView) findViewById(R.id.wv_number);

        final DecimalFormat df = new DecimalFormat("0.00");//格式化

        double recommend_energy=Double.valueOf(PetInfoInstance.getInstance().getSuggest_energy());
        energylist.clear();
        for (int i = 80; i <= 130; i += 5) {
            energylist.add(df.format((i / 100f) * recommend_energy));
            mItems.add(i+"%推荐运动量("+df.format((i / 100f) * recommend_energy)+"千卡)");
        }
        mWvNumber.setItems(mItems);

        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    public void setOnPickNumberListener(OnPickNumberListener listener) {
        mOnPickNumberListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnPickNumberListener != null) {
            String str = energylist.get(mWvNumber.getSeletedIndex());
            mOnPickNumberListener.onConfirmNumber(str);
        }
        dismiss();
    }
}
