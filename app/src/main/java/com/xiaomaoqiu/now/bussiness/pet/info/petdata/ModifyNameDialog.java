package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.text.InputType;
import android.widget.EditText;

import com.xiaomaoqiu.now.base.InputDialog;

public class ModifyNameDialog extends InputDialog {

    @Override
    protected CharSequence getInputTitle()
    {
        return "修改名字";
    }

    @Override
    protected CharSequence getInputLabel()
    {
        return "填写宠物宝宝昵称";
    }

    @Override
    protected void setEditMode(EditText v)
    {
        v.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
    }
}
