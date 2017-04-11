package com.xiaomaoqiu.now.view;


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

import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.LoginPresenter;
import com.xiaomaoqiu.now.bussiness.user.LogoutView;
import com.xiaomaoqiu.pet.R;

@SuppressLint("WrongConstant")
public class ExitDialog_RAW_Activity extends Activity implements LogoutView{
	Button btn_exit_ok;
	Button btn_exit_cacel;

	LoginPresenter loginPresenter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog_from_settings);

		btn_exit_ok= (Button) findViewById(R.id.btn_exit_ok);
		btn_exit_ok.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				loginPresenter.logout();
			}
		});
		btn_exit_cacel= (Button) findViewById(R.id.btn_exit_cacel);
		btn_exit_cacel.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				finish();
			}
		});

		loginPresenter=new LoginPresenter(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}

	@Override
	public void success() {
		Intent intent = new Intent(ExitDialog_RAW_Activity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
		overridePendingTransition(0, 0);
		startActivity(intent);
	}
}
