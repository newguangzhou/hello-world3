package com.xiaomaoqiu.old.utils;

import android.view.View;
import android.widget.TextView;
import android.util.Log;
/**
 * Created by Administrator on 2015/6/17.
 */
public class ViewHelper {
    private static String TAG="ViewHelper";

    public static void SetViewText(View v,String strText)
    {
        TextView txtView =(TextView)v;
        if(txtView!=null)
            txtView.setText(strText);
        else
            Log.e(TAG,"convert View to TextView Failed");
    }
}
