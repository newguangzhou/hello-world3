package com.xiaomaoqiu.pet.ui.mainPages.pageMe;

import android.text.InputType;
import android.widget.EditText;

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
