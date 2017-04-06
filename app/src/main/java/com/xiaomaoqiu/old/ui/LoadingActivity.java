package com.xiaomaoqiu.old.ui;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.xiaomaoqiu.old.R;

public class LoadingActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.loading);
			
	new Handler().postDelayed(new Runnable(){
		@Override
		public void run(){
			Intent intent = new Intent (LoadingActivity.this,WhatsnewActivity.class);
			startActivity(intent);
			LoadingActivity.this.finish();
			Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
		}
	}, 200);
   }
}