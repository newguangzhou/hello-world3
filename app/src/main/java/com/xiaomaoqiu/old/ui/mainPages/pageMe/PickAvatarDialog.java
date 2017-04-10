package com.xiaomaoqiu.old.ui.mainPages.pageMe;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomaoqiu.pet.R;


/**
 * Created by simon on 15/8/8.
 */
public class PickAvatarDialog extends DialogFragment implements View.OnClickListener
{
    interface OnPickPhotoMode
    {
        void onPhotoMode(DialogFragment dialog,int mode);
    }

    OnPickPhotoMode m_PickPhotoModeListener;

    public void setM_PickPhotoModeListener(OnPickPhotoMode pls){
        m_PickPhotoModeListener = pls;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_pick_photo,container);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        v.findViewById(R.id.btn_pick_from_library).setOnClickListener(this);
        v.findViewById(R.id.btn_take_photo).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if(m_PickPhotoModeListener!=null) {
            m_PickPhotoModeListener.onPhotoMode(this, v.getId());
        } else {
            dismiss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentStyleOver);
    }

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null) {
            return;
        }

        android.view.Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}