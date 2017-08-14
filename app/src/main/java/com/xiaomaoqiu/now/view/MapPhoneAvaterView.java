package com.xiaomaoqiu.now.view;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.pet.R;

/**
 * Created by Administrator on 2017/1/14.
 */

public class MapPhoneAvaterView extends LinearLayout {
    private String avaterUrl;
    private SimpleDraweeView avater;
    private boolean isShowed = false;

    public MapPhoneAvaterView(Context context) {
        this(context, null);
    }

    public MapPhoneAvaterView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MapPhoneAvaterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.map_phone_locview, this, true);
//        avater = (SimpleDraweeView) rootView.findViewById(R.id.map_phone_loca_avater);
    }


}
