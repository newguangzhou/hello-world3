package com.dacer.androidcharts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Dacer on 11/4/13.
 * Edited by Lee youngchan 21/1/14
 * Edited by dector 30-Jun-2014
 */
public class LineView extends View {
    private int mViewHeight = 0;
    private int bottomInterval;//底部X轴坐标间隔
    //drawBackground
    private boolean autoSetDataOfGird = true;
    private boolean autoSetGridWidth = true;
    private int totalGridTextNum;//竖直文字显示总数
    private String verticalUnit="";//竖直方向单位
    private boolean isPerVerticalUnitShow;//是否所有竖直单位都显示
    private int horizonalTrans;//水平方向坐标偏移，因为有坐标系存在

    private int dataOfAGird = 10;
    private int bottomTextHeight = 0;
    private ArrayList<SecondBottomBean> secondBottomTextList=new ArrayList<SecondBottomBean>();
    private ArrayList<String> bottomTextList = new ArrayList<String>();
    
    private ArrayList<ArrayList<Double>> dataLists;
    
    private ArrayList<Integer> xCoordinateList = new ArrayList<Integer>();
    private ArrayList<Integer> yCoordinateList = new ArrayList<Integer>();
    
    private ArrayList<ArrayList<Dot>> drawDotLists = new ArrayList<ArrayList<Dot>>();

    private Paint bottomTextPaint = new Paint();
    private Paint seconBottomTextPaint=new Paint();
    private int bottomTextDescent=0;

    //popup
    private Paint popupTextPaint = new Paint();
    private final int bottomTriangleHeight = 12;
    public boolean showPopup = true; 

	private Dot pointToSelect;
	private Dot selectedDot;

    private int topLineLength = MyUtils.dip2px(getContext(), 12);; // | | ←this
                                                                   //-+-+-
    private int sideLineLength = MyUtils.dip2px(getContext(),20)/3*2;// --+--+--+--+--+--+--
                                                                     //  ↑this
    private int defGridWidth = MyUtils.dip2px(getContext(),45);
    private int backgroundGridWidth = defGridWidth;
    //Constants
    private final int popupTopPadding = MyUtils.dip2px(getContext(),2);
    private final int popupBottomMargin = MyUtils.dip2px(getContext(), 5);
    private final int bottomTextTopMargin = MyUtils.sp2px(getContext(),5);
    private final int bottomLineLength = MyUtils.sp2px(getContext(), 22);
    private final int DOT_INNER_CIR_RADIUS = MyUtils.dip2px(getContext(), 3);
    private final int DOT_OUTER_CIR_RADIUS = MyUtils.dip2px(getContext(),5);
    private final int MIN_TOP_LINE_LENGTH = MyUtils.dip2px(getContext(),12);
    private final int MIN_VERTICAL_GRID_NUM = 4;
    private final int MIN_HORIZONTAL_GRID_NUM = 1;
    private final int BACKGROUND_LINE_COLOR = Color.parseColor("#EEEEEE");
    private final int BOTTOM_TEXT_COLOR = Color.parseColor("#9B9A9B");

    private int bottomTextColor;
    private int seconBottomTextColor;
    private int bottomTextSize;
    private int secondBottomTextSize;
    
    public static final int SHOW_POPUPS_All = 1;
    public static final int SHOW_POPUPS_MAXMIN_ONLY = 2;
    public static final int SHOW_POPUPS_NONE = 3;

    private int showPopupType = SHOW_POPUPS_NONE;
    public void setShowPopup(int popupType) {
		this.showPopupType = popupType;
	}

    private boolean isNeedBackLines;
	//점선표시
    private Boolean drawDotLine = false;
    //라인컬러
    private int[] colorArray = {Color.parseColor("#e74c3c"),
            Color.parseColor("#2980b9"),
            Color.parseColor("#1abc9c")};
    private int[] dotModeArray=new int[3];//圆点绘制模式 1.空心 2.实心 3 不绘制

    private boolean[] drawLine = {true,true,true};
    //popup 컬러
    private int[] popupColorArray = {R.drawable.popup_red,R.drawable.popup_blue,R.drawable.popup_green};

    // onDraw optimisations
    private final Point tmpPoint = new Point();
    
	public void setDrawDotLine(Boolean drawDotLine) {
		this.drawDotLine = drawDotLine;
	}

	private Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            for(ArrayList<Dot> data : drawDotLists){
            	for(Dot dot : data){
                    dot.update();
                    if(!dot.isAtRest()){
                        needNewFrame = true;
                    }
                }
            }
            if (needNewFrame) {
                postDelayed(this, 25);
            }
            invalidate();
        }
    };

    public LineView(Context context){
        this(context, null);
    }
    public LineView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineView);
        sideLineLength = (int)a.getDimension(R.styleable.LineView_sideLength, sideLineLength);
        colorArray [0] = a.getColor(R.styleable.LineView_line1_color,colorArray [0]);
        colorArray [1] = a.getColor(R.styleable.LineView_line2_color,colorArray [1]);
        colorArray [2] = a.getColor(R.styleable.LineView_line3_color,colorArray [2]);
        drawLine [0]  = a.getBoolean(R.styleable.LineView_line1_drawLine, drawLine[0]);
        drawLine [1]  = a.getBoolean(R.styleable.LineView_line2_drawLine, drawLine[1]);
        drawLine [2]  = a.getBoolean(R.styleable.LineView_line3_drawLine,drawLine [2]);
        dotModeArray[0]=a.getInt(R.styleable.LineView_dot_mode1,1);
        dotModeArray[1]=a.getInt(R.styleable.LineView_dot_mode2,1);
        dotModeArray[2]=a.getInt(R.styleable.LineView_dot_mode3,1);
        bottomInterval=a.getInteger(R.styleable.LineView_bottom_interval,1);
        bottomTextDescent=a.getDimensionPixelSize(R.styleable.LineView_bottom_padding,0);
        bottomTextColor=a.getColor(R.styleable.LineView_bottom_text_color,BOTTOM_TEXT_COLOR);
        bottomTextSize=a.getDimensionPixelSize(R.styleable.LineView_bottom_text_size,20);
        seconBottomTextColor=a.getColor(R.styleable.LineView_bottom_second_text_color,BOTTOM_TEXT_COLOR);
        secondBottomTextSize=a.getDimensionPixelSize(R.styleable.LineView_bottom_second_text_size,20);
        isNeedBackLines=a.getBoolean(R.styleable.LineView_need_back_lines,true);
        totalGridTextNum=a.getInt(R.styleable.LineView_vertical_text_num,4);
        horizonalTrans=a.getDimensionPixelSize(R.styleable.LineView_horizonal_trans,10);
        verticalUnit=a.getString(R.styleable.LineView_vertical_axis_unit);
        isPerVerticalUnitShow=a.getBoolean(R.styleable.LineView_if_pervertical_unit_show,false);
        a.recycle();

        popupTextPaint.setAntiAlias(true);
        popupTextPaint.setColor(Color.WHITE);
        popupTextPaint.setTextSize(MyUtils.sp2px(getContext(), 13));
        popupTextPaint.setStrokeWidth(5);
        popupTextPaint.setTextAlign(Paint.Align.CENTER);

        bottomTextPaint.setAntiAlias(true);
        bottomTextPaint.setTextSize(bottomTextSize);
        bottomTextPaint.setTextAlign(Paint.Align.CENTER);
        bottomTextPaint.setStyle(Paint.Style.FILL);
        bottomTextPaint.setColor(bottomTextColor);

        seconBottomTextPaint.setAntiAlias(true);
        seconBottomTextPaint.setTextSize(secondBottomTextSize);
        seconBottomTextPaint.setColor(seconBottomTextColor);
        seconBottomTextPaint.setStyle(Paint.Style.FILL);
        seconBottomTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    protected  int getHorizontalGridWidth()
    {
        return backgroundGridWidth;
    }

    public void setBottomTextList(ArrayList<String> bottomTextList){
        setBottomTextList(bottomTextList,null);
    }
    /**
     * dataList will be reset when called is method.
     * @param bottomTextList The String ArrayList in the bottom.
     */
    public void setBottomTextList(ArrayList<String> bottomTextList,ArrayList<SecondBottomBean> secondbottomTextList){
        this.bottomTextList = bottomTextList;
        if(null !=secondbottomTextList){
            this.secondBottomTextList=secondbottomTextList;
        }

        Rect r = new Rect();
        int longestWidth = 0;
        String longestStr = "";
        for(String s:bottomTextList){
            bottomTextPaint.getTextBounds(s,0,s.length(),r);
            if(bottomTextHeight<r.height()){
                bottomTextHeight = r.height();
            }
            if(autoSetGridWidth&&(longestWidth<r.width())){
                longestWidth = r.width();
                longestStr = s;
            }
            if(bottomTextDescent<(Math.abs(r.bottom))){
                bottomTextDescent = Math.abs(r.bottom);
            }
        }


        if(autoSetGridWidth){
            backgroundGridWidth = defGridWidth;
            if(backgroundGridWidth<longestWidth){
                backgroundGridWidth = longestWidth+(int)bottomTextPaint.measureText(longestStr,0,1);
            }
            if(sideLineLength<longestWidth/2){
                sideLineLength = longestWidth/2;
            }
        }


        refreshXCoordinateList(getHorizontalGridNum());
    }

    /**
     *
     * @param dataLists The Integer ArrayLists for showing,
     *                 dataList.size() must < bottomTextList.size()
     */
    public void setDataList(ArrayList<ArrayList<Double>> dataLists){
    	selectedDot = null;
        this.dataLists = dataLists;
        for(ArrayList<Double> list : dataLists){
        	if(list.size() > bottomTextList.size()){
                throw new RuntimeException("dacer.LineView error:" +
                        " dataList.size() > bottomTextList.size() !!!");
            }
        }
        Double biggestData = 0.;
        for(ArrayList<Double> list : dataLists){
        	if(autoSetDataOfGird){
                for(Double i:list){
                    if(biggestData<i){
                        biggestData = i;
                    }
                }
        	}
        	dataOfAGird = 1;
        	while(biggestData/10 > dataOfAGird){
        		dataOfAGird *= 10;
        	}
        }

        showPopup = true;
        if(mViewHeight!=0) {
            refreshAfterDataChanged();
            setMinimumWidth(0); // It can help the LineView reset the Width,
                                // I don't know the better way..
            postInvalidate();
        }
    }

    private void refreshAfterDataChanged(){
        int verticalGridNum = getVerticalGridlNum();
        refreshTopLineLength(verticalGridNum);
        refreshYCoordinateList(verticalGridNum);
        refreshDrawDotList(verticalGridNum);
    }

    private int getVerticalGridlNum(){
        int verticalGridNum = MIN_VERTICAL_GRID_NUM;
        if(dataLists != null && !dataLists.isEmpty()){
        	for(ArrayList<Double> list : dataLists){
	        	for(Double integer:list){
	        		if(verticalGridNum<(integer+1)){
	        			verticalGridNum = (int)Math.ceil(integer)+1;
	        		}
	        	}
        	}
        }
        return verticalGridNum;
    }

    private int getHorizontalGridNum(){
        int horizontalGridNum = bottomTextList.size()-1;
        if(horizontalGridNum<MIN_HORIZONTAL_GRID_NUM){
            horizontalGridNum = MIN_HORIZONTAL_GRID_NUM;
        }
        return horizontalGridNum;
    }

    private void refreshXCoordinateList(int horizontalGridNum){
        xCoordinateList.clear();
        for(int i=0;i<(horizontalGridNum+1);i++){
            xCoordinateList.add(horizonalTrans+sideLineLength + getHorizontalGridWidth()*i);
        }

    }

    private void refreshYCoordinateList(int verticalGridNum){
        yCoordinateList.clear();
        for(int i=0;i<(verticalGridNum+1);i++){
            yCoordinateList.add(topLineLength +
                    ((mViewHeight-topLineLength-
                            bottomLineLength-bottomTextDescent)*i/(verticalGridNum)));
        }
    }

    private void refreshDrawDotList(int verticalGridNum){
        if(dataLists != null && !dataLists.isEmpty()){
    		if(drawDotLists.size() == 0){
    			for(int k = 0; k < dataLists.size(); k++){
    				drawDotLists.add(new ArrayList<LineView.Dot>());
    			}
    		}
        	for(int k = 0; k < dataLists.size(); k++){
        		int drawDotSize = drawDotLists.get(k).isEmpty()? 0:drawDotLists.get(k).size();
        		
        		for(int i=0;i<dataLists.get(k).size();i++){
                    int x = xCoordinateList.get(i);
                    int y = yCoordinateList.get(verticalGridNum - (int)Math.ceil(dataLists.get(k).get(i)));
                    if(i>drawDotSize-1){
                    	//도트리스트를 추가한다.
                        drawDotLists.get(k).add(new Dot(x, y, x, y, (int)Math.ceil(dataLists.get(k).get(i)),k));
                    }else{
                    	//도트리스트에 타겟을 설정한다.
                        drawDotLists.get(k).set(i, drawDotLists.get(k).get(i).setTargetData(x,y,(int)Math.ceil(dataLists.get(k).get(i)),k));
                    }
                }
        		
        		int temp = drawDotLists.get(k).size() - dataLists.get(k).size();
        		for(int i=0; i<temp; i++){
        			drawDotLists.get(k).remove(drawDotLists.get(k).size()-1);
        		}
        	}
        }
        removeCallbacks(animator);
        post(animator);
    }

    private void refreshTopLineLength(int verticalGridNum){
        // For prevent popup can't be completely showed when backgroundGridHeight is too small.
        // But this code not so good.
        if((mViewHeight-topLineLength-bottomTextHeight-bottomTextTopMargin)/
                (verticalGridNum+2)<getPopupHeight()){
            topLineLength = getPopupHeight()+DOT_OUTER_CIR_RADIUS+DOT_INNER_CIR_RADIUS+2;
        }else{
            topLineLength = MIN_TOP_LINE_LENGTH;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackgroundLines(canvas);
        drawLines(canvas);
        drawDots(canvas);

        
        for(int k=0; k < drawDotLists.size(); k++){
        	double MaxValue = Collections.max(dataLists.get(k));
            double MinValue = Collections.min(dataLists.get(k));
        	for(Dot d: drawDotLists.get(k)){
        		if(showPopupType == SHOW_POPUPS_All)
        			drawPopup(canvas, String.valueOf(d.data), d.setupPoint(tmpPoint),popupColorArray[k%3]);
        		else if(showPopupType == SHOW_POPUPS_MAXMIN_ONLY){
        			if(d.data == MaxValue)
        				drawPopup(canvas, String.valueOf(d.data), d.setupPoint(tmpPoint),popupColorArray[k%3]);
        			if(d.data == MinValue)
        				drawPopup(canvas, String.valueOf(d.data), d.setupPoint(tmpPoint),popupColorArray[k%3]);
        		}
        	}
        }
// 선택한 dot 만 popup 이 뜨게 한다.        
        if(showPopup && selectedDot != null){
            drawPopup(canvas,
                    String.valueOf(selectedDot.data),
                    selectedDot.setupPoint(tmpPoint),popupColorArray[selectedDot.linenumber%3]);
        }
    }

    /**
     *
     * @param canvas  The canvas you need to draw on.
     * @param point   The Point consists of the x y coordinates from left bottom to right top.
     *                Like is
     *                
     *                3
     *                2
     *                1
     *                0 1 2 3 4 5
     */
    private void drawPopup(Canvas canvas,String num, Point point,int PopupColor){
        boolean singularNum = (num.length() == 1);
        int sidePadding = MyUtils.dip2px(getContext(),singularNum? 8:5);
        int x = point.x;
        int y = point.y-MyUtils.dip2px(getContext(),5);
        Rect popupTextRect = new Rect();
        popupTextPaint.getTextBounds(num,0,num.length(),popupTextRect);
        Rect r = new Rect(x-popupTextRect.width()/2-sidePadding,
                y - popupTextRect.height()-bottomTriangleHeight-popupTopPadding*2-popupBottomMargin,
                x + popupTextRect.width()/2+sidePadding,
                y+popupTopPadding-popupBottomMargin);

        NinePatchDrawable popup = (NinePatchDrawable)getResources().getDrawable(PopupColor);
        popup.setBounds(r);
        popup.draw(canvas);
        canvas.drawText(num, x, y-bottomTriangleHeight-popupBottomMargin, popupTextPaint);
    }

    private int getPopupHeight(){
        Rect popupTextRect = new Rect();
        popupTextPaint.getTextBounds("9", 0, 1, popupTextRect);
        Rect r = new Rect(-popupTextRect.width()/2,
                 - popupTextRect.height()-bottomTriangleHeight-popupTopPadding*2-popupBottomMargin,
                 + popupTextRect.width()/2,
                +popupTopPadding-popupBottomMargin);
        return r.height();
    }

    //画圆点
    private void drawDots(Canvas canvas){
        Paint bigCirPaint = new Paint();
        bigCirPaint.setAntiAlias(true);
        Paint smallCirPaint = new Paint(bigCirPaint);
        smallCirPaint.setColor(Color.parseColor("#FFFFFF"));
        if(drawDotLists!=null && !drawDotLists.isEmpty()){
        	for(int k=0; k < drawDotLists.size(); k++){	
        		bigCirPaint.setColor(colorArray[k%3]);
                int mode=dotModeArray[k%3];
                int index=0;
                if(mode == 3){
                    break;
                }
                else if(mode == 2){
                    for(Dot dot : drawDotLists.get(k)){
                        if(index++ % bottomInterval == 0) {
                            canvas.drawCircle(dot.x, dot.y, DOT_INNER_CIR_RADIUS, bigCirPaint);
                        }
                    }
                }else {
                    for (Dot dot : drawDotLists.get(k)) {
                        if(index++ % bottomInterval == 0) {
                            canvas.drawCircle(dot.x, dot.y, DOT_OUTER_CIR_RADIUS, bigCirPaint);
                            canvas.drawCircle(dot.x, dot.y, DOT_INNER_CIR_RADIUS, smallCirPaint);
                        }
                    }
                }
        	}
        }
    }

    //선그리기
    private void drawLines(Canvas canvas){
        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(MyUtils.dip2px(getContext(), 2));
        for(int k = 0; k<drawDotLists.size(); k ++){
            if(!drawLine[k%3]) continue;
        	linePaint.setColor(colorArray[k%3]);
	        for(int i=0; i<drawDotLists.get(k).size()-1; i++){
	            canvas.drawLine(drawDotLists.get(k).get(i).x,
	                    drawDotLists.get(k).get(i).y,
	                    drawDotLists.get(k).get(i+1).x,
	                    drawDotLists.get(k).get(i+1).y,
	                    linePaint);
	        }
        }
    }

    
     private void drawBackgroundLines(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(MyUtils.dip2px(getContext(), 1f));
        paint.setColor(BACKGROUND_LINE_COLOR);
        PathEffect effects = new DashPathEffect(
                new float[]{10,5,10,5}, 1);

        //draw vertical lines
         if(isNeedBackLines) {
             /*for (int i = 0; i < xCoordinateList.size(); i++) {
                 canvas.drawLine(xCoordinateList.get(i),
                         0,
                         xCoordinateList.get(i),
                         mViewHeight - bottomTextTopMargin - bottomTextHeight - bottomTextDescent,
                         paint);
             }*/

             //draw dotted lines
             paint.setPathEffect(effects);
             Path dottedPath = new Path();
             for (int i = 0; i < yCoordinateList.size(); i++) {
                 if ((yCoordinateList.size() - 1 - i) % dataOfAGird == 0) {
                     dottedPath.moveTo(horizonalTrans, yCoordinateList.get(i));
                     dottedPath.lineTo(getWidth(), yCoordinateList.get(i));
                     canvas.drawPath(dottedPath, paint);
                 }
             }


         }

         drawVerticalText(canvas);
    	  //draw bottom text
      if(bottomTextList != null){
          int secondIndex=0;
          if(null ==secondBottomTextList || secondBottomTextList.size() == 0){
              secondIndex=-1;
          }
          bottomTextPaint.setTextAlign(Paint.Align.CENTER);
    	  for(int i=0;i<bottomTextList.size();i++){
              if(secondIndex >= 0 && secondBottomTextList.get(secondIndex).index == i){
                  canvas.drawText(secondBottomTextList.get(secondIndex).data, horizonalTrans+sideLineLength + getHorizontalGridWidth() * i, mViewHeight-10, seconBottomTextPaint);
                  if(++ secondIndex >= secondBottomTextList.size()){
                      secondIndex=-1;
                  }
              }
              if(i%bottomInterval == 0) {
                  canvas.drawText(bottomTextList.get(i), horizonalTrans+sideLineLength + getHorizontalGridWidth() * i, mViewHeight - bottomTextDescent, bottomTextPaint);
              }
    	  }
      }

      
      if(!drawDotLine){
    	//draw solid lines
          for(int i=0;i<yCoordinateList.size();i++){
              if((yCoordinateList.size()-1-i)%dataOfAGird == 0){
                  canvas.drawLine(0,yCoordinateList.get(i),getWidth(),yCoordinateList.get(i),paint);
              }
          }
      }
    }

    /**
     * 绘制竖直坐标系
     * @param canvas
     */
    private void drawVerticalText(Canvas canvas){
        int perVerticalTextSize=dataOfAGird;
        if(totalGridTextNum >1){
            perVerticalTextSize=(yCoordinateList.size() - 1)/dataOfAGird/(totalGridTextNum-1);
        }
        bottomTextPaint.setTextAlign(Paint.Align.LEFT);
        for(int i=0;i<totalGridTextNum;i++){
            int index=yCoordinateList.size() - 1-(i*dataOfAGird*perVerticalTextSize);
            if(index >0){
                String text=i*dataOfAGird*perVerticalTextSize+"";
                if(isPerVerticalUnitShow || i == totalGridTextNum-1){
                    text+=verticalUnit;
                }
                canvas.drawText(text, 0, yCoordinateList.get(index), bottomTextPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);
        refreshAfterDataChanged();
        setMeasuredDimension(mViewWidth,mViewHeight);
    }

    private int measureWidth(int measureSpec){
        int horizontalGridNum = getHorizontalGridNum();
        int preferred = defGridWidth*horizontalGridNum+sideLineLength*2;
        int width = getMeasurement(measureSpec, preferred);
        if(width < preferred)
        {
            backgroundGridWidth = (width-horizonalTrans - sideLineLength*2)/horizontalGridNum;
            refreshXCoordinateList(horizontalGridNum);
        }
        return width;
    }

    private int measureHeight(int measureSpec){
        int preferred = 0;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred){
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement;
        switch(MeasureSpec.getMode(measureSpec)){
            case MeasureSpec.EXACTLY:
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:
                measurement = Math.min(preferred, specSize);
                break;
            default:
                measurement = preferred;
                break;
        }
        return measurement;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pointToSelect = findPointAt((int) event.getX(), (int) event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pointToSelect != null) {
                selectedDot = pointToSelect;
                pointToSelect = null;
                postInvalidate();
            }
        }

        return true;
    }

    private Dot findPointAt(int x, int y) {
        if (drawDotLists.isEmpty()) {
            return null;
        }

        final int width = getHorizontalGridWidth()/2;
        final Region r = new Region();

        for (ArrayList<Dot> data : drawDotLists) {
            for (Dot dot : data) {
                final int pointX = dot.x;
                final int pointY = dot.y;

                r.set(pointX - width, pointY - width, pointX + width, pointY + width);
                if (r.contains(x, y)){
                    return dot;
                }
            }
        }

        return null;
    }


    
    class Dot{
        int x;
        int y;
        int data;
        int targetX;
        int targetY;
        int linenumber;
        int velocity = MyUtils.dip2px(getContext(),5);

        Dot(int x,int y,int targetX,int targetY,Integer data,int linenumber){
            this.x = x;
            this.y = y;
            this.linenumber = linenumber;
            setTargetData(targetX, targetY,data,linenumber);
        }

        Point setupPoint(Point point) {
            point.set(x, y);
            return point;
        }

        Dot setTargetData(int targetX,int targetY,Integer data,int linenumber){
            this.targetX = targetX;
            this.targetY = targetY;
            this.data = data;
            this.linenumber = linenumber;
            return this;
        }

        boolean isAtRest(){
            return (x==targetX)&&(y==targetY);
        }

        void update(){
            x = updateSelf(x, targetX, velocity);
            y = updateSelf(y, targetY, velocity);
        }

        private int updateSelf(int origin, int target, int velocity){
            if (origin < target) {
                origin += velocity;
            } else if (origin > target){
                origin-= velocity;
            }
            if(Math.abs(target-origin)<velocity){
                origin = target;
            }
            return origin;
        }
    }
}
