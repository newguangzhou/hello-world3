package com.xiaomaoqiu.now.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.xiaomaoqiu.pet.R;


/**
 * Dialog 工具类
 * @author user
 *
 */
public class AppDialog extends Dialog {

	public AppDialog(Context context, int layout, int width, int height,
                     int animId, int gravity) {
		this(context,R.style.AppDialog, layout, width, height,
		animId, gravity);
	}
	public AppDialog(Context context, int style, int layout, int width, int height,
                     int animId, int gravity) {
		super(context,style);
		setContentView(layout);
		Window window = getWindow();
		window.setWindowAnimations(animId);
		window.setGravity(gravity);
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = width;
		params.height = height;
		window.setAttributes(params);
	}

	public AppDialog(Context context) {
		super(context, R.style.AppDialog);
	}

}
