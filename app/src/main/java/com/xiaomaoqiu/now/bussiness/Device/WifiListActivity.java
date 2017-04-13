package com.xiaomaoqiu.now.bussiness.Device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/4/12.
 */

public class WifiListActivity extends BaseActivity {



//    RecyclerView mRecyclerView;
//    List<>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.title_home_wifi));
        setContentView(R.layout.activity_wifilist);
        setNextView("下一步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        initView();
    }

    private void initView() {

    }


}
