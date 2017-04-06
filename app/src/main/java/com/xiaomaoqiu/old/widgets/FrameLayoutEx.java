package com.xiaomaoqiu.old.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.xiaomaoqiu.old.R;

/**
 * Created by Administrator on 2015/6/17.
 */
public class FrameLayoutEx extends FrameLayout {
    private Animation slideLeftIn;// 从屏幕左边进来
    private Animation slideLeftOut;// 从屏幕左边出去
    private Animation slideRightIn;// 从屏幕右边进来
    private Animation slideRightOut;// 从屏幕右边出去


    public FrameLayoutEx(Context context, AttributeSet attrs)
    {
        super(context,attrs);

        slideLeftIn = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
    }

    public void switchFrame(int iFrom,int iTo)
    {
        View vFrom = findViewById(iFrom);
        View vTo   = findViewById(iTo);
        if(vFrom!=null && vTo != null)
        {
            if(iFrom < iTo)
            {
                vFrom.startAnimation(slideLeftOut);
                vTo.startAnimation(slideRightIn);
            }else
            {
                vFrom.startAnimation(slideRightOut);
                vTo.startAnimation(slideLeftIn);
            }
            vTo.bringToFront();
        }
    }

}
