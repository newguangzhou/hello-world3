package com.xiaomaoqiu.pet.ui.dialog;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.PetInfo;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.utils.AsyncImageTask;

public class GoOutConfirmDialog extends Activity implements AsyncImageTask.ImageCallback {
	//private MyDialog dialog;
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_go_out);


		layout=(LinearLayout)findViewById(R.id.ll_root);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
		loadImage();
	}

	private void loadImage(){
		PetInfo info=UserMgr.INSTANCE.getPetInfo();
		if(null == info || TextUtils.isEmpty(info.getHeaderImg())){
			return;
		}
		ImageView imgLogo = (ImageView)findViewById(R.id.go_sport_dialog_head);
		AsyncImageTask.INSTANCE.loadImage(imgLogo, info.getHeaderImg(), this);
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

	@Override
	public void imageLoaded(String url, Bitmap obj, ImageView view) {
		if(obj != null)
		{
			view.setImageBitmap(obj);
		}
	}
}
