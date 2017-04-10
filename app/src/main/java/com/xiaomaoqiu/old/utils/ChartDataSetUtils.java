package com.xiaomaoqiu.old.utils;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineDataSet;
import com.xiaomaoqiu.pet.R;

import java.util.List;

import mbg.chartviews.CustomBarDataSet;

/**
 * Created by Administrator on 2017/1/3.
 */

public class ChartDataSetUtils {
    public static final float CIRCLE_RADIUS_BIG=4.0f;
    public static final float CIRCLE_RADIUS_HOLE=2.8f;
    public static final float CIRCLE_RADIUS_FILL=2.5f;

    public static final float LINE_WIDTH_NORMAL=2.0f;
    public static final float LINE_WIDTH_WIDE=2.5f;
    public static final float LINE_WIDTH_HIGHLIGHT_NARROW=13.0f;
    public static final float LINE_WIDTH_HIGHLIGHT_WIDE=30.0f;

    public static LineDataSet getFillLineDataSet(float lineWidth,int lineColor,int circleColor,float circleSize,float hightLightWidth,int highlightColor){
        LineDataSet dataSet=new LineDataSet(null,"");
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setColor(lineColor);
        dataSet.setLineWidth(lineWidth);
        if(0.0f == circleSize){
            dataSet.setDrawCircles(false);
        }else {
            dataSet.setCircleColor(circleColor);
            dataSet.setCircleRadius(circleSize);
            dataSet.setDrawCircleHole(false);
        }
        dataSet.setFillColor(lineColor);
        dataSet.setHighLightColor(highlightColor);
        dataSet.setDrawValues(false);//默认不绘制值
        dataSet.setDrawHorizontalHighlightIndicator(false);//默认不绘制水平线
        dataSet.setHighlightLineWidth(hightLightWidth);
        return dataSet;
    }

    public static LineDataSet getHoleLineDataSet(float lineWidth,int lineColor,int circleColor,float circleSize,float holeSize,int holeColor,float hightlightWidth,int highlightColor){
        LineDataSet dataSet=new LineDataSet(null,"");
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setColor(lineColor);
        dataSet.setCircleColor(circleColor);
        dataSet.setLineWidth(lineWidth);
        dataSet.setCircleRadius(circleSize);
        dataSet.setFillColor(lineColor);
        dataSet.setCircleColorHole(holeColor);
        dataSet.setCircleHoleRadius(holeSize);
        dataSet.setDrawCircleHole(true);
        dataSet.setHighLightColor(highlightColor);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setDrawValues(false);
        dataSet.setDrawHorizontalHighlightIndicator(false);//默认不绘制水平线
        dataSet.setHighlightLineWidth(hightlightWidth);
        return dataSet;
    }

    public static LineDataSet getDefaultBlueHoleDataSet(Context context){
        float lineWidth=LINE_WIDTH_NORMAL;
        int circleColor=context.getResources().getColor(R.color.total_color_3);
        float circleRadius=CIRCLE_RADIUS_BIG;
        float holeRadius=CIRCLE_RADIUS_HOLE;
        int holeColor=Color.WHITE;
        float hightLightWidth=LINE_WIDTH_HIGHLIGHT_WIDE;
        int highLightcolor=context.getResources().getColor(R.color.chart_normal_select_color);
        return getHoleLineDataSet(lineWidth,holeColor,circleColor,circleRadius,holeRadius,holeColor,hightLightWidth,highLightcolor);
    }

    public static LineDataSet getDefaultBlueFillDataSet(Context context){
        float lineWidth=LINE_WIDTH_NORMAL;
        int circleColor=context.getResources().getColor(R.color.total_color_3);
        float circleRadius=CIRCLE_RADIUS_FILL;
        int lineColor=Color.WHITE;
        float hightLightWidth=LINE_WIDTH_HIGHLIGHT_NARROW;
        int highLightcolor=context.getResources().getColor(R.color.chart_normal_select_color);
        return getFillLineDataSet(lineWidth,lineColor,circleColor,circleRadius,hightLightWidth,highLightcolor);
    }
    public static LineDataSet getDefaultBlueDataSetFill(Context context){
        float lineWidth=LINE_WIDTH_NORMAL;
        int lineColor=context.getResources().getColor(R.color.total_color_3_chart);
        int circleColor=context.getResources().getColor(R.color.total_color_3);
        float circleRadius=CIRCLE_RADIUS_FILL;
        float hightLightWidth=LINE_WIDTH_HIGHLIGHT_NARROW;
        int highLightcolor=context.getResources().getColor(R.color.chart_fill_select_color);
        return getFillLineDataSet(lineWidth,lineColor,circleColor,circleRadius,hightLightWidth,highLightcolor);
    }

    public static LineDataSet getDefaultRedDataSetFill(Context context){
        float lineWidth=LINE_WIDTH_NORMAL;
        int lineColor=context.getResources().getColor(R.color.total_color_2_chart);
        int circleColor=context.getResources().getColor(R.color.total_color_2);
        float circleRadius=CIRCLE_RADIUS_FILL;
        float hightLightWidth=LINE_WIDTH_HIGHLIGHT_NARROW;
        int highLightcolor=context.getResources().getColor(R.color.chart_fill_select_color);
        return getFillLineDataSet(lineWidth,lineColor,circleColor,circleRadius,hightLightWidth,highLightcolor);
    }

    public static LineDataSet getDefaultRedDataSetWithPoint(Context context){
        float lineWidth=LINE_WIDTH_WIDE;
        int circleColor=context.getResources().getColor(R.color.total_color_2);
        float circleRadius=CIRCLE_RADIUS_HOLE;
        float hightLightWidth=LINE_WIDTH_HIGHLIGHT_WIDE;
        int highLightcolor=context.getResources().getColor(R.color.chart_normal_select_color);
        return getFillLineDataSet(lineWidth,circleColor,circleColor,circleRadius,hightLightWidth,highLightcolor);
    }

    public static LineDataSet getDefaultRedDataSetWithoutPoint(Context context){
        float lineWidth=LINE_WIDTH_NORMAL;
        int circleColor=context.getResources().getColor(R.color.total_color_2);
        float circleRadius=0;
        float hightLightWidth=LINE_WIDTH_HIGHLIGHT_NARROW;
        int highLightcolor=context.getResources().getColor(R.color.chart_normal_select_color);
        return getFillLineDataSet(lineWidth,circleColor,circleColor,circleRadius,hightLightWidth,highLightcolor);
    }

    public static BarDataSet getDefaultBarDateSet(List<BarEntry> list, Context context){
        CustomBarDataSet dataSet=new CustomBarDataSet(list,"");
        int colorBlue=context.getResources().getColor(R.color.total_color_2);
        int colorRed=context.getResources().getColor(R.color.total_color_3);
        dataSet.setColors(new int[]{colorBlue,colorRed});
        dataSet.setHighLightColor(context.getResources().getColor(R.color.total_color_8));
        dataSet.setHighLightAlpha(255);
        dataSet.setHighlightEnabled(true);
        dataSet.setDrawValues(false);
        dataSet.setIsFills(new boolean[]{false,true});
        dataSet.setSlashInterval(context.getResources().getDimensionPixelSize(R.dimen.chart_bar_interval));
        dataSet.setSlashPositive(false);
        return dataSet;
    }

}
