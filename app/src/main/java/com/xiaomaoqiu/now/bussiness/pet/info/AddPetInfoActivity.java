package com.xiaomaoqiu.now.bussiness.pet.info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaomaoqiu.now.Constants;
import com.xiaomaoqiu.now.EventManage;
import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.Device.DeviceInfoInstance;
import com.xiaomaoqiu.now.bussiness.Device.InitBindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.Device.InitWifiListActivity;
import com.xiaomaoqiu.now.bussiness.InitMapLocationActivity;
import com.xiaomaoqiu.now.bussiness.MainActivity;
import com.xiaomaoqiu.now.bussiness.bean.PetInfoBean;
import com.xiaomaoqiu.now.bussiness.pet.PetInfoInstance;
import com.xiaomaoqiu.now.bussiness.pet.PetUtil;
import com.xiaomaoqiu.now.bussiness.pet.SelectPetTypeActivity;
import com.xiaomaoqiu.now.bussiness.user.RebootActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.util.DialogUtil;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.crop.Crop;
import com.xiaomaoqiu.now.base.InputDialog;
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.ModifyNameDialog;
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.ModifyWeightDialog;
import com.xiaomaoqiu.now.bussiness.pet.info.petdata.SelectAvatarSourceDialog;
import com.xiaomaoqiu.pet.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;

import mbg.bottomcalender.BottomCalenderView;


/**
 * Created by Administrator on 2015/6/12.
 */
public class AddPetInfoActivity extends BaseActivity {
    private final int REQ_CODE_BIRTHDAY = 1;
    private final int REQ_CODE_WEIGHT = 2;
    private final int REQ_CODE_INTRO = 3;
    private final int REQ_CODE_NAME = 4;
    private final int REQ_CODE_VARIETY = 5;


    private final int REQ_CODE_PHOTO_SOURCE = 6;//选择方式
    private final int REQ_CODE_GET_PHOTO_FROM_GALLERY = 10;//从相册获取
    private final int REQ_CODE_GET_PHOTO_FROM_TAKEPHOTO = 11;//拍照完


    View btn_go_back;
    View tv_next;


    SimpleDraweeView imgLogo;
    private ToggleButton chk_gender;
    private TextView txt_birthday;
    private TextView txt_weight;
    private EditText txt_pet_name;
    private TextView txt_variety;


    PetInfoBean modifyBean = PetInfoInstance.getInstance().packBean;

//    LoginPresenter loginPresenter;


    @Override
    public int frameTemplate() {//没有标题栏
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //未进入主页
        SPUtil.putHome(false);

        setContentView(R.layout.add_pet_info);
        initView();
        EventBus.getDefault().register(this);
//        loginPresenter=new LoginPresenter(this);

    }


    private void initView() {
        btn_go_back = this.findViewById(R.id.btn_go_back);
        btn_go_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                DialogToast.createDialogWithTwoButton(AddPetInfoActivity.this, "确认退出登录？", new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                loginPresenter.logout();
//                            }
//                        }
//                );
                DeviceInfoInstance.getInstance().unbindDevice();
            }
        });
        tv_next = this.findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (DoubleClickUtil.isFastMiniDoubleClick()) {
                    return;
                }
                if (TextUtils.isEmpty(modifyBean.nick)) {
                    ToastUtil.showTost("请填写宠物名字");
                    return;
                }
                if (TextUtils.isEmpty(modifyBean.birthday)) {
                    ToastUtil.showTost("请选择宠物生日");
                    return;
                }
                if (TextUtils.isEmpty(modifyBean.weight)) {
                    ToastUtil.showTost("请填写宠物体重");
                    return;
                }
                if (TextUtils.isEmpty(modifyBean.description)) {
                    ToastUtil.showTost("请选择宠物类别");
                    return;
                }
//                if (TextUtils.isEmpty(modifyBean.birthday) || TextUtils.isEmpty(modifyBean.nick) || TextUtils.isEmpty(modifyBean.weight)||TextUtils.isEmpty(modifyBean.description)) {
//                    ToastUtil.showTost("信息需要完整");
//                    return;
//                }

                PetInfoInstance.getInstance().addPetInfo(modifyBean);
            }
        });


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

        txt_pet_name = (EditText) findViewById(R.id.txt_pet_name);
        if (!TextUtils.isEmpty(modifyBean.nick)) {
            (txt_pet_name).setText(modifyBean.nick);
        }

        txt_pet_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                modifyName();
            }
        });

//        else {
//            modifyBean.nick = "旺财";
//        }


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

        ((TextView) findViewById(R.id.txt_variety)).setText(modifyBean.description);


        chk_gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

        if (TextUtils.isEmpty(UserInstance.getInstance().wifi_bssid)) {
            intent = new Intent(AddPetInfoActivity.this, InitWifiListActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (UserInstance.getInstance().latitude == -1) {
            intent = new Intent();
            intent.setClass(this, InitMapLocationActivity.class);
            startActivity(intent);
            finish();
            return;
        }
//        if (UserInstance.getInstance().agree_policy == 0) {
//            intent = new Intent(this, RebootActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
        intent = new Intent(AddPetInfoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    //设备离线
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void onDeviceOffline(EventManage.DeviceOffline event) {
        DialogUtil.showDeviceOfflineDialog(this, "离线通知");
    }

    //解绑成功
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 0)
    public void unbindDeviceSuccess(EventManage.unbindDeviceSuccess event) {
        Intent intent = new Intent(PetAppLike.mcontext, InitBindDeviceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PetAppLike.mcontext.startActivity(intent);

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
            bottomCalenderView = new BottomCalenderView(this, mPetBirthDay.year, mPetBirthDay.month, mPetBirthDay.day, new BottomCalenderView.OnDatePickedListener() {
                @Override
                public void onDatePicked(int year, int month, int day) {
                    PetInfoInstance.MyDate tmpDateFormatBirthday = new PetInfoInstance.MyDate(year, month, day);
                    modifyBean.dateFormat_birthday = tmpDateFormatBirthday;
                    PetInfoInstance.getInstance().packBean.dateFormat_birthday= modifyBean.dateFormat_birthday;
                    modifyBean.birthday = tmpDateFormatBirthday.toString();
                    txt_birthday.setText(modifyBean.birthday);
                    DecimalFormat df = new DecimalFormat("0.00");//格式化
                    PetInfoInstance.getInstance().setSuggest_energy(df.format(PetUtil.getInstance().calculateEnergy()));
                    modifyBean.target_energy =df.format(PetUtil.getInstance().calculateEnergy());
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
                    PetInfoInstance.getInstance().packBean.weight= modifyBean.weight;
                    DecimalFormat df = new DecimalFormat("0.00");//格式化
                    PetInfoInstance.getInstance().setSuggest_energy(df.format(PetUtil.getInstance().calculateEnergy()));
                    modifyBean.target_energy =df.format(PetUtil.getInstance().calculateEnergy());
                    txt_weight.setText(modifyBean.weight + "kg");

                }
                break;
            case REQ_CODE_VARIETY:
            case REQ_CODE_INTRO:
                modifyBean.description = PetUtil.getInstance().dogName;
                modifyBean.pet_type_id = PetInfoInstance.getInstance().packBean.pet_type_id;
                DecimalFormat df = new DecimalFormat("0.00");//格式化
                PetInfoInstance.getInstance().setSuggest_energy(df.format(PetUtil.getInstance().calculateEnergy()));
                modifyBean.target_energy =df.format(PetUtil.getInstance().calculateEnergy());
                txt_variety.setText(modifyBean.description);
                break;
            case REQ_CODE_PHOTO_SOURCE:
                if (data != null) {
                    int mode = data.getIntExtra(SelectAvatarSourceDialog.TAG_MODE, -1);
                    onPhotoSource(mode);
                }


                break;
            case REQ_CODE_GET_PHOTO_FROM_GALLERY:
                if (data != null && data.getData() != null) {

                    Bundle bundle = new Bundle();
                    // 选择图片后进入裁剪
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


//    public void success() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    @Override
    public void onBackPressed() {
        DialogUtil.showTwoButtonDialog(this,getString(R.string.dialog_exit_app_title),getString(R.string.dialog_exit_app_tab1),getString(R.string.dialog_exit_login_tab2),new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                    }
                },
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
//        DialogToast.createDialogWithTwoButton(this, "确定要退出小毛球吗？", new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                }
//        );
    }
}