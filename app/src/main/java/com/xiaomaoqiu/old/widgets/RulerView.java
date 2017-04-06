package com.xiaomaoqiu.old.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/4/20.
 */
public class RulerView extends ViewGroup {

    public RulerView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int actualWidth = r - l - getPaddingLeft() - getPaddingRight();
        int x = getPaddingLeft();// 横坐标开始
        int y = getPaddingTop();//纵坐标开始

        if(childCount<2) return;

        int stepWid = actualWidth/(childCount-1);

        for(int i=0;i<childCount;i++){
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();

            int x1 = x;
            if(i>0)
            {
                if(i<childCount-1) x1 -= width/2;
                else x1 -= width;
            }
            view.layout(x1,y,x1+width,y+height);
            x += stepWid;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int specHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        int lineHeight = specHeight - getPaddingTop() - getPaddingBottom();

        for(int index = 0;index<childCount;index++){
            View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int height = child.getMeasuredHeight();
            lineHeight = Math.max(lineHeight,height);
        }
        setMeasuredDimension(specWidth, lineHeight +getPaddingTop() + getPaddingBottom());
    }
}
