package com.xiaomaoqiu.now.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.bean.PetStatusBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.push.PushEventManage;
import com.xiaomaoqiu.now.view.BatteryIngNoticeDialog;
import com.xiaomaoqiu.now.view.BatteryNoticeDialog;
import com.xiaomaoqiu.now.view.CustomProgress;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

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
    public static boolean lowBatteryIsClosed = false;//低电量弹窗关闭过
    public static Dialog superLowBatteryDialog;//超低电量
    public static boolean superLowBatteryIsClosed = false;//超低电量关闭过

    public static BatteryNoticeDialog commonBatteryNoticeDialog;//正常电量弹窗
    public static BatteryIngNoticeDialog batteryIngNoticeDialog;//正在充电中

    public static Dialog unbindDialog;//确认解除绑定

    public static Dialog safeCautionDialog;//安全弹窗

    public static Dialog petisFindedDialog;//宠物是否找到

    public static Dialog petAtHomeDialog;//宠物是否到家


    public static Dialog toSportDialog;//去运动弹窗
    public static Dialog toHomeDialog;//回家弹窗


    //正在充电中弹窗
    public static void showBatteryIngNoticeDialog(Context context) {
        closeAllDialog();
        if (batteryIngNoticeDialog == null) {
            batteryIngNoticeDialog = new BatteryIngNoticeDialog(context);
        }
        batteryIngNoticeDialog.show();
    }

    //正常电量弹窗
    public static void showCommonBatteryNoticeDialog(Context context, float level, String time, String content) {
        closeAllDialog();
        if (commonBatteryNoticeDialog == null) {
            commonBatteryNoticeDialog = new BatteryNoticeDialog(context, level, time, content);
        }
        commonBatteryNoticeDialog.show();
        lowBatteryIsClosed = false;
        superLowBatteryIsClosed = false;
    }

    //展示设备离线弹窗
    public static void showDeviceOfflineDialog(Context context, String title) {
        closeAllDialog();
        if (offlineDialog == null) {
            offlineDialog = new AppDialog(context, R.layout.dialog_device_already_bind, -1, -2, 0, Gravity.CENTER);
            TextView tv_title = (TextView) offlineDialog.findViewById(R.id.tv_title);
            tv_title.setText(title);
            TextView tv_old_account = (TextView) offlineDialog.findViewById(R.id.tv_old_account);
            tv_old_account.setText(context.getString(R.string.dialog_offline_message));
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
    public static void showLowBatteryDialog(Context context) {
        closeAllDialog();
        if (lowBatteryDialog == null) {
            lowBatteryDialog = new AppDialog(context, R.layout.dialog_lowbattery, -1, -2, 0, Gravity.CENTER);
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
        if (!lowBatteryIsClosed) {//如果没有关闭过，再弹出来
            lowBatteryDialog.show();
            lowBatteryIsClosed = true;
            superLowBatteryIsClosed = false;
        }
    }

    //设备超低电量
    public static void showSuperLowBatteryDialog(Context context) {
        closeAllDialog();
        if (superLowBatteryDialog == null) {
            superLowBatteryDialog = new AppDialog(context, R.layout.dialog_superlowbattery, -1, -2, 0, Gravity.CENTER);
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
        if (!superLowBatteryIsClosed) {
            superLowBatteryDialog.show();
            lowBatteryIsClosed = false;
            superLowBatteryIsClosed = true;
        }
    }

    //关闭所有弹窗
    public static void closeAllDialog() {
        if (offlineDialog != null && offlineDialog.isShowing()) {
            try {
                offlineDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        offlineDialog = null;
        if (lowBatteryDialog != null && lowBatteryDialog.isShowing()) {
            try {
                lowBatteryDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        lowBatteryDialog = null;
        if (superLowBatteryDialog != null && superLowBatteryDialog.isShowing()) {
            try {
                superLowBatteryDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        superLowBatteryDialog = null;
        if (commonBatteryNoticeDialog != null && commonBatteryNoticeDialog.isShowing()) {
            try {
                commonBatteryNoticeDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        commonBatteryNoticeDialog = null;
        if (batteryIngNoticeDialog != null && batteryIngNoticeDialog.isShowing()) {
            try {
                batteryIngNoticeDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        batteryIngNoticeDialog = null;
//        if(safeCautionDialog!=null&&safeCautionDialog.isShowing()){
//            try{
//                safeCautionDialog.dismiss();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        safeCautionDialog=null;
    }


    //设备被绑定弹窗
    public static void showDeviceAlreadyBindedDialog(Context context, String oldaccount) {
        final Dialog dialog = new AppDialog(context, R.layout.dialog_device_already_bind, -1, -2, 0, Gravity.CENTER);
        TextView tv_old_account = (TextView) dialog.findViewById(R.id.tv_old_account);
        if (oldaccount == null || "".equals(oldaccount) || "null".equals(oldaccount)) {
            tv_old_account.setText("追踪器已被绑定");
        } else {
            tv_old_account.setText("追踪器已被账号 " + oldaccount + " 绑定\n" +
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
    public static void showLogoutDialog(Context context, String remoteTime, String osName) {
        final Dialog dialog = new AppDialog(context, R.layout.dialog_logout, -1, -2, 0, Gravity.CENTER);
        TextView tv_remote_login = (TextView) dialog.findViewById(R.id.tv_remote_login);
        if ((remoteTime != null) && (osName != null) && (!"".equals(remoteTime)) && !"".equals(osName)) {
            tv_remote_login.setText(remoteTime + "\n" + "您的账号在另一台【" + osName + "】手机上登录");
        }
        Button logout_confirm = (Button) dialog.findViewById(R.id.logout_confirm);
        logout_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    //设备确认开机
    public static void showDeviceOpenOnline(Context context, final View.OnClickListener oklistener, final View.OnClickListener cancellistener) {
        final Dialog dialog = new AppDialog(context, R.layout.dialog_device_show, -1, -2, 0, Gravity.CENTER);
        TextView tv_message = (TextView) dialog.findViewById(R.id.tv_message);
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (oklistener != null) {
                    oklistener.onClick(v);
                }

            }
        });
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (cancellistener != null) {
                    cancellistener.onClick(v);
                }

            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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


    //确认解除绑定
    public static void showUnBindDialog(final Context context, final View.OnClickListener okListener, final View.OnClickListener cancelListener) {
        if (!canShowDialog(context)) return;
        final Dialog dialog = new AppDialog(context, R.layout.dialog_unbind, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(0);
        Button two_button_confirm = (Button) dialog.findViewById(R.id.two_button_confirm);
        two_button_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (okListener != null) {
                    okListener.onClick(v);
                }
            }
        });
        Button two_button_cancle = (Button) dialog.findViewById(R.id.two_button_cancle);
        two_button_cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    public static void showOneButtonDialog(final Context context, String messageString, final View.OnClickListener listener) {
        if (!canShowDialog(context)) return;
        final Dialog dialog = new AppDialog(context, R.layout.dialog_one_button, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(0);
        TextView tv_one_button_message = (TextView) dialog.findViewById(R.id.tv_one_button_message);
        tv_one_button_message.setText(messageString);
        Button btn_one_button_confirm = (Button) dialog.findViewById(R.id.btn_one_button_confirm);
        btn_one_button_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public static Thread noresponsethread;

    //已经找到宠物弹窗petisFindedDialog
    public static void showPetFindedDialog(final Context context, String messageString, String cancleText, String confirmText, final View.OnClickListener onCancleClickListener, final View.OnClickListener onConfirmClickListener) {
        if (!canShowDialog(context)) return;
        if (petisFindedDialog != null && petisFindedDialog.isShowing()) {
            try {
                petisFindedDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        petisFindedDialog = null;
        petisFindedDialog = new AppDialog(context, R.layout.dialog_two_button, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        petisFindedDialog.getWindow().setWindowAnimations(0);
        TextView two_button_message = (TextView) petisFindedDialog.findViewById(R.id.two_button_message);
        Button two_button_cancle = (Button) petisFindedDialog.findViewById(R.id.two_button_cancle);
        Button two_button_confirm = (Button) petisFindedDialog.findViewById(R.id.two_button_confirm);
        two_button_cancle.setText(cancleText);
        two_button_confirm.setText(confirmText);
        two_button_message.setText(messageString);

        two_button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (petisFindedDialog.isShowing()) {
                    petisFindedDialog.dismiss();
                }
                if (onCancleClickListener != null) {
                    onCancleClickListener.onClick(v);
                }
            }
        });
        two_button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (petisFindedDialog.isShowing()) {
                    petisFindedDialog.dismiss();
                }
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onClick(v);
                }
            }
        });

        petisFindedDialog.setCancelable(false);
        petisFindedDialog.setCanceledOnTouchOutside(false);
        petisFindedDialog.show();

        if (noresponsethread == null) {
            try {
                noresponsethread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(600000);
                        } catch (Exception e) {

                        }
                        if (PetInfoInstance.getInstance().PET_MODE == Constants.PET_STATUS_FIND) {
                            ApiUtils.getApiService().findPet(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id(), Constants.GPS_CLOSE).enqueue(new XMQCallback<PetStatusBean>() {
                                @Override
                                public void onSuccess(Response<PetStatusBean> response, PetStatusBean message) {
                                    HttpCode ret = HttpCode.valueOf(message.status);
                                    switch (ret) {
                                        case EC_SUCCESS:
                                            if (DeviceInfoInstance.getInstance().online != true) {
                                                DeviceInfoInstance.getInstance().online = true;
                                                EventBus.getDefault().post(new PushEventManage.deviceOnline());
                                            }
                                            PetInfoInstance.getInstance().setPetMode(Constants.PET_STATUS_COMMON);
                                            EventBus.getDefault().post(new EventManage.petModeChanged());
                                            break;
                                        case EC_OFFLINE:
                                            DeviceInfoInstance.getInstance().online = false;
                                            EventBus.getDefault().post(new EventManage.DeviceOffline());
                                            break;
                                    }

                                }

                                @Override
                                public void onFail(Call<PetStatusBean> call, Throwable t) {

                                }
                            });
                        }

                        noresponsethread = null;
                    }
                });
            } catch (Exception e) {

            }
            noresponsethread.start();
        }

    }

    //宠物是否到家  petAtHomeDialog
    public static void showPetAtHomeDialog(final Context context, String messageString, String cancleText, String confirmText, final View.OnClickListener onCancleClickListener, final View.OnClickListener onConfirmClickListener) {
        if (!canShowDialog(context)) return;
        if (safeCautionDialog != null && safeCautionDialog.isShowing()) {
            try {
                safeCautionDialog.dismiss();
            } catch (Exception e) {

            }
        }

        if (petAtHomeDialog != null && petAtHomeDialog.isShowing()) {
            try {
                petAtHomeDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        petAtHomeDialog = null;
        petAtHomeDialog = new AppDialog(context, R.layout.dialog_two_button, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        petAtHomeDialog.getWindow().setWindowAnimations(0);
        TextView two_button_message = (TextView) petAtHomeDialog.findViewById(R.id.two_button_message);
        Button two_button_cancle = (Button) petAtHomeDialog.findViewById(R.id.two_button_cancle);
        Button two_button_confirm = (Button) petAtHomeDialog.findViewById(R.id.two_button_confirm);
        two_button_cancle.setText(cancleText);
        two_button_confirm.setText(confirmText);
        two_button_message.setText(messageString);

        two_button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (petAtHomeDialog.isShowing()) {
                    petAtHomeDialog.dismiss();
                }
                if (onCancleClickListener != null) {
                    onCancleClickListener.onClick(v);
                }
            }
        });
        two_button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (petAtHomeDialog.isShowing()) {
                    petAtHomeDialog.dismiss();
                }
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onClick(v);
                }
            }
        });

        petAtHomeDialog.setCancelable(false);
        petAtHomeDialog.setCanceledOnTouchOutside(false);
        petAtHomeDialog.show();

    }

    //宠物安全提醒
    public static void showSafeCautionDialog(final Context context, String titleString, String bodyString, String test1, String test2, String test3,
                                             final View.OnClickListener listener1, final View.OnClickListener listener2, final View.OnClickListener listener3) {
        if (!canShowDialog(context)) return;
        closeAllDialog();
        if (petAtHomeDialog != null && petAtHomeDialog.isShowing()) {
            try {
                petAtHomeDialog.dismiss();
            } catch (Exception e) {

            }
        }
        if (safeCautionDialog != null && safeCautionDialog.isShowing()) {
            try {
                safeCautionDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        safeCautionDialog = null;
        safeCautionDialog = new AppDialog(context, R.layout.dialog_three_button, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        safeCautionDialog.getWindow().setWindowAnimations(0);
        TextView tv_title = (TextView) safeCautionDialog.findViewById(R.id.tv_title);
        TextView tv_message = (TextView) safeCautionDialog.findViewById(R.id.tv_message);
        Button btn_three_one = (Button) safeCautionDialog.findViewById(R.id.btn_three_one);
        Button btn_three_two = (Button) safeCautionDialog.findViewById(R.id.btn_three_two);
        Button btn_three_three = (Button) safeCautionDialog.findViewById(R.id.btn_three_three);

        tv_title.setText(titleString);
        tv_message.setText(bodyString);
        btn_three_one.setText(test1);
        btn_three_two.setText(test2);
        btn_three_three.setText(test3);

        btn_three_one.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (safeCautionDialog.isShowing()) {
                    safeCautionDialog.dismiss();
                }
                if (listener1 != null) {
                    listener1.onClick(v);
                }
            }
        });
        btn_three_two.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (safeCautionDialog.isShowing()) {
                    safeCautionDialog.dismiss();
                }
                if (listener2 != null) {
                    listener2.onClick(v);
                }
            }
        });
        btn_three_three.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (safeCautionDialog.isShowing()) {
                    safeCautionDialog.dismiss();
                }
                if (listener3 != null) {
                    listener3.onClick(v);
                }
            }
        });
        safeCautionDialog.setCancelable(false);
        safeCautionDialog.setCanceledOnTouchOutside(false);
        safeCautionDialog.show();

    }


    /**
     * 两个按钮弹窗
     *
     * @param context
     * @param messageString
     * @param cancleText
     * @param confirmText
     * @param onCancleClickListener
     * @param onConfirmClickListener
     */
    public static void showTwoButtonDialog(final Context context, String messageString, String cancleText, String confirmText, final View.OnClickListener onCancleClickListener, final View.OnClickListener onConfirmClickListener) {
        if (!canShowDialog(context)) return;
//        closeAllDialog();
        final Dialog dialog = new AppDialog(context, R.layout.dialog_two_button, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(0);
        TextView two_button_message = (TextView) dialog.findViewById(R.id.two_button_message);
        Button two_button_cancle = (Button) dialog.findViewById(R.id.two_button_cancle);
        Button two_button_confirm = (Button) dialog.findViewById(R.id.two_button_confirm);
        two_button_cancle.setText(cancleText);
        two_button_confirm.setText(confirmText);
        two_button_message.setText(messageString);

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


    public static void showThreeButtonDialog(final Context context, String titleString, String bodyString, String test1, String test2, String test3,
                                             final View.OnClickListener listener1, final View.OnClickListener listener2, final View.OnClickListener listener3) {
        if (!canShowDialog(context)) return;
        closeAllDialog();
        final Dialog dialog = new AppDialog(context, R.layout.dialog_three_button, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.style.mystyle, Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(0);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.tv_message);
        Button btn_three_one = (Button) dialog.findViewById(R.id.btn_three_one);
        Button btn_three_two = (Button) dialog.findViewById(R.id.btn_three_two);
        Button btn_three_three = (Button) dialog.findViewById(R.id.btn_three_three);

        tv_title.setText(titleString);
        tv_message.setText(bodyString);
        btn_three_one.setText(test1);
        btn_three_two.setText(test2);
        btn_three_three.setText(test3);

        btn_three_one.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (listener1 != null) {
                    listener1.onClick(v);
                }
            }
        });
        btn_three_two.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (listener2 != null) {
                    listener2.onClick(v);
                }
            }
        });
        btn_three_three.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (listener3 != null) {
                    listener3.onClick(v);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }


}
