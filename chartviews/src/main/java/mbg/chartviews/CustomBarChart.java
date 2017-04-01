package mbg.chartviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/8.
 */

public class CustomBarChart extends CustomCombineChart {

    private IBarDataSet dataSets;
    private int highlightWidth;
    public CustomBarChart(Context context) {
        this(context,null);
    }

    public CustomBarChart(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public CustomBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr,R.layout.barchart);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.CustomBarChart);
        highlightWidth=typedArray.getDimensionPixelSize(R.styleable.CustomBarChart_hightlightWidth,20);
        typedArray.recycle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    protected void initChart() {
        mChart=(BorderHightBarChart)findViewById(R.id.bar_chart);
        ((BorderHightBarChart)mChart).setHighlightBorderWidth(highlightWidth);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(null == listener){
                    return;
                }
                if(xAxisData == null || xAxisData.size() <= 0){
                    return;
                }
                if(!(e instanceof BarEntry) || dataSets == null){
                    return;
                }
                int index=dataSets.getEntryIndex((BarEntry) e);
                index = index >= xAxisData.size() ? xAxisData.size()-1 : index;
                float[] values=((BarEntry)e).getYVals();
                ArrayList<Float> yValues=new ArrayList<Float>();
                if(null != values && values.length > 0){
                    for(int i=0;i<values.length;i++){
                        yValues.add(values[i]);
                    }
                }
                listener.onChartValueSelect(index,xAxisData.get(index),yValues);
            }

            @Override
            public void onNothingSelected() {
                if(null != listener){
                    listener.onCancleSelect();
                }
            }
        });
    }


    public void setData(BarDataSet dataSet){
        if(null == dataSet || dataSet.getValues() == null){
            return;
        }
        for(BarEntry entry : dataSet.getValues()){
            entry.setX(entry.getX()+getInnerPaddingLeft());
        }
        dataSet.calcMinMax();
        this.dataSets=dataSet;
    }



    @Override
    public void drawChart() {
        super.drawChart();
        BarData data=new BarData(dataSets);
        data.setBarWidth(0.5f);
        mChart.getXAxis().setAxisMaximum(data.getXMax()+getInnerPaddingRight());
        mChart.setData(data);
        mChart.invalidate();
        setXaixs();
    }

    @Override
    protected void createXaixsLable(String text,boolean isLast){
        TextView textView=new TextView(getContext());
        LinearLayout.LayoutParams textParam=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParam.weight=1;
        textView.setLayoutParams(textParam);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,xAxisTextSize);
        textView.setTextColor(xAxisTextColor);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        xAxisFristContainer.addView(textView);
        if(isLast){
            return;
        }
        View view=new View(getContext());
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,0);
        params.weight=1;
        view.setLayoutParams(params);
        xAxisFristContainer.addView(view);
    }

}
