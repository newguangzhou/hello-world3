package com.xiaomaoqiu.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.xiaomaoqiu.pet.R;

/**
 * Created by huangjx on 2016/7/31.
 */
public class StartPetFindingDialog extends Dialog {
    public interface OnDialogDismiss{
        void onDismiss(int nID);
    }
    OnDialogDismiss mOnDismiss;

    public StartPetFindingDialog(Context context, OnDialogDismiss onDismissListener, int theme) {
        super(context, theme);
        mOnDismiss = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_start_pet_finding);
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDismiss.onDismiss(v.getId());
                dismiss();
            }
        });
    }
}
