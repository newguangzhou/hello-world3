package com.xiaomaoqiu.pet.ui.mainPages.pageMe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.dataCenter.HttpCode;
import com.xiaomaoqiu.pet.dataCenter.LoginMgr;
import com.xiaomaoqiu.pet.dataCenter.PetInfo;
import com.xiaomaoqiu.pet.dataCenter.UserMgr;
import com.xiaomaoqiu.pet.utils.AsyncImageTask;
import com.xiaomaoqiu.pet.utils.HttpUtil;
import com.xiaomaoqiu.pet.widgets.ActivityEx;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import mbg.bottomcalender.BottomCalenderView;


/**
 * Created by Administrator on 2015/6/12.
 */
public class PetInfoActivity extends ActivityEx implements PetInfo.Callback_PetInfo, AsyncImageTask.ImageCallback{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.pet_info));
        setContentView(R.layout.me_pet_info);
        initView();
    }


    private void initView()
    {
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

        /*findViewById(R.id.btn_modify_intro).setOnClickListener(new View.OnClickListener() {
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

        (((ToggleButton) findViewById(R.id.chk_gender))).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PetInfo petInfo=new PetInfo();
                if(isChecked) {
                    petInfo.setSex(PetInfo.Female);
                }else{
                    petInfo.setSex(PetInfo.Male);
                }
                UserMgr.INSTANCE.updatePetInfo(petInfo,PetInfo.FieldMask_Sex);
            }
        });
        initPetInfo(UserMgr.INSTANCE.getPetInfo(), PetInfo.FieldMask_All);
    }

    private  void initPetInfo(PetInfo petInfo,int nFieldMask)
    {
        if((nFieldMask & PetInfo.FieldMask_Name) != 0)
            ((TextView)findViewById(R.id.txt_pet_name)).setText(petInfo.getName());


        if((nFieldMask & PetInfo.FieldMask_Sex) != 0) {
            ((ToggleButton) findViewById(R.id.chk_gender)).setChecked(petInfo.getSex() == PetInfo.Female);
            if(petInfo.getSex() == petInfo.Female){
                ((ImageView)findViewById(R.id.img_pet_sex)).setImageResource(R.drawable.petinfo_sex_female);
            }else{
                ((ImageView)findViewById(R.id.img_pet_sex)).setImageResource(R.drawable.petinfo_sex_male);
            }
        }
        if((nFieldMask & PetInfo.FieldMask_Birth) != 0) {
            PetInfo.Date date = petInfo.getBirthday();
            ((TextView) findViewById(R.id.txt_birthday)).setText(String.format("%d-%d-%d", date.year, date.month, date.day));
        }
        if((nFieldMask & PetInfo.FieldMask_Weight) != 0)
            ((TextView)findViewById(R.id.txt_weight)).setText(String.format("%.2f kg", petInfo.getWeight()));
       /* if((nFieldMask & PetInfo.FieldMask_Desc) != 0)
            ((TextView)findViewById(R.id.txt_intro)).setText(petInfo.getDesc());*/

        if((nFieldMask & PetInfo.FieldMask_Header)!=0)
        {
            Log.v("petinfo","set pet header:"+petInfo.getHeaderImg());
            ImageView imgLogo = (ImageView)findViewById(R.id.img_pet_avatar);
            AsyncImageTask.INSTANCE.loadImage(imgLogo, petInfo.getHeaderImg(), this);
        }
        if((nFieldMask & PetInfo.FieldMask_Desc) != 0){
            ((TextView)findViewById(R.id.txt_variety)).setText(petInfo.getDesc());
        }
    }

    private void modifyVariety()
    {
        Intent intent = new Intent(this,ModifyVarietyDialog.class);
        startActivityForResult(intent,REQ_CODE_VARIETY);
    }

    public  void modifyAvatar()
    {
        Intent intent = new Intent(this,SelectAvatarSourceDialog.class);
        startActivityForResult(intent,REQ_CODE_PHOTO_SOURCE);
    }


    private void modifyName() {
        Intent intent = new Intent(this,ModifyNameDialog.class);
        intent.putExtra(InputDialog.TAG_VALUE, UserMgr.INSTANCE.getPetInfo().getName());
        startActivityForResult(intent, REQ_CODE_NAME);
    }

    private BottomCalenderView bottomCalenderView;
    private void modifyBirthday() {
        PetInfo.Date mPetBirthDay = UserMgr.INSTANCE.getPetInfo().getBirthday();
        if(null == mPetBirthDay){
            return;
        }
        if (bottomCalenderView == null) {
            bottomCalenderView = new BottomCalenderView(this, mPetBirthDay.year, mPetBirthDay.month, mPetBirthDay.day, new BottomCalenderView.OnDatePickedListener() {
                @Override
                public void onDatePicked(int year, int month, int day) {
                    PetInfo petInfo=new PetInfo();
                    petInfo.setBirthday(new PetInfo.Date(year, month, day));
                    UserMgr.INSTANCE.updatePetInfo(petInfo,PetInfo.FieldMask_Birth);
                }
            }, null);
        }
        bottomCalenderView.show();
    }

    private void modifyWeight()
    {
        Intent intent = new Intent(this,ModifyWeightDialog.class);
        intent.putExtra(InputDialog.TAG_VALUE, String.valueOf(UserMgr.INSTANCE.getPetInfo().getWeight()));
        startActivityForResult(intent, REQ_CODE_WEIGHT);
    }

    private void modifyIntro()
    {
        Intent intent = new Intent(this,ModifyIntroDialog.class);
        intent.putExtra(InputDialog.TAG_VALUE, UserMgr.INSTANCE.getPetInfo().getDesc());
        startActivityForResult(intent, REQ_CODE_INTRO);
    }

    private void onPhotoSource(int mode)
    {
        if(mode == R.id.btn_pick_from_library)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            startActivityForResult(intent, REQ_CODE_PHOTO_ZOOM);

        }else if(mode == R.id.btn_take_photo)
        {
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
        if(resultCode != Activity.RESULT_OK) return;
        PetInfo petInfo = new PetInfo();
        switch(requestCode){
            case REQ_CODE_NAME:
                petInfo.setName(data.getStringExtra(InputDialog.TAG_VALUE));
                UserMgr.INSTANCE.updatePetInfo(petInfo,PetInfo.FieldMask_Name);
                break;
            case REQ_CODE_WEIGHT:// ModifyWeightDialog
                petInfo.setWeight(Double.valueOf(data.getStringExtra(InputDialog.TAG_VALUE)));
                UserMgr.INSTANCE.updatePetInfo(petInfo,PetInfo.FieldMask_Weight);
                break;
            case REQ_CODE_VARIETY:
            case REQ_CODE_INTRO:
                petInfo.setDesc(data.getStringExtra(ModifyVarietyDialog2.TAG_PARAM1));
                UserMgr.INSTANCE.updatePetInfo(petInfo,PetInfo.FieldMask_Desc);
                break;
            case REQ_CODE_PHOTO_SOURCE:
                int mode = data.getIntExtra(SelectAvatarSourceDialog.TAG_MODE,-1);
                onPhotoSource(mode);
                break;
            case REQ_CODE_PHOTO_GRAPH:
                // 设置文件保存路径
                try {
                    File picture = new File(Environment.getExternalStorageDirectory()
                            + "/temp.jpg");
                    startPhotoZoom(Uri.fromFile(picture));
                }catch (Exception e)
                {
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
                    if(photo!=null)
                    {
                        try {

                            ImageView imgAvatar = (ImageView) findViewById(R.id.img_pet_avatar);
                            imgAvatar.setImageBitmap(photo); //把图片显示在ImageView控件上

                            String strImg = Environment.getExternalStorageDirectory() + "/temp.jpg";
                            //把Bitmap保存到sd卡中
                            File fImage = new File(strImg);
                            FileOutputStream iStream = new FileOutputStream(fImage);
                            photo.compress(Bitmap.CompressFormat.JPEG, 75, iStream);// (0-100)压缩文件
                            iStream.close();

                            HttpUtil.uploadFile("file.pet.upload_logo",strImg,new JsonHttpResponseHandler(){
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            //{"status": 0, "file_url": "http://www.xxx.com/abc.jpg"}
                                            Log.v("http", "file.pet.upload_logo:" + response.toString());
                                            HttpCode ret = HttpCode.valueOf(response.optInt("status", -1));
                                            if (ret == HttpCode.EC_SUCCESS) {
                                                //更新头像属性
                                                String urlLogo = response.optString("file_url");

                                                PetInfo petInfo = new PetInfo();
                                                petInfo.setHeaderImg(urlLogo);
                                                UserMgr.INSTANCE.updatePetInfo(petInfo, PetInfo.FieldMask_Header);
                                            }
                                        }


                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            Log.v("http", "file.pet.upload_logo:" + responseString);
                                        }
                                    },LoginMgr.INSTANCE.getUid(), LoginMgr.INSTANCE.getToken());

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
                break;
        }
    }

    @Override
    public void onPetInfoChanged(PetInfo petInfo,int nFieldMask) {
        initPetInfo(petInfo,nFieldMask);
    }

    @Override
    public void imageLoaded(String url, Bitmap obj, ImageView view) {
        if(obj != null)
        {
            view.setImageBitmap(obj);
        }
    }

    public static void skip(Context context){
        Intent intent=new Intent(context,PetInfoActivity.class);
        context.startActivity(intent);
    }

    public static void skipWithFinish(Activity activity){
        skip(activity);
        activity.finish();
    }
}