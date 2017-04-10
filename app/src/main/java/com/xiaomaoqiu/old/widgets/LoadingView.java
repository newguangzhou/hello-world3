package com.xiaomaoqiu.old.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xiaomaoqiu.pet.R;


/**
 * Created by simon on 15/8/11.
 */
public class LoadingView extends LinearLayout {
    public LoadingView(Context context, AttributeSet attrs){
        super(context, attrs);

        inflate(context, R.layout.item_loading, this);
    }
}
