package com.xiaomaoqiu.now.view;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.pet.R;

/**
 * Created by Administrator on 2017/1/14.
 */

public class MapPetCommonNotAtHomeView extends LinearLayout {
    private String avaterUrl;
    private SimpleDraweeView avater;
    private boolean isShowed = false;

    public MapPetCommonNotAtHomeView(Context context) {
        this(context, null);
    }

    public MapPetCommonNotAtHomeView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MapPetCommonNotAtHomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.map_pet_common_notathomeview, this, true);
        avater = (SimpleDraweeView) rootView.findViewById(R.id.map_pet_athome_avater);
//        String url = PetInfoInstance.getInstance().packBean.logo_url;
//        setAvaterUrl(url);

    }

    public void setAvaterUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            Log.e("longtianlove","url为空");
            return;
        }
        Log.e("longtianlove",url);
        avaterUrl = url;
        if (null == avater) {
            return;
        }
        Uri uri = Uri.parse(avaterUrl);
        avater.setImageURI(uri);
//        AsyncImageTask.INSTANCE.loadImage(avater, avaterUrl, this);
    }

}
