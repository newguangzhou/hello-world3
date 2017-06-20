package com.xiaomaoqiu.now.map.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/1/15.
 */

public class JsonToObject {
    public static final <T> T toObject(String json, Class<T> cls) {
        if ( null == json ){
            return null;
        }

        try{
            return JSON.parseObject(json, cls);
        }
        catch (Exception e){
            //e.printStackTrace();
        }
        return null;
    }

    public static final <T> List<T> toArray(String json, Class<T> cls) {
        if ( null == json ){
            return null;
        }

        try{
            return JSON.parseArray(json, cls);
        }
        catch (Exception e){
            //e.printStackTrace();
        }
        return null;
    }

    public static JSONObject toObject(String json) {
        if ( null == json ){
            return null;
        }

        try{
            return JSON.parseObject(json);
        }
        catch (Exception e){
            //e.printStackTrace();
        }
        return null;
    }
}
