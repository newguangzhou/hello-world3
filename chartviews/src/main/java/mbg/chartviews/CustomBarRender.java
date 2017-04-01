package mbg.chartviews;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Region;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Created by Administrator on 2017/1/8.
 */

public class CustomBarRender extends BarChartRenderer {

    public CustomBarRender(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        mBorderRec=new RectF();
    }



    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();

        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            //BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
            List<BarEntry> entries=set.getEntriesForXValue(high.getX());
            float maxY=0;
            for(BarEntry entry: entries){
                maxY = maxY > entry.getY() ? maxY:entry.getY();
            }
            BarEntry e=new BarEntry(high.getX(),maxY);

            if (!isInBoundsX(e, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            boolean isStack = (high.getStackIndex() >= 0  && e.isStacked()) ? true : false;

            final float y1;
            final float y2;

            if (isStack) {

                if(mChart.isHighlightFullBarEnabled()) {

                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();

                } else {

                    Range range = e.getRanges()[high.getStackIndex()];

                    y1 = range.from;
                    y2 = range.to;
                }

            } else {
                y1 = e.getY();
                y2 = 0.f;
            }

            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);
            setHighlightDrawPos(high, mBarRect);
            prepareBarHighLightBorder();

            c.clipRect(mBorderRec);
            c.clipRect(mBarRect, Region.Op.DIFFERENCE);
            c.drawRect(mBorderRec, mHighlightPaint);
            //c.drawRect(mBarRect, mHighlightPaint);
        }
    }

    private void prepareBarHighLightBorder(){
        float left=mBarRect.left-highLightBorderWidth;
        float right=mBarRect.right+highLightBorderWidth;
        float top=mBarRect.top-highLightBorderWidth;
        float bottom=mBarRect.bottom;
        mBorderRec.set(left,top,right,bottom);
    }

    private RectF mBorderRec;

    private float highLightBorderWidth=0;
    public void setBorderWidth(float width){
        highLightBorderWidth=width;
    }


    private RectF mBarShadowRectBuffer = new RectF();
    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int)(Math.ceil((float)(dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        final boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }
            if(dataSet instanceof CustomBarDataSet){
                if(((CustomBarDataSet) dataSet).isFill(j/4)){
                    int degree=((CustomBarDataSet) dataSet).getSlashDegree();
                    float interval=((CustomBarDataSet) dataSet).getSlashInterval();
                    drawSlash(c,buffer.buffer[j],buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3],interval,degree);
                }else{
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint);
                }
            }else{
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }

    /**
     * 画斜线图
     */
    private void drawSlash(Canvas c,float left,float top,float right,float bottom,float inerval,int degree){
        RectF innerRect=new RectF(left,top,right,bottom);
        float outerSideLength= (float) ((innerRect.width()+innerRect.height())/2*Math.sqrt(2));
        float centerX=innerRect.centerX();
        float centerY=innerRect.centerY();
        float outerLeft=centerX-(outerSideLength/2);
        float outerRight=centerX+(outerSideLength/2);
        float outerTop=centerY-(outerSideLength/2);
        float outerBottom=centerY+outerSideLength/2;
        c.clipRect(innerRect);
        c.rotate(degree,centerX,centerY);
        for(float y=outerBottom;y>=outerTop;y-=inerval*2){
            float topy=y-inerval;
            RectF clipRect=new RectF(outerLeft,topy,outerRight,y);
            c.clipRect(clipRect, Region.Op.DIFFERENCE);
        }
        c.rotate(-degree,centerX,centerY);
        c.drawRect(innerRect,mRenderPaint);
        c.restore();
        c.save();
    }
}
