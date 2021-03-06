package com.xiaomaoqiu.now.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.pet.R;

public class InputDialog extends BaseActivity {
    public static String TAG_VALUE = "value";

    protected CharSequence getInputTitle()
    {
        return "undefined";
    }

    protected CharSequence getInputLabel()
    {
        return "";
    }

    protected void setEditMode(EditText v)
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getInputTitle());
        setContentView(R.layout.dlg_input);
        setNextView("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeValue();
            }
        });

        Intent intent = getIntent();
        EditText editText = (EditText) findViewById(R.id.edit_value);

        setEditMode(editText);

        editText.setText(intent.getStringExtra(TAG_VALUE));
        editText.setHint(getInputLabel());


        Editable text = editText.getText();
        Spannable spanText = text;
        Selection.setSelection(spanText, text.length());

    }

    public void changeValue(){
        EditText editText = (EditText) findViewById(R.id.edit_value);
        String name=editText.getText().toString();
        if(TextUtils.isEmpty(name)){
            showToast("输入不能为空，请输入！");
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
