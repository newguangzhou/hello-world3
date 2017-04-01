package com.xiaomaoqiu.pet.ui.mainPages;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.ui.dialog.ContactServiceDialog;
import com.xiaomaoqiu.pet.ui.dialog.ExitDialog;
import com.xiaomaoqiu.pet.ui.mainPages.pageMe.AboutActivity;
import com.xiaomaoqiu.pet.ui.mainPages.pageMe.HardwareActivity;
import com.xiaomaoqiu.pet.ui.mainPages.pageMe.MessageActivity;
import com.xiaomaoqiu.pet.ui.mainPages.pageMe.PetInfoActivity;
import com.xiaomaoqiu.pet.ui.mainPages.pageMe.hardware.BindDevice;
import com.xiaomaoqiu.pet.widgets.FragmentEx;

/**
 * Created by Administrator on 2015/6/12.
 */
public class PageMe extends FragmentEx {

    static int REQ_EXIT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View mRoot = inflater.inflate(R.layout.main_tab_me, container, false);

        initView(mRoot);

        return mRoot;
    }


    private void initView(View root)
    {
        root.findViewById(R.id.btn_pet_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PetInfoActivity.class);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ExitDialog.class);
                startActivityForResult(intent,REQ_EXIT);
            }
        });

        root.findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_message).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_contact_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactServiceDialog dlg = new ContactServiceDialog(PageMe.this.getActivity());
                dlg.show();
            }
        });

        root.findViewById(R.id.btn_mall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getString(R.string.url_mall));
                Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_hardware).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserMgr.INSTANCE.getPetInfo().getDevInfo().getDeviceExist()) {
                    Intent intent=new Intent(getActivity(), HardwareActivity.class);
                    startActivity(intent);
                }
                else{
                    BindDevice.skipTo(v.getContext());
                }

            }
        });

        root.findViewById(R.id.btn_me_home_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_EXIT && resultCode == Activity.RESULT_OK)
        {
            getActivity().finish();
        }
    }
}