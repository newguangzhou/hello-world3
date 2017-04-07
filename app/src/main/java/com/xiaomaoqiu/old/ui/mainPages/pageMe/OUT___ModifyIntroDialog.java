package com.xiaomaoqiu.old.ui.mainPages.pageMe;

import android.text.InputType;
import android.widget.EditText;

public class OUT___ModifyIntroDialog extends InputDialog {

    @Override
    protected CharSequence getInputTitle()
    {
        return "修改介绍";
    }

    @Override
    protected CharSequence getInputLabel()
    {
        return "请输入介绍内容";
    }

    @Override
    protected void setEditMode(EditText v)
    {
        v.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
    }
}
