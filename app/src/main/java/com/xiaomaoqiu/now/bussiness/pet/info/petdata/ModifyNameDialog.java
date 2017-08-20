package com.xiaomaoqiu.now.bussiness.pet.info.petdata;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import com.xiaomaoqiu.now.base.InputDialog;
import com.xiaomaoqiu.pet.R;

public class ModifyNameDialog extends InputDialog {

    @Override
    protected CharSequence getInputTitle() {
        return "修改名字";
    }

    @Override
    protected CharSequence getInputLabel() {
        return "填写宠物宝宝昵称";
    }

    @Override
    protected void setEditMode(EditText v) {
        v.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EditText editText = (EditText) findViewById(R.id.edit_value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int byteLength = editable.toString().getBytes().length;
                int charLenght = editable.toString().length();

                if (byteLength >= 16) {
                    editable.delete(charLenght,charLenght+1);
                }
            }
        });
    }
}
