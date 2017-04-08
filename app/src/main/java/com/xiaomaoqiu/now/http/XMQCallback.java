package com.xiaomaoqiu.now.http;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by long on 17/4/7.
 */

public abstract class XMQCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {

//        T t = response.body();
//        CommonJson commonJson = null;
//        if (t != null && t instanceof CommonJson) {
//            commonJson = (CommonJson) t;
//        }
//
//        //预留第一个Code值，用来异常情况下的提示。Code定义不规范，之后改。
//        if (commonJson != null && commonJson.code == 200002) {
//            if (commonJson != null && !TextUtils.isEmpty(commonJson.msg)) {
//                ToastUtil.showShortToast(commonJson.msg);
//            }
//            return;
//        }
//
//        if (response.code() >= 200 && response.code() < 300) {
//            if (commonJson != null) {
//                commonJson.exchange();
//                if (!TextUtils.isEmpty(commonJson.extion)) {
//                    Constants.API_EXTION = commonJson.extion;
//                }
//                if (HttpErrorCodeManager.codeAnalyse(commonJson.code,commonJson.message())) {
//                    return;
//                }
//
//                if (Switch.CHINAHR == 2||Switch.CHINAHR == 3) {
//                    //上线状态下：此处加了统一对网络请求的异常捕获，不让用户崩溃。然后上传异常信息到bugly。
//                    try {
//                        onSuccess(response, (T) commonJson.getBody());
//                    } catch (Exception e) {
//                        CrashReport.postCatchedException(e);
//                        ToastUtil.showShortToast("操作失败，重新操作试试吧！");
//                        LogUtil.i("lz", "CHrCallBack 的onSuccess出错。");
//                    }
//                } else {
//                    onSuccess(response, (T) commonJson.getBody());
//                }
//
//                return;
//            }
//        }
//        onFail(call, null);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFail(call, t);
    }

    public abstract void onSuccess(Response<T> response, T commJson);

    public abstract void onFail(Call<T> call, Throwable t);
}
