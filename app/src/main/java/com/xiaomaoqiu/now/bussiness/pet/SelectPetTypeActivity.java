package com.xiaomaoqiu.now.bussiness.pet;

import android.app.Activity;
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
        int pet_type_id;
        switch (v.getId()) {
            case R.id.iv_pettype_dog:
                Intent intent = new Intent(this, selectDog1Activity.class);
                startActivityForResult(intent, REQ_CODE_VARIETY);
                pet_type_id=1;
                finish();
                break;

            case R.id.iv_pettype_cat:

                PetUtil.getInstance().dogName=("猫");
                PetUtil.getInstance().energyType="1";
                pet_type_id=2;
                finish();
                break;

            case R.id.iv_pettype_other:

                PetUtil.getInstance().dogName=("其他");
                PetUtil.getInstance().energyType="1";
                pet_type_id=-1;
                finish();
                break;
            default:
                PetUtil.getInstance().dogName=("其他");
                PetUtil.getInstance().energyType="2";
                pet_type_id=-1;
                finish();
                break;
        }
        PetInfoInstance.getInstance().packBean.pet_type_id=pet_type_id;

    }
}
