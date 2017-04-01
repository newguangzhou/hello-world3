package mbg.chartviews;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

/**
 * Created by Administrator on 2017/1/8.
 */

public class BorderHightBarChart extends BarChart {


    public BorderHightBarChart(Context context) {
        this(context,null);
    }

    public BorderHightBarChart(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public BorderHightBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer=new CustomBarRender(this,mAnimator,mViewPortHandler);
    }

    public void setHighlightBorderWidth(float width){
        if(null != mRenderer){
            ((CustomBarRender)mRenderer).setBorderWidth(width);
        }
    }
}
