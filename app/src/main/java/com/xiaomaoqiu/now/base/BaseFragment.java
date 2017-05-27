package com.xiaomaoqiu.now.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * Created by Administrator on 2015/6/19.
 */
public class BaseFragment extends Fragment {

    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity= (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        NotificationCenter.INSTANCE.addObserver(this);
    }

    @Override
    public void onDestroy()
    {
//        NotificationCenter.INSTANCE.removeObserver(this);
        super.onDestroy();
    }
}
