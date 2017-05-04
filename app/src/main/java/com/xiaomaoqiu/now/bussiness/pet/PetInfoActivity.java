package com.xiaomaoqiu.now.bussiness.pet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bean.nocommon.PetInfoBean;
import com.xiaomaoqiu.now.bean.nocommon.PictureBean;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.DoubleClickUtil;
import com.xiaomaoqiu.now.util.ImageUtil;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.crop.Crop;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.InputDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyNameDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyVarietyDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyVarietyDialog2;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyWeightDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.SelectAvatarSourceDialog;
import com.xiaomaoqiu.pet.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import mbg.bottomcalender.BottomCalenderView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Administrator on 2015/6/12.
 */
public class PetInfoActivity extends BaseActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.pet_info));
        setContentView(R.layout.me_pet_info);
        initView();
        setNextView("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DoubleClickUtil.isFastMiniDoubleClick()) {
                    return;
                }
                if (TextUtils.isEmpty(modifyBean.birthday) || TextUtils.isEmpty(modifyBean.name) || TextUtils.isEmpty(modifyBean.weight)) {
                    ToastUtil.showTost("信息需要完整");
                    return;
                }
                PetInfoInstance.getInstance().updatePetInfo(modifyBean);
            }
        });
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
        txt_pet_name = (TextView) findViewById(R.id.txt_pet_name);
        if (!TextUtils.isEmpty(modifyBean.name)) {
            (txt_pet_name).setText(modifyBean.name);
        } else {
            modifyBean.name = "旺财";
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


    private void modifyVariety() {
        Intent intent = new Intent(this, ModifyVarietyDialog.class);
        startActivityForResult(intent, REQ_CODE_VARIETY);
    }

    public void modifyAvatar() {
        Intent intent = new Intent(this, SelectAvatarSourceDialog.class);
        startActivityForResult(intent, REQ_CODE_PHOTO_SOURCE);
    }


    private void modifyName() {
        Intent intent = new Intent(this, ModifyNameDialog.class);
        intent.putExtra(InputDialog.TAG_VALUE, modifyBean.name);
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
                String nameBackString = data.getStringExtra(InputDialog.TAG_VALUE);
                modifyBean.name = nameBackString;
                (txt_pet_name).setText(modifyBean.name);
                break;
            case REQ_CODE_WEIGHT:// ModifyWeightDialog
                modifyBean.weight = data.getStringExtra(InputDialog.TAG_VALUE);
                txt_weight.setText(modifyBean.weight + "kg");
                break;
            case REQ_CODE_VARIETY:
            case REQ_CODE_INTRO:
                if (!TextUtils.isEmpty(modifyBean.description)) {
                    modifyBean.description = data.getStringExtra(ModifyVarietyDialog2.TAG_PARAM1);
//                UserMgr.INSTANCE.updatePetInfo(petInfo, PetInfo.FieldMask_Desc);
                    txt_variety.setText(modifyBean.description);
//                    PetInfoInstance.getInstance().updatePetInfo(modifyBean,param);
                }
                break;
            case REQ_CODE_PHOTO_SOURCE:
                if (data != null && data.getData() != null) {
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
                //todo 判断相机是否有返回
                File picture = new File(Environment.getExternalStorageDirectory()
                        + "/temp.jpg");
                if (!picture.exists()) {
                    return;
                }
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
}