package com.xiaomaoqiu.now.view.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/5/27.
 */

public class ThreePartLineView extends View {

    private float mBorderWidth;
    private int mBorderColor;

    private Paint mPaint;

    private RectF mBounds;
    private float width;
    private float height;
    float radius;
    float smallLength;
    float largeLength;


    public ThreePartLineView(Context context) {
        super(context);
        init();
    }

    public ThreePartLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        TypedArray typedArray = context.getTheme()
//                .obtainStyledAttributes(
//                        attrs,
//                        R.styleable.CustomView
//                        , 0, 0);
//
//        try{
//            mBorderColor = typedArray.getColor(R.styleable.CustomView_border_color,0xff000000);
//            mBorderWidth = typedArray.getDimension(R.styleable.CustomView_border_width,2);
//        }finally {
//            typedArray.recycle();
//        }
        mBorderColor = 0xff000000;
        mBorderWidth = 2;

        init();
    }

    public ThreePartLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(mBorderWidth);
//        mPaint.setColor(mBorderColor);
    }

    int target=100;
    int reality=50;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBounds = new RectF(getLeft(),getTop(),getRight(),getBottom());

        width = mBounds.right - mBounds.left;
        height = mBounds.bottom - mBounds.top;

        if(width<height){
            radius = width/4;
        }else{
            radius = height/4;
        }

        smallLength =10;
        largeLength=20;
    }

    public void setData(int target,int reality){
        this.target=target;
        this.reality=reality;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int realityWidth=target==0?0:((reality*getMeasuredWidth())/target);


        mPaint.setColor(getResources().getColor(R.color.total_color_3));
        canvas.drawRect(realityWidth,0,getWidth(),getHeight(),mPaint);

        mPaint.setColor(getResources().getColor(R.color.total_color_2));
        canvas.drawRect(0,0,realityWidth,getHeight(),mPaint);
    }
}
