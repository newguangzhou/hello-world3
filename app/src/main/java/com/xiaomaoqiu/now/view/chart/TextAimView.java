package com.xiaomaoqiu.now.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.util.DensityUtil;

/**
 * Created by long on 2017/5/27.
 */

public class TextAimView extends View {

    public TextAimView(Context context) {
        super(context);
        init();
    }

    public TextAimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextAimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0x66555555);
        mPaint.setTextSize(DensityUtil.dip2px(PetAppLike.mcontext,12));
    }

    int aim =0;
    String aimString;
    String totalString;
    private Paint mPaint;



    public void setAim(String aimString,String totalString,int aim) {
        this.aimString=aimString;
        this.totalString=totalString;
        this.aim = aim;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(TextUtils.isEmpty(aimString)||TextUtils.isEmpty(totalString)||("0".equals(totalString))){
            return;
        }
        canvas.drawText("0",0,40,mPaint);
        canvas.drawText(aimString,aim,40,mPaint);
        canvas.drawText(totalString,getWidth()-100,40,mPaint);
    }
}
