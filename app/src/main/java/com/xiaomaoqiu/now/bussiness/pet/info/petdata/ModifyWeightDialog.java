package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import com.xiaomaoqiu.now.base.InputDialog;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.pet.R;

public class ModifyWeightDialog extends InputDialog {

    @Override
    protected CharSequence getInputTitle()
    {
        return "修改体重";
    }

    @Override
    protected CharSequence getInputLabel()
    {
        return "请输入宠物体重(kg)";
    }

    @Override
    protected void setEditMode(EditText v)
    {
        v.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public void changeValue() {
        EditText editText = (EditText) findViewById(R.id.edit_value);
        String name=editText.getText().toString();
        if(TextUtils.isEmpty(name)){
            ToastUtil.showTost("输入不能为空，请输入！");
            return;
        }
        double temp=Double.valueOf(name);
        if(!(temp>0)){
            ToastUtil.showTost("体重要大于0");
            return;
        }
        if(temp>200){
            ToastUtil.showTost("体重不能大于200");
            return;
        }
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(TAG_VALUE, editText.getText().toString());
        //设置返回数据
        setResult(RESULT_OK, intent);
        finish();
    }
}
