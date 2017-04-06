package com.xiaomaoqiu.old.ui.mainPages.pageMe;

import android.text.InputType;
import android.widget.EditText;

public class ModifyNameDialog extends InputDialog {

    @Override
    protected CharSequence getInputTitle()
    {
        return "修改名字";
    }

    @Override
    protected CharSequence getInputLabel()
    {
        return "请输入狗狗名字";
    }

    @Override
    protected void setEditMode(EditText v)
    {
        v.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
    }
}
