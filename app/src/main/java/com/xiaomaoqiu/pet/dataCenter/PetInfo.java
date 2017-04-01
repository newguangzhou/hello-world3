package com.xiaomaoqiu.pet.dataCenter;

import com.xiaomaoqiu.pet.notificationCenter.NotificationCenter;

import org.json.JSONObject;

import java.util.Scanner;

/**
 * Created by huangjx on 16/3/30.
 */
public class PetInfo {

    public interface Callback_PetInfo{
        void onPetInfoChanged(PetInfo petInfo,int nFieldMask);
    }

    public interface Callback_PetLocating
    {
        void onLocateResult(boolean bFound, double latitude, double longitude);
    }

    static public class Date
    {
        public Date(int y, int m, int d){
            setDate(y,m,d);
        }

        public void setDate(int y,int m, int d)
        {
            year = y;
            month= m;
            day =d;
        }
        public int year;
        public int month;
        public int day;
    }

    //字段位,用于更新信息
    public static int FieldMask_Name = 1;
    public static int FieldMask_Sex = 1<<1;
    public static int FieldMask_Header = 1<<2;
    public static int FieldMask_Weight = 1<<3;
    public static int FieldMask_Desc = 1<<4;
    public static int FieldMask_TypeID = 1<<5;
    public static int FieldMask_Birth = 1<<6;
    public static int FieldMask_AtHome = 1<<7;

    public static int FieldMask_All = -1;

    public static int Male = 1;
    public static int Female = 2;

    private long petID = -1;    //ID
    private String name;        //名称
    private int sex;            //性别
    private String headerImg;   //头像
    private Date birthday = new Date(0,0,0);    //生日
    private double weight;      //体重
    private String variety;     //品种
    private String desc;        //说明
    private  int   petTypeId;   //品种2
    private  boolean petAtHome=true; //true - 宠物在家, false - 宠物活动中
    private DeviceInfo  devInfo = new DeviceInfo();

    public void notifyChanged(int nFieldMask)
    {
        NotificationCenter.INSTANCE.getObserver(Callback_PetInfo.class).onPetInfoChanged(this,nFieldMask);
    }

    public void initFromJson(JSONObject jsonPetInfo)
    {
        //{"status": 0, "pet_id": 1462953761, "description": "haha", "weight": "10.00", "sex": 1, "nick": "test", "birthday": "1970-01-01", "logo_url": "", "pet_type_id": 1}
        petID = jsonPetInfo.optLong("pet_id");
        name = jsonPetInfo.optString("nick");
        sex = jsonPetInfo.optInt("sex", 0);
        headerImg = jsonPetInfo.optString("logo_url");
        Scanner scanner = new Scanner(jsonPetInfo.optString("birthday"));
        scanner.useDelimiter("-");
        int year = scanner.nextInt();
        int month = scanner.nextInt();
        int day = scanner.nextInt();
        birthday.setDate(year,month,day);

        weight = jsonPetInfo.optDouble("weight");
        desc = jsonPetInfo.optString("description");
        petTypeId = jsonPetInfo.optInt("pet_type_id", 1);
        petAtHome = true;
        notifyChanged(FieldMask_All);
    }

    public  String getDesc() {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public int getPetTypeId(){
        return petTypeId;
    }

    public long getPetID() {
        return petID;
    }

    public void setPetID(long petID) {
        this.petID = petID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public void setBirthday(Date date){
        birthday = date;
    }

    public Date getBirthday() {return birthday;}

    public void setWeight(double weight) {
        this.weight = weight;
    }
    public double getWeight() {return weight;}

    public void setVariety(String variety){
        this.variety = variety;
    }
    public String getVariety(){return variety;}

    public void setAtHome(boolean bAtHome){this.petAtHome = bAtHome;}
    public boolean getAtHome(){return petAtHome;}

    public DeviceInfo getDevInfo() {
        return devInfo;
    }
}
