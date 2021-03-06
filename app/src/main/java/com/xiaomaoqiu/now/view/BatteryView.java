package com.xiaomaoqiu.now.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.pet.R;

/**
 * Created by huangjx on 2016/7/24.
 */
@SuppressLint("AppCompatCustomView")
public class BatteryView extends ImageView {

    private float[] batteryLevels;
    public int[] batteryLevelRes;
    float mWarnLevel = 0.3f;//报警电量
    float mEmptyLevel = 0.05f;//电量超低警示
    private boolean charging;
    private int charginRid;

    private Activity mactivity;


    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        charging=false;
        batteryLevels=new float[]{0.75f,0.5f,0.25f,0.15f,0f};
        batteryLevelRes=new int[5];
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BatteryView);
        batteryLevelRes[0]=a.getResourceId(R.styleable.BatteryView_fullRid,R.drawable.battery100);
        batteryLevelRes[1]=a.getResourceId(R.styleable.BatteryView_highRid,R.drawable.battery75);
        batteryLevelRes[2]=a.getResourceId(R.styleable.BatteryView_halfRid,R.drawable.battery50);
        batteryLevelRes[3]=a.getResourceId(R.styleable.BatteryView_lowRid,R.drawable.battery25);
        batteryLevelRes[4]=a.getResourceId(R.styleable.BatteryView_emptyRid,R.drawable.battery10);
        charginRid=a.getResourceId(R.styleable.BatteryView_chargingRid,R.drawable.battery_charging);
        a.recycle();
    }


    public void setActivity(Activity activity){
        mactivity=activity;
    }

    public void setDeviceOffline(){
        setImageResource(R.drawable.battery_bkgnd);
    }

    public void setBatteryLevel(float level,String time)
    {
        if(level <= mWarnLevel)
        {
//            new BatteryNoticeDialog(getContext(),level,time,getContext().getResources().getString(R.string.tip_battery_null));
            DialogUtil.showCommonBatteryNoticeDialog(mactivity,level,time,getContext().getResources().getString(R.string.tip_battery_null));
        }
        //如果超过1，说明在充电中……
        if(level>1.0f){
            setImageResource(charginRid);
            //如果正在充电，可以再次弹窗框
            DialogUtil.lowBatteryIsClosed=false;
            DialogUtil.superLowBatteryIsClosed=false;
//            new BatteryIngNoticeDialog(getContext());
            return;
        }
        for(int i=0; i<batteryLevels.length ;i++){
            if(level > batteryLevels[i]){
                setImageResource(batteryLevelRes[i]);
                break;
            }
            setImageResource(batteryLevelRes[4]);
        }
    }



    public void pushBatteryDialog(float level,String time){
        //如果超过1，说明在充电中……
        if(level>1.0f){
            setImageResource(charginRid);
//            new BatteryIngNoticeDialog(getContext());

            //如果正在充电，可以再次弹窗框
            DialogUtil.lowBatteryIsClosed=false;
            DialogUtil.superLowBatteryIsClosed=false;

            DialogUtil.showBatteryIngNoticeDialog(mactivity);
            return;
        }
//        new BatteryNoticeDialog(getContext(),level,time,getContext().getResources().getString(R.string.tip_battery_null));
        DialogUtil.showCommonBatteryNoticeDialog(mactivity,level,time,getContext().getResources().getString(R.string.tip_battery_null));

        for(int i=0; i<batteryLevels.length ;i++){
            if(level > batteryLevels[i]){
                setImageResource(batteryLevelRes[i]);
                break;
            }
            setImageResource(batteryLevelRes[4]);
        }
    }



    public void showBatterylevel(float level,String time){
        if(level>1.0f){


            //如果正在充电，可以再次弹窗框
            DialogUtil.lowBatteryIsClosed=false;
            DialogUtil.superLowBatteryIsClosed=false;
            setImageResource(charginRid);
            return;
        }
        for(int i=0; i<batteryLevels.length ;i++){
            if(level > batteryLevels[i]){
                setImageResource(batteryLevelRes[i]);
                break;
            }
            setImageResource(batteryLevelRes[4]);
        }
    }



}
