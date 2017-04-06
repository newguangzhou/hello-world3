package com.xiaomaoqiu.old.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by simon on 15/7/22.
 */
public class WordWrapView extends ViewGroup {
    private static final int PADDING_HORZ = 10;//水平方向padding
    private static final int PADDING_VERT = 5;//垂直方向padding
    private static final int TEXT_MARGIN = 10;
    /**
     * @param context
     */
    public WordWrapView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public WordWrapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    
    /**
     * @param context
     * @param attrs
     */
    public WordWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int actualWidth = r - l - getPaddingLeft() - getPaddingRight();
        int right = r - getPaddingRight();
        int x = getPaddingLeft();// 横坐标开始
        int y = getPaddingTop();//纵坐标开始
        int lineHeight = 0;
        for(int i=0;i<childCount;i++){
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            if(width > actualWidth)
                width = actualWidth;
            lineHeight = lineHeight<height?height:lineHeight;
            if(x+width > right && width < actualWidth)
            {//next line
                x=getPaddingLeft();
                i--;
                y += lineHeight + TEXT_MARGIN;
                lineHeight = 0;
            }else
            {
                view.layout(x,y,x+width,y+height);
                x+=width + TEXT_MARGIN;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = 0;//横坐标
        int y = 0;//纵坐标
        int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int maxContentWidth = specWidth - getPaddingLeft() -getPaddingRight();//实际宽度
        int childCount = getChildCount();
        int lineHeight = 0;

        for(int index = 0;index<childCount;index++){
            View child = getChildAt(index);
            child.setPadding(PADDING_HORZ, PADDING_VERT, PADDING_HORZ, PADDING_VERT);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            if(width > maxContentWidth)
                width = maxContentWidth;

            if(x+width>maxContentWidth){//换行
                x = 0;
                y += (lineHeight + TEXT_MARGIN);
                lineHeight = 0;
                index --;
            }else
            {
                x += width + TEXT_MARGIN;
                lineHeight = lineHeight<height?height:lineHeight;
            }
        }
        y+=lineHeight;
        setMeasuredDimension(specWidth, y+getPaddingTop() + getPaddingBottom());
    }

}
