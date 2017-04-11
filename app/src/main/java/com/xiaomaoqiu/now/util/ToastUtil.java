package com.xiaomaoqiu.now.util;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.xiaomaoqiu.now.PetAppLike;

/**
 * Created by long on 2017/4/11.
 */
@SuppressLint("WrongConstant")
public class ToastUtil {
    public static void showTost(String msg){
        Toast.makeText(PetAppLike.mcontext,msg,Toast.LENGTH_SHORT).show();
    }
    public static void showNetError(){
        Toast.makeText(PetAppLike.mcontext,"网络错误，请稍后重试",Toast.LENGTH_SHORT).show();
    }
}
