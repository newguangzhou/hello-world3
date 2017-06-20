package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.text.InputType;
import android.widget.EditText;

import com.xiaomaoqiu.now.base.InputDialog;

public class ModifyWeightDialog extends InputDialog {

    @Override
    protected CharSequence getInputTitle()
    {
        return "修改体重";
    }

    @Override
    protected CharSequence getInputLabel()
    {
        return "请输入狗狗体重(kg)";
    }

    @Override
    protected void setEditMode(EditText v)
    {
        v.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
    }
}
