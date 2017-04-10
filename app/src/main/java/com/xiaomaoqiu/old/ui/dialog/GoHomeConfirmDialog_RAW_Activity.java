package com.xiaomaoqiu.old.ui.dialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaomaoqiu.pet.R;

@SuppressLint("WrongConstant")
public class GoHomeConfirmDialog_RAW_Activity extends Activity {
	//private MyDialog dialog;
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_go_home);

		layout=(LinearLayout)findViewById(R.id.ll_root);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}

	public void onBtnConfirm(View v) {
		setResult(RESULT_OK);
		finish();
	}
	public void onBtnCancel(View v) {
		setResult(RESULT_CANCELED);
		this.finish();
	}
	
}
