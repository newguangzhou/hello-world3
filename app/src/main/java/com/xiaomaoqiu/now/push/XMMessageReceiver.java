package com.xiaomaoqiu.now.push;

import android.content.Context;
import android.text.TextUtils;


import com.xiaomaoqiu.now.util.LogUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by long on 17/5/1.
 */

public class XMMessageReceiver extends PushMessageReceiver {
    public static final String TAG = "xmpush";
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;
    //通透消息到达
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        LogUtil.e(TAG, mMessage);
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias=message.getAlias();
        } else if(!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount=message.getUserAccount();
        }
    }
    //消息推送被点击
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        dealReport(mMessage,PUSHACTION_OPEN);
        LogUtil.e(TAG,mMessage);
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias=message.getAlias();
        } else if(!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount=message.getUserAccount();
        }
//        String url = getTargetUrl();
//        com.chinahr.android.common.instance.UrlManager.getInstance().filterPushUrl(context, url, false);
    }
    //通知消息到达
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        dealReport(mMessage,PUSHACTION_RECEIVE);
        LogUtil.e(TAG,mMessage);
        if(!TextUtils.isEmpty(message.getTopic())) {
            mTopic=message.getTopic();
        } else if(!TextUtils.isEmpty(message.getAlias())) {
            mAlias=message.getAlias();
        } else if(!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount=message.getUserAccount();
        }
//        setRedPoint(context);
    }
    //命令结果
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        LogUtil.e(TAG,command);
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                LogUtil.e(TAG,mRegId);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                if(XMPushManager.getOnRegisterResult()!=null){
                    XMPushManager.getOnRegisterResult().onSuccess();
                }
            }else{
                if(XMPushManager.getOnRegisterResult()!=null){
                    XMPushManager.getOnRegisterResult().onFailed();
                }
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }
    //注册结果回调
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        LogUtil.e(TAG,command);
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }
    }

    //解析服务端返回的url
    private String getTargetUrl(){
        String url = "";
        try {
            JSONObject jsonObject = new JSONObject(mMessage);
            JSONObject messageObject = new JSONObject(jsonObject.optString("msg"));
            url = messageObject.optString("url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private static int PUSHACTION_RECEIVE = 1;//收到消息
    private static int PUSHACTION_OPEN = 2;//点击消息，打开通知。

//    /**
//     * 设置红点
//     */
//    private void setRedPoint(Context context){
//        try {
//            JSONObject jsonObject = new JSONObject(mMessage);
//            JSONObject messageObject = new JSONObject(jsonObject.optString("msg"));
//            String url = messageObject.optString("url");
//            String type = messageObject.optString("type");
//            String appPushId = messageObject.optString("appPushId");
//            reoprtToServer(Integer.parseInt(type),appPushId,PUSHACTION_RECEIVE);
//            if(TextUtils.isEmpty(type))return;
//            if(!TextUtils.isEmpty(url)){
//                PushRedPointManager.addRedPoint(type, 1);
//                new PushRedPointBrocastReceiver().sendRedPointReceiver(context,type);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 处理统计
     * @param messageContent
     */
    public void dealReport(String messageContent,int action){
        try {
            JSONObject jsonObject = new JSONObject(messageContent);
            JSONObject messageObject = new JSONObject(jsonObject.optString("msg"));
            String type = messageObject.optString("type");
            String appPushId = messageObject.optString("appPushId");
//            reoprtToServer(Integer.parseInt(type),appPushId,action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 接收到消息以及点击消息的时候上报。
//     * @param type
//     * @param appPushId
//     * @param action
//     */
//    public void reoprtToServer(int type,String appPushId,int action){
//        ApiUtils.getAppConfigService().clickPush(appPushId,action,type,0).enqueue(new ChinaHrCallBack<CommonJson>() {
//            @Override
//            public void onSuccess(Call<CommonJson> call, Response<CommonJson> response) {
//                LogUtil.i("lz","reoprtToServer,onSuccess");
//            }
//
//            @Override
//            public void onFail(Call<CommonJson> call, Throwable t) {
//                LogUtil.i("lz","reoprtToServer,onFail");
//            }
//        });
//    }

}
