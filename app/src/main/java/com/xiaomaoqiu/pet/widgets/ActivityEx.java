package com.xiaomaoqiu.pet.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.notificationCenter.NotificationCenter;

/**
 * Created by Administrator on 2015/6/17.
 */
public class ActivityEx extends Activity {
    private static String TAG = "ActivityEx";
    private ViewGroup m_titleView;
    private TextView mNextView;

    //标题栏模板
    public int frameTemplate(){
        return R.layout.activity_template;
    }

    //是否显示返回按钮
    public boolean canGoBack()
    {
        return true;
    }

    public ViewGroup getTitleView()
    {
        return m_titleView;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        NotificationCenter.INSTANCE.addObserver(this);
    }

    private View.OnClickListener onNextClickLisener;
    public void setNextView(String text, View.OnClickListener listener){
        if(listener == null){
            return;
        }
        onNextClickLisener=listener;
        TextView view=(TextView)findViewById(R.id.bt_next);
        view.setVisibility(View.VISIBLE);
        view.setText(text);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClickLisener.onClick(v);
            }
        });
    }
    @Override
    protected void onDestroy()
    {
        NotificationCenter.INSTANCE.removeObserver(this);
        super.onDestroy();
    }
    public ViewGroup getRightView(){ return (ViewGroup)m_titleView.findViewById(R.id.fl_right);}

    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);
        if(m_titleView==null)
            return;
        TextView tvTitle = (TextView)m_titleView.findViewById(R.id.tv_title);
        if(tvTitle!=null)
        {
            tvTitle.setText(getTitle());
        }
    }

    @Override
    public void setContentView(int layoutResID)
    {
        if(frameTemplate() == 0)
        {
            super.setContentView(layoutResID);
        }else
        {
            super.setContentView(frameTemplate());
            m_titleView = (ViewGroup)findViewById(R.id.ll_title);
            View btnGoBack = findViewById(R.id.btn_go_back);
            ViewGroup vContainer = (ViewGroup) findViewById(R.id.fl_container);
            if(btnGoBack==null)
            {
                Log.e(TAG, "find go back button in template failed");
                return;
            }
            if(vContainer==null)
            {
                Log.e(TAG, "find container in template failed");
                return;
            }

            if(canGoBack())
            {
                btnGoBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }else
            {
                btnGoBack.setVisibility(View.INVISIBLE);
            }

            TextView tvTitle = (TextView)m_titleView.findViewById(R.id.tv_title);
            if(tvTitle!=null)
            {
                tvTitle.setText(getTitle());
            }
            getLayoutInflater().inflate(layoutResID, vContainer);
        }
    }

    protected void showToast(String Msg){
        Toast.makeText(this,Msg,Toast.LENGTH_SHORT).show();
    }
}
