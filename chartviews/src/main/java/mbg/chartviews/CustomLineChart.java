package mbg.chartviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/2.
 */

public class CustomLineChart extends CustomCombineChart {


    private boolean isFill;
    private ArrayList<ILineDataSet> lineDataSets;

    public CustomLineChart(Context context) {
        this(context,null);
    }

    public CustomLineChart(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public CustomLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr,R.layout.linechart);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.CustomLineChart);
        isFill=typedArray.getBoolean(R.styleable.CustomLineChart_isFill,false);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        lineDataSets=new ArrayList<>();
    }

    @Override
    protected void initChart() {
        mChart=(LineChart)findViewById(R.id.combined_chart);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(null == listener){
                    return;
                }
                if(xAxisData == null || xAxisData.size() == 0){
                    return;
                }
                if(lineDataSets == null || lineDataSets.size() == 0){
                    return;
                }
                int index=0;
                ArrayList<Float> values=new ArrayList<Float>();
                for(ILineDataSet dataSet : lineDataSets){
                   List<Entry> entries= dataSet.getEntriesForXValue(e.getX());
                    if(entries == null || entries.size() < 1)
                        continue;
                    index =dataSet.getEntryIndex(entries.get(0));
                    values.add(entries.get(0).getY());
                }
                index = index >= xAxisData.size() ? xAxisData.size()-1 : index;
                listener.onChartValueSelect(index,xAxisData.get(index),values);
            }

            @Override
            public void onNothingSelected() {
                if(null != listener){
                    listener.onCancleSelect();
                }
            }
        });
    }

    public void addData(LineDataSet data){
        if(null == data || data.getValues() == null){
            return;
        }
        for(Entry avalue : data.getValues()){
            avalue.setX(avalue.getX()+getInnerPaddingLeft());
        }
        data.setDrawFilled(isFill);
        if(isFill) {
            data.setFillAlpha(255);
            data.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        }
        data.calcMinMax();
        lineDataSets.add(data);
    }

    @Override
    public void drawChart(){
        super.drawChart();
        LineData data=new LineData(lineDataSets);
        mChart.getXAxis().setAxisMaximum(data.getXMax()+getInnerPaddingRight());
        mChart.setData(data);
        mChart.invalidate();
        setXaixs();
    }

    public void drawChartWithAnimate(){
        LineData data=new LineData(lineDataSets);
        mChart.setData(data);
        mChart.animateY(2000);
    }


}
