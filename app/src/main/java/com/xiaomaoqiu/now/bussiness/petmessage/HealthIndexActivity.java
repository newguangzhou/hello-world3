package com.xiaomaoqiu.now.bussiness.petmessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bean.nocommon.BaseBean;
import com.xiaomaoqiu.now.bean.nocommon.Summary;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.old.ui.dialog.GoOutConfirmDialog_RAW_Activity;
import com.xiaomaoqiu.old.ui.dialog.PickSportNumberDialog_RAW_Activity;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

@SuppressLint("WrongConstant")
public class HealthIndexActivity extends BaseActivity implements PickSportNumberDialog_RAW_Activity.OnPickNumberListener, View.OnClickListener {

    static int REQ_CODE_GO_OUT = 1;

    private LinearLayout mBtSport,mBtExpert,mBtAdnust;
    private TextView mTextSportSum,mTextSleepSum,mTextNotice;

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.health_index));
        setContentView(R.layout.activity_health_index);
        initView();
        initData();
    }

    private void initView(){
        mBtSport=(LinearLayout)findViewById(R.id.health_index_bt_sport);
        mBtSport.setOnClickListener(this);
        findViewById(R.id.health_index_bt_expert).setOnClickListener(this);
        findViewById(R.id.health_index_bt_change).setOnClickListener(this);
        mTextSportSum=(TextView)findViewById(R.id.health_index_sport_summary);
        mTextSleepSum=(TextView)findViewById(R.id.health_index_sleep_summary);
        mTextNotice=(TextView)findViewById(R.id.health_index_expert_summary);
    }
    private void initData()
    {
//        HttpUtil.get2("pet.health.summary", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                //{"status": 0, "sleep_summary": "睡眠总结", "expert_remind": "专家提醒", "activity_summary": "活动总结"}
//                Log.v("http", "pet.health.summary:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    String tipSleep = response.optString("sleep_summary");
//                    String tipSport = response.optString("activity_summary");
//                    String tipSummary = response.optString("expert_remind");
//                    mTextSportSum.setText(tipSport);
//                    mTextSleepSum.setText(tipSleep);
//                    mTextNotice.setText(tipSummary);
//                }
//            }
//
//        }, UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id());
        ApiUtils.getApiService().getSummary(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(), PetInfoInstance.getInstance().getPet_id()).enqueue(new XMQCallback<Summary>() {
            @Override
            public void onSuccess(Response<Summary> response, Summary message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        mTextSportSum.setText(message.activity_summary);
                        mTextSleepSum.setText(message.sleep_summary);
                        mTextNotice.setText(message.activity_summary);
                        break;

                }
            }

            @Override
            public void onFail(Call<Summary> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == REQ_CODE_GO_OUT)
        {
            PetInfoInstance.getInstance().setAtHome(false);
//            HttpUtil.get2("pet.activity", new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    Log.v("http", "pet.activity:" + response.toString());
//                    HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                    if (ret == HttpCode.EC_SUCCESS) {
//                        PetInfoInstance.getInstance().setAtHome(false);
//                    }
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
//                {
//                    Toast.makeText(HealthIndexActivity.this,"网络连接失败",Toast.LENGTH_LONG).show();
//                }
//
//            },UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),1 );
//            ApiUtils.getApiService().toActivity(UserInstance.getInstance().getUid(),
//                    UserInstance.getInstance().getToken(),
//                    PetInfoInstance.getInstance().getPet_id(),
//                    Constants.TO_SPORT_ACTIVITY_TYPE
//            ).enqueue(new XMQCallback<BaseBean>() {
//                @Override
//                public void onSuccess(Response<BaseBean> response, BaseBean message) {
//                    HttpCode ret = HttpCode.valueOf(message.status);
//                    switch (ret) {
//                        case EC_SUCCESS:
//                            PetInfoInstance.getInstance().setAtHome(false);
//                            break;
//                    }
//                }
//
//                @Override
//                public void onFail(Call<BaseBean> call, Throwable t) {
//
//                }
//            });

            finish();
        }
    }

    @Override
    public void onConfirmNumber(final int number) {
        //设定运动目标
//        HttpUtil.get2("pet.healthy.set_sport_info", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.v("http", "pet.healthy.set_sport_info:" + response.toString());
//                HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                if (ret == HttpCode.EC_SUCCESS) {
//                    Toast.makeText(HealthIndexActivity.this,"设定运动量成功",Toast.LENGTH_LONG).show();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
//            {
//                Toast.makeText(HealthIndexActivity.this,"网络连接失败",Toast.LENGTH_LONG).show();
//            }
//
//        },UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),number );
        ApiUtils.getApiService().setSportInfo(UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),number).enqueue(new XMQCallback<BaseBean>() {
            @Override
            public void onSuccess(Response<BaseBean> response, BaseBean message) {
                HttpCode ret = HttpCode.valueOf(message.status);
                switch (ret) {
                    case EC_SUCCESS:
                        ToastUtil.showTost("设定运动量成功");
                        EventManage.targetSportData event= new EventManage.targetSportData();
                        PetInfoInstance.getInstance().setTarget_step(number);
                        EventBus.getDefault().post(event);
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
            case R.id.health_index_bt_sport:
                if(PetInfoInstance.getInstance().getAtHome())
                {
                    Intent intent = new Intent(HealthIndexActivity.this,GoOutConfirmDialog_RAW_Activity.class);
                    startActivityForResult(intent,REQ_CODE_GO_OUT );
                }else
                {
                    Toast.makeText(v.getContext(), "宠物运动中", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.health_index_bt_expert:
                break;
            case R.id.health_index_bt_change:
                PickSportNumberDialog_RAW_Activity dialog = new PickSportNumberDialog_RAW_Activity(HealthIndexActivity.this,R.style.MyDialogStyleBottom);
                dialog.setOnPickNumberListener(HealthIndexActivity.this);
                dialog.show();
                break;
        }
    }

}
