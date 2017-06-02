package com.xiaomaoqiu.now.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.view.CustomProgress;
import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/4/25.
 */

public class DialogUtil {
    private static CustomProgress mCustomProgress;
    /**
     * 显示带文本的加载进度对话框
     */
    public static void showProgress(Context context, String str) {
        if (mCustomProgress == null) {
            mCustomProgress = CustomProgress.show(context, str, false, null);
        } else {
            mCustomProgress.setMessage(str);
            mCustomProgress.show();
        }
    }

    /**
     * 关掉加载进度对话框
     */
    public static void closeProgress() {
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            try {//bug fixxed with umeng at 5.0.1 by long
                mCustomProgress.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
            mCustomProgress = null;
        }
    }
    /**
     * 如果Activity destory了不能运行dialog bug fixxed with umeng at 5.0.1 by long
     * @param context
     * @return
     */
    public static boolean canShowDialog(Context context){
        if(context==null)return false;
        if(context instanceof BaseActivity){
            BaseActivity act = (BaseActivity)context;
            if(act.isDestroy()){
                return false;
            }
        }
        return true;
    }

    public static void showTwoButtonDialog(final Context context, CharSequence charSequence, String cancleText, String confirmText, final View.OnClickListener onCancleClickListener, final View.OnClickListener onConfirmClickListener) {
        if(!canShowDialog(context))return;
        final Dialog dialog = new AppDialog(context, R.layout.dialog_two_button, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(0);
        TextView two_button_message = (TextView) dialog.findViewById(R.id.two_button_message);
        Button two_button_cancle = (Button) dialog.findViewById(R.id.two_button_cancle);
        Button two_button_confirm = (Button) dialog.findViewById(R.id.two_button_confirm);
        two_button_cancle.setText(cancleText);
        two_button_confirm.setText(confirmText);
        two_button_message.setText(charSequence);

        two_button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (onCancleClickListener != null) {
                    onCancleClickListener.onClick(v);
                }
            }
        });
        two_button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onClick(v);
                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    //设备被绑定弹窗
    public static void showDeviceAlreadyBindedDialog(Context context,String oldaccount){
        final Dialog dialog = new AppDialog(context,R.layout.dialog_device_already_bind,-1,-2,0,Gravity.CENTER);
        TextView tv_old_account= (TextView) dialog.findViewById(R.id.tv_old_account);
        tv_old_account.setText("设备已被账号 "+oldaccount+" 绑定" +
                "无法绑定到当前帐号");
        Button already_bind_confirm = (Button) dialog.findViewById(R.id.already_bind_confirm);
        already_bind_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //异地登录弹窗
    public static void showLogoutDialog(Context context){
        final Dialog dialog = new AppDialog(context,R.layout.dialog_logout,-1,-2,0,Gravity.CENTER);
        Button logout_confirm = (Button) dialog.findViewById(R.id.logout_confirm);
        logout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
