package com.xiaomaoqiu.now.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xiaomaoqiu.old.utils.DensityUtil;
import com.xiaomaoqiu.pet.R;


public class ContactServiceDialog extends Dialog {

    public ContactServiceDialog(Context context) {
        this(context,R.style.MyDialogStyleBottom);
    }

    public ContactServiceDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_service_dialog);
        initParams();
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactServiceDialog.this.dismiss();
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
