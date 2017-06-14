package com.xiaomaoqiu.now.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaomaoqiu.old.utils.DensityUtil;
import com.xiaomaoqiu.pet.R;

/**
 * Created by Administrator on 2016/12/31.
 */

public class DialogToast extends Dialog implements View.OnClickListener {

    private String mContent,mOkText,mQuitText;
    private View.OnClickListener clickListener;
    private View.OnClickListener quitClickListener;

    private TextView mContentText;
    private Button mButton;
    private Button mButtonQuit;

    /**
     * 只有一个按钮
     * @param context
     * @param content
     * @param okBtnText
     * @param clickListener
     */
    public DialogToast(Context context, String content, String okBtnText, View.OnClickListener clickListener){
        this(context,content,okBtnText,clickListener,true);
    }

    /**
     * 只有一个按钮
     * @param context
     * @param content
     * @param okBtnText
     * @param clickListener
     * @param canTouchCancle
     */
    public DialogToast(Context context, String content, String okBtnText, View.OnClickListener clickListener,boolean canTouchCancle){
        super(context, R.style.MyDialogStyleBottom);
        this.mContent=content;
        this.mOkText=okBtnText;
        this.clickListener=clickListener;
        initViewOnlyOk(R.layout.dialog_toast_layout);
        setCanceledOnTouchOutside(canTouchCancle);
        show();
    }


    /**
     * 创建两个按钮对话框
     * @param context
     * @param content
     * @param okListener
     * @param quitListener
     * @return
     */
    public static DialogToast createDialogWithTwoButton(Context context, String content,  View.OnClickListener okListener,View.OnClickListener quitListener){
        return new DialogToast(context,content,okListener,quitListener);
    }


    public static DialogToast createDialogWithTwoButton(Context context, String content,  View.OnClickListener okListener){
        return new DialogToast(context,content,okListener,null);
    }

    public static DialogToast createDialogWithTwoButtonWithOKText(Context context, String content, String okText,String quitBenText, View.OnClickListener okListener,View.OnClickListener quitClickListener){
        return new DialogToast(context,content,okText,quitBenText,okListener,quitClickListener,false);
    }

    /**
     * 有两个按钮
     * @param context
     * @param content
     * @param okListener
     * @param quitListener
     */
    public DialogToast(Context context, String content,  View.OnClickListener okListener,View.OnClickListener quitListener){
        this(context,content,null,null,okListener,quitListener,true);
    }
    /**
     * 有两个按钮
     * @param context
     * @param content
     * @param okBtnText
     * @param quitBenText
     * @param okListener
     * @param quitListener
     * @param canTouchCancle
     */
    public DialogToast(Context context, String content, String okBtnText,String quitBenText, View.OnClickListener okListener,View.OnClickListener quitListener,boolean canTouchCancle){
        super(context, R.style.MyDialogStyleBottom);
        this.mContent=content;
        this.mOkText=okBtnText;
        this.mQuitText=quitBenText;
        this.clickListener=okListener;
        this.quitClickListener=quitListener;
        initViewWithQuit(R.layout.dialog_toast_layout2);
        setCanceledOnTouchOutside(canTouchCancle);
        show();
    }

    private void initViewWithQuit(int layoutId){
        initViewOnlyOk(layoutId);
        mButtonQuit=(Button)findViewById(R.id.dialog_toast_quit);
        mButtonQuit.setOnClickListener(this);
        if(!TextUtils.isEmpty(mQuitText)){
            mButtonQuit.setText(mQuitText);
        }
    }

    private void initViewOnlyOk(int layoutId){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutId, null);
        setContentView(view);
        initParams();

        mContentText=(TextView)findViewById(R.id.dialog_toast_content);
        mContentText.setText(mContent);
        mButton=(Button)findViewById(R.id.dialog_toast_ok);
        mButton.setOnClickListener(this);
        if(!TextUtils.isEmpty(mOkText)){
            mButton.setText(mOkText);
        }
    }
    private void initParams(){
        //获取到当前Activity的Window
        Window dialog_window = getWindow();
        //获取到LayoutParams
        WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
        //设置宽度
        int margin=getContext().getResources().getDimensionPixelSize(R.dimen.dialog_service_margin)*2;
        dialog_window_attributes.width= DensityUtil.getScreenWidth(getContext())-margin;
        dialog_window.setAttributes(dialog_window_attributes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_toast_ok:
                dismiss();
                if(null == clickListener){
                    return;
                }
                clickListener.onClick(v);
                break;
            case R.id.dialog_toast_quit:
                dismiss();
                if(null ==  quitClickListener){
                    return;
                }
                quitClickListener.onClick(v);
                break;
        }
    }
}
