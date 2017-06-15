package com.xiaomaoqiu.now.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.view.BatteryIngNoticeDialog;
import com.xiaomaoqiu.now.view.BatteryNoticeDialog;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCustomProgress = null;
        }
    }

    public static Dialog offlineDialog;//设备离线框
    public static Dialog lowBatteryDialog;//低电量弹窗
    public static boolean lowBatteryIsClosed=false;//低电量弹窗关闭过
    public static Dialog superLowBatteryDialog;//超低电量
    public static boolean superLowBatteryIsClosed=false;//超低电量关闭过

    public static BatteryNoticeDialog commonBatteryNoticeDialog;//正常电量弹窗
    public static BatteryIngNoticeDialog batteryIngNoticeDialog;//正在充电中


    //正在充电中弹窗
    public static void showBatteryIngNoticeDialog(Context context){
        closeAllDialog();
        if(batteryIngNoticeDialog==null){
            batteryIngNoticeDialog=new BatteryIngNoticeDialog(context);
        }
        batteryIngNoticeDialog.show();
    }

    //正常电量弹窗
    public static void showCommonBatteryNoticeDialog(Context context, float level, String time, String content){
        closeAllDialog();
        if(commonBatteryNoticeDialog==null){
            commonBatteryNoticeDialog=new BatteryNoticeDialog(context,level,time,content);
        }
        commonBatteryNoticeDialog.show();
    }

    //展示设备离线弹窗
    public static void showDeviceOfflineDialog(Context context) {
        closeAllDialog();
        if (offlineDialog == null) {
            offlineDialog = new AppDialog(context, R.layout.dialog_device_already_bind, -1, -2, 0, Gravity.CENTER);
            TextView tv_old_account = (TextView) offlineDialog.findViewById(R.id.tv_old_account);
            tv_old_account.setText("设备处于离线状态，请开机");
            Button already_bind_confirm = (Button) offlineDialog.findViewById(R.id.already_bind_confirm);
            already_bind_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    offlineDialog.dismiss();
                }
            });

            offlineDialog.setCancelable(false);
            offlineDialog.setCanceledOnTouchOutside(false);
        }
        offlineDialog.show();
    }

    //关闭设备离线弹窗
    public static void closeDeviceOfflineDialog() {
        if (offlineDialog != null && offlineDialog.isShowing()) {

            try {
                offlineDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            offlineDialog = null;
        }
    }

    //设备低电量弹窗
    public static void showLowBatteryDialog(Context context){
        closeAllDialog();
        if(lowBatteryDialog==null){
            lowBatteryDialog=new AppDialog(context,R.layout.dialog_lowbattery,-1, -2, 0, Gravity.CENTER);
            Button btn_ok = (Button) lowBatteryDialog.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lowBatteryDialog.dismiss();
                }
            });

            lowBatteryDialog.setCancelable(false);
            lowBatteryDialog.setCanceledOnTouchOutside(false);
        }
        if(!lowBatteryIsClosed) {//如果没有关闭过，再弹出来
            lowBatteryDialog.show();
            lowBatteryIsClosed=true;
            superLowBatteryIsClosed=false;
        }
    }

    //设备超低电量
    public static void showSuperLowBatteryDialog(Context context){
        closeAllDialog();
        if(superLowBatteryDialog==null){
            superLowBatteryDialog=new AppDialog(context,R.layout.dialog_superlowbattery,-1, -2, 0, Gravity.CENTER);
            Button btn_ok = (Button) superLowBatteryDialog.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              superLowBatteryDialog.dismiss();
                                          }
            });
            superLowBatteryDialog.setCancelable(false);
            superLowBatteryDialog.setCanceledOnTouchOutside(false);
        }
        if(!superLowBatteryIsClosed) {
            superLowBatteryDialog.show();
            lowBatteryIsClosed=false;
            superLowBatteryIsClosed=true;
        }
    }


    //关闭所有弹窗
    public static void closeAllDialog(){
        if(offlineDialog != null && offlineDialog.isShowing()){
            try {
                offlineDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        offlineDialog = null;
        if(lowBatteryDialog!=null&&lowBatteryDialog.isShowing()){
            try {
                lowBatteryDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        lowBatteryDialog=null;
        if(superLowBatteryDialog!=null&&superLowBatteryDialog.isShowing()){
            try {
                superLowBatteryDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        superLowBatteryDialog=null;
        if(commonBatteryNoticeDialog!=null&&commonBatteryNoticeDialog.isShowing()){
            try {
                commonBatteryNoticeDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        commonBatteryNoticeDialog=null;
        if(batteryIngNoticeDialog!=null&&batteryIngNoticeDialog.isShowing()){
            try{
                batteryIngNoticeDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        batteryIngNoticeDialog=null;
    }


    /**
     * 如果Activity destory了不能运行dialog bug fixxed with umeng at 5.0.1 by long
     *
     * @param context
     * @return
     */
    public static boolean canShowDialog(Context context) {
        if (context == null) return false;
        if (context instanceof BaseActivity) {
            BaseActivity act = (BaseActivity) context;
            if (act.isDestroy()) {
                return false;
            }
        }
        return true;
    }

    public static void showTwoButtonDialog(final Context context, CharSequence charSequence, String cancleText, String confirmText, final View.OnClickListener onCancleClickListener, final View.OnClickListener onConfirmClickListener) {
        if (!canShowDialog(context)) return;
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
    public static void showDeviceAlreadyBindedDialog(Context context, String oldaccount) {
        final Dialog dialog = new AppDialog(context, R.layout.dialog_device_already_bind, -1, -2, 0, Gravity.CENTER);
        TextView tv_old_account = (TextView) dialog.findViewById(R.id.tv_old_account);
        if (oldaccount == null || "".equals(oldaccount) || "null".equals(oldaccount)) {
            tv_old_account.setText("设备已被绑定");
        } else {
            tv_old_account.setText("设备已被账号 " + oldaccount + " 绑定\n" +
                    "无法绑定到当前帐号");
        }


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
    public static void showLogoutDialog(Context context,String remoteTime,String osName) {
        final Dialog dialog = new AppDialog(context, R.layout.dialog_logout, -1, -2, 0, Gravity.CENTER);
        TextView tv_remote_login= (TextView) dialog.findViewById(R.id.tv_remote_login);
        if((remoteTime!=null)&&(osName!=null)&&(!"".equals(remoteTime))&&!"".equals(osName)){
            tv_remote_login.setText(remoteTime+"\n"+"您的账户在"+osName+"手机上登录");
        }
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
