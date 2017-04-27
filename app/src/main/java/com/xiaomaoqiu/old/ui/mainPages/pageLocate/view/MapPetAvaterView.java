package com.xiaomaoqiu.old.ui.mainPages.pageLocate.view;

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

public class MapPetAvaterView extends LinearLayout {
    private String avaterUrl;
    private SimpleDraweeView avater;
    private boolean isShowed = false;

    public MapPetAvaterView(Context context) {
        this(context, null);
    }

    public MapPetAvaterView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MapPetAvaterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.map_pet_locview, this, true);
        avater = (SimpleDraweeView) rootView.findViewById(R.id.map_pet_loca_avater);
        String url = PetInfoInstance.getInstance().packBean.logo_url;
        setAvaterUrl(url);

    }

    public void setAvaterUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        avaterUrl = url;
        if (null == avater) {
            return;
        }
        Uri uri = Uri.parse(avaterUrl);
        avater.setImageURI(uri);
//        AsyncImageTask.INSTANCE.loadImage(avater, avaterUrl, this);
    }


}
