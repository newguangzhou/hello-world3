package mbg.chartviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/2.
 */

public abstract class CustomCombineChart extends RelativeLayout {

    public static final int LINE_SOLID=0;
    public static final int LINE_DOTTED=1;
    /**
     * yAxis
     */
    private float yAxisTextSize;
    private int yAxisTextColor;
    private boolean isYaxisShow;
    private int axisColor;
    private float axisWidth;
    private float yAxisMaxValue;
    private String yAxisFormat="";
    private boolean isOnlyMaxShowFormat;
    private int yAxisLabelCount;
    private int yAxisWidth;
    /**
     * xAxis
     */
    protected float xAxisTextSize,xAxis2TextSize;
    protected int xAxisTextColor,xAxis2TextColor;
    private int xAxisInnerpadding;
    private boolean isXaxisShow;

    private int lineMode;
    protected float innerPaddingLeft,innerPaddingRight;

    protected BarLineChartBase mChart;

    protected LinearLayout yAxisContainer;
    protected LinearLayout xAixsContainser,xAxisFristContainer,xAxisSecContainer;
    protected View xAixsSecView;
    protected TextView xAxisSecText;


    /**
     * datas
     */
    protected ArrayList<String> xAxisData;
    protected onChartValueSelectListener listener;

    public void setOnChartValueSelectListener(onChartValueSelectListener listener){
        this.listener=listener;
    }

    public CustomCombineChart(Context context) {
        this(context,null);
    }

    public CustomCombineChart(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public CustomCombineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,R.layout.linechart);
    }

    public CustomCombineChart(Context context, AttributeSet attrs, int defStyleAttr,int Rid){
        super(context, attrs, defStyleAttr);
        initValues(context,attrs);
        LayoutInflater.from(context).inflate(Rid,this,true);
    }
    private void initValues(Context context,AttributeSet attrs){
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.CustomCombineChart);
        yAxisTextSize=ta.getDimensionPixelSize(R.styleable.CustomCombineChart_yAxis_textSize,10);
        yAxisTextColor=ta.getColor(R.styleable.CustomCombineChart_yAxis_textColor, Color.GRAY);
        xAxisTextSize=ta.getDimensionPixelSize(R.styleable.CustomCombineChart_xAxis_textSize,15);
        xAxisTextColor=ta.getColor(R.styleable.CustomCombineChart_xAxis_textColor, Color.GRAY);
        xAxis2TextSize=ta.getDimensionPixelSize(R.styleable.CustomCombineChart_xAxis_sec_textSize,10);
        xAxis2TextColor=ta.getColor(R.styleable.CustomCombineChart_xAxis_sec_textColor, Color.GRAY);
        xAxisInnerpadding=ta.getDimensionPixelSize(R.styleable.CustomCombineChart_xAxis_inner_padding,0);
        lineMode=ta.getInt(R.styleable.CustomCombineChart_line_mode,LINE_SOLID);
        isXaxisShow=ta.getBoolean(R.styleable.CustomCombineChart_xAxis_show,true);
        isYaxisShow=ta.getBoolean(R.styleable.CustomCombineChart_yAxis_show,true);
        axisColor=ta.getColor(R.styleable.CustomCombineChart_axisColor,Color.GRAY);
        axisWidth=px2dip(context,ta.getDimensionPixelSize(R.styleable.CustomCombineChart_axis_width,2));
        yAxisMaxValue=ta.getFloat(R.styleable.CustomCombineChart_yAxis_maxValue,0.0f);
        yAxisFormat=ta.getString(R.styleable.CustomCombineChart_yAxis_format);
        isOnlyMaxShowFormat=ta.getBoolean(R.styleable.CustomCombineChart_yAxis_format_only_top,false);
        yAxisLabelCount=ta.getInt(R.styleable.CustomCombineChart_yAxis_label_count,3);
        yAxisWidth=ta.getDimensionPixelSize(R.styleable.CustomCombineChart_yAxis_width,0);
        innerPaddingLeft=ta.getFloat(R.styleable.CustomCombineChart_innerPaddingLeftValue,0.f);
        innerPaddingRight=ta.getFloat(R.styleable.CustomCombineChart_innerPaddingRightValue,0.f);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initYaixs();
        initXaixs();
        //mChart=(CombinedChart)findViewById(R.id.combined_chart);
        initChart();
        invalidateChart();
    }

    public float getInnerPaddingLeft(){
        return innerPaddingLeft;
    }

    public float getInnerPaddingRight(){
        return innerPaddingRight;
    }

    protected abstract void initChart();

    /**
     * 初始化图表
     */
    private void invalidateChart(){
        RelativeLayout.LayoutParams params= (LayoutParams) mChart.getLayoutParams();
        params.leftMargin=yAxisWidth;
        mChart.setLayoutParams(params);
        /**
         * 禁止所有放大缩小和拖动事件
         */
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        /**
         * x轴
         */
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setGridColor(axisColor);
        xAxis.setGridLineWidth(axisWidth);
        xAxis.setDrawAxisLine(false);//不显示坐标线
        xAxis.setDrawGridLines(isXaxisShow);
        if(isXaxisShow && lineMode == LINE_DOTTED){
            xAxis.enableGridDashedLine(10,10,0);
        }
        xAxis.setDrawLabels(false);
        xAxis.setAxisMinimum(0f);//留待考察
//        xAxis.setSpaceMin(0.f);
        /**
         * y轴
         */
        YAxis yAxis = mChart.getAxisLeft();//右Y坐标轴
        yAxis.setGridLineWidth(axisWidth);
        yAxis.setGridColor(axisColor);
        yAxis.setDrawGridLines(isYaxisShow);//是否绘制
        if(isYaxisShow && LINE_DOTTED == lineMode){
            yAxis.enableGridDashedLine(10,10,0);
        }
        yAxis.setDrawAxisLine(false);//不绘制坐标线
        yAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        yAxis.setLabelCount(yAxisLabelCount);
        yAxis.setAxisMaximum(yAxisMaxValue);
        yAxis.setDrawLabels(false);
        mChart.getAxisRight().setEnabled(false);

        //初始化数据
        xAxisData=new ArrayList<>();
    }

    /**
     * 设置x轴标签
     * @param labels
     */
    public void setXAxisLabels(ArrayList<String> labels){
        if(null == labels || labels.size() == 0){
            return;
        }
        xAxisData=labels;
        for(int i=0 ; i<labels.size()-1;i++){
            createXaixsLable(labels.get(i),false);
        }
        createXaixsLable(labels.get(labels.size()-1),true);
    }

    protected void setXaixs(){
        if(null == xAxisData || xAxisData.size() == 0){
            return;
        }
        mChart.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mChart.getViewTreeObserver().removeOnPreDrawListener(this);
                int width=mChart.getWidth();
                LayoutParams params= (LayoutParams) xAixsContainser.getLayoutParams();
                int leftPadding= (int) (width*getInnerPaddingLeft()/mChart.getXChartMax());
                int rightPadding= (int) (width*getInnerPaddingRight()/mChart.getXChartMax());
                params.leftMargin+=leftPadding;
                params.rightMargin+=rightPadding;
                xAixsContainser.setLayoutParams(params);
                return false;
            }
        });
    }

    public void drawChart(){
    }

    /**
     * 设置x轴二级标签数据
     * @param text
     * @param index
     */
    public void setSecondXAis(String text,int index,int totalSize){
        if(TextUtils.isEmpty(text) || index<0){
            return;
        }
        LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) xAixsSecView.getLayoutParams();
        leftParams.weight = totalSize-index;
        xAixsSecView.setLayoutParams(leftParams);
        LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) xAxisSecText.getLayoutParams();
        rightParams.weight = index;
        xAxisSecText.setLayoutParams(rightParams);
        xAxisSecText.setText(text);
        //secXaxisLabel=text;
        //secXaxisIndex = index;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return  (pxValue / scale + 0.5f);
    }

    private void initXaixs(){
        /**
         * x坐标系父LinearLayout
         */
        xAixsContainser=(LinearLayout)findViewById(R.id.xaxis_container);
        LayoutParams parentParams= (LayoutParams) xAixsContainser.getLayoutParams();
        parentParams.leftMargin+=yAxisWidth;
        xAixsContainser.setLayoutParams(parentParams);
        xAxisFristContainer=(LinearLayout)findViewById(R.id.first_xaxis_container);
        xAxisSecContainer=(LinearLayout)findViewById(R.id.secondx_container);
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) xAxisSecContainer.getLayoutParams();
        params.topMargin=xAxisInnerpadding;
        xAxisSecContainer.setLayoutParams(params);
        xAixsSecView=findViewById(R.id.leftview);
        xAxisSecText=(TextView)findViewById(R.id.righttext);
        xAxisSecText.setTextColor(xAxis2TextColor);
        xAxisSecText.setTextSize(TypedValue.COMPLEX_UNIT_PX,xAxis2TextSize);
    }

    private void initYaixs(){
        yAxisContainer=(LinearLayout)findViewById(R.id.yaixs_container);
        int perValue= (int) (yAxisMaxValue/yAxisLabelCount);
        TextView topTextView=createYaxisLable(false);
        topTextView.setText(perValue*yAxisLabelCount+yAxisFormat);
        for(int i=yAxisLabelCount-1; i>0 ;i--){
            TextView textView=createYaxisLable(false);
            if(isOnlyMaxShowFormat){
                textView.setText(perValue*i+"");
            }else{
                textView.setText(perValue*i+yAxisFormat);
            }
        }
        TextView textView=createYaxisLable(true);
        if(isOnlyMaxShowFormat) {
            textView.setText(0 + "");
        }else{
            textView.setText(0+yAxisFormat);
        }
    }

    private TextView createYaxisLable(boolean isBottom){
        TextView textView=new TextView(getContext());
        LinearLayout.LayoutParams textParam=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textParam);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,yAxisTextSize);
        textView.setTextColor(yAxisTextColor);
        yAxisContainer.addView(textView);

        if(!isBottom){
            View view=new View(getContext());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,0);
            params.weight=1;
            view.setLayoutParams(params);
            yAxisContainer.addView(view);
        }
        return textView;
    }


    protected void createXaixsLable(String text,boolean isLast){
        TextView textView=new TextView(getContext());
        LinearLayout.LayoutParams textParam=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        /*if(!isLast){
            textParam.width=0;
            textParam.weight=1;
        }*/
        textParam.weight=1;
        textView.setLayoutParams(textParam);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,xAxisTextSize);
        textView.setTextColor(xAxisTextColor);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        xAxisFristContainer.addView(textView);/*
        if(isLast){
            return;
        }
        View view=new View(getContext());
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,0);
        params.weight=1;
        view.setLayoutParams(params);
        xAxisFristContainer.addView(view);*/
    }




}
