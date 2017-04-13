package com.xiaomaoqiu.now.http;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomaoqiu.now.Environment;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.ExitDialog_RAW_Activity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaomaoqiu.now.http.HttpCode.EC_PET_NOT_EXIST;

/**
 * Created by long on 17/4/7.
 */
@SuppressLint("WrongConstant")
public abstract class XMQCallback<T extends BaseBean> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() >= 200 && response.code() < 300) {
            T message = response.body();
            if (message != null) {

                //如果宠物不存在了，直接退到添加宠物页面
                HttpCode ret = HttpCode.valueOf(message.status);
                if (EC_PET_NOT_EXIST == ret) {
                    onFail(call, null);
                    Intent intent = new Intent(PetAppLike.mcontext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PetAppLike.mcontext.startActivity(intent);
                    ToastUtil.showTost("请先填写您的宠物信息");
                    return;
                }

                if (PetAppLike.environment == Environment.Release) {
                    //上线状态下：此处加了统一对网络请求的异常捕获，不让用户崩溃。然后上传异常信息到bugly。
                    try {
                        onSuccess(response, message);
                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                        Toast.makeText(PetAppLike.mcontext, "**服务器小哥去看片了，稍等一下**", Toast.LENGTH_SHORT).show();
                        Log.e(PetAppLike.TAG, "callback出错了" + e.getMessage());
                    }
                } else {
                    onSuccess(response, message);
                }
                return;
            }
        }
        ToastUtil.showNetError();
        onFail(call, null);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFail(call, t);
    }

    public abstract void onSuccess(Response<T> response, T message);

    public abstract void onFail(Call<T> call, Throwable t);
}
