package com.xiaomaoqiu.old.ui.dialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.pet.R;
@SuppressLint("WrongConstant")
public class GoOutConfirmDialog_RAW_Activity extends Activity  {
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
		String url="";
//        PetInfo petInfo= UserMgr.INSTANCE.getPetInfo();
//        if(null != petInfo){
//            url=petInfo.getHeaderImg();
//        }
		url= PetInfoInstance.getInstance().packBean.logo_url;

		SimpleDraweeView imgLogo = (SimpleDraweeView)findViewById(R.id.go_sport_dialog_head);
		Uri uri = Uri.parse(url);
		imgLogo.setImageURI(uri);
//		AsyncImageTask.INSTANCE.loadImage(imgLogo, url, this);
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
