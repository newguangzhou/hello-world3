package com.xiaomaoqiu.pet.widgets;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xiaomaoqiu.pet.notificationCenter.NotificationCenter;

/**
 * Created by Administrator on 2015/6/19.
 */
public class FragmentEx extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        NotificationCenter.INSTANCE.addObserver(this);
    }

    @Override
    public void onDestroy()
    {
        NotificationCenter.INSTANCE.removeObserver(this);
        super.onDestroy();
    }
}
