package com.xiaomaoqiu.now.bussiness.pet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.bean.PetInfoBean;
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.user.LoginActivity;
import com.xiaomaoqiu.now.bussiness.user.LoginPresenter;
import com.xiaomaoqiu.now.bussiness.user.LogoutView;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.DialogToast;
import com.xiaomaoqiu.now.view.crop.Crop;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.InputDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyNameDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyWeightDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.SelectAvatarSourceDialog;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import mbg.bottomcalender.BottomCalenderView;


/**
 * Created by Administrator on 2015/6/12.
 */
public class AddPetInfoActivity extends BaseActivity implements LogoutView {
    private final int REQ_CODE_BIRTHDAY = 1;
    private final int REQ_CODE_WEIGHT = 2;
    private final int REQ_CODE_INTRO = 3;
    private final int REQ_CODE_NAME = 4;
    private final int REQ_CODE_VARIETY = 5;


    private final int REQ_CODE_PHOTO_SOURCE = 6;//选择方式
    private final int REQ_CODE_GET_PHOTO_FROM_GALLERY = 10;//从相册获取
    private final int REQ_CODE_GET_PHOTO_FROM_TAKEPHOTO = 11;//拍照完

    SimpleDraweeView imgLogo;
    private ToggleButton chk_gender;
    private TextView txt_birthday;
    private TextView txt_weight;
    private TextView txt_pet_name;
    private TextView txt_variety;


    PetInfoBean modifyBean = PetInfoInstance.getInstance().packBean;

    LoginPresenter loginPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.pet_info));
        setContentView(R.layout.me_pet_info);
        initView();
        setNextView("下一步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DoubleClickUtil.isFastMiniDoubleClick()) {
                    return;
                }
                if (TextUtils.isEmpty(modifyBean.birthday) || TextUtils.isEmpty(modifyBean.nick) || TextUtils.isEmpty(modifyBean.weight)||TextUtils.isEmpty(modifyBean.description)) {
                    ToastUtil.showTost("信息需要完整");
                    return;
                }

                PetInfoInstance.getInstance().addPetInfo(modifyBean);
            }
        });
        EventBus.getDefault().register(this);
        loginPresenter=new LoginPresenter(this);

    }


    private void initView() {


        findViewById(R.id.img_pet_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                modifyAvatar();
            }
        });

        findViewById(R.id.btn_modify_birthday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyBirthday();
            }
        });

        findViewById(R.id.btn_modify_weight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyWeight();
            }
        });

        /*findViewById(R.id.btn_modify_intro).setOnClickListener(now View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyIntro();
            }
        });*/

        findViewById(R.id.btn_modify_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyName();
            }
        });

        findViewById(R.id.btn_modify_variety).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyVariety();
            }
        });

        chk_gender = (ToggleButton) findViewById(R.id.chk_gender);

        modifyBean = PetInfoInstance.getInstance().packBean;
        initPetInfo();
    }

    private void initPetInfo() {
        View btnGoBack = findViewById(R.id.btn_go_back);
        btnGoBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogToast.createDialogWithTwoButton(AddPetInfoActivity.this, "确认退出登录？", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                loginPresenter.logout();
                            }
                        }
                );
            }
        });


        txt_pet_name = (TextView) findViewById(R.id.txt_pet_name);
        if (!TextUtils.isEmpty(modifyBean.nick)) {
            (txt_pet_name).setText(modifyBean.nick);
        } else {
            modifyBean.nick = "旺财";
        }


        ((ToggleButton) findViewById(R.id.chk_gender)).setChecked(modifyBean.sex == Constants.Female);
        if (modifyBean.sex == Constants.Female) {
            modifyBean.sex = Constants.Female;
            ((ImageView) findViewById(R.id.img_pet_sex)).setImageResource(R.drawable.petinfo_sex_female);
        } else {
            ((ImageView) findViewById(R.id.img_pet_sex)).setImageResource(R.drawable.petinfo_sex_male);
            modifyBean.sex = Constants.Male;
        }

        txt_birthday = (TextView) findViewById(R.id.txt_birthday);
        txt_birthday.setText(modifyBean.birthday);
        txt_weight = (TextView) findViewById(R.id.txt_weight);
        if (!TextUtils.isEmpty(modifyBean.weight)) {

            txt_weight.setText(modifyBean.weight + "kg");
        }

        txt_variety = (TextView) findViewById(R.id.txt_variety);


        imgLogo = (SimpleDraweeView) findViewById(R.id.img_pet_avatar);
        Uri uri = Uri.parse(modifyBean.logo_url);
        imgLogo.setImageURI(uri);
//        AsyncImageTask.INSTANCE.loadImage(imgLogo, petInfoBean.logo_url, this);

        ((TextView) findViewById(R.id.txt_variety)).setText(modifyBean.description);


        chk_gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                PetInfoBean tmpBean = PetInfoInstance.getInstance().getPackBean();
                if (isChecked) {
                    modifyBean.sex = Constants.Female;
                } else {
                    modifyBean.sex = Constants.Male;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void addPetInfoSuccess(EventManage.addPetInfoSuccess event) {
        EventBus.getDefault().unregister(this);
        Intent intent;
//        if (TextUtils.isEmpty(UserInstance.getInstance().device_imei)) {
//            intent = new Intent(AddPetInfoActivity.this, InitBindDeviceActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }

        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            intent = new Intent(AddPetInfoActivity.this, InitWifiListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        intent = new Intent(AddPetInfoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    private void modifyVariety() {
        Intent intent = new Intent(this, SelectPetTypeActivity.class);
        startActivityForResult(intent, REQ_CODE_VARIETY);
    }

    public void modifyAvatar() {
        Intent intent = new Intent(this, SelectAvatarSourceDialog.class);
        startActivityForResult(intent, REQ_CODE_PHOTO_SOURCE);
    }


    private void modifyName() {
        Intent intent = new Intent(this, ModifyNameDialog.class);
        intent.putExtra(InputDialog.TAG_VALUE, modifyBean.nick);
        startActivityForResult(intent, REQ_CODE_NAME);
    }

    private BottomCalenderView bottomCalenderView;

    private void modifyBirthday() {
        PetInfoInstance.MyDate mPetBirthDay = modifyBean.dateFormat_birthday;
        if (bottomCalenderView == null) {
            bottomCalenderView = new BottomCalenderView(this, 2000, 1, 1, new BottomCalenderView.OnDatePickedListener() {
                @Override
                public void onDatePicked(int year, int month, int day) {
                    PetInfoInstance.MyDate tmpDateFormatBirthday = new PetInfoInstance.MyDate(year, month, day);
                    modifyBean.dateFormat_birthday = tmpDateFormatBirthday;
                    modifyBean.birthday = tmpDateFormatBirthday.toString();
                    txt_birthday.setText(modifyBean.birthday);
                }
            }, null);
        }
        bottomCalenderView.show();
    }

    private void modifyWeight() {
        Intent intent = new Intent(this, ModifyWeightDialog.class);
        intent.putExtra(InputDialog.TAG_VALUE, modifyBean.weight);
        startActivityForResult(intent, REQ_CODE_WEIGHT);
    }

//    private void modifyIntro()
//    {
//        Intent intent = new Intent(this,OUT___ModifyIntroDialog.class);
//        intent.putExtra(InputDialog.TAG_VALUE, UserMgr.INSTANCE.getPetInfo().getDesc());
//        startActivityForResult(intent, REQ_CODE_INTRO);
//    }

    private void onPhotoSource(int mode) {
        if (mode == R.id.btn_pick_from_library) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, REQ_CODE_GET_PHOTO_FROM_GALLERY);

        } else if (mode == R.id.btn_take_photo) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "temp.jpg")));
            startActivityForResult(intent, REQ_CODE_GET_PHOTO_FROM_TAKEPHOTO);
        }
    }


    private void beginCrop(Uri source, Bundle bundle) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "Bcropped"));
        Crop.of(source, destination).asSquare().start(this, bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_NAME:
                if (data != null) {
                    String nameBackString = data.getStringExtra(InputDialog.TAG_VALUE);
                    modifyBean.nick = nameBackString;
                    (txt_pet_name).setText(modifyBean.nick);
                }
                break;
            case REQ_CODE_WEIGHT:// ModifyWeightDialog
                if (data != null) {
                    modifyBean.weight = data.getStringExtra(InputDialog.TAG_VALUE);
                    txt_weight.setText(modifyBean.weight + "kg");
                }
                break;
            case REQ_CODE_VARIETY:
            case REQ_CODE_INTRO:
                    modifyBean.description = PetUtil.getInstance().dogName;
                    modifyBean.target_energy=PetUtil.getInstance().energyType;
//                UserMgr.INSTANCE.updatePetInfo(petInfo, PetInfo.FieldMask_Desc);
                    txt_variety.setText(modifyBean.description);
//                    PetInfoInstance.getInstance().updatePetInfo(modifyBean,param);
                break;
            case REQ_CODE_PHOTO_SOURCE:
                if(data!=null) {
                    int mode = data.getIntExtra(SelectAvatarSourceDialog.TAG_MODE, -1);
                    onPhotoSource(mode);
                }


                break;
            case REQ_CODE_GET_PHOTO_FROM_GALLERY:
                if (data != null && data.getData() != null) {

                    Bundle bundle = new Bundle();
                    // 选择图片后进入裁剪
                    String path = data.getData().getPath();
                    Uri source = data.getData();
                    beginCrop(source, bundle);
                }
                break;
            case REQ_CODE_GET_PHOTO_FROM_TAKEPHOTO:
                File picture = new File(Environment.getExternalStorageDirectory()
                        + "/temp.jpg");
                Bundle bundle = new Bundle();
                // 选择图片后进入裁剪
                Uri source = Uri.fromFile(picture);
                beginCrop(source, bundle);

                break;

            case Crop.REQUEST_CROP:
                modifyBean.logo_url = PetInfoInstance.getInstance().getPackBean().logo_url;
                Uri uri = Uri.parse(modifyBean.logo_url);
                imgLogo.setImageURI(uri);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void success() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DialogToast.createDialogWithTwoButton(this, "确定要退出小毛球吗？", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }
}