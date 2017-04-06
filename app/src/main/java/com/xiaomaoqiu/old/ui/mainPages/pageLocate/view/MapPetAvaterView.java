package com.xiaomaoqiu.old.ui.mainPages.pageLocate.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.old.dataCenter.PetInfo;
import com.xiaomaoqiu.old.dataCenter.UserMgr;
import com.xiaomaoqiu.old.utils.AsyncImageTask;

/**
 * Created by Administrator on 2017/1/14.
 */

public class MapPetAvaterView extends LinearLayout implements AsyncImageTask.ImageCallback {
    private String avaterUrl;
    private ImageView avater;
    private boolean isShowed=false;

    public MapPetAvaterView(Context context) {
        this(context,null);
    }

    public MapPetAvaterView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public MapPetAvaterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView= LayoutInflater.from(context).inflate(R.layout.map_pet_locview,this,true);
        avater=(ImageView)rootView.findViewById(R.id.map_pet_loca_avater);
        PetInfo petInfo=UserMgr.INSTANCE.getPetInfo();
        if(null != petInfo && !TextUtils.isEmpty(petInfo.getHeaderImg())){
            setAvaterUrl(petInfo.getHeaderImg());
        }
    }

    public void setAvaterUrl(String url){
        if(TextUtils.isEmpty(url)){
            return;
        }
        avaterUrl=url;
        if(null == avater){
            return;
        }
        AsyncImageTask.INSTANCE.loadImage(avater, avaterUrl, this);
    }

    @Override
    public void imageLoaded(String url, Bitmap obj, ImageView view) {
        if(null == obj){
            return;
        }
        isShowed=true;
        view.setImageBitmap(obj);
    }
}
