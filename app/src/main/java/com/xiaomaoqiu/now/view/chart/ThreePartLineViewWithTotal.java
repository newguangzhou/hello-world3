package com.xiaomaoqiu.now.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/5/27.
 */

public class ThreePartLineViewWithTotal extends View {

    private Paint mPaint;

    private RectF mBounds;
    private float width;
    private float height;
    float radius;
    float smallLength;
    float largeLength;


    public ThreePartLineViewWithTotal(Context context) {
        super(context);
        init();
    }

    public ThreePartLineViewWithTotal(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThreePartLineViewWithTotal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    int deep;
    int light;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBounds = new RectF(getLeft(), getTop(), getRight(), getBottom());

        width = mBounds.right - mBounds.left;
        height = mBounds.bottom - mBounds.top;

        if (width < height) {
            radius = width / 4;
        } else {
            radius = height / 4;
        }

        smallLength = 10;
        largeLength = 20;
    }

    public void setData(int deep, int light) {
        this.deep = deep;
        this.light = light;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int lightWidth = ((light * getMeasuredWidth()) / 16);
        int deepWidth = (((light + deep) * getMeasuredWidth()) / 16);

        mPaint.setColor(getResources().getColor(R.color.total_color_2));
        canvas.drawRect(lightWidth, 0, deepWidth, getHeight(), mPaint);

        mPaint.setColor(getResources().getColor(R.color.total_color_3));
        canvas.drawRect(0, 0, lightWidth, getHeight(), mPaint);
    }
}
