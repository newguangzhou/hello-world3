package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.util.DensityUtil;
import com.xiaomaoqiu.pet.R;

/**
 * Created by Administrator on 2017/1/15.
 */

public class AsynImgDialog extends Dialog implements View.OnClickListener{

    private Button mOkBtn,mQuitBtn;
    private TextView tv_message;
    public static SimpleDraweeView asynImg;
    private View.OnClickListener okClickListener,quitClickListner;
    private int innerpadding=0;

    public static double startSalary=0;
    public static double stopSalary=0;
    public static long startTime=0;
    public static long stopTime=0;

    public  static String message;


    public static AsynImgDialog createGoSportDialig(Context context, View.OnClickListener okClickListener){
        String url="";
//        PetInfo petInfo= UserMgr.INSTANCE.getPetInfo();
//        if(null != petInfo){
//            url=petInfo.getHeaderImg();
//        }
        url= PetInfoInstance.getInstance().packBean.logo_url;
        message=context.getString(R.string.tosport_tip1);
        return new AsynImgDialog(context,R.layout.asyn_dialog_go_out,okClickListener,null,url,true,0);
    }

    public static AsynImgDialog createGoHomeDialog(Context context, View.OnClickListener okClickListener){
        String url="";
//        PetInfo petInfo= UserMgr.INSTANCE.getPetInfo();
//        if(null != petInfo){
//            url=petInfo.getHeaderImg();
//        }
        url= PetInfoInstance.getInstance().packBean.logo_url;
        int margin=context.getResources().getDimensionPixelSize(R.dimen.dialog_service_margin)*2;
        message="亲爱的铲屎官，请确认"+PetInfoInstance.getInstance().getNick()+"跟您一块儿安全到家哦！";
        return new AsynImgDialog(context,R.layout.asyn_dialog_go_home,okClickListener,null,url,true,margin);
    }

    public AsynImgDialog(Context context, int lId, View.OnClickListener okClick,View.OnClickListener cancelClick,String url,boolean cancelable,int padding){
        super(context, R.style.MyDialogStyleBottom);
        okClickListener=okClick;
        quitClickListner=cancelClick;
        innerpadding=padding;
        initView(lId,url);
        setCanceledOnTouchOutside(cancelable);
        show();
    }

    private void initView(int layoutId,String url){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutId, null);
        setContentView(view);
        initParams();

        mOkBtn=(Button) findViewById(R.id.asyn_dialog_ok);
        mQuitBtn=(Button)findViewById(R.id.asyn_dialog_quit);
        asynImg= (SimpleDraweeView) findViewById(R.id.asyn_dialog_imgview);
        tv_message= (TextView) findViewById(R.id.tv_message);
        tv_message.setText(message);
        mOkBtn.setOnClickListener(this);
        mQuitBtn.setOnClickListener(this);
        if(!TextUtils.isEmpty(url)){
            Uri uri = Uri.parse(url);
            asynImg.setImageURI(uri);
//            AsyncImageTask.INSTANCE.loadImage(asynImg, url, this);
        }
    }

    private void initParams(){
        //获取到当前Activity的Window
        Window dialog_window = getWindow();
        //获取到LayoutParams
        WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
        //设置宽度
        dialog_window_attributes.width= DensityUtil.getScreenWidth(getContext())-innerpadding;
        dialog_window.setAttributes(dialog_window_attributes);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()){
            case R.id.asyn_dialog_ok:
                if(null != okClickListener){
                    okClickListener.onClick(v);
                }
                break;
            case R.id.asyn_dialog_quit:
                if(null != quitClickListner){
                    quitClickListner.onClick(v);
                }
                break;
        }
    }


}
