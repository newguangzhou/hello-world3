package com.xiaomaoqiu.now.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaomaoqiu.pet.R;


/**
 * 运动指数页标题view
 * Created by long on 2016/12/11.
 */

public class SportTitleView extends RelativeLayout {
    private String title;
    private String firstText;
    private String secondText;
    public SportTitleView(Context context) {
        this(context,null);
    }

    public SportTitleView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public SportTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a= context.obtainStyledAttributes(attrs, R.styleable.SleepTitleView);
        title=a.getString(R.styleable.SleepTitleView_titletext);
        firstText=a.getString(R.styleable.SleepTitleView_first_text);
        secondText=a.getString(R.styleable.SleepTitleView_second_text);
        a.recycle();
        initText();
        LayoutInflater.from(context).inflate(R.layout.sport_title_view,this,true);
    }

    private void initText(){
        if(TextUtils.isEmpty(firstText)){
            firstText="深睡";
        }
        if(TextUtils.isEmpty(secondText)){
            secondText="小憩";
        }
        if(TextUtils.isEmpty(title)){
            title="今日睡眠";
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        TextView titleView=(TextView)findViewById(R.id.sleep_title_view_title);
        titleView.setText(title);
        TextView first=(TextView)findViewById(R.id.sleep_level_high_text);
        first.setText(firstText);
        TextView second=(TextView)findViewById(R.id.sleep_level_low_text);
        second.setText(secondText);

    }
}
