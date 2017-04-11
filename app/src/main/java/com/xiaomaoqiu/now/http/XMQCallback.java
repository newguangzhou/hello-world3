package com.xiaomaoqiu.now.http;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomaoqiu.now.Environment;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.util.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
