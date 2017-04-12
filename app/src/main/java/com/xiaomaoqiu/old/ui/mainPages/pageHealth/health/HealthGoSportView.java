package com.xiaomaoqiu.old.ui.mainPages.pageHealth.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.PetInfoActivity;
import com.xiaomaoqiu.pet.R;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/12/25.
 */
@SuppressLint("WrongConstant")
public class HealthGoSportView extends RelativeLayout implements View.OnClickListener {
    public static final int STATUS_DEFAULT=1;
    public static final int STATUS_SPORT=2;
    public static final int STATUS_BACK=3;

    private static final int HEAD_TRANSLATE_INTERVAL=300;

    private SimpleDraweeView mHead;

    private LinearLayout mSportContainer;
    private Button mBtConformSport,mBtQuitSport;

    private LinearLayout mBackContainer;
    private Button mBtAlreadyBack,mBtNotBack;

    private int mShowBackColor,mHideBackColor;
    private int mBackPaddingTop;//回家对话框头像据对话框顶部位置

    private int mBackHeadX=0,mBackHeadY=0;//回家对话框头像左上角坐标
    private int mSportHeadX=0,mSportHeadY=0;//去运动对话框左上角坐标
    private int mDefaultHeadX=0,mDefaultHeadY=0;//头像默认位置
    private boolean isDataInited=false;

    private int mCurStatus;//当前状态 1、只显示头像 2、显示去运动对话框 3、显示回家对话框

    public HealthGoSportView(Context context) {
        this(context,null);
    }

    public HealthGoSportView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public HealthGoSportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCurStatus=1;
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.HealthGoSportView);
        mShowBackColor=a.getColor(R.styleable.HealthGoSportView_show_dialogback, Color.TRANSPARENT);
        mHideBackColor=a.getColor(R.styleable.HealthGoSportView_hide_dialogback,Color.TRANSPARENT);
        mBackPaddingTop=a.getDimensionPixelSize(R.styleable.HealthGoSportView_back_paddingTop,0);
        a.recycle();
        LayoutInflater.from(context).inflate(R.layout.health_go_sport_view,this,true);
    }

    /**
     * 设置当前状态
     * @param status
     */
    public void setStatus(int status){
        if(status > 3 || status <1){
            status=1;
        }
        mCurStatus=status;
    }

    public boolean isShowing(){
        return mBackContainer.isShown() || mSportContainer.isShown();
    }

    public void show(int status){
        setStatus(status);
        if(!isDataInited){
            isDataInited=true;
            initData();
        }
        show();
    }
    public void show(){
        switch (mCurStatus){
            case STATUS_DEFAULT:
                showOnlyHead();
                break;
            case STATUS_SPORT:
                showSportDialog();
                break;
            case STATUS_BACK:
                showBackDialog();
                break;
        }
    }

    /**
     * 只显示头像
     */
    private void showOnlyHead(){
        setBackgroundColor(mShowBackColor);
        mSportContainer.setVisibility(GONE);
        mBackContainer.setVisibility(GONE);
        mHead.animate()
                .x(mDefaultHeadX)
                .y(mDefaultHeadY)
                .setDuration(HEAD_TRANSLATE_INTERVAL)
                .start();
    }

    /**
     * 显示去运动对话框
     */
    private void showSportDialog(){
        setBackgroundColor(mHideBackColor);
        mHead.animate()
                .x(mSportHeadX)
                .y(mSportHeadY)
                .setDuration(HEAD_TRANSLATE_INTERVAL)
                .start();
        mSportContainer.setVisibility(VISIBLE);
    }

    /**
     * 显示回家对话框
     */
    private void showBackDialog(){
        setBackgroundColor(mHideBackColor);
        mHead.animate()
                .x(mBackHeadX)
                .y(mBackHeadY)
                .setDuration(HEAD_TRANSLATE_INTERVAL)
                .start();
        mBackContainer.setVisibility(VISIBLE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setBackgroundColor(mShowBackColor);
        initView();
    }

    private void initView(){
        mHead= (SimpleDraweeView) findViewById(R.id.go_sport_head);
        mHead.setOnClickListener(this);

        mSportContainer=(LinearLayout)findViewById(R.id.go_sport_sport_container);
        mBtConformSport=(Button)findViewById(R.id.go_sport_bt_sport_ok);
        mBtQuitSport=(Button)findViewById(R.id.go_sport_bt_sport_quit);
        mBtConformSport.setOnClickListener(this);
        mBtQuitSport.setOnClickListener(this);

        mBackContainer=(LinearLayout)findViewById(R.id.go_sport_back_container);
        mBtAlreadyBack=(Button)findViewById(R.id.go_sport_bt_already_bac);
        mBtNotBack=(Button)findViewById(R.id.go_sport_bt_not_back);
        mBtAlreadyBack.setOnClickListener(this);
        mBtNotBack.setOnClickListener(this);
    }

    private void initData(){
        mBackHeadX=mBackContainer.getLeft()+(mBackContainer.getRight()-mBackContainer.getLeft()-mHead.getWidth())/2;
        mBackHeadY=mBackContainer.getTop()+mBackPaddingTop;
        mSportHeadX=mSportContainer.getLeft();
        LinearLayout ggSpeak=(LinearLayout)findViewById(R.id.go_sport_sport_ggspeak);
        mSportHeadY=mSportContainer.getTop()+(ggSpeak.getHeight()+ggSpeak.getPaddingTop()-mHead.getHeight())/2;
        mDefaultHeadX=mHead.getLeft();
        mDefaultHeadY=mHead.getTop();
    }


    private void goSport(){
//        HttpUtil.get2("pet.activity", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.v("http", "pet.activity:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    PetInfoInstance.getInstance().setAtHome(false);
//                    show(STATUS_DEFAULT);
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
//            {
//                Toast.makeText(getContext(),"网络连接失败",Toast.LENGTH_LONG).show();
//            }
//
//        }, UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),1 );
        ApiUtils.getApiService().toActivity(UserInstance.getUserInstance().getUid(),
                UserInstance.getUserInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id(),
                Constants.TO_SPORT_ACTIVITY_TYPE
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        PetInfoInstance.getInstance().setAtHome(false);
                        show(STATUS_DEFAULT);
                        break;
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });
    }

    private void goHome(){
//        HttpUtil.get2("pet.activity", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.v("http", "pet.activity:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    PetInfoInstance.getInstance().setAtHome(true);
//                    show(STATUS_DEFAULT);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_LONG).show();
//            }
//
//        },UserInstance.getUserInstance().getUid(), UserInstance.getUserInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),2 );
        ApiUtils.getApiService().toActivity(UserInstance.getUserInstance().getUid(),
                UserInstance.getUserInstance().getToken(),
                PetInfoInstance.getInstance().getPet_id(),
                Constants.TO_HOME_ACTIVITY_TYPE
        ).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        PetInfoInstance.getInstance().setAtHome(true);
                        show(STATUS_DEFAULT);
                        break;
                }
            }

            @Override
            public void onFail(Call<BaseBean> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_sport_bt_sport_ok:
                goSport();
                break;
            case R.id.go_sport_bt_already_bac:
                goHome();
                break;
            case R.id.go_sport_head:
                Intent intent = new Intent(getContext(),PetInfoActivity.class);
                getContext().startActivity(intent);
                break;
            default:
                show(STATUS_DEFAULT);
                break;
        }
    }
}
