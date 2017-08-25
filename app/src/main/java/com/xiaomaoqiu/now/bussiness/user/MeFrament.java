package com.xiaomaoqiu.now.bussiness.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomaoqiu.now.base.BaseFragment;
import com.xiaomaoqiu.now.bussiness.AboutActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.Device.MeWifiListActivity;
import com.xiaomaoqiu.now.bussiness.BaseWebViewActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.PetInfoActivity;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.ContactServiceDialog;
import com.xiaomaoqiu.pet.R;

/**
 * Created by Administrator on 2015/6/12.
 */
public class MeFrament extends BaseFragment implements LogoutView{

    LoginPresenter loginPresenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View mRoot = inflater.inflate(R.layout.main_tab_me, container, false);

        initView(mRoot);
        loginPresenter=new LoginPresenter(this);


        return mRoot;
    }


    private void initView(View root) {
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
                DialogUtil.showTwoButtonDialog(getActivity(),getString(R.string.dialog_exit_login_title),getString(R.string.dialog_exit_login_tab1),getString(R.string.dialog_exit_login_tab2),new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                    }
                },
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        loginPresenter.logout();
                    }
                }
                );
            }
        });

        root.findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_contact_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactServiceDialog dlg = new ContactServiceDialog(MeFrament.this.getActivity());
                dlg.show();
            }
        });

        root.findViewById(R.id.btn_mall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), BaseWebViewActivity.class);
                intent.putExtra("web_url","https://www.xiaomaoqiu.com/wechat_shop.html");
                intent.putExtra("title","小毛球商城");
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_hardware).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DeviceActivity.class);
                    startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_me_home_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DeviceInfoInstance.getInstance().online){
                    ToastUtil.showTost("追踪器已离线，此功能暂时无法使用");
                    return;
                }
                Intent intent=new Intent();
                intent.setClass(getActivity(), MeWifiListActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void success() {
        if(getActivity()==null){
            return;
        }
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}