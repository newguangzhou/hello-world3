package com.xiaomaoqiu.now.bussiness.pet;

import com.xiaomaoqiu.now.PetAppLike;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.pet.R;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by long on 2017/5/25.
 */

public class PetUtil {
    private static PetUtil instance;

    private PetUtil() {

    }

    public static PetUtil getInstance() {
        if (instance == null) {
            instance = new PetUtil();

        }
        return instance;
    }


    public HashMap<String, String> allDogEnergyAndNameMap = new HashMap<>();
    /**
     * 所有狗的名字
     */
    public String[] allDogNameList;

    /**
     * 所有狗的能量值
     */
    public String[] allDogEnergyList;


    public String dogName;
    public String energyType = "";


    public void init() {
        dogName = SPUtil.getPetDescription();
        energyType = SPUtil.getTargetEnergy();
        allDogEnergyAndNameMap.clear();
        allDogNameList = PetAppLike.mcontext.getResources().getStringArray(R.array.alldog_name);
        allDogEnergyList = PetAppLike.mcontext.getResources().getStringArray(R.array.all_energyFormulaCodeList);
        int length = allDogNameList.length;
        for (int i = 0; i < length; i++) {
            allDogEnergyAndNameMap.put(allDogNameList[i], allDogEnergyList[i]);
        }
    }


    public void setPetName(String name) {
        dogName = name;
        energyType = allDogEnergyAndNameMap.get(name);
    }


    double RER;

    double method1;
    double method2;
    double method3;
    double method4;
    String age;

    double method1Energy;
    double method2Energy;
    double method3Energy;
    double method4Energy;

    //计算RER
    private double calculateRER() {
        double temp = 0;
        if (!"".equals(PetInfoInstance.getInstance().packBean.weight)) {
            try {
                temp =70* Math.pow(Double.valueOf(PetInfoInstance.getInstance().packBean.weight), 0.75);
            } catch (Exception e) {
                temp = 8;
            }
        }
        //todo 有问题
        return temp;
    }

    //计算年龄
    private void calculateAllEnergy() {
        //当前时间
        Date today = new Date();
        int todayYear = today.getYear();
        int todayMonth = today.getMonth();

        //宠物生日
        PetInfoInstance.MyDate dateFormat_birthday = PetInfoInstance.getInstance().packBean.dateFormat_birthday;
        int petYear = dateFormat_birthday.year;
        int petMonth = dateFormat_birthday.month;
        int result = (todayYear+1900 - petYear) * 12 + (todayMonth+1 - petMonth);
        if (result <= 4) {
            method1Energy = method1 * 0.6;
            method2Energy = method2 * 0.6;
            method3Energy = method3 * 0.6;
            method4Energy = method4 * 0.6;
        } else if (result <= 12) {
            method1Energy = method1 * 0.8;
            method2Energy = method2 * 0.8;
            method3Energy = method3 * 0.8;
            method4Energy = method4 * 0.8;
        } else if (result <= 96) {
            method1Energy = method1;
            method2Energy = method2;
            method3Energy = method3;
            method4Energy = method4;
        } else {
            method1Energy = method1 * 0.8;
            method2Energy = method2 * 0.8;
            method3Energy = method3 * 0.8;
            method4Energy = method4 * 0.8;
        }

    }

    //计算运动量推荐值
    public double calculateEnergy() {

        //        RER
        RER = calculateRER();
        method1 = RER * 0.07;
        method2 = RER * 0.08;
        method3 = RER * 0.09;
        method4 = RER * 0.10;

        calculateAllEnergy();



        double temp;
        if((energyType==null)||("".equals(energyType))){
            temp=method2Energy;
            return temp;
        }

        switch (energyType) {
            case "1":
                temp = method1Energy;
                break;
            case "2":
                temp = method2Energy;
                break;
            case "3":
                temp = method3Energy;
                break;
            case "4":
                temp = method4Energy;
                break;
            default:
                temp = method2Energy;
                break;
        }


        return temp;
    }
}
