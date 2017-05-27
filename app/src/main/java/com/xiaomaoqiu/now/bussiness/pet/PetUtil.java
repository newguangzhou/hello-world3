package com.xiaomaoqiu.now.bussiness.pet;

import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.push.PushDataCenter;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.pet.R;

import java.util.HashMap;

/**
 * Created by long on 2017/5/25.
 */

public class PetUtil {
    private static PetUtil instance;
    private PetUtil(){

    }
    public static PetUtil getInstance(){
        if(instance==null){
            instance=new PetUtil();

        }
        return instance;
    }


    public   HashMap<String,String> allDogEnergyAndNameMap=new HashMap<>();
    /**
     * 所有狗的名字
     */
    public String[] allDogNameList;

    /**
     * 所有狗的能量值
     */
    public String[] allDogEnergyList;


    public String dogName;
    public String energyType="";

    public void init(){
        dogName= SPUtil.getPetDescription();
        energyType=SPUtil.getEnergyType();
        allDogEnergyAndNameMap.clear();
        allDogNameList= PetAppLike.mcontext.getResources().getStringArray(R.array.alldog_name);
        allDogEnergyList=PetAppLike.mcontext.getResources().getStringArray(R.array.all_energyFormulaCodeList);
        int length=allDogNameList.length;
        for(int i=0;i<length;i++){
            allDogEnergyAndNameMap.put(allDogNameList[i],allDogEnergyList[i]);
        }
    }


    public void setPetName(String name){
        dogName=name;
        energyType=allDogEnergyAndNameMap.get(name);
    }



}
