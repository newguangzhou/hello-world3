package com.xiaomaoqiu.old.widgets;

/**
 * Created by simon on 15/7/27.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xiaomaoqiu.pet.R;


/**
 * 自定义的圆角矩形ImageView，可以直接当组件在布局中使用。
 * @author caizhiming
 *
 */
public class RoundRectImageView extends ImageView{

    private int   m_cornerSize;
    Bitmap  m_bmpOrg;

    public RoundRectImageView(Context context) {
        this(context,null);
    }

    public RoundRectImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundRectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundRectImageView, defStyle, 0);
        m_cornerSize = a.getInt(R.styleable.RoundRectImageView_cornerSize,5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w,h,oldw,oldh);
        if(m_bmpOrg!=null)
        {
            Bitmap bmpRound = getRoundedCornerBitmap(m_bmpOrg,m_cornerSize,getWidth(),getHeight());
            super.setImageBitmap(bmpRound);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap)
    {
        m_bmpOrg = bitmap;
        if(getWidth()!=0 && getHeight()!=0) {
            Bitmap bmpRound = getRoundedCornerBitmap(bitmap, m_cornerSize, getWidth(), getHeight());
            super.setImageBitmap(bmpRound);
        }
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap,  int roundPixels, int width, int height) {
        if(width == 0 || height == 0)
        {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        final Rect destRect=new Rect(0,0,width,height);
        final RectF destRectF = new RectF(destRect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFF000000);
        canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

        return output;
    }
}