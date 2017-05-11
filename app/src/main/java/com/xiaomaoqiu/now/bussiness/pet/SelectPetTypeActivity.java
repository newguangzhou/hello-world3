package com.xiaomaoqiu.now.bussiness.pet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.pet.R;

/**
 * Created by long on 2017/5/10.
 */

public class SelectPetTypeActivity extends BaseActivity implements View.OnClickListener {

    private final int REQ_CODE_VARIETY = 5;

    View iv_pettype_dog;
    View iv_pettype_cat;
    View iv_pettype_other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("选择品种");
        setContentView(R.layout.activity_selectpettype);
        initView();
        initListner();
    }

    void initView() {
        iv_pettype_dog = findViewById(R.id.iv_pettype_dog);
        iv_pettype_cat = findViewById(R.id.iv_pettype_cat);
        iv_pettype_other = findViewById(R.id.iv_pettype_other);
    }

    void initListner() {
        iv_pettype_dog.setOnClickListener(this);
        iv_pettype_cat.setOnClickListener(this);
        iv_pettype_other.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        switch (v.getId()) {
            case R.id.iv_pettype_dog:
                Intent intent = new Intent(this, selectDog1Activity.class);
                startActivityForResult(intent, REQ_CODE_VARIETY);
                break;

            case R.id.iv_pettype_cat:

                data.putExtra(selectDog1Activity.TAG_PARAM1, "猫");
                setResult(1, data);
                finish();
                break;

            case R.id.iv_pettype_other:

                data.putExtra(selectDog1Activity.TAG_PARAM1, "其他");
                setResult(1, data);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_CODE_VARIETY:
            if (data != null) {
                data.putExtra(selectDog1Activity.TAG_PARAM1,  data.getStringExtra(selectDog2Activity.TAG_PARAM1));
                setResult(1, data);
                finish();
            }
            break;
        }
    }
}
