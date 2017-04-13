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
import com.xiaomaoqiu.now.bussiness.Device.BindDeviceActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.ApiUtils;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.now.http.XMQCallback;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.InputDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyNameDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyVarietyDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyVarietyDialog2;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.ModifyWeightDialog;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.SelectAvatarSourceDialog;
import com.xiaomaoqiu.pet.R;

import java.io.File;
import java.io.FileOutputStream;

import mbg.bottomcalender.BottomCalenderView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Administrator on 2015/6/12.
 */
public class PetInfoActivity extends BaseActivity  {
    private final int REQ_CODE_BIRTHDAY = 1;
    private final int REQ_CODE_WEIGHT = 2;
    private final int REQ_CODE_INTRO = 3;
    private final int REQ_CODE_NAME = 4;
    private final int REQ_CODE_VARIETY = 5;
    private final int REQ_CODE_PHOTO_SOURCE = 6;
    private final int REQ_CODE_PHOTO_GRAPH = 7;// 拍照
    private final int REQ_CODE_PHOTO_RESULT = 8;// 结果
    private final int REQ_CODE_PHOTO_ZOOM = 9; // 缩放

    final String IMAGE_UNSPECIFIED = "image/*";

    private  ToggleButton chk_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.pet_info));
        setContentView(R.layout.me_pet_info);
        initView();
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

        chk_gender=(ToggleButton) findViewById(R.id.chk_gender);


        initPetInfo();
    }

    private void initPetInfo() {
        PetInfoBean petInfoBean = PetInfoInstance.getInstance().getPackBean();
        ((TextView) findViewById(R.id.txt_pet_name)).setText(petInfoBean.name);


        ((ToggleButton) findViewById(R.id.chk_gender)).setChecked(petInfoBean.sex == Constants.Female);
        if (petInfoBean.sex == Constants.Female) {
            ((ImageView) findViewById(R.id.img_pet_sex)).setImageResource(R.drawable.petinfo_sex_female);
        } else {
            ((ImageView) findViewById(R.id.img_pet_sex)).setImageResource(R.drawable.petinfo_sex_male);
        }

        ((TextView) findViewById(R.id.txt_birthday)).setText(petInfoBean.birthday);
        if(!TextUtils.isEmpty(petInfoBean.weight)) {
            ((TextView) findViewById(R.id.txt_weight)).setText(petInfoBean.weight + "kg");
        }

        SimpleDraweeView imgLogo = (SimpleDraweeView) findViewById(R.id.img_pet_avatar);
        Uri uri = Uri.parse(petInfoBean.logo_url);
        imgLogo.setImageURI(uri);
//        AsyncImageTask.INSTANCE.loadImage(imgLogo, petInfoBean.logo_url, this);

        ((TextView) findViewById(R.id.txt_variety)).setText(petInfoBean.description);


        chk_gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PetInfoBean tmpBean = PetInfoInstance.getInstance().getPackBean();
                if (isChecked) {
                    tmpBean.sex = Constants.Female;
                } else {
                    tmpBean.sex = Constants.Male;
                }
                PetInfoInstance.getInstance().updatePetInfo(tmpBean);
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
        intent.putExtra(InputDialog.TAG_VALUE, PetInfoInstance.getInstance().packBean.name);
        startActivityForResult(intent, REQ_CODE_NAME);
    }

    private BottomCalenderView bottomCalenderView;

    private void modifyBirthday() {
        PetInfoInstance.Date mPetBirthDay=PetInfoInstance.getInstance().getPackBean().dateFormat_birthday;
        if (bottomCalenderView == null) {
            bottomCalenderView = new BottomCalenderView(this, mPetBirthDay.year, mPetBirthDay.month, mPetBirthDay.day, new BottomCalenderView.OnDatePickedListener() {
                @Override
                public void onDatePicked(int year, int month, int day) {
                    PetInfoInstance.Date tmpDateFormatBirthday=new PetInfoInstance.Date(year,month,day);
                    PetInfoInstance.getInstance().setDateFormat_birthday(tmpDateFormatBirthday);
                    PetInfoInstance.getInstance().updatePetInfo(PetInfoInstance.getInstance().getPackBean());
                }
            }, null);
        }
        bottomCalenderView.show();
    }

    private void modifyWeight() {
        Intent intent = new Intent(this, ModifyWeightDialog.class);
        intent.putExtra(InputDialog.TAG_VALUE, PetInfoInstance.getInstance().packBean.weight);
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
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            startActivityForResult(intent, REQ_CODE_PHOTO_ZOOM);

        } else if (mode == R.id.btn_take_photo) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "temp.jpg")));
            startActivityForResult(intent, REQ_CODE_PHOTO_GRAPH);
        }
    }

    /**
     * 收缩图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQ_CODE_PHOTO_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
//        PetInfo petInfo = new PetInfo();
        final PetInfoBean bean=PetInfoInstance.getInstance().packBean;
        switch (requestCode) {
            case REQ_CODE_NAME:
//                petInfo.setName(data.getStringExtra(InputDialog.TAG_VALUE));
//                UserMgr.INSTANCE.updatePetInfo(petInfo, PetInfo.FieldMask_Name);
                String nameBackString=data.getStringExtra(InputDialog.TAG_VALUE);
                bean.name=nameBackString;
                PetInfoInstance.getInstance().updatePetInfo(bean);
                break;
            case REQ_CODE_WEIGHT:// ModifyWeightDialog
                bean.weight=data.getStringExtra(InputDialog.TAG_VALUE);
//                UserMgr.INSTANCE.updatePetInfo(petInfo, PetInfo.FieldMask_Weight);
                PetInfoInstance.getInstance().updatePetInfo(bean);
                break;
            case REQ_CODE_VARIETY:
            case REQ_CODE_INTRO:
                bean.description=data.getStringExtra(ModifyVarietyDialog2.TAG_PARAM1);
//                UserMgr.INSTANCE.updatePetInfo(petInfo, PetInfo.FieldMask_Desc);
                PetInfoInstance.getInstance().updatePetInfo(bean);
                break;
            case REQ_CODE_PHOTO_SOURCE:
                int mode = data.getIntExtra(SelectAvatarSourceDialog.TAG_MODE, -1);
                onPhotoSource(mode);
                break;
            case REQ_CODE_PHOTO_GRAPH:
                // 设置文件保存路径
                try {
                    File picture = new File(Environment.getExternalStorageDirectory()
                            + "/temp.jpg");
                    startPhotoZoom(Uri.fromFile(picture));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQ_CODE_PHOTO_ZOOM:
                startPhotoZoom(data.getData());
                break;
            case REQ_CODE_PHOTO_RESULT:
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    if (photo != null) {
                        try {

                            ImageView imgAvatar = (ImageView) findViewById(R.id.img_pet_avatar);
                            imgAvatar.setImageBitmap(photo); //把图片显示在ImageView控件上

                            String strImg = Environment.getExternalStorageDirectory() + "/temp.jpeg";
                            //把Bitmap保存到sd卡中
                            File fImage = new File(strImg);
                            FileOutputStream iStream = new FileOutputStream(fImage);
                            photo.compress(Bitmap.CompressFormat.JPEG, 75, iStream);// (0-100)压缩文件
                            iStream.close();

//                            HttpUtil.uploadFile("file.pet.upload_logo", strImg, new JsonHttpResponseHandler() {
//                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                    //{"status": 0, "file_url": "http://www.xxx.com/abc.jpg"}
//                                    Log.v("http", "file.pet.upload_logo:" + response.toString());
//                                    HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
//                                    if (ret == HttpCode.EC_SUCCESS) {
//                                        //更新头像属性
//                                        String urlLogo = response.optString("file_url");
//
////                                        PetInfo petInfo = new PetInfo();
//                                        modifyBean.logo_url=urlLogo;
////                                        UserMgr.INSTANCE.updatePetInfo(petInfo, PetInfo.FieldMask_Header);
//                                        PetInfoInstance.getInstance().updatePetInfo(modifyBean);
//                                    }
//                                }
//
//
//                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                    Log.v("http", "file.pet.upload_logo:" + responseString);
//                                }
//                            }, UserInstance.getInstance().getUid(), UserInstance.getInstance().getToken());
                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), fImage);
                            MultipartBody.Part body = MultipartBody.Part.createFormData("flieName", fImage.getName(), requestFile);
                            ApiUtils.getApiService().uploadLogo(UserInstance.getInstance().getUid(),UserInstance.getInstance().getToken(),PetInfoInstance.getInstance().getPet_id(),body).enqueue(
                                    new XMQCallback<PictureBean>() {
                                        @Override
                                        public void onSuccess(Response<PictureBean> response, PictureBean message) {
                                            HttpCode ret = HttpCode.valueOf(message.status);
                                            switch (ret) {
                                                case EC_SUCCESS:
                                                    //更新头像属性
                                                    String urlLogo = message.file_url;
                                                    bean.logo_url=urlLogo;
                                                    PetInfoInstance.getInstance().updatePetInfo(bean);
                                                    break;

                                                default:
//                                                    ToastUtil.showTost("");
                                            }
                                        }

                                        @Override
                                        public void onFail(Call<PictureBean> call, Throwable t) {

                                        }
                                    }
                            );

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;
        }
    }
}