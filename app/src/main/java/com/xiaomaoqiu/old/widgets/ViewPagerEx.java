package com.xiaomaoqiu.old.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/6/17.
 */
public class ViewPagerEx extends ViewPager {
    private boolean m_bTouchable = false;

    public ViewPagerEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTouchable(boolean bTouchable)
    {
        m_bTouchable=bTouchable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        if (m_bTouchable) {
            return super.onTouchEvent(arg0);
        } else {
            return false;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        if (m_bTouchable) {
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }

    }
}
