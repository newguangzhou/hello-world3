package com.xiaomaoqiu.now.bussiness.user;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.now.bussiness.MainActivity;
@SuppressLint("WrongConstant")
public class ExitFromSettings_RAW_Activity extends Activity {
	//private MyDialog dialog;
	private LinearLayout layout;
	Button exitBtn0;
	Button exitBtn1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog_from_settings);
		//dialog=now MyDialog(this);
		layout=(LinearLayout)findViewById(R.id.exit_layout2);
		exitBtn0= (Button) findViewById(R.id.exitBtn0);
		exitBtn1= (Button) findViewById(R.id.exitBtn1);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
		exitBtn1.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		exitBtn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				finish();
//				MainActivity.instance.finish();//关闭Main 这个Activity
				Intent intent = new Intent(ExitFromSettings_RAW_Activity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
				overridePendingTransition(0, 0);
				startActivity(intent);
				//todo 退出登录之后填写
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	

	
}
