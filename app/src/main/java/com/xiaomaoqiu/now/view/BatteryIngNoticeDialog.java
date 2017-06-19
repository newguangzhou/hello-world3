package com.xiaomaoqiu.now.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaomaoqiu.old.utils.DensityUtil;
import com.xiaomaoqiu.pet.R;

/**
 * Created by Administrator on 2016/12/31.
 */

public class BatteryIngNoticeDialog extends Dialog {


    private Button mButton;


    public BatteryIngNoticeDialog(Context context){
        super(context, R.style.MyDialogStyleTop);
        initView();
        setCanceledOnTouchOutside(true);
        show();
    }


    private void initView(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_battery_ing_notice, null);
        setContentView(view);
        initParams();

        mButton=(Button)findViewById(R.id.battery_notice_bt);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void initParams(){
        //获取到当前Activity的Window
        Window dialog_window = getWindow();
        //获取到LayoutParams
        WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
        //设置宽度
        int margin=getContext().getResources().getDimensionPixelSize(R.dimen.dialog_service_margin)*2;
        dialog_window_attributes.width= DensityUtil.getScreenWidth(getContext())-margin;
        dialog_window.setAttributes(dialog_window_attributes);
    }

}